package net.warp.plugin.warpmasterthieving;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("warpmasterthieving")
public interface WarpMasterThieverConfig extends Config {

    @ConfigItem(
            keyName = "eat",
            name = "Eat food",
            description = "Eat food to heal",
            position = 0
    )
    default boolean eat()
    {
        return true;
    }

    @Range(max = 100)
    @ConfigItem(
            keyName = "eatHealthPercent",
            name = "Health %",
            description = "Health % to eat at",
            position = 1
    )
    default int healthPercent()
    {
        return 65;
    }

    @ConfigItem(
            keyName = "foodName",
            name = "Food name:",
            description = "Name of the food..",
            position = 2
    )
    default String foodName() { return "Cake"; }

    @ConfigItem(
            keyName = "foodAmount",
            name = "Amount:",
            description = "Amount of food to take",
            position = 3
    )
    default int foodAmount() { return 5; }

    @ConfigItem(
            keyName = "seedToDrop",
            name = "Seeds to drop: ",
            description = "Itemnames to drop",
            position = 4
    )
    default String seedToDrop() { return "Hammerstone seed,"; }
}
