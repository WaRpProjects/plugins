package net.warp.plugin.warpskiller.Tasks;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.client.config.ConfigManager;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.Magic;
import net.unethicalite.api.plugins.Task;
import net.warp.plugin.warpskiller.SkillTask;
import net.warp.plugin.warpskiller.WarpSkillerConfig;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;

@Slf4j
public class AlchTask implements Task {

    public AlchTask(WarpSkillerPlugin warpSkillerPlugin)
    {
        plugin = warpSkillerPlugin;
    }

    private int[] staffID = {12000, 1387};


    private WarpSkillerPlugin plugin;

    @Provides
    WarpSkillerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WarpSkillerConfig.class);
    }

    @Override
    public boolean validate()
    {
            return plugin.config.skillTask() == SkillTask.MAGIC;
    }

    private boolean needFireRune = true;

    @Override
    public int execute() {
        log.debug("Alching it is..");

        var bankNPC = NPCs.getNearest(plugin.bankNPCID);


        switch(plugin.config.spellType())
        {
            case LOW_ALCH:
            case HIGH_ALCH:

                if (!Inventory.contains(plugin.config.alchItem()) || !Inventory.contains(plugin.config.spellType().getRuneID()) || !hasStaff())
                {
                    log.debug("No items starting Bank");
                    if (!Bank.isOpen() && bankNPC != null)
                    {
                        bankNPC.interact("Bank");
                        log.debug("Bank opened");
                        return Rand.nextInt(923, 1892);
                    }

                    if (Bank.isOpen() && Bank.contains(staffID) && !Inventory.contains(staffID) && !staffEquiped())
                    {
                        log.debug("Getting Staff");
                        Item staff = Bank.getFirst(staffID);
                        Bank.withdraw(staff.getId(), 1, Bank.WithdrawMode.ITEM);
                        needFireRune = false;
                        return Rand.nextInt(923, 1892);
                    }

                    if (Bank.isOpen() && Bank.contains("Nature rune") && !Inventory.contains("Nature rune"))
                    {
                        log.debug("Getting Nature rune");
                        Bank.withdraw("Nature rune", Rand.nextInt(20000, 434203), Bank.WithdrawMode.ITEM);
                        return Rand.nextInt(923, 1892);
                    }

                    if (Bank.isOpen() && needFireRune)
                    {
                        log.debug("Getting Fire rune");
                        Bank.withdraw("Fire rune", Rand.nextInt(20000, 434203), Bank.WithdrawMode.ITEM);
                        return Rand.nextInt(923, 1892);
                    }

                    if (Bank.isOpen() && !Inventory.contains(plugin.config.alchItem()))
                    {
                        log.debug("Getting alch item: " + plugin.config.alchItem());
                        Item test = Bank.getFirst(plugin.config.alchItem());
                        Bank.withdraw(test.getId(), Rand.nextInt(20000, 434203), Bank.WithdrawMode.NOTED);
                        return Rand.nextInt(923, 1892);
                    }
                }

                if (Bank.isOpen() && Inventory.contains(plugin.config.alchItem()))
                {
                    log.debug("Closing bank");
                    Bank.close();
                    return Rand.nextInt(796, 923);
                }

                if (!needFireRune && !staffEquiped() && Inventory.contains(staffID))
                {
                    Inventory.getFirst(staffID).interact("Wield");
                    return Rand.nextInt(796, 923);
                }

                if (Inventory.contains(plugin.config.alchItem()) && Inventory.contains("Nature rune"))
                {
                    Magic.cast(plugin.config.spellType().getSpellType(), Inventory.getFirst(plugin.config.alchItem()));
                    return Rand.nextInt(923, 1243);
                }

                break;
            case SUPERHEAT:

                if (!Inventory.contains(plugin.config.barType().getOres()) || !Inventory.contains(plugin.config.spellType().getRuneID()) || !hasStaff())
                {
                    log.debug("No items starting Bank");
                    if (!Bank.isOpen() && bankNPC != null)
                    {
                        bankNPC.interact("Bank");
                        log.debug("Bank opened");
                        return Rand.nextInt(923, 1892);
                    }

                    if (Bank.isOpen() && Bank.contains(staffID) && !Inventory.contains(staffID) && !staffEquiped())
                    {

                        log.debug("Getting Staff");
                        Bank.depositAllExcept(staffID);
                        Item staff = Bank.getFirst(staffID);
                        Bank.withdraw(staff.getId(), 1, Bank.WithdrawMode.ITEM);
                        needFireRune = false;
                        return Rand.nextInt(923, 1892);
                    }

                    if (Bank.isOpen() && Bank.contains("Nature rune") && !Inventory.contains("Nature rune"))
                    {

                        log.debug("Getting Nature rune");
                        Bank.depositAllExcept(staffID);
                        Bank.withdraw("Nature rune", Rand.nextInt(20000, 434203), Bank.WithdrawMode.ITEM);
                        return Rand.nextInt(923, 1892);
                    }

                    if (Bank.isOpen() && needFireRune)
                    {
                        log.debug("Getting Fire rune");
                        Bank.withdraw("Fire rune", Rand.nextInt(20000, 434203), Bank.WithdrawMode.ITEM);
                        return Rand.nextInt(923, 1892);
                    }

                    if (Bank.isOpen() && !Inventory.contains(plugin.config.barType().getOres()))
                    {
                        log.debug("Getting ores: " + plugin.config.alchItem());
                        Bank.depositAllExcept(554, 561, 1387, 12000);
                        Item test = Bank.getFirst(plugin.config.barType().getOres());
                        Bank.withdraw(test.getId(), 27, Bank.WithdrawMode.ITEM);
                        return Rand.nextInt(923, 1892);
                    }
                }

                if (Bank.isOpen() && Inventory.contains(plugin.config.barType().getOres()))
                {
                    log.debug("Closing bank");
                    Bank.close();
                    return Rand.nextInt(796, 923);
                }

                if (!needFireRune && !staffEquiped() && Inventory.contains(staffID))
                {
                    Inventory.getFirst(staffID).interact("Wield");
                    return Rand.nextInt(796, 923);
                }

                if (Inventory.contains(plugin.config.barType().getOres()) && Inventory.contains("Nature rune"))
                {
                    Magic.cast(plugin.config.spellType().getSpellType(), Inventory.getFirst(plugin.config.barType().getOres()));
                    return Rand.nextInt(923, 1243);
                }

                break;
        }
        return Rand.nextInt(1200, 1500);
    }

    private boolean staffEquiped()
    {
        return Equipment.contains(staffID);
    }

    private boolean hasStaff () { return Inventory.contains(staffID) || Equipment.contains(staffID); }

}
