package net.warp.plugin.warpcutter;

import lombok.Getter;
@Getter
public enum Tree
{
    YEW("Yew"),
    MAGIC( "Magic tree");
        private final String treeName;
        Tree(String treeName)
        {
            this.treeName = treeName;
        }

}
