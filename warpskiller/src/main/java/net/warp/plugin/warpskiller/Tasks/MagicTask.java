package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.unethicalite.api.game.Prices;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.GrandExchange;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.Magic;
import net.unethicalite.api.plugins.Task;
import net.warp.plugin.warpskiller.Items.SkillTask;
import net.warp.plugin.warpskiller.PluginStatus;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;

@Slf4j
public class MagicTask implements Task {

    public MagicTask(WarpSkillerPlugin plugin)
    {
        this.plugin = plugin;
    }
    private final WarpSkillerPlugin plugin;
    @Override
    public boolean validate()
    {
            return plugin.config.skillTask() == SkillTask.MAGIC;
    }
    private boolean needPrice = true;
    private int alchMoney;
    private int buyPrice;
    @Override
    public int execute() {
        log.debug("MagicTask started");

        if (needItems())
        {
            log.debug("We need items");
            plugin.banking = true;
            return -1;
        }

        if (!staffEquipped() && Inventory.contains(12000, 1387))
        {
            Inventory.getFirst(12000, 1387).interact("Wield");
            return -1;
        }

        switch(plugin.config.spellType())
        {
            case PLANK_MAKE:
                if (Inventory.contains(995) && Inventory.contains("Mahogany logs"))
                {
                    plugin.status = PluginStatus.PLANK_MAKE;
                    Magic.cast(plugin.config.spellType().getSpellType(), Inventory.getFirst("Mahogany logs"));
                    return -3;
                }
                break;

            case LOW_ALCH:
            case HIGH_ALCH:
                plugin.status = PluginStatus.ALCH;
                if (Inventory.contains(plugin.config.alchItem()) && Inventory.contains("Nature rune"))
                {
                    if (needPrice)
                    {
                        Item natureRune = Inventory.getFirst("Nature rune");
                        Item alchItem = Inventory.getFirst(plugin.config.alchItem());
                        buyPrice += getPrice(natureRune.getId());
                        buyPrice += getPrice(alchItem.getId());
                        ItemComposition temp = alchItem.getComposition();
                        alchMoney = temp.getHaPrice();
                        needPrice = false;
                    }
                    Magic.cast(plugin.config.spellType().getSpellType(), Inventory.getFirst(plugin.config.alchItem()));
                    plugin.alchProfit += alchMoney - buyPrice;
                    return -4;
                }

                break;
            case SUPERHEAT:
                plugin.status = PluginStatus.SUPERHEAT;
                if (Inventory.contains(plugin.config.barType().getOres()) && Inventory.contains("Nature rune"))
                {
                    Magic.cast(plugin.config.spellType().getSpellType(), Inventory.getFirst(plugin.config.barType().getOres()));
                    return -3;
                }
                break;
        }
        return -1;
    }
    private boolean staffEquipped() { return Equipment.contains(12000, 1387); }
    private boolean needItems ()
    {
        switch (plugin.config.spellType())
        {
            case PLANK_MAKE:
                return !Inventory.contains(995) || !Inventory.contains("Mahogany logs");
            case HIGH_ALCH:
            case LOW_ALCH:
                return !Inventory.contains("Nature rune") || !Inventory.contains(plugin.config.alchItem());
            case SUPERHEAT:
                return !Inventory.contains("Nature rune") || !Inventory.contains(plugin.config.barType().getOres());
            default:
                return true;
        }
    }

    private int getPrice(int itemID)
    {
        return Prices.getItemPrice(itemID);
    }
}
