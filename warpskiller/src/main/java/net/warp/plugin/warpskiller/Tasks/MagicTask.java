package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.Magic;
import net.unethicalite.api.plugins.Task;
import net.warp.plugin.warpskiller.Items.SkillTask;
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
            case LOW_ALCH:
            case HIGH_ALCH:

                if (Inventory.contains(plugin.config.alchItem()) && Inventory.contains("Nature rune"))
                {
                    Magic.cast(plugin.config.spellType().getSpellType(), Inventory.getFirst(plugin.config.alchItem()));
                    return -4;
                }
                break;
            case SUPERHEAT:

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
            case HIGH_ALCH:
            case LOW_ALCH:
                return !Inventory.contains("Nature rune") || !Inventory.contains(plugin.config.alchItem());
            case SUPERHEAT:
                return !Inventory.contains("Nature rune") || !Inventory.contains(plugin.config.barType().getOres());
            default:
                return true;
        }
    }
}
