package net.warp.plugin.warpgauntlet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Prayer;

@AllArgsConstructor
@Getter
public enum AttackPhase
{
    MAGIC(Prayer.PROTECT_FROM_MAGIC),
    RANGE(Prayer.PROTECT_FROM_MISSILES);

    private final Prayer prayer;
}