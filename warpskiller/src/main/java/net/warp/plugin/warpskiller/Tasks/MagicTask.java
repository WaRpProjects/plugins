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
            return Rand.nextInt(484, 858);
        }

        if (!staffEquiped() && Inventory.contains(12000, 1387))
        {
            Inventory.getFirst(12000, 1387).interact("Wield");
            return Rand.nextInt(796, 923);
        }

        switch(plugin.config.spellType())
        {
            case LOW_ALCH:
            case HIGH_ALCH:

                if (Inventory.contains(plugin.config.alchItem()) && Inventory.contains("Nature rune"))
                {
                    Magic.cast(plugin.config.spellType().getSpellType(), Inventory.getFirst(plugin.config.alchItem()));
                    return Rand.nextInt(1586, 2234);
                }
                break;
            case SUPERHEAT:

                if (Inventory.contains(plugin.config.barType().getOres()) && Inventory.contains("Nature rune"))
                {
                    Magic.cast(plugin.config.spellType().getSpellType(), Inventory.getFirst(plugin.config.barType().getOres()));
                    return Rand.nextInt(923, 1243);
                }
                break;
        }
        return Rand.nextInt(1200, 1500);
    }

    private boolean staffEquiped() { return Equipment.contains(12000, 1387); }
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
