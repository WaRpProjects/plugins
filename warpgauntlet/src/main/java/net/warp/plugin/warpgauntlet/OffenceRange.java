package net.warp.plugin.warpgauntlet;

import lombok.Getter;
import net.runelite.api.Prayer;


@Getter
public enum OffenceRange
{
    EAGLE_EYE("Eagle eye", Prayer.EAGLE_EYE),
    RIGOUR("Rigour", Prayer.RIGOUR);

    private final String prayerName;
    private final Prayer prayer;
    OffenceRange(String prayerName, Prayer prayer)
    {
        this.prayerName = prayerName;
        this.prayer = prayer;
    }
}
