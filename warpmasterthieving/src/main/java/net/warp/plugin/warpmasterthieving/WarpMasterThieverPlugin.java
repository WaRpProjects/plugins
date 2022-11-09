package net.warp.plugin.warpmasterthieving;

import com.google.inject.Inject;
import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.plugins.LoopedPlugin;
import org.pf4j.Extension;

import java.util.Comparator;

@PluginDescriptor(
        name = "WaRp Masterfarmer thieving",
        description = "Steals from the poor Masterfarmers",
        enabledByDefault = false
)

@Slf4j
@Extension
public class WarpMasterThieverPlugin extends LoopedPlugin {

    @Provides
    WarpMasterThieverConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WarpMasterThieverConfig.class);
    }

    @Inject
    WarpMasterThieverConfig config;

    private final int bankBooth = 10355;

    private final WorldArea farmerArea = new WorldArea(new WorldPoint(3078, 3252, 0), new WorldPoint(3083, 3248, 0));
    private final WorldArea bankArea = new WorldArea(new WorldPoint(3092, 3241, 0), new WorldPoint(3094, 3245, 0));

    @Override
    protected int loop() {

        var bank = TileObjects
                .getSurrounding(Players.getLocal().getWorldLocation(), 30, bankBooth)
                .stream()
                .min(Comparator.comparing(x -> x.distanceTo(Players.getLocal().getWorldLocation())))
                .orElse(null);

        var farmer = NPCs.getNearest(5730);

        String[] seeds = config.seedToDrop().split(",");

        if (Combat.getHealthPercent() <= config.healthPercent() && Inventory.contains(config.foodName()))
        {
            log.debug("Eating: " + config.foodName());
            Inventory.getFirst(config.foodName()).interact("Eat");
            return Rand.nextInt(685, 1293);
        }

        if (seeds != null)
        {
            if (Inventory.contains(seeds))
            {
                log.debug("Dropping shit");
                Inventory.getFirst(config.seedToDrop()).interact("Drop");
                return Rand.nextInt(394, 864);
            }
        }

        if (!Inventory.contains(config.foodName()) || Inventory.isFull())
        {
            log.debug("Banking start");
            if (bank == null && !Players.getLocal().isMoving())
            {
                log.debug("Walking to bank");
                Movement.walkTo(bankArea.getRandom());
                return Rand.nextInt(1293, 1892);
            }

            if (bank != null && !Bank.isOpen() && !Movement.isWalking())
            {
                log.debug("Click bank");
                bank.interact("Bank");
                return Rand.nextInt(1293, 1892);
            }

            if (Inventory.contains(x -> x.getName().contains("seed")))
            {
                Bank.depositAllExcept(config.foodName());
                return Rand.nextInt(233, 433);
            }

            if(Bank.isOpen() && Inventory.getCount(config.foodName()) < config.foodAmount())
            {
                log.debug("Withdrawing food");
                Bank.depositAllExcept(config.foodName());
                Bank.withdraw(config.foodName(), config.foodAmount() - Inventory.getCount(config.foodName()), Bank.WithdrawMode.ITEM);
                return Rand.nextInt(874, 1293);
            }
        }

        if (Bank.isOpen() && Inventory.getCount(config.foodName()) == config.foodAmount())
        {
            log.debug("Closing bank");
            Bank.close();
            return 343;
        }

        if (farmer == null && Inventory.contains(config.foodName()))
        {
            log.debug("Waiting for farmer...");
            Movement.walkTo(farmerArea.getRandom());
            return Rand.nextInt(923, 3843);
        }

        if (farmer != null && Inventory.contains(config.foodName()) && Players.getLocal().getGraphic() != 245)
        {
            farmer.interact("Pickpocket");
            return Rand.nextInt(622, 765);
        }

        return Rand.nextInt(789, 1345);
    }
}
