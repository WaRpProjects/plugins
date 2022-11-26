package net.warp.plugin.warpskiller;

import lombok.Getter;

@Getter
public enum PluginStatus {
    IDLE("Idle"),
    ALCH("Alching"),
    SUPERHEAT("Super-heating"),
    PLANK_MAKE("Plank make"),
    BANK("Banking"),
    GEMCUTTING("Cutting Gems"),
    GLASSBLOW("Blowing glass"),
    CLEANHERBS("Cleaning Herbs"),
    POTIONMAKING("Making potions"),
    FLETCHBOWS("Fletching Bows"),
    WALKING("Walking");

    private final String status;

    PluginStatus(String status)
    {
        this.status = status;
    }
}