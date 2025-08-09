package net.runelite.client.plugins.microbot.shank.api.fluent.impl.general;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.general.TickManipulationAction;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.util.TimingUtils;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

@Slf4j
public class TickManipulationActionImpl implements TickManipulationAction {

    private Action mainAction = Action.NONE;
    private Action beforeAction = Action.NONE;
    private Action afterAction = Action.NONE;
    private BooleanSupplier completionCondition = () -> true; // Default always consider main action complete
    private LongSupplier timeoutSupplier = () -> 600L; // Default 1 tick
    private LongSupplier pollingRateSupplier = () -> 25L;
    private BooleanSupplier stopCondition = () -> true; // Default stop after first iteration
    private long maxDurationMs = Long.MAX_VALUE;

    @Override
    public TickManipulationAction action(Action mainAction) {
        this.mainAction = mainAction;
        log.debug("Set main action: {}", mainAction.getClass().getSimpleName());
        return this;
    }

    @Override
    public TickManipulationAction before(Action beforeAction) {
        this.beforeAction = beforeAction;
        log.debug("Set before action: {}", beforeAction.getClass().getSimpleName());
        return this;
    }

    @Override
    public TickManipulationAction after(Action afterAction) {
        this.afterAction = afterAction;
        log.debug("Set after action: {}", afterAction.getClass().getSimpleName());
        return this;
    }

    @Override
    public TickManipulationAction waitUntil(BooleanSupplier completionCondition) {
        this.completionCondition = completionCondition;
        log.debug("Set completion condition");
        return this;
    }

    @Override
    public TickManipulationAction timeout(long timeoutMs) {
        this.timeoutSupplier = () -> timeoutMs;
        log.debug("Set timeout: {}ms", timeoutMs);
        return this;
    }

    @Override
    public TickManipulationAction timeout(LongSupplier timeoutSupplier) {
        this.timeoutSupplier = timeoutSupplier;
        log.debug("Set dynamic timeout supplier");
        return this;
    }

    @Override
    public TickManipulationAction pollingRate(long pollingRateMs) {
        this.pollingRateSupplier = () -> pollingRateMs;
        log.debug("Set polling rate: {}ms", pollingRateMs);
        return this;
    }

    @Override
    public TickManipulationAction pollingRate(LongSupplier pollingRateSupplier) {
        this.pollingRateSupplier = pollingRateSupplier;
        log.debug("Set dynamic polling rate supplier");
        return this;
    }

    @Override
    public TickManipulationAction maxDuration(LongSupplier maxDuration) {
        this.maxDurationMs = maxDuration.getAsLong();
        log.debug("Set max duration: {}ms", this.maxDurationMs);
        return this;
    }

    @Override
    public TickManipulationAction maxDuration(long maxDuration) {
        this.maxDurationMs = maxDuration;
        log.debug("Set max duration: {}ms", maxDuration);
        return this;
    }

    @Override
    public TickManipulationAction repeating() {
        this.stopCondition = () -> false; // Never stop
        log.info("Configured for infinite repetition");
        return this;
    }

    @Override
    public TickManipulationAction repeatUntil(BooleanSupplier stopCondition) {
        this.stopCondition = stopCondition;
        log.info("Configured to repeat until stop condition met");
        return this;
    }

    @Override
    public boolean execute() {
        log.info("Starting tick manipulation execution");
        long startTime = System.currentTimeMillis();

        try {
            // Single execution if stopCondition is true initially
            if (stopCondition.getAsBoolean()) {
                log.info("Stop condition already true, executing single cycle");
                boolean result = executeOneCycle();
                log.info("Single cycle completed in {}ms: {}",
                        System.currentTimeMillis() - startTime, result ? "SUCCESS" : "FAILED");
                return result;
            }

            // Repeating execution using TimingUtils
            log.info("Starting repeating execution with max duration: {}ms", maxDurationMs);
            boolean result = TimingUtils.repeatUntil(
                    this::executeOneCycle,
                    stopCondition,
                    pollingRateSupplier,
                    maxDurationMs
            );

            long duration = System.currentTimeMillis() - startTime;
            log.info("Repeating execution completed in {}ms: {}", duration, result ? "SUCCESS" : "FAILED");
            return result;

        } catch (Exception e) {
            log.error("Error during tick manipulation execution", e);
            return false;
        }
    }

    private boolean executeOneCycle() {
        log.debug("Starting tick manipulation cycle");
        long startTime = System.currentTimeMillis();
        // Before action
        if (!beforeAction.execute()) {
            log.warn("Before action failed, aborting cycle");
            return false;
        }
        long beforeEndTime = System.currentTimeMillis() - startTime;
        log.debug("Before action completed successfully in {}ms", beforeEndTime);

        long beforeSleepTime = TimingUtils.randomJitter(600, 10).getAsLong();
        log.debug("Sleeping for {}ms before executing main action", beforeSleepTime);
        TimingUtils.sleep(beforeSleepTime);

        // Main action
        if (!mainAction.execute()) {
            log.warn("Main action failed, aborting cycle");
            return false;
        }
        long mainEndTime = System.currentTimeMillis() - startTime - beforeEndTime;
        log.debug("Main action completed successfully in {}ms", mainEndTime);

        // Wait for completion
        long timeout = timeoutSupplier.getAsLong();
        log.debug("Waiting for completion condition with timeout: {}ms", timeout);
        boolean conditionMet = TimingUtils.waitUntil(
                completionCondition,
                pollingRateSupplier,
                timeout
        );

        long completeEndTime = System.currentTimeMillis() - startTime - beforeEndTime - mainEndTime;
        if (conditionMet) {
            log.debug("Completion condition met in {}ms", completeEndTime);
        } else {
            log.debug("Completion condition timed out after {}ms", timeout);
        }

        // After action
        if (!afterAction.execute()) {
            log.warn("After action failed, aborting cycle");
            return false;
        }
        long afterEndTime = System.currentTimeMillis() - startTime - beforeEndTime - mainEndTime - completeEndTime;
        log.debug("After action completed successfully in {}ms", afterEndTime);
        long afterSleepTime = TimingUtils.randomJitter(50, 10).getAsLong();
        log.debug("Sleeping for {}ms before executing main action", afterSleepTime);
        TimingUtils.sleep(afterEndTime);

        log.debug("Tick manipulation cycle completed successfully");

        return true;
    }
}
