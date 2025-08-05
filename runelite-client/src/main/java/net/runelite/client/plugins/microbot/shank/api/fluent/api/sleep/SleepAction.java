package net.runelite.client.plugins.microbot.shank.api.fluent.api.sleep;

import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.ActionChain;

import java.util.function.LongSupplier;

public interface SleepAction extends Action {
    /** Execute a single action repeatedly while waiting */
    SleepAction whileDoing(Action concurrentAction);
    SleepAction whileDoing(Action concurrentAction, LongSupplier actionRateSupplier);
    SleepAction whileDoing(Action concurrentAction, long actionRateMs);
}
