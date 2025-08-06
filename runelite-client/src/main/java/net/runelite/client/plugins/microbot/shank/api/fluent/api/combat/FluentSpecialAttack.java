package net.runelite.client.plugins.microbot.shank.api.fluent.api.combat;

import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;

public interface FluentSpecialAttack {
    boolean canUse();
    boolean canUse(int threshold);
    boolean isFull();
    boolean isLow();

    int getEnergy();

    Action use();
}
