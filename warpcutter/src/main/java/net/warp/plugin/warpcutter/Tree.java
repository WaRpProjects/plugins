package net.warp.plugin.warpcutter;

import lombok.Getter;
@Getter
public enum Tree
{
    YEW(60, "Yew"),
    MAGIC(75, "Magic tree");
        private final int level;
        private final String names;
        Tree(int level, String names)
        {
            this.level = level;
            this.names = names;
        }

}
