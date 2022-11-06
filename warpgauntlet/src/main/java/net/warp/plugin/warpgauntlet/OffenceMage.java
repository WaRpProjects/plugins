package net.warp.plugin.warpgauntlet;

import lombok.Getter;
import net.runelite.api.Prayer;

@Getter
public enum OffenceMage
{
    MYSTIC_MIGHT("Mystic Might", Prayer.MYSTIC_MIGHT),
    AUGURY("Augury", Prayer.AUGURY);

    private final String prayerName;
    private final Prayer prayer;

    OffenceMage(String prayerName, Prayer prayer)
    {
        this.prayerName = prayerName;
        this.prayer = prayer;
    }
}
