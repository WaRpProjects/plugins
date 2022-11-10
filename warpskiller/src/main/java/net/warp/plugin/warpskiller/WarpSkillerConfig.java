package net.warp.plugin.warpskiller;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import javax.xml.crypto.dsig.keyinfo.KeyName;

@ConfigGroup("warpskiller")
public interface WarpSkillerConfig extends Config {

    @ConfigSection(
        name = "Skill",
        description = "General settings",
        position = 0,
        closedByDefault = false
    )
    String general = "General";

    @ConfigSection(
            name = "Crafting",
            description = "Crafting settings",
            position = 1,
            closedByDefault = true,
            hidden = true
    )
    String gemCutter = "Cuts gems";

    @ConfigSection(
            name = "Magic",
            description = "Magic spell to cast",
            position = 2,
            closedByDefault = true
    )
    String magicSpell = "magicSpell";

    @ConfigSection(
            name = "Fletching",
            description = "Fletch settings",
            position = 3,
            closedByDefault = true
    )
    String fletching = "fletching";



    @ConfigItem(
            keyName = "skillTask",
            name = "Select a skill:",
            description = "Select a skill to boat",
            position = 0,
            section = general
    )
    default SkillTask skillTask() { return SkillTask.SLEEP; }

    @ConfigItem(
            keyName = "gemType",
            name = "Gem to cut:",
            description = "What gem to cut",
            position = 0,
            section = gemCutter
    )
    default Gems gemType() { return Gems.SAPPHIRE; }

    @ConfigItem(
            keyName = "spellType",
            name = "Spell to Cast:",
            description = "What spell to cast",
            position = 0,
            section = magicSpell
    )
    default Spells spellType() { return Spells.HIGH_ALCH; }

    @ConfigItem(
            keyName = "alchItem",
            name = "Item to alch:",
            description = "Name of the item to alch",
            position = 1,
            section = magicSpell
    )
    default String alchItem() { return "Yew shortbow (u)"; }

    @ConfigItem(
            keyName = "barType",
            name = "Bar:",
            description = "Which bar to Superheat",
            position = 2,
            section = magicSpell
    )
    default Bars barType() {return Bars.IRON; }

    @ConfigItem(
            keyName = "bowType",
            name = "Bow: ",
            description = "What type of bow to fletch",
            position = 0,
            section = fletching
    )
    default Bows bow() { return Bows.LONGBOW; }

    @ConfigItem(
            keyName = "logType",
            name = "Log:",
            description = "What log to use",
            position = 1,
            section = fletching
    )
    default Logs log() { return Logs.YEW; }

}
