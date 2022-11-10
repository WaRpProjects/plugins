package net.warp.plugin.warpgauntlet;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ProjectileSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.interaction.InteractMethod;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.widgets.Prayers;
import net.unethicalite.api.widgets.Widgets;

import org.checkerframework.checker.units.qual.A;
import org.pf4j.Extension;

import java.util.Set;
@PluginDescriptor(
        name = "WaRp Hunllef Swapper",
        description = "Helps with Hunllef/Corrupted",
        enabledByDefault = false
)
@Slf4j
@Extension
public class WarpGauntletPlugin extends LoopedPlugin
{
    /*
    ToDo:
    * Find a way to see if i can auto attack
    * Prayer flick
    * Add Demi Bosses
    */
    private final Set<Integer> magicAttackID = Set.of(1707, 1708);
    private final Set<Integer> rangeAttackID = Set.of(1711, 1712);
    private final Set<Integer> prayerAttackID = Set.of(1713, 1714);
    private final int[] bowID = { 23901, 23902, 23903, 23855, 23856, 23857 };
    private final int[] staffID = { 23898, 23899, 23900, 23852, 23853, 23854 };
    private final int[] hunlefID = { 9021, 9022, 9023, 9024, 9035, 9036, 9037, 9038 };
    private final int[] potionID = { 23882, 23883, 23884, 23885 };
    private final int[] foodID = { 23874, 25958 };

    private NPC hunllef = null;
    private int attackCount = 4;
    private AttackPhase attackPhase;
    @Provides
    WarpGauntletConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WarpGauntletConfig.class);
    }
    @Inject
    WarpGauntletConfig config;
    @Inject
    private Client client;
    @Subscribe
    private void onVarbitChanged (VarbitChanged varbitChanged)
    {
        if (varbitChanged.getVarbitId() == 9177)
        {
            if (isHunllefVarbitSet() && !isPrayingMissiles())
            {
                log.debug("Entering Hunlef toggle MISSILE Prayer");
                togglePrayer(Prayer.PROTECT_FROM_MISSILES);
                attackPhase = AttackPhase.RANGE;
            }
        }
    }
    @Subscribe
    private void onGameTick (GameTick gameTick)
    {
        if (isHunllefVarbitSet())
        {
            hunllef = NPCs.getNearest(hunlefID);
            if (npcHeadIcon(hunllef) == HeadIcon.MAGIC && swapWeapon(bowID))
            {
                log.debug("equipping Bow");
                Inventory.getFirst(bowID).interact(InteractMethod.PACKETS, "Wield");
            }

            if (npcHeadIcon(hunllef) == HeadIcon.RANGED && swapWeapon(staffID))
            {
                log.debug("equipping Staff");
                Inventory.getFirst(staffID).interact(InteractMethod.PACKETS, "Wield");
            }
        }
    }
    @Subscribe
    private void onChatMessage (ChatMessage chatMessage)
    {
        if (chatMessage.getMessage().contains("Your prayers have been") && isHunllefVarbitSet())
        {
            log.debug("Prayers have been disabled");
        }
    }
    @Subscribe
    private void onProjectileSpawned (ProjectileSpawned projectileSpawned)
    {
        if (isHunllefVarbitSet())
        {
            Projectile projectile = projectileSpawned.getProjectile();
            if (hunllefAttack(projectile))
            {
                --attackCount;
                log.debug("Counting down attacks: " + attackCount);

                if (attackCount == 0)
                {
                    log.debug("Time to switch Defencive prayer");
                    switch(attackPhase)
                    {
                        case MAGIC:
                            attackPhase = AttackPhase.RANGE;
                            break;
                        case RANGE:
                            attackPhase = AttackPhase.MAGIC;
                            break;
                    }
                    log.debug("Switching attack phase: " + attackPhase);
                    attackCount = 4;
                }
            }
        }
    }
    @Override
    protected int loop()
    {
        if (isHunllefVarbitSet())
        {
            Item potion = Inventory.getFirst(potionID);
            Item food = Inventory.getFirst(foodID);

            if (config.eat() && Combat.getHealthPercent() <= config.healthPercent() && food != null)
            {
                log.debug("Eating");
                food.interact(InteractMethod.PACKETS, "Eat");
                return 600;
            }
            if (config.drinkPot() && Prayers.getPoints() <= config.prayerPoints() && potion != null)
            {
                log.debug("Drinking Prayer pot at " + Prayers.getPoints() + "Prayer points");
                potion.interact(InteractMethod.PACKETS, "Drink");
                return 600;
            }

            if (!Prayers.isEnabled(attackPhase.getPrayer()) || playerHeadIcon() == null)
            {
                log.debug("Defencive prayer: " + attackPhase.getPrayer());
                togglePrayer(attackPhase.getPrayer());
                return 600;
            }

            if (Equipment.contains(bowID) && !Prayers.isEnabled(config.offencePrayerRange().getPrayer()))
            {
                log.debug("Offencive prayer");
                togglePrayer(config.offencePrayerRange().getPrayer());
                return 600;
            }

            if (Equipment.contains(staffID) && !Prayers.isEnabled(config.offencePrayerMage().getPrayer()))
            {
                log.debug("Offencive Mage prayer");
                togglePrayer(config.offencePrayerMage().getPrayer());
                return 600;
            }
        }
        return 300;
    }

    private void togglePrayer(Prayer prayer)
    {
        Widget widget = Widgets.get(prayer.getWidgetInfo());
        if (widget != null)
        {
            widget.interact(InteractMethod.PACKETS, 0);
        }
    }

    private HeadIcon npcHeadIcon(NPC npc) { return npc.getTransformedComposition().getOverheadIcon(); }
    private HeadIcon playerHeadIcon() { return Players.getLocal().getOverheadIcon(); }
    private boolean isPrayingMissiles() { return Prayers.isEnabled(Prayer.PROTECT_FROM_MISSILES); }
    private boolean swapWeapon(int[] weapon) { return !Equipment.contains(weapon) && config.swapWeapon(); }
    private boolean hunllefAttack(Projectile projectile) { return magicAttackID.contains(projectile.getId()) || rangeAttackID.contains(projectile.getId()) || prayerAttackID.contains(projectile.getId());}
    private boolean isHunllefVarbitSet() { return client.getVar(9177) == 1; }

}
