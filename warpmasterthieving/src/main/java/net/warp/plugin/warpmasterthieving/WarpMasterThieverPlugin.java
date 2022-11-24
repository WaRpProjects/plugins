package net.warp.plugin.warpmasterthieving;

import com.google.inject.Inject;
import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;

import net.runelite.api.Player;
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
                .getSurrounding(Players.getLocal().getWorldLocation(), 23, bankBooth)
                .stream()
                .min(Comparator.comparing(x -> x.distanceTo(Players.getLocal().getWorldLocation())))
                .orElse(null);

        var farmer = NPCs.getNearest(5730);

        String[] seeds = config.seedToDrop().split(",");

        Player local = Players.getLocal();

        if (Combat.getHealthPercent() <= config.healthPercent() && Inventory.contains(config.foodName()))
        {
            log.debug("Eating: " + config.foodName());
            Inventory.getFirst(config.foodName()).interact("Eat");
            return -2;
        }

        if (Inventory.contains(seeds))
        {
            log.debug("Dropping seeds");
            Inventory.getFirst(seeds).interact("Drop");
            return -1;
        }

        if (Movement.isWalking() || local.getGraphic() == 245)
        {
            return -2;
        }

        if (Bank.isOpen())
        {
            if (Inventory.contains(x -> x.getName().contains("seed")))
            {
                log.debug("Deposit seeds");
                Bank.depositInventory();
                return -1;
            }

            if (Inventory.getCount(config.foodName()) < config.foodAmount())
            {
                log.debug("Withdraw food");
                Bank.withdraw(config.foodName(), config.foodAmount() - Inventory.getCount(config.foodName()), Bank.WithdrawMode.ITEM);
                return -2;
            }

            if (Inventory.getCount(config.foodName()) >= config.foodAmount())
            {
                log.debug("Closing bank");
                Bank.close();
                return -2;
            }
        }

        if (!Inventory.contains(config.foodName()) || Inventory.isFull()) {

            if (!bankArea.contains(local.getWorldLocation()))
            {
                log.debug("Walking to bank");
                Movement.walkTo(bankArea.getRandom());
                return -1;
            }

            if (!Bank.isOpen()) {
                log.debug("Click bank");
                bank.interact("Bank");
                return -2;
            }
        }

        if (farmer != null)
        {
            farmer.interact("Pickpocket");
            return Rand.nextInt(344, 544);
        }

        if (!farmerArea.contains(local.getWorldLocation()))
        {
            log.debug("Walking to farmer");
            Movement.walkTo(farmerArea.getRandom());
            return -2;
        }
        return -3;
    }
}
