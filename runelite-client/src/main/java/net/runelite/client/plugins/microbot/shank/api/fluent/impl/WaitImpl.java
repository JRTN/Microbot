package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import net.runelite.client.plugins.microbot.shank.api.fluent.api.Wait;
import net.runelite.client.plugins.microbot.shank.api.fluent.impl.flow.Action;
import net.runelite.client.plugins.microbot.shank.api.fluent.impl.util.TimingUtils;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class WaitImpl implements Wait {

    @Override
    public Action forMilliseconds(LongSupplier millisecondsSupplier) {
        return () -> TimingUtils.sleep(millisecondsSupplier.getAsLong());
    }

    @Override
    public Action forMilliseconds(long milliseconds, long jitter) {
        return forMilliseconds(TimingUtils.randomJitter(milliseconds, jitter));
    }

    @Override
    public Action forMilliseconds(long milliseconds) {
        return forMilliseconds(milliseconds, 0);
    }

    @Override
    public Action waitUntil(
            BooleanSupplier condition, LongSupplier pollingRateSupplier, long timeoutMs) {
        return () -> TimingUtils.waitUntil(condition, pollingRateSupplier, timeoutMs);
    }

    @Override
    public Action repeatUntil(
            Supplier<Boolean> action,
            BooleanSupplier exitCondition,
            LongSupplier pollingRateSupplier,
            long timeoutMs) {
        return () -> TimingUtils.repeatUntil(action, exitCondition, pollingRateSupplier, timeoutMs);
    }
}
