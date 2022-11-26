package net.warp.plugin.warpcrabs;

import net.runelite.client.config.*;

@ConfigGroup("warpcrabs")
public interface WarpCrabsConfig extends Config
{
    @ConfigSection(
            name = "Location",
            description = "Settings",
            position = 0
    )
    String general = "Location";

    @ConfigSection(
            name = "Food",
            description = "Food",
            position = 1
    )
    String food = "Food";

    @ConfigSection(
            name = "High Alch",
            description = "Magic settings",
            position = 2
    )
    String highAlch = "High Alch";

    @ConfigSection(
            name = "Ammo",
            description = "Ammo",
            position = 3
    )
    String ammo = "Ammo";

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
            position = 0,
            section = ammo
    )
    default boolean getAmmo() { return false; }

    @ConfigItem(
            keyName = "ammoName",
            name = "Ammo name:",
            description = "Name of ammo to pickup",
            position = 1,
            section = ammo
    )
    default String ammoName() { return "Iron knife"; }

    @ConfigItem(
            keyName = "eatFood",
            name = "Eat food",
            description = "Eat food?",
            position = 0,
            section = food
    )
    default boolean eatFood() { return false; }

    @Range(max = 100)
    @ConfigItem(
            keyName = "eatHealthPercent",
            name = "Health %",
            description = "Health % to eat at",
            position = 1,
            section = food
    )
    default int healthPercent()
    {
        return 65;
    }

    @ConfigItem(
            keyName = "foodName",
            name = "Food name:",
            description = "Name of the food to eat",
            position = 2,
            section = food
    )
    default String foodName() { return "Salmon"; }

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
