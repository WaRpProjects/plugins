package net.warp.plugin.warpskiller.Skills;

import lombok.Getter;

@Getter
public enum SkillTask {
    SLEEP("Sleep"),
    CRAFT("Crafting"),
    MAGIC("Magic"),
    FLETCH("Fletching"),
    HERBLORE("Herblore");

    private final String taskName;
    SkillTask(String taskName)
    {
        this.taskName = taskName;
    }

}
