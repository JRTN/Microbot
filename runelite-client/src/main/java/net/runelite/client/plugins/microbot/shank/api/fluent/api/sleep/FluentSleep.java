package net.runelite.client.plugins.microbot.shank.api.fluent.api.sleep;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface FluentSleep {
    SleepAction forMilliseconds(long milliseconds, long jitter);
    SleepAction forMilliseconds(LongSupplier millisecondsSupplier);
    SleepAction forMilliseconds(long milliseconds);
    SleepAction waitUntil(BooleanSupplier condition, LongSupplier pollingRateSupplier, long timeoutMs);
    SleepAction repeatUntil(Supplier<Boolean> action, BooleanSupplier exitCondition,
                       LongSupplier pollingRateSupplier, long timeoutMs);

}
