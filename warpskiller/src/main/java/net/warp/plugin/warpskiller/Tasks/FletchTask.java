package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.widgets.Widgets;
import net.warp.plugin.warpskiller.Bows;
import net.warp.plugin.warpskiller.Logs;
import net.warp.plugin.warpskiller.SkillTask;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;
@Slf4j
public class FletchTask implements Task {
    public FletchTask(WarpSkillerPlugin plugin) { this.plugin = plugin; }
    private final WarpSkillerPlugin plugin;
    @Override
    public boolean validate() {
        return plugin.config.skillTask() == SkillTask.FLETCH;
    }

    @Override
    public int execute()
    {
        String knifeName = "Knife";
        String logName = getLogName();

        if (!Inventory.contains(logName) || !Inventory.contains(knifeName))
        {
            plugin.banking = true;
            return Rand.nextInt(484, 858);
        }

        Item logs = Inventory.getFirst(logName);
        Item knife = Inventory.getFirst(knifeName);

        if (canFletch(logs, knife)) {
            Widget skillMenu = Widgets.get(270, 0);

            if (skillMenu == null) {
                log.debug("Using know on logs");
                knife.useOn(logs);
                return Rand.nextInt(597, 893);
            }

            if (skillMenu != null) {

                Widget shortbow = Widgets.get(270, 15);
                Widget longbow = Widgets.get(270, 16);

                if (plugin.config.bow() == Bows.SHORTBOW && shortbow != null) {
                    shortbow.interact(0);
                    return Rand.nextInt(897, 1293);
                }

                if (plugin.config.bow() == Bows.LONGBOW && longbow != null) {
                    longbow.interact(0);
                    return Rand.nextInt(897, 1293);
                }
            }
        }
        return Rand.nextInt(483, 928);
    }
    private String getLogName()
    {
        if (plugin.config.log() == Logs.LOGS) return Logs.LOGS.getLogName();
        return plugin.config.log().getLogName() + "logs";
    }
    private boolean canFletch(Item bow, Item knife)
    {
        return bow != null && knife != null && !Players.getLocal().isAnimating();
    }
}
