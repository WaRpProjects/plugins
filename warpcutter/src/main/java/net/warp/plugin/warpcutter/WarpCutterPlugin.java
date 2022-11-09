package net.warp.plugin.warpcutter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.LoopedPlugin;

import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.Comparator;


@PluginDescriptor(
        name = "WaRp Woodcutter",
        description = "Chops trees in the woodcutting guild",
        enabledByDefault = false
)

@Slf4j
@Extension
public class WarpCutterPlugin extends LoopedPlugin
{


    @Provides
    WarpCutterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WarpCutterConfig.class);
    }

    @Inject
    private WarpCutterConfig config;

    private final String[] logNames = {"Yew logs", "Magic logs"};
    private final String[] axeNames = {"Bronze axe", "Iron axe", "Steel axe", "Mithril axe", "Adamant axe", "Rune axe", "Dragon axe"};



    @Override
    protected int loop()
    {

        var tree = TileObjects
                .getSurrounding(Players.getLocal().getWorldLocation(), 15, config.boom().getNames())
                .stream()
                .min(Comparator.comparing(x -> x.distanceTo(Players.getLocal().getWorldLocation())))
                .orElse(null);

        var bank = TileObjects
                .getSurrounding(Players.getLocal().getWorldLocation(), 23, 28861)
                .stream()
                .min(Comparator.comparing(x -> x.distanceTo(Players.getLocal().getWorldLocation())))
                .orElse(null);


        if (Equipment.fromSlot(EquipmentInventorySlot.WEAPON).getName() == "Dragon axe" && Combat.getSpecEnergy() == 100)
        {
            log.debug("Can do Special.");
            Combat.toggleSpec();
        }


        if (Players.getLocal().isAnimating() || Players.getLocal().isMoving())
        {

            return Rand.nextInt(794, 1738);
        }

        if (Inventory.isFull() && !Bank.isOpen() && bank != null)
        {
            bank.interact("Use");
            return Rand.nextInt(794, 1738);
        }

        if (Inventory.contains(logNames) && Bank.isOpen())
        {
            Bank.depositAllExcept(axeNames);
            return Rand.nextInt(794, 1738);
        }

        if (!Inventory.isFull() && tree != null)
        {
            tree.interact("Chop down");
            return Rand.nextInt(1794, 4738);
        }


        return Rand.nextInt(1794, 3738);
    }
}
