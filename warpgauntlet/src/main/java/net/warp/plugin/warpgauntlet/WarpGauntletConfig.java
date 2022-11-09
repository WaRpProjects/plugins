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
            keyName = "drinkPot",
            name = "Drink Egniol",
            description = "Drink Egniol pots",
            position = 2
    )
    default boolean drinkPot() { return true; }

    @ConfigItem(
            keyName = "prayerPoints",
            name = "When to drink Egniol",
            description = "When to drink Egniol potion",
            position = 3
    )
    default int prayerPoints() { return 28; }

    @ConfigItem(
            keyName = "swapWeapon",
            name = "Swap weapons",
            description = "Swap weapons",
            position = 4
    )
    default boolean swapWeapon() { return true; }

    @ConfigItem(
            keyName = "offencePrayerRange",
            name = "Range prayer",
            description = "What offence range prayer to use",
            position = 5
    )
    default OffenceRange offencePrayerRange()
    {
        return OffenceRange.RIGOUR;
    }

    @ConfigItem(
            keyName = "offencePrayerMage",
            name = "Mage prayer",
            description = "What offence mage prayer to use",
            position = 6
    )
    default OffenceMage offencePrayerMage()
    {
        return OffenceMage.AUGURY;
    }
}
