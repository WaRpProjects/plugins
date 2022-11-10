package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.input.Keyboard;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;
import net.warp.plugin.warpskiller.SkillTask;
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
            return Rand.nextInt(484, 858);
        }

        Item uncutGem = Inventory.getFirst(gemType);
        Item chisel = Inventory.getFirst("Chisel");

        if (uncutGem != null && chisel != null && !Bank.isOpen() && !Players.getLocal().isAnimating())
        {
            if (makeMenu == null)
            {
                log.debug("Using Chisel on Gem");
                chisel.useOn(uncutGem);
                return Rand.nextInt(923, 1723);
            }

            if (makeMenu != null)
            {
                Keyboard.sendSpace();
                return Rand.nextInt(33043, 35934);
            }
        }
            return Rand.nextInt(1200, 1500);
    }
}
