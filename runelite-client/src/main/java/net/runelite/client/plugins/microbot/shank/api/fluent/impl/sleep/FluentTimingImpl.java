package net.runelite.client.plugins.microbot.shank.api.fluent.impl.sleep;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.sleep.FluentTiming;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.sleep.SleepAction;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.util.TimingUtils;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

@Slf4j
public class FluentTimingImpl implements FluentTiming {

    private long defaultTimeoutMs = 30_000;
    private long defaultPollingRateMs = 600;

    @Override
    public SleepAction sleep(LongSupplier millisecondsSupplier) {
        log.debug("Creating sleep action with supplier");
        return new SleepActionImpl(millisecondsSupplier);
    }

    @Override
    public SleepAction sleep(long milliseconds, long jitter) {
        log.debug("Creating sleep action: {}ms with jitter: {}ms", milliseconds, jitter);
        return sleep(TimingUtils.randomJitter(milliseconds, jitter));
    }

    @Override
    public SleepAction sleep(long milliseconds) {
        log.debug("Creating sleep action: {}ms", milliseconds);
        return sleep(() -> milliseconds);
    }

    @Override
    public SleepAction sleepUntil(
            BooleanSupplier condition, LongSupplier pollingRateSupplier, long timeoutMs) {
        log.debug("Creating sleepUntil action with timeout: {}ms", timeoutMs);
        return new SleepActionImpl(condition, pollingRateSupplier, timeoutMs);
    }

    @Override
    public SleepAction sleepUntil(BooleanSupplier condition, LongSupplier pollingRateSupplier) {
        log.debug("Creating sleepUntil action with default timeout: {}ms", defaultTimeoutMs);
        return sleepUntil(condition, pollingRateSupplier, defaultTimeoutMs);
    }

    @Override
    public SleepAction sleepUntil(BooleanSupplier condition, long pollingRate, long timeoutMs) {
        log.debug("Creating sleepUntil action with polling rate: {}ms, timeout: {}ms", pollingRate, timeoutMs);
        return sleepUntil(condition, () -> pollingRate, timeoutMs);
    }

    @Override
    public SleepAction sleepUntil(BooleanSupplier condition, long pollingRate) {
        log.debug("Creating sleepUntil action with polling rate: {}ms, default timeout: {}ms", pollingRate, defaultTimeoutMs);
        return sleepUntil(condition, pollingRate, defaultTimeoutMs);
    }

    @Override
    public SleepAction sleepUntil(BooleanSupplier condition) {
        log.debug("Creating sleepUntil action with default polling rate: {}ms and timeout: {}ms", defaultPollingRateMs, defaultTimeoutMs);
        return sleepUntil(condition, defaultPollingRateMs, defaultTimeoutMs);
    }

    @Override
    public SleepAction repeatUntil(
            Supplier<Boolean> action,
            BooleanSupplier exitCondition,
            LongSupplier pollingRateSupplier,
            long timeoutMs) {
        log.debug("Creating repeatUntil action with timeout: {}ms", timeoutMs);
        return new SleepActionImpl(action, exitCondition, pollingRateSupplier, timeoutMs);
    }
}
