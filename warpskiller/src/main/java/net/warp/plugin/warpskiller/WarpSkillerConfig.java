package net.warp.plugin.warpskiller;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.warp.plugin.warpskiller.Items.*;

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
    String crafting = "Crafting";

    @ConfigSection(
            name = "Magic",
            description = "Magic spell to cast",
            position = 2,
            closedByDefault = true
    )
    String magic = "Magic";

    @ConfigSection(
            name = "Fletching",
            description = "Fletch settings",
            position = 3,
            closedByDefault = true
    )
    String fletching = "Fletching";

    @ConfigSection(
            name = "Herblore",
            description = "Herblore settings",
            position = 4,
            closedByDefault = true
    )
    String herblore = "Herblore";

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
            section = crafting
    )
    default Gems gemType() { return Gems.SAPPHIRE; }

    @ConfigItem(
            keyName = "spellType",
            name = "Spell to Cast:",
            description = "What spell to cast",
            position = 0,
            section = magic
    )
    default Spells spellType() { return Spells.HIGH_ALCH; }

    @ConfigItem(
            keyName = "alchItem",
            name = "Item to alch:",
            description = "Name of the item to alch",
            position = 1,
            section = magic
    )
    default String alchItem() { return "Yew shortbow (u)"; }

    @ConfigItem(
            keyName = "barType",
            name = "Bar:",
            description = "Which bar to Superheat",
            position = 2,
            section = magic
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
    @ConfigItem(
            keyName = "herbloreType",
            name = "Herblore: ",
            description = "What to do",
            position = 0,
            section = herblore
    )
    default Herblore herbloreType() { return Herblore.CLEAN; }

    @ConfigItem(
            keyName = "potionItem1",
            name = "Item name:",
            description = "What item to combine",
            position = 1,
            section = herblore
    )
    default String potionItem1() { return "Vial of water"; }

    @ConfigItem(
            keyName = "potionItem2",
            name = "Item name:",
            description = "What item to combine",
            position = 1,
            section = herblore
    )
    default String potionItem2() { return "Guam leaf"; }
}
