package net.warp.plugin.warpskiller;

import lombok.Getter;

@Getter
public enum Bars {
    IRON("Iron", "Iron ore");
    //STEEL("N", "Iron ore", "Coal");

    private String barName;
    private String[] ores;

    Bars(String barName, String... ores)
    {
        this.barName = barName;
        this.ores = ores;
    }
}
