package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.widgets.Widgets;
import net.warp.plugin.warpskiller.Items.Crafting;
import net.warp.plugin.warpskiller.Items.SkillTask;
import net.warp.plugin.warpskiller.PluginStatus;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;

@Slf4j
public class BlowTask implements Task {

    public BlowTask(WarpSkillerPlugin plugin)
    {
        this.plugin = plugin;
    }

    private WarpSkillerPlugin plugin;

    @Override
    public boolean validate()
    {
        return plugin.config.skillTask() == SkillTask.CRAFT && plugin.config.craftTask() == Crafting.GLASSBLOW;
    }

    @Override
    public int execute() {
        plugin.status = PluginStatus.GLASSBLOW;

        String pipeName = "Glassblowing pipe";
        String glassName = "Molten glass";

        plugin.item1 = pipeName;
        plugin.item1Amount = 1;
        plugin.item2 = glassName;
        plugin.item2Amount = 27;

        if (!Inventory.contains(pipeName) || !Inventory.contains(glassName))
        {
            plugin.banking = true;
            return -2;
        }

        Item pipe = Inventory.getFirst(pipeName);
        Item glass = Inventory.getFirst(glassName);

        Widget makeMenu = Widgets.get(270,0);

        if (pipe != null && glass != null)
        {
            if (makeMenu == null)
            {
                log.debug("Using pipe on glass");
                pipe.useOn(glass);
                return -2;
            }

            if (makeMenu.isVisible())
            {
                Widget menu = Widgets.get(270, plugin.config.glassBlow().getWidgetChild());
                menu.interact(0);
                Time.sleepUntil(() -> !Inventory.contains(glass.getName()), 60000);
                return -1;
            }
        }
        return -2;
    }
}
