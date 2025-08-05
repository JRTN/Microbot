package net.runelite.client.plugins.microbot.shank.api.fluent.impl.sleep;

import net.runelite.client.plugins.microbot.shank.api.fluent.api.sleep.FluentTiming;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.sleep.SleepAction;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.util.TimingUtils;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class FluentTimingImpl implements FluentTiming {

    @Override
    public SleepAction sleep(LongSupplier millisecondsSupplier) {
        return new SleepActionImpl(millisecondsSupplier);
    }

    @Override
    public SleepAction sleep(long milliseconds, long jitter) {
        return sleep(TimingUtils.randomJitter(milliseconds, jitter));
    }

    @Override
    public SleepAction sleep(long milliseconds) {
        return sleep(() -> milliseconds);
    }

    @Override
    public SleepAction sleepUntil(
            BooleanSupplier condition, LongSupplier pollingRateSupplier, long timeoutMs) {
        return new SleepActionImpl(condition, pollingRateSupplier, timeoutMs);
    }

    @Override
    public SleepAction repeatUntil(
            Supplier<Boolean> action,
            BooleanSupplier exitCondition,
            LongSupplier pollingRateSupplier,
            long timeoutMs) {
        return new SleepActionImpl(action, exitCondition, pollingRateSupplier, timeoutMs);
    }
}
