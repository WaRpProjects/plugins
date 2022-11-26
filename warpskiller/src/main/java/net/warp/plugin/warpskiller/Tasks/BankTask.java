package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.Task;
import net.warp.plugin.warpskiller.Items.Logs;
import net.warp.plugin.warpskiller.Items.SkillTask;
import net.warp.plugin.warpskiller.Items.Spells;
import net.warp.plugin.warpskiller.PluginStatus;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;
import java.util.Set;

@Slf4j
public class BankTask implements Task {

    public BankTask(WarpSkillerPlugin plugin) { this.plugin = plugin; }
    private final WarpSkillerPlugin plugin;

    private final String natureRune = "Nature rune";
    private final String fireRune = "Fire rune";
    private final Set<String> bankObjects = Set.of("Bank booth", "Grand Exchange booth", "Bank chest") ;
    private final String[] bankText = {"Bank", "Use"};
    private final int[] fireStaffID = {12000, 1387};

    private boolean needFireRune;

    @Override
    public boolean validate()
    {
        return plugin.banking;
    }

    @Override
    public int execute()
    {
        plugin.status = PluginStatus.BANK;
        var bankObject = TileObjects
                .getNearest(x -> x.hasAction(bankText) &&
                        bankObjects.contains(x.getName()));

        plugin.status = PluginStatus.BANK;
        if(!Bank.isOpen() && bankObject != null)
        {
            log.debug("Opening bank");
            bankObject.interact(bankText);
            Time.sleepUntil(Bank::isOpen, -2);
            return -1;
        }
        if (Bank.isOpen())
        {
            if (plugin.firstRun && plugin.config.skillTask() != SkillTask.MAGIC)
            {
                Bank.depositInventory();
                Time.sleepUntil(Inventory::isEmpty, -2);
                plugin.firstRun = false;
                return -1;
            }

            switch(plugin.config.skillTask())
            {
                case HERBLORE:
                case CRAFT:
                case FLETCH:
                    log.debug("Banking for: " + plugin.config.skillTask());
                    Item bankItem;

                    for (Item item : Inventory.getAll())
                    {
                        if (item.getName().equals(plugin.item1) || item.getName().equals(plugin.item2))
                        {
                            continue;
                        }
                        log.debug("Removing item: " + item.getName());
                        Bank.depositAll(item.getName());
                        Time.sleepUntil(() -> !Inventory.contains(item.getName()), -2);
                        return -2;
                    }

                    if (!plugin.item1.contains("Null"))
                    {
                        if (!Inventory.contains(plugin.item1))
                        {
                            bankItem = Bank.getFirst(x -> x.getName().contains(plugin.item1));

                            if (bankItem != null)
                            {
                                log.debug("Getting " + bankItem.getName() + " from bank");
                                Bank.withdraw(bankItem.getName(), plugin.item1Amount, Bank.WithdrawMode.ITEM);
                                Time.sleepUntil(() -> Inventory.contains(plugin.item1), -2);
                                return -2;
                            }
                            log.debug("Couldn't find item1 in bank");
                        }
                    }

                    if (!plugin.item2.contains("Null"))
                    {
                        if (!Inventory.contains(plugin.item2))
                        {
                            bankItem = Bank.getFirst(x -> x.getName().contains(plugin.item2));

                            if (bankItem != null)
                            {
                                log.debug("Getting " + bankItem.getName() + " from bank");
                                Bank.withdraw(bankItem.getName(), plugin.item2Amount, Bank.WithdrawMode.ITEM);
                                Time.sleepUntil(() -> Inventory.contains(plugin.item2), -2);
                                return -2;
                            }
                            log.debug("Couldn't find item2 in bank");
                        }
                    }
                    break;

                case MAGIC:
                    switch (plugin.config.spellType())
                    {
                        case PLANK_MAKE:
                            String logName = "Mahogany logs";
                            String plankName = "Mahogany plank";
                            String coins = "Coins";

                            if (Inventory.contains(plankName))
                            {
                                log.debug("Deposit: " + plankName);
                                Bank.depositAll(plankName);
                                Time.sleepUntil(() -> !Inventory.contains(plankName), -3);
                            }

                            if (!Inventory.contains(coins))
                            {
                                log.debug("Withdraw coins");
                                Bank.withdrawAll(coins, Bank.WithdrawMode.ITEM);
                                Time.sleepUntil(() -> Inventory.contains(coins), -3);
                                return -1;
                            }

                            if (!Inventory.contains(logName))
                            {
                                log.debug("Withdraw Logs");
                                Bank.withdrawAll(logName, Bank.WithdrawMode.ITEM);
                                Time.sleepUntil(() -> Inventory.contains(logName), -3);
                                return -1;
                            }
                            break;


                        case HIGH_ALCH:
                        case LOW_ALCH:

                            String alchItem = plugin.config.alchItem();
                            String ore = plugin.config.barType().getOres();

                            if (plugin.staff == null) { plugin.staff = Bank.getFirst(fireStaffID); }

                            if (plugin.staff != null && Bank.contains(fireStaffID) && !Inventory.contains(fireStaffID) && !itemEquipped(fireStaffID))
                            {
                                log.debug("Withdrawing: " + plugin.staff.getName());
                                Bank.depositAllExcept(alchItem, ore, plugin.staff.getName());
                                Time.sleep(150);
                                Bank.withdraw(plugin.staff.getId(), 1, Bank.WithdrawMode.ITEM);
                                needFireRune = false;
                                return -2;
                            }

                            if (Bank.contains(natureRune) && !Inventory.contains(natureRune))
                            {
                                log.debug("Withdrawing: " + natureRune);
                                Bank.depositAllExcept(plugin.staff.getName());
                                Time.sleep(150);
                                Bank.withdraw(natureRune, Rand.nextInt(20000, 434203), Bank.WithdrawMode.ITEM);
                                return -1;
                            }

                            if (needFireRune)
                            {
                                log.debug("Withdrawing: " + fireRune);
                                Bank.depositAllExcept(plugin.staff.getName(), natureRune);
                                Bank.withdraw(fireRune, Rand.nextInt(20000, 6748833), Bank.WithdrawMode.ITEM);
                                return -1;
                            }

                            if (!Inventory.contains(alchItem))
                            {
                                log.debug("Withdrawing: " + alchItem);
                                Bank.depositAllExcept(plugin.staff.getName(), natureRune, fireRune);
                                Time.sleep(150);
                                Bank.withdrawAll(alchItem, Bank.WithdrawMode.NOTED);
                                return -1;
                            }
                            break;
                        case SUPERHEAT:
                            String ore1 = plugin.config.barType().getOres();
                            if (!Inventory.contains(ore1))
                            {
                                log.debug("Withdrawing: " + ore1);
                                Bank.depositAllExcept(plugin.staff.getName(), natureRune, fireRune);
                                Time.sleep(150);
                                Bank.withdraw(ore1, 27, Bank.WithdrawMode.ITEM);
                                return -1;
                            }
                            break;
                    }
                    break;
            }
            log.debug("Closing bank");
            Bank.close();
            Time.sleepUntil(() -> !Bank.isOpen(), -2);
            plugin.banking = false;
        }
        log.debug("Banking is: " + plugin.banking);
        return -1;
    }

    private boolean itemEquipped(int... ID)
    {
        return Equipment.contains(ID);
    }
}
