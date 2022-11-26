package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.input.Keyboard;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;
import net.warp.plugin.warpskiller.Items.Crafting;
import net.warp.plugin.warpskiller.Items.SkillTask;
import net.unethicalite.api.plugins.Task;
import net.warp.plugin.warpskiller.PluginStatus;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;


@Slf4j
public class GemTask implements Task {

    public GemTask(WarpSkillerPlugin plugin)
    {
        this.plugin = plugin;
    }
    private final WarpSkillerPlugin plugin;
    @Override
    public boolean validate()
    {
        return plugin.config.skillTask() == SkillTask.CRAFT && plugin.config.craftTask() == Crafting.GEMCUTTING;
    }

    @Override
    public int execute()
    {
        plugin.status = PluginStatus.GEMCUTTING;

        String gemName = "Uncut " + plugin.config.gemType().getGemName();
        String chiselName = "Chisel";

        Widget makeMenu = Widgets.get(270, 0);

        plugin.item1 = chiselName;
        plugin.item1Amount = 1;
        plugin.item2 = gemName;
        plugin.item2Amount = 27;

        log.debug("GemTask started");
        if (!Inventory.contains(gemName) || !Inventory.contains(chiselName) )
        {
            plugin.banking = true;
            return -1;
        }


        Item uncutGem = Inventory.getFirst(gemName);
        Item chisel = Inventory.getFirst(chiselName);

        if (uncutGem != null && chisel != null)
        {
            if (makeMenu == null)
            {
                log.debug("Using Chisel on Gem");
                chisel.useOn(uncutGem);
                return -2;
            }

            if (makeMenu.isVisible())
            {
                Keyboard.sendSpace();
                Time.sleepUntil(() -> !Inventory.contains(uncutGem.getName()), 60000);
                return -1;
            }
        }
        return -2;
    }
}
