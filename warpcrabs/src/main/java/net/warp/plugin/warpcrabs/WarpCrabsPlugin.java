package net.warp.plugin.warpcrabs;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.Player;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.Magic;
import net.unethicalite.api.magic.SpellBook;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.widgets.Tab;
import net.unethicalite.api.widgets.Tabs;
import net.unethicalite.api.widgets.Widgets;
import org.pf4j.Extension;

import javax.inject.Inject;

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
    private TimerUtil timerUtil;
    private boolean timerRunning;
    private final WorldPoint walkLocation = new WorldPoint(1760, 3509, 0);

    @Override
    protected int loop()
    {
        Player local = Players.getLocal();

        if (!Combat.isRetaliating())
        {
            log.debug("Setting up Retaliate");
            Tabs.open(Tab.COMBAT);
            Widget retaliateWidget = Widgets.get(593, 30);
            retaliateWidget.interact(0);
            return -1;
        }

        if (config.highAlch())
        {
            Item alchItem = Inventory.getFirst(config.alchItem());

            log.debug("Going for alch");
            if (Inventory.contains("Nature rune", "Fire rune") && Inventory.contains(alchItem.getName()))
            {
                Magic.cast(SpellBook.Standard.HIGH_LEVEL_ALCHEMY, alchItem);
                return -4;
            }
        }

        if (Movement.isWalking())
        {
            return -1;
        }

        if (local.getWorldLocation().distanceTo(config.location().getLocationPoint()) < 1 && !timerRunning)
        {
            log.debug("Setting timer");
            timerUtil.setSleepTime(10);
            timerRunning = true;
            return -1;
        }

        if (timerUtil.toMinutes((int)timerUtil.getElapsedTime()) >= 10 && timerRunning)
        {
            if (walkLocation.distanceTo(local.getWorldLocation()) > 4)
            {
                log.debug("Walking to get agro again.");
                Movement.walkTo(walkLocation);
                return -2;
            }
            timerRunning = false;
            return -2;
        }

        if (!timerRunning && local.getWorldLocation().distanceTo(config.location().getLocationPoint()) == 0)
        {
            log.debug("Moving to: " + config.location().getLocationName());
            Movement.walkTo(config.location().getLocationPoint());
            return -2;
        }

        if (config.getAmmo())
        {
            TileItem ammo = TileItems.getNearest(x -> x.distanceTo(local.getWorldLocation()) < 2 && x.getName().contains(config.ammoName()));
            if (ammo != null)
            {
                log.debug("Picking up ammo");
                ammo.pickup();
                return -2;
            }

            Item ammoEquip = Inventory.getFirst(config.ammoName());
            if (ammoEquip != null)
            {
                log.debug("Equipping ammo");
                ammoEquip.interact("Wield");
                return -2;
            }
        }
        return -2;
    }
}
