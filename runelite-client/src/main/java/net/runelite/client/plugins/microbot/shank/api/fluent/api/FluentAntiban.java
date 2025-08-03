package net.runelite.client.plugins.microbot.shank.api.fluent.api;

import net.runelite.client.plugins.microbot.shank.api.fluent.impl.flow.Action;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;

/** Fluent API for anti-ban system operations. */
public interface FluentAntiban {

    /** Creates an action that triggers an action cooldown based on current anti-ban settings. */
    Action actionCooldown();

    /** Creates an action that attempts to trigger a micro-break based on configured chance. */
    Action microBreak();

    /** Creates an action that moves the mouse off-screen to simulate taking a break. */
    Action moveMouseOffScreen();

    /** Creates an action that moves the mouse off-screen based on a specified chance. */
    Action moveMouseOffScreenWithChance(double chance);

    /** Creates an action that moves the mouse to a random position on screen. */
    Action moveMouseRandomly();

    /** Creates an action that sets the current anti-ban activity. */
    Action setActivity(Activity activity);

    /** Creates an action that sets the activity intensity level. */
    Action setActivityIntensity(ActivityIntensity intensity);

    /** Checks if the anti-ban system is currently active. */
    boolean isActive();

    /** Checks if an action cooldown is currently active. */
    boolean isCooldownActive();

    /** Checks if a micro-break is currently active. */
    boolean isMicroBreakActive();
}
