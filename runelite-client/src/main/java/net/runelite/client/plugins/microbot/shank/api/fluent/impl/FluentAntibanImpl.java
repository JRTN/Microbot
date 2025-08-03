package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentAntiban;
import net.runelite.client.plugins.microbot.shank.api.fluent.impl.flow.Action;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;

public class FluentAntibanImpl implements FluentAntiban {
    @Override
    public Action actionCooldown() {
        return null;
    }

    @Override
    public Action microBreak() {
        return null;
    }

    @Override
    public Action moveMouseOffScreen() {
        return null;
    }

    @Override
    public Action moveMouseOffScreenWithChance(double chance) {
        return null;
    }

    @Override
    public Action moveMouseRandomly() {
        return null;
    }

    @Override
    public Action setActivity(Activity activity) {
        return null;
    }

    @Override
    public Action setActivityIntensity(ActivityIntensity intensity) {
        return null;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean isCooldownActive() {
        return false;
    }

    @Override
    public boolean isMicroBreakActive() {
        return false;
    }
}
