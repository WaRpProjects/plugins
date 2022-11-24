package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.Task;
import net.warp.plugin.warpskiller.Items.Herblore;
import net.warp.plugin.warpskiller.Items.Logs;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j
public class BankTask implements Task {

    public BankTask(WarpSkillerPlugin plugin) { this.plugin = plugin; }
    private final WarpSkillerPlugin plugin;

    private final String knife = "Knife";
    private final String chisel = "Chisel";
    private final String natureRune = "Nature rune";
    private final String fireRune = "Fire rune";
    private Set<String> bankObjects = Set.of("Bank booth", "Grand Exchange booth", "Bank chest") ;
    private String[] bankText = {"Bank", "Use"};
    private final int[] staffID = {12000, 1387};

    private boolean needFireRune;

    @Override
    public boolean validate()
    {
        return plugin.banking;
    }

    @Override
    public int execute() {

        var bankObject = TileObjects
                .getNearest(x -> x.hasAction(bankText) &&
                        bankObjects.contains(x.getName()));

        if(!Bank.isOpen() && bankObject != null)
        {
            log.debug("Opening bank");
            bankObject.interact(bankText);
            return Rand.nextInt(523, 892);
        }
        if (Bank.isOpen())
        {
            if (plugin.firstRun)
            {
                Bank.depositInventory();
                plugin.firstRun = false;
                return 300;
            }

            switch(plugin.config.skillTask())
            {
                case HERBLORE:
                    switch (plugin.config.herbloreType())
                    {
                        case CLEAN:
                            Item grimy = Bank.getFirst(x -> x.getName().contains("Grimy"));
                            log.debug("Deposit inventory");

                            if (grimy != null && !Inventory.contains(grimy.getName()))
                            {
                                Bank.depositInventory();
                                log.debug("Withdrawing Grimy");
                                Bank.withdraw(grimy.getName(), 300, Bank.WithdrawMode.ITEM);
                                return -4;
                            }
                            break;
                        case POTION:
                            Bank.depositAllExcept(plugin.config.potionItem1(), plugin.config.potionItem2());
                            if (!Inventory.contains(plugin.config.potionItem1()) && Bank.contains(plugin.config.potionItem1()))
                            {
                                Bank.withdraw(plugin.config.potionItem1(), 14, Bank.WithdrawMode.ITEM);
                                return -2;
                            }
                            if (!Inventory.contains(plugin.config.potionItem2()) && Bank.contains(plugin.config.potionItem2()))
                            {
                                Bank.withdraw(plugin.config.potionItem2(), 14, Bank.WithdrawMode.ITEM);
                                return -2;
                            }
                            break;
                    }
                    break;

                case CRAFT:
                    String gemName = "Uncut " + plugin.config.gemType().getGemName();
                    Bank.depositAllExcept(chisel, gemName);
                    if (!Inventory.contains(chisel) && Bank.contains(chisel))
                    {
                        log.debug("Withdrawing chisel");
                        Bank.withdraw(chisel, 1 ,Bank.WithdrawMode.ITEM);
                        return Rand.nextInt(384, 766);
                    }

                    if (!Inventory.contains(gemName))
                    {
                        log.debug("Withdrawing: " + gemName);
                        Bank.withdraw(gemName, 27, Bank.WithdrawMode.ITEM);
                        return Rand.nextInt(463, 844);
                    }
                    break;
                case MAGIC:

                    String alchItem = plugin.config.alchItem();
                    String ore = plugin.config.barType().getOres();

                    if (plugin.staff == null) { plugin.staff = Bank.getFirst(staffID); }

                    if (plugin.staff != null && Bank.contains(staffID) && !Inventory.contains(staffID) && !staffEquiped())
                    {

                        log.debug("Withdrawing: " + plugin.staff.getName());
                        Bank.depositAllExcept(alchItem, ore, plugin.staff.getName());
                        Bank.withdraw(plugin.staff.getId(), 1, Bank.WithdrawMode.ITEM);
                        needFireRune = false;
                        return Rand.nextInt(423, 892);
                    }

                    if (Bank.contains(natureRune) && !Inventory.contains(natureRune))
                    {
                        log.debug("Withdrawing: " + natureRune);
                        Bank.depositAllExcept(plugin.staff.getName());
                        Bank.withdraw(natureRune, Rand.nextInt(20000, 434203), Bank.WithdrawMode.ITEM);
                        return Rand.nextInt(423, 892);
                    }

                    if (needFireRune)
                    {
                        log.debug("Withdrawing: " + fireRune);
                        Bank.depositAllExcept(plugin.staff.getName(), natureRune);
                        Bank.withdraw(fireRune, Rand.nextInt(20000, 6748833), Bank.WithdrawMode.ITEM);
                        return Rand.nextInt(423, 892);
                    }

                    switch (plugin.config.spellType())
                    {
                        case HIGH_ALCH:
                        case LOW_ALCH:
                            if (!Inventory.contains(alchItem))
                            {
                                log.debug("Withdrawing: " + alchItem);
                                Bank.depositAllExcept(plugin.staff.getName(), natureRune, fireRune);
                                Bank.withdraw(alchItem, Rand.nextInt(20000, 6748833), Bank.WithdrawMode.NOTED);
                                return Rand.nextInt(423, 892);
                            }
                            break;
                        case SUPERHEAT:
                            if (!Inventory.contains(ore))
                            {
                                log.debug("Withdrawing: " + ore);
                                Bank.depositAllExcept(plugin.staff.getName(), natureRune, fireRune);
                                Bank.withdraw(ore, 27, Bank.WithdrawMode.ITEM);
                                return Rand.nextInt(723, 892);
                            }
                            break;
                    }
                    break;
                case FLETCH:
                    String logName = getLogName();
                    Bank.depositAllExcept(knife, logName);
                    if (!Inventory.contains(knife))
                    {
                        log.debug("Withdrawing: " + knife);
                        Bank.withdraw(knife, 1, Bank.WithdrawMode.ITEM);
                        return Rand.nextInt(723, 892);
                    }

                    if (!Inventory.contains(logName))
                    {
                        log.debug("Withdrawing: " + logName);
                        Bank.withdraw(logName, 27, Bank.WithdrawMode.ITEM);
                        return Rand.nextInt(723, 892);
                    }
                    break;
            }

            log.debug("Closing bank");
            Bank.close();
            plugin.banking = false;
        }
        log.debug("Banking is: " + plugin.banking);
        return Rand.nextInt(485, 999);
    }

    private boolean staffEquiped()
    {
        return Equipment.contains(staffID);
    }
    private String getLogName()
    {
        if (plugin.config.log() == Logs.LOGS) return Logs.LOGS.getLogName();
        return plugin.config.log().getLogName() + "logs";
    }

    private void stopScript()
    {
       //Swin
    }
}
