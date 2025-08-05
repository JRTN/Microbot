package net.runelite.client.plugins.microbot.shank.api.fluent.api.sleep;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface FluentTiming {
    SleepAction sleep(long milliseconds, long jitter);
    SleepAction sleep(LongSupplier millisecondsSupplier);
    SleepAction sleep(long milliseconds);
    SleepAction sleepUntil(BooleanSupplier condition, LongSupplier pollingRateSupplier, long timeoutMs);
    SleepAction repeatUntil(Supplier<Boolean> action, BooleanSupplier exitCondition,
                       LongSupplier pollingRateSupplier, long timeoutMs);

}
