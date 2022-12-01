package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.widgets.Production;
import net.unethicalite.api.widgets.Widgets;
import net.warp.plugin.warpskiller.Items.Bows;
import net.warp.plugin.warpskiller.Items.Logs;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;

@Slf4j
public class MakeTask implements Task {

    public MakeTask(WarpSkillerPlugin plugin)
    {
        this.plugin = plugin;
    }

    private WarpSkillerPlugin plugin;
    @Override
    public boolean validate()
    {
        return plugin.makeTask;
    }

    @Override
    public int execute() {

        if (!Inventory.contains(plugin.item1) || !Inventory.contains(plugin.item2))
        {
            plugin.banking = true;
            return -1;
        }

        Item item1 = Inventory.getFirst(plugin.item1);
        Item item2 = Inventory.getFirst(plugin.item2);

        if (item1 != null && item2 != null)
        {
            if (!Production.isOpen())
            {
                log.debug("Using " + item1.getName() + " on " + item2.getName());
                item1.useOn(item2);
                Time.sleepUntil(Production::isOpen, -2);
                return -1;
            }

            if (Production.isOpen() && getWidget() != null)
            {
                log.debug("Production menu is open");
                getWidget().interact(0);
                Time.sleepUntil(() -> !Inventory.contains(item2.getName()), 60000);
                return -1;
            }
        }

        log.debug("End of execute?");
        return -1;
    }

    private Widget getWidget()
    {
        switch (plugin.config.skillTask())
        {
            case FLETCH:
                if (plugin.config.log() == Logs.LOGS)
                {
                    return Widgets.get(270, plugin.config.bow() == Bows.SHORTBOW ? 16 : 17);
                }
                return Widgets.get(270, plugin.config.bow() == Bows.SHORTBOW ? 15 : 16);

            case CRAFT:
                switch (plugin.config.craftTask())
                {
                    case GLASSBLOW:
                        return Widgets.get(270, plugin.config.glassBlow().getWidgetChild());
                    case GEMCUTTING:
                        return Widgets.get(270, 14);
                    case AMETHYST:
                        return Widgets.get(270, plugin.config.amethystType().getWidgetChild());
                }
                break;

            case HERBLORE:
                return Widgets.get(270,14);
        }
        return null;
    }
}
