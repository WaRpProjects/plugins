package net.warp.plugin.warpcrabs;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("warpcrabs")
public interface WarpCrabsConfig extends Config
{
    @ConfigSection(
            name = "Warp Crabs",
            description = "Settings",
            position = 0
    )
    String general = "Warp Crabs";

    @ConfigSection(
            name = "High Alch",
            description = "Magic settings",
            position = 0
    )
    String highAlch = "High Alch";

    @ConfigItem(
            keyName = "location",
            name = "Location",
            description = "What location",
            position = 0,
            section = general
    )
    default Locations location() { return Locations.SPOT_1; }

    @ConfigItem(
            keyName = "getAmmo",
            name = "pickup Ammo",
            description = "Pickup ammo",
            position = 1,
            section = general
    )
    default boolean getAmmo() { return false; }

    @ConfigItem(
            keyName = "ammoName",
            name = "Ammo name:",
            description = "Name of ammo to pickup",
            position = 2,
            section = general
    )
    default String ammoName() { return "Iron knife"; }

    @ConfigItem(
            keyName = "alchItems",
            name = "High Alch",
            description = "High Alch items",
            position = 0,
            section = highAlch
    )
    default boolean highAlch() { return false; }

    @ConfigItem(
            keyName = "alchItem",
            name = "Item to Alch",
            description = "High Alch item",
            position = 1,
            section = highAlch
    )
    default String alchItem() { return "Rune full helm"; }


}
