package net.warp.plugin.warpgauntlet;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
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
    * Add Corrupted ID's
    * Find a way to see if i can auto attack
    * Add Prayer pot to Config
    * Prayer flick
    * Add Demi Bosses
    */
    private static final Set<Integer> magicAttackID = Set.of(1706, 1707, 1708);

    private static final Set<Integer> rangeAttackID = Set.of(1711, 1712);

    private static final int[] bowID = { 23901, 23902, 23903, 23855, 23856, 23857 };

    private static final int[] staffID = { 23898, 23899, 23900, 23852, 23853, 23854 };

    private static final int[] hunlefID = { 9021, 9022, 9023, 9024, 9035, 9036, 9037, 9038 };

    private static final int[] potionID = { 23882, 23883, 23884, 23885 };
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
    private void onVarbitChanged (VarbitChanged varbitChanged)
    {
        if (varbitChanged.getVarbitId() == 9177)
        {
            if (isHunllefVarbitSet() && !Prayers.isEnabled(Prayer.PROTECT_FROM_MISSILES))
            {
                togglePrayer(Prayer.PROTECT_FROM_MISSILES);
            }
        }
    }

    @Subscribe
    private void onGameTick (GameTick gameTick)
    {
        if (isHunllefVarbitSet())
        {
            Item staff = Inventory.getFirst(staffID);
            Item bow = Inventory.getFirst(bowID);
            hunlef = NPCs.getNearest(hunlefID);
            if (config.swapWeapon() && hunlef.getTransformedComposition().getOverheadIcon() == HeadIcon.MAGIC && !Equipment.contains(bowID))
            {
                log.debug("Magic overhead");
                if (bow != null)
                {
                    log.debug("equiping Bow");
                    bow.interact(InteractMethod.PACKETS, "Wield");
                }
            }

            if (config.swapWeapon() && hunlef.getTransformedComposition().getOverheadIcon() == HeadIcon.RANGED && !Equipment.contains(staffID))
            {
                log.debug("Range overhead");
                if (staff != null)
                {
                    log.debug("equiping Staff");
                    staff.interact(InteractMethod.PACKETS, "Wield");
                }
            }
        }
    }

    @Subscribe
    private void onProjectileSpawned (ProjectileSpawned projectileSpawned)
    {
        if (isHunllefVarbitSet())
        {

            Projectile projectile = projectileSpawned.getProjectile();
            if (magicAttackID.contains(projectile.getId()))
            {
                if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MAGIC))
                {
                    togglePrayer(Prayer.PROTECT_FROM_MAGIC);
                }
            }
            if (rangeAttackID.contains(projectile.getId()))
            {
                if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MISSILES))
                {
                    togglePrayer(Prayer.PROTECT_FROM_MISSILES);
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
            Item food = Inventory.getFirst(23874, 25958);

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
    private boolean isGauntletVarbitSet()
    {
        return client.getVar(9178) == 1;
    }

    private boolean isHunllefVarbitSet()
    {
        return client.getVar(9177) == 1;
    }

}
