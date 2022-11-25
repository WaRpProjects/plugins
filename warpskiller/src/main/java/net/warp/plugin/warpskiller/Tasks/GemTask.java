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
import net.warp.plugin.warpskiller.Items.SkillTask;
import net.unethicalite.api.plugins.Task;
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
        return plugin.config.skillTask() == SkillTask.CRAFT;
    }

    @Override
    public int execute() {

        String gemType = "Uncut " + plugin.config.gemType().getGemName();
        Widget makeMenu = Widgets.get(270, 0);

        log.debug("GemTask started");
        if (!Inventory.contains(gemType) || !Inventory.contains("Chisel") )
        {
            plugin.banking = true;
            return -1;
        }

        Item uncutGem = Inventory.getFirst(gemType);
        Item chisel = Inventory.getFirst("Chisel");

        if (uncutGem != null && chisel != null && !Players.getLocal().isAnimating())
        {
            if (makeMenu == null)
            {
                log.debug("Using Chisel on Gem");
                chisel.useOn(uncutGem);
                Time.sleepUntil(() -> !Inventory.contains(uncutGem.getName()), 2000);
                return -1;
            }

            if (makeMenu != null)
            {
                Keyboard.sendSpace();
                return -1;
            }
        }
            return -2;
    }
}
