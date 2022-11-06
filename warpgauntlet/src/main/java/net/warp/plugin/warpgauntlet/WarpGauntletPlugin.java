package net.warp.plugin.warpgauntlet;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ProjectileSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.widgets.Prayers;
import org.pf4j.Extension;

import java.util.Set;

@PluginDescriptor(
        name = "WaRp Gauntlet Swapper",
        description = "Swaps prayer and weapon in Gauntlet fight",
        enabledByDefault = false
)

@Slf4j
@Extension
public class WarpGauntletPlugin extends LoopedPlugin
{

    private static final Set<Integer> magicAttackID = Set.of(1706, 1707);

    private static final Set<Integer> rangeAttackID = Set.of(1711, 1712);

    private static final int[] bowID = { 23901, 23902, 23903};

    private static final int[] staffID = { 23898, 23899, 23900 };

    private static final int[] hunlefID = { 9021, 9022, 9023, 9024, 9035, 9036, 9037, 9038 };

    private static final int[] potionID = { 23882, 23883, 23884 };
    private NPC hunlef = null;


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
    private void onProjectileSpawned (ProjectileSpawned projectileSpawned)
    {
        if (isHunllefVarbitSet())
        {
            Projectile projectile = projectileSpawned.getProjectile();
            if (magicAttackID.contains(projectile.getId()))
            {
                log.debug("Magic attack");
                if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MAGIC))
                {
                    Prayers.toggle(Prayer.PROTECT_FROM_MAGIC);
                }
            }

            if (rangeAttackID.contains(projectile.getId()))
            {
                log.debug("Range attack");
                if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MISSILES))
                {
                    Prayers.toggle(Prayer.PROTECT_FROM_MISSILES);
                }
            }
        }
    }

    @Override
    protected int loop()
    {
        if (isHunllefVarbitSet())
        {
            Item staff = Inventory.getFirst(staffID);
            Item bow = Inventory.getFirst(bowID);
            Item potion = Inventory.getFirst(potionID);
            Item food = Inventory.getFirst(23874);

            hunlef = NPCs.getNearest(hunlefID);

            if (config.eat() && Combat.getHealthPercent() <= config.healthPercent())
            {

                food.interact("Eat");
                return 323;
            }

            if (Prayers.getPoints() <= 27)
            {
                potion.interact("Drink");
                return 323;
            }

            if (hunlef.getTransformedComposition().getOverheadIcon() == HeadIcon.MAGIC && !Equipment.contains(bowID))
            {
                log.debug("Magic overhead");
                if (bow != null)
                {
                    log.debug("equiping Bow");
                    bow.interact("Wield");
                    return 323;
                }

            }

            if (hunlef.getTransformedComposition().getOverheadIcon() == HeadIcon.RANGED && !Equipment.contains(staffID))
            {
                log.debug("Range overhead");
                if (staff != null)
                {
                    log.debug("equiping Staff");
                    staff.interact("Wield");
                    return 343;
                }

            }

            if (Equipment.contains(bowID) && !Prayers.isEnabled(config.offencePrayerRange().getPrayer()))
            {
                Prayers.toggle(config.offencePrayerRange().getPrayer());
                return 234;
            }

            if (Equipment.contains(staffID) && !Prayers.isEnabled(config.offencePrayerMage().getPrayer()))
            {
                Prayers.toggle(config.offencePrayerMage().getPrayer());
                return 125;
            }
        }
        return 323;
    }



    private boolean isGauntletVarbitSet()
    {
        return client.getVar(9178) == 1;
    }

    private boolean isHunllefVarbitSet()
    {
        return client.getVar(9177) == 1;
    }

}
