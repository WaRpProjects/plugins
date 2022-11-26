package net.warp.plugin.warpskiller.Items;

import lombok.Getter;

@Getter
public enum Crafting {
    GEMCUTTING("Gem cutting"),
    GLASSBLOW("Blow glass");

    private final String craftName;

    Crafting (String craftName)
    {
        this.craftName = craftName;
    }
}
