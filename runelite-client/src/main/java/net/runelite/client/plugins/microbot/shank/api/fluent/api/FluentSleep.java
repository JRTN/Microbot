package net.runelite.client.plugins.microbot.shank.api.fluent.api;

import net.runelite.client.plugins.microbot.shank.api.fluent.impl.flow.Action;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface FluentSleep {
    Action forMilliseconds(long milliseconds, long jitter);
    Action forMilliseconds(LongSupplier millisecondsSupplier);
    Action forMilliseconds(long milliseconds);
    Action waitUntil(BooleanSupplier condition, LongSupplier pollingRateSupplier, long timeoutMs);
    Action repeatUntil(Supplier<Boolean> action, BooleanSupplier exitCondition,
                       LongSupplier pollingRateSupplier, long timeoutMs);

}
