package net.warp.plugin.warpcutter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("warpcutter")
public interface WarpCutterConfig extends Config
{
    @ConfigSection(
            name = "Warp woodcutter",
            description = "Settings",
            position = 0
    )
    String general = "Warp woodcutter";

    @ConfigItem(
            keyName = "treeType",
            name = "Tree type",
            description = "The type of tree to chop",
            position = 0,
            section = general
    )
    default Tree treeType()
    {
        return Tree.YEW;
    }


}
