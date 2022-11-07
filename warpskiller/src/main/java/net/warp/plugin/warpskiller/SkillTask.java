package net.warp.plugin.warpskiller;

import lombok.Getter;

@Getter
public enum SkillTask {
    FLETCH("Fletch", 1232),
    CRUSH("Crush", 343, "Pestle and mortar"),
    ALCH("Alch", 2102),
    STRING("String bow", 1000),
    SMELT("Smelt", 2000, "Cannon"),
    SMITH("Smith", 2000, "Hammer");

    private final String taskName;
    private final int sleep;
    private final String itemName;
    SkillTask(String taskName, int sleep, String... itemName)
    {
        this.taskName = taskName;
        this.sleep = sleep;
        this.itemName = String.valueOf(itemName);
    }

}
