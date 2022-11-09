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
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.interaction.InteractMethod;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.widgets.Prayers;
import net.unethicalite.api.widgets.Widgets;

import org.pf4j.Extension;

import java.util.Set;
@PluginDescriptor(
        name = "WaRp Hunlef Swapper",
        description = "Helps with Hunlef/Corrupted",
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
    private final Set<Integer> magicAttackID = Set.of(1706, 1707, 1708);
    private final Set<Integer> rangeAttackID = Set.of(1711, 1712);
    private final int[] bowID = { 23901, 23902, 23903, 23855, 23856, 23857 };
    private final int[] staffID = { 23898, 23899, 23900, 23852, 23853, 23854 };
    private final int[] hunlefID = { 9021, 9022, 9023, 9024, 9035, 9036, 9037, 9038 };
    private final int[] potionID = { 23882, 23883, 23884, 23885 };
    private final int[] foodID = { 23874, 25958 };
    private NPC hunlef = null;
    private int attackCount = 4;
    private AttackPhase attackPhase = AttackPhase.RANGE;
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
            hunlef = NPCs.getNearest(hunlefID);
            if (config.swapWeapon() && npcHeadIcon(hunlef) == HeadIcon.MAGIC && !Equipment.contains(bowID))
            {
                log.debug("equiping Bow");
                Inventory.getFirst(bowID).interact(InteractMethod.PACKETS, "Wield");
            }
            if (config.swapWeapon() && npcHeadIcon(hunlef) == HeadIcon.RANGED && !Equipment.contains(staffID))
            {
                log.debug("equiping Staff");
                Inventory.getFirst(staffID).interact(InteractMethod.PACKETS, "Wield");
            }
        }
    }
    @Subscribe
    private void onChatMessage (ChatMessage chatMessage)
    {
        if (chatMessage.getMessage().contains("Your prayers have been") && isHunllefVarbitSet())
        {
            log.debug("Prayers been disabled turning on: " + attackPhase.getPrayer().name());
            togglePrayer(attackPhase.getPrayer());
        }
    }
    @Subscribe
    private void onProjectileSpawned (ProjectileSpawned projectileSpawned)
    {
        if (isHunllefVarbitSet())
        {
            Projectile projectile = projectileSpawned.getProjectile();
            if (magicAttackID.contains(projectile.getId()) || rangeAttackID.contains(projectile.getId()))
            {
                --attackCount;
                log.debug("Attack count: " + attackCount);
            }

            if (magicAttackID.contains(projectile.getId()))
            {
                if (!isPrayingMagic())
                {
                    togglePrayer(Prayer.PROTECT_FROM_MAGIC);
                    attackPhase = AttackPhase.MAGIC;
                }
            }
            if (rangeAttackID.contains(projectile.getId()))
            {
                if (!isPrayingMissiles())
                {
                    togglePrayer(Prayer.PROTECT_FROM_MISSILES);
                    attackPhase = AttackPhase.RANGE;
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
                food.interact(InteractMethod.PACKETS, "Eat");
                return 300;
            }
            if (config.drinkPot() && Prayers.getPoints() <= config.prayerPoints() && potion != null)
            {
                potion.interact(InteractMethod.PACKETS, "Drink");
                return 300;
            }

            if (attackPhase == AttackPhase.MAGIC && !isPrayingMagic())
            {
                log.debug("Magic phase switching Prayer");
                togglePrayer(Prayer.PROTECT_FROM_MAGIC);
                return 300;
            }

            if (attackPhase == AttackPhase.RANGE && !isPrayingMissiles())
            {
                log.debug("Range phase switching prayer");
                togglePrayer(Prayer.PROTECT_FROM_MISSILES);
                return 300;
            }

            if (attackCount == 0 && isPrayingMissiles() && attackPhase == AttackPhase.RANGE)
            {
                log.debug("Pre switching Prayers: MAGIC");
                togglePrayer(Prayer.PROTECT_FROM_MAGIC);
                attackPhase = AttackPhase.MAGIC;
                attackCount = 4;
                return 300;
            }

            if (attackCount == 0 && isPrayingMagic() && attackPhase == AttackPhase.MAGIC)
            {
                log.debug("Pre switching Prayers: MISSILES" );
                togglePrayer(Prayer.PROTECT_FROM_MISSILES);
                attackPhase = AttackPhase.RANGE;
                attackCount = 4;
                return 300;
            }

            if (Equipment.contains(bowID) && !Prayers.isEnabled(config.offencePrayerRange().getPrayer()))
            {
                togglePrayer(config.offencePrayerRange().getPrayer());
                return 300;
            }

            if (Equipment.contains(staffID) && !Prayers.isEnabled(config.offencePrayerMage().getPrayer()))
            {
                togglePrayer(config.offencePrayerMage().getPrayer());
                return 300;
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

    private boolean isPrayingMagic() { return Prayers.isEnabled(Prayer.PROTECT_FROM_MAGIC); }
    private boolean isPrayingMissiles() { return Prayers.isEnabled(Prayer.PROTECT_FROM_MISSILES); }
    private boolean isHunllefVarbitSet()
    {
        return client.getVar(9177) == 1;
    }

}
