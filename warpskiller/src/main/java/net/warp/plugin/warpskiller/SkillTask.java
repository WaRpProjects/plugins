package net.warp.plugin.warpskiller;

import lombok.Getter;

@Getter
public enum SkillTask {
    CRAFT("Craft"),
    FLETCH("Fletch"),
    MAGIC("Magic");

    private final String taskName;
    SkillTask(String taskName)
    {
        this.taskName = taskName;
    }

}
