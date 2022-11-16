package net.warp.plugin.warpskiller;

import lombok.Getter;

@Getter
public enum SkillTask {
    SLEEP("Sleep"),
    CRAFT("Craft"),
    MAGIC("Magic"),
    FLETCH("Fletch"),
    HERBLORE("Herblore");

    private final String taskName;
    SkillTask(String taskName)
    {
        this.taskName = taskName;
    }

}
