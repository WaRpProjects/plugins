package net.warp.plugin.warpgauntlet;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("warpgauntlet")
public interface WarpGauntletConfig extends Config
{
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
            keyName = "offencePrayerRange",
            name = "Range prayer",
            description = "What offence range prayer to use",
            position = 2
    )

    default OffenceRange offencePrayerRange()
    {
        return OffenceRange.RIGOUR;
    }

    @ConfigItem(
            keyName = "offencePrayerMage",
            name = "Mage prayer",
            description = "What offence mage prayer to use",
            position = 3
    )

    default OffenceMage offencePrayerMage()
    {
        return OffenceMage.AUGURY;
    }
}
