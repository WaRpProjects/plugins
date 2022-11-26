package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.input.Keyboard;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.widgets.Widgets;
import net.warp.plugin.warpskiller.Items.SkillTask;
import net.warp.plugin.warpskiller.PluginStatus;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;
@Slf4j
public class HerbloreTask implements Task {

    public HerbloreTask(WarpSkillerPlugin plugin)
    {
        this.plugin = plugin;
    }

    WarpSkillerPlugin plugin;
    @Override
    public boolean validate() {
        return plugin.config.skillTask() == SkillTask.HERBLORE;
    }

    @Override
    public int execute()
    {
        switch(plugin.config.herbloreType())
        {
            case POTION:
                plugin.status = PluginStatus.POTIONMAKING;

                plugin.item1 = plugin.config.potionItem1();
                plugin.item1Amount = 14;
                plugin.item2 = plugin.config.potionItem2();
                plugin.item2Amount = 14;

                Widget makeMenu = Widgets.get(270, 0);
                if (needItems())
                {
                    plugin.banking = true;
                    return -3;
                }

                Item item1 = Inventory.getFirst(plugin.config.potionItem1());
                Item item2 = Inventory.getFirst(plugin.config.potionItem2());

                if (item1 != null && item2 != null)
                {
                    if (makeMenu == null)
                    {
                        log.debug("using item: " + item1 + " on " + item2);
                        item1.useOn(item2);
                        Time.sleepUntil(() -> makeMenu.isVisible(), 2000);
                        return -2;
                    }

                    if (makeMenu.isVisible())
                    {
                        log.debug("Send space");
                        Keyboard.sendSpace();
                        Time.sleepUntil(() -> !Inventory.contains(item1.getName()), 22000);
                        return -2;
                    }
                }

                break;
            case CLEAN:
                plugin.status = PluginStatus.CLEANHERBS;

                plugin.item1 = "Grimy";
                plugin.item1Amount = 28;
                plugin.item2 = "Null";
                plugin.item2Amount = 0;

                Item grimy = Inventory.getFirst(x -> x.getName().contains("Grimy"));

                if (!Inventory.contains(x -> x.getName().contains("Grimy")))
                {
                    plugin.banking = true;
                    return -4;
                }
                if (grimy != null)
                {
                    grimy.interact("Clean");
                    return -1;
                }
                break;
        }
        return -2;
    }
    private boolean needItems() { return !Inventory.contains(plugin.config.potionItem1()) || !Inventory.contains(plugin.config.potionItem2()); }
}
