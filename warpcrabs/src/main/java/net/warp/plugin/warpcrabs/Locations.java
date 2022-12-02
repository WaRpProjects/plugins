package net.warp.plugin.warpcrabs;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
@Getter
public enum Locations {
    SPOT_1("Sandcrab1", new WorldPoint(1737, 3469, 0)),
    SPOT_2("Sandcrab2", new WorldPoint(1749, 3469, 0)),
    SPOT_3("Sandcrab3", new WorldPoint(1765, 3468, 0)),
    SPOT_4("Sandcrab4", new WorldPoint(1773, 3461, 0));

    private final String locationName;
    private final WorldPoint locationPoint;

    Locations(String locationName, WorldPoint locationPoint)
    {
        this.locationName = locationName;
        this.locationPoint = locationPoint;
    }
}
