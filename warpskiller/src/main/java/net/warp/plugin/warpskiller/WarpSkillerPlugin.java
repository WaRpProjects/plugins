package net.warp.plugin.warpskiller;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.plugins.TaskPlugin;
import net.warp.plugin.warpskiller.Tasks.*;

import org.pf4j.Extension;

@PluginDescriptor(
        name = "WaRp Skiller",
        description = "Skills at GE.",
        enabledByDefault = false
)

@Slf4j
@Extension
public class WarpSkillerPlugin extends TaskPlugin {
    @Provides
    WarpSkillerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WarpSkillerConfig.class);
    }

    public boolean banking;
    public boolean firstRun = true;
    public Item staff = null;

    @Subscribe
    private void onConfigChanged (ConfigChanged configChanged)
    {
        if (configChanged.getGroup().equals("warpskiller"))
        {
            log.debug("Resetting");
            firstRun = true;
        }
    }

    @Inject
    public WarpSkillerConfig config;
    private final Task[] taskList = { new BankTask(this),  new MagicTask(this), new FletchTask(this) , new GemTask(this) };

    @Override
    public Task[] getTasks() {
        return taskList;
    }

}
