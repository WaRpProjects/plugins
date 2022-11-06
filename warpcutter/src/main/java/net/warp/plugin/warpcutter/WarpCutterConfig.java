package net.warp.plugin.warpcutter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("warpcutter")
public interface WarpCutterConfig extends Config
{
    @ConfigItem(
            keyName = "boom",
            name = "Tree type",
            description = "The type of tree to chop",
            position = 0
    )

    default Tree boom()
    {
        return Tree.YEW;
    }


}
