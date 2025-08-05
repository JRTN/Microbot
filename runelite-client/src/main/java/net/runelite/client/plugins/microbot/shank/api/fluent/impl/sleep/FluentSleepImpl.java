package net.runelite.client.plugins.microbot.shank.api.fluent.impl.sleep;

import net.runelite.client.plugins.microbot.shank.api.fluent.api.sleep.FluentSleep;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.sleep.SleepAction;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.util.TimingUtils;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class FluentSleepImpl implements FluentSleep {

    @Override
    public SleepAction forMilliseconds(LongSupplier millisecondsSupplier) {
        return new SleepActionImpl(millisecondsSupplier);
    }

    @Override
    public SleepAction forMilliseconds(long milliseconds, long jitter) {
        return forMilliseconds(TimingUtils.randomJitter(milliseconds, jitter));
    }

    @Override
    public SleepAction forMilliseconds(long milliseconds) {
        return forMilliseconds(() -> milliseconds);
    }

    @Override
    public SleepAction waitUntil(
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
