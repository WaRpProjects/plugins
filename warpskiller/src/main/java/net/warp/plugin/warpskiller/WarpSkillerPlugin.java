package net.warp.plugin.warpskiller;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.Skill;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.plugins.TaskPlugin;
import net.warp.plugin.warpskiller.Tasks.*;

import org.pf4j.Extension;

@PluginDescriptor(
        name = "WaRp Skiller",
        description = "Skills at any bank.",
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

    @Inject
    private BuilderOverlay builderOverlay;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    public Client client;


    //Banking stuff
    public boolean banking;
    public boolean firstRun = true;
    public String item1 = "Null";
    public String item2 = "Null";
    public int item1Amount = 0;
    public int item2Amount = 0;
    public Item staff = null;
    public int staffID;

    //Paint info
    public long startTime;
    public int herbloreExp;
    public int magicExp;
    public int craftingExp;
    public int fletchExp;
    public int alchProfit;
    public PluginStatus status;


    @Subscribe
    private void onConfigChanged (ConfigChanged configChanged)
    {
        if (configChanged.getGroup().equals("warpskiller"))
        {
            log.debug("Resetting");
            firstRun = true;
        }
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(builderOverlay);
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        status = PluginStatus.IDLE;
        craftingExp = Skills.getExperience(Skill.CRAFTING);
        magicExp = Skills.getExperience(Skill.MAGIC);
        herbloreExp = Skills.getExperience(Skill.HERBLORE);
        fletchExp = Skills.getExperience(Skill.FLETCHING);
        alchProfit = 0;
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(builderOverlay);
    }

    @Inject
    public WarpSkillerConfig config;
    private final Task[] taskList = { new BankTask(this),  new MagicTask(this), new FletchTask(this) , new GemTask(this), new BlowTask(this), new HerbloreTask(this) };

    @Override
    public Task[] getTasks() {
        return taskList;
    }

}
