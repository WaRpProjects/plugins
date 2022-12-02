package net.warp.plugin.warpcrabs;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.game.Prices;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.Magic;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.widgets.Tab;
import net.unethicalite.api.widgets.Tabs;
import net.unethicalite.api.widgets.Widgets;
import org.pf4j.Extension;

import javax.inject.Inject;

//Thanks to Keegan for the support

@PluginDescriptor(
        name = "WaRp Crabs",
        description = "Get crabs",
        enabledByDefault = false
)
@Slf4j
@Extension
public class WarpCrabsPlugin extends LoopedPlugin {

    @Provides
    WarpCrabsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WarpCrabsConfig.class);
    }
    @Inject
    private WarpCrabsConfig config;
    @Inject
    public TimerUtil timerUtil;
    @Inject
    public Client client;
    @Inject
    private ConfigManager configManager;
    @Inject
    private BuilderOverlay builderOverlay;
    @Inject
    private OverlayManager overlayManager;
    private boolean timerRunning = false;
    private boolean getAmmo = false;
    private final WorldPoint walkLocation = new WorldPoint(1760, 3509, 0);
    private WorldPoint customLocation;
    private WorldPoint fightPoint;
    private int gameTicks = 5;

    public int alchProfit;
    public int rangeExp;
    public int attackExp;
    public int strengthExp;
    public int defenceExp;
    public int mageExp;
    public long startTime;
    public String status = "Idle";


    private Alch alch;
    @Override
    protected void startUp()
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        overlayManager.add(builderOverlay);
        rangeExp = Skills.getExperience(Skill.RANGED);
        attackExp = Skills.getExperience(Skill.ATTACK);
        strengthExp = Skills.getExperience(Skill.STRENGTH);
        defenceExp = Skills.getExperience(Skill.DEFENCE);
        mageExp = Skills.getExperience(Skill.MAGIC);
        alchProfit = 0;
        startTime = System.currentTimeMillis();

        if (config.useCustomLocation())
        {
            customLocation = Players.getLocal().getWorldLocation();
            configManager.setConfiguration("warpcrabs", "customLocation", String.format("%s %s %s", customLocation.getX(), customLocation.getY(), customLocation.getPlane()));
        }
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(builderOverlay);
    }

    @Subscribe
    private void onGameTick (GameTick gameTick)
    {
        if (gameTicks <= 0)
        {
            alch = Skills.getLevel(Skill.MAGIC) > 54 ? Alch.HIGH_ALCH : Alch.LOW_ALCH;
            if (Inventory.contains(config.alchItem()) && config.highAlch())
            {
                Item alchItem = Inventory.getFirst(config.alchItem());
                if (gotRunes())
                {
                    status = "Alching";
                    log.debug("Doing an: " + alch.getSpell() + " on item " + config.alchItem());
                    Magic.cast(alch.getSpell(), alchItem);
                }
            }
            gameTicks = alch.getTick();
        }
        gameTicks--;
    }

    @Override
    protected int loop()
    {
        Player local = Players.getLocal();
        NPC crab = null;

        if (config.useCustomLocation())
        {
            fightPoint = customLocation;
        }
        else
        {
            fightPoint = config.location().getLocationPoint();
        }

        if (!Combat.isRetaliating())
        {
            status = "Setup";
            log.debug("Setting up Retaliate");
            Tabs.open(Tab.COMBAT);
            Time.sleepUntil(() -> Tabs.isOpen(Tab.COMBAT), -2);
            Widget retaliateWidget = Widgets.get(593, 30);
            retaliateWidget.interact(0);
            return -1;
        }

        if (config.eatFood() && Combat.getHealthPercent() <= config.healthPercent())
        {
            Item food = Inventory.getFirst(config.foodName());
            if (food != null)
            {
                status = "Eating";
                log.debug("Eating: " + config.foodName());
                food.interact("Eat");
                return -1;
            }
            log.debug("Error on eating no food");
        }

        if (Movement.isWalking())
        {
            status = "Walking";
            return -1;
        }

        if (local.getWorldLocation().distanceTo(fightPoint) < 3 && !timerRunning)
        {
            status = "Setting Timer";
            timerUtil.setSleepTime(Rand.nextInt(8, 10));
            timerRunning = true;
            return -1;
        }

        if (timerUtil.toMinutes((int)timerUtil.getElapsedTime()) >= 10 && timerRunning)
        {
            status = "Re aggro";
            if (walkLocation.distanceTo(local.getWorldLocation()) > 4)
            {
                log.debug("Walking to get aggro again.");
                Movement.walkTo(walkLocation);
                return -1;
            }
            timerRunning = false;
            return -1;
        }

        if (!timerRunning && local.getWorldLocation().distanceTo(fightPoint) != 0 && !getAmmo)
        {
            status = "Walking";
            log.debug("Moving to: " + config.location().getLocationName());
            Movement.walkTo(fightPoint);
            return -1;
        }

        if (local.getInteracting() != NPCs.getNearest("Sand Crab"))
        {
            status = "Getting Crab";
            crab = NPCs.getNearest(x -> x.getName().contains("Sand Crab") && local.distanceTo(x.getWorldLocation()) < 2);

            if (crab == null)
            {
                NPC crabObject = NPCs.getNearest(x -> x.getName().equals("Sandy rocks") && local.distanceTo(x.getWorldLocation()) < 8);
                if (crabObject != null)
                {
                    status = "Moving to Crab";
                    Movement.walkTo(crabObject.getWorldLocation());
                    Time.sleepUntil(() -> crabObject.getWorldLocation() == local.getWorldLocation(), -4);
                    return -1;
                }
                return -1;
            }
        }

        if (crab != null && local.isIdle() && !crab.isDead())
        {
            status = "Attack Crab";
            crab.interact("Attack");
            Time.sleepUntil(() -> !local.isIdle(), -2);
            return -1;
        }

        if (config.getAmmo() && local.isIdle())
        {
            TileItem ammo = TileItems.getNearest(x -> x.distanceTo(local.getWorldLocation()) < 2 && x.getName().contains(config.ammoName()));
            if (ammo != null)
            {
                status = "Pickup Ammo";
                getAmmo = true;
                log.debug("Picking up ammo");
                ammo.pickup();
                Time.sleepUntil(() -> ammo == null, -2);
                getAmmo = false;
                return -1;
            }

            Item ammoEquip = Inventory.getFirst(config.ammoName());
            if (ammoEquip != null)
            {
                log.debug("Equipping ammo");
                ammoEquip.interact("Wield");
                Time.sleepUntil(() -> !Inventory.contains(ammoEquip.getName()), -2);
                return -1;
            }
        }

        if (local.getWorldLocation() != fightPoint && local.isIdle())
        {
            Movement.walk(fightPoint);
            Time.sleepUntil(() -> local.getWorldLocation() == fightPoint, -2);
            return -1;
        }
        return -1;
    }


    private void calculateProfit()
    {
        if (config.highAlch())
        {
            Item alchItem = Inventory.getFirst(config.alchItem());
            if (alchItem != null)
            {
                ItemComposition alchComp = alchItem.getComposition();
                int buyPrice = Prices.getItemPrice(alchItem.getId());
                int alchPrice = alchComp.getHaPrice();
                int runePrice = Prices.getItemPrice(554) + Prices.getItemPrice(561);
                alchProfit += alchPrice - (buyPrice + runePrice);
            }
        }
    }


    private boolean gotRunes()
    {

        int fireRune = 4;
        int natureRune = 10;

        if (client.getVarbitValue(Varbits.RUNE_POUCH_RUNE1) == natureRune && client.getVarbitValue(Varbits.RUNE_POUCH_RUNE2) == fireRune)
        {
            if (client.getVarbitValue(client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT1)) >= 1 && client.getVarbitValue(client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT2)) >= 5)
            {
                return true;
            }
        }

        if (client.getVarbitValue(Varbits.RUNE_POUCH_RUNE1) == fireRune && client.getVarbitValue(Varbits.RUNE_POUCH_RUNE2) == natureRune)
        {
            if (client.getVarbitValue(client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT1)) >= 5 && client.getVarbitValue(client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT2)) >= 1)
            {
                return true;
            }
        }

        if (Inventory.getCount(true, "Nature rune") > 1 && Inventory.getCount(true,"Fire rune") > 5)
        {
            return true;
        }

        return false;
    }
}
