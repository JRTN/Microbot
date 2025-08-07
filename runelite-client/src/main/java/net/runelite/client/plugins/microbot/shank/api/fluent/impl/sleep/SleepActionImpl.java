package net.runelite.client.plugins.microbot.shank.api.fluent.impl.sleep;

import com.google.common.annotations.Beta;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.sleep.SleepAction;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.util.TimingUtils;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

@Beta //This may be removed if it proves too complicated
@Slf4j
public class SleepActionImpl implements SleepAction {

    private final SleepType sleepType;
    private final BooleanSupplier waitCondition;
    private final LongSupplier sleepDurationSupplier;
    private final LongSupplier pollingRateSupplier;
    private final long timeoutMs;
    private final Supplier<Boolean> actionToRepeat;

    private Action concurrentAction;
    private LongSupplier actionRateSupplier = () -> 100L;

    private enum SleepType {
        SIMPLE_SLEEP,
        WAIT_UNTIL,
        REPEAT_UNTIL
    }

    public SleepActionImpl(LongSupplier sleepDurationSupplier) {
        this.sleepType = SleepType.SIMPLE_SLEEP;
        this.sleepDurationSupplier = sleepDurationSupplier;
        this.waitCondition = null;
        this.pollingRateSupplier = null;
        this.timeoutMs = 0;
        this.actionToRepeat = null;
        log.debug("Created simple sleep action");
    }

    public SleepActionImpl(
            BooleanSupplier waitCondition, LongSupplier pollingRateSupplier, long timeoutMs) {
        this.sleepType = SleepType.WAIT_UNTIL;
        this.waitCondition = waitCondition;
        this.pollingRateSupplier = pollingRateSupplier;
        this.timeoutMs = timeoutMs;
        this.sleepDurationSupplier = null;
        this.actionToRepeat = null;
        log.debug("Created wait until action with timeout: {}ms", timeoutMs);
    }

    public SleepActionImpl(
            Supplier<Boolean> actionToRepeat,
            BooleanSupplier exitCondition,
            LongSupplier pollingRateSupplier,
            long timeoutMs) {
        this.sleepType = SleepType.REPEAT_UNTIL;
        this.actionToRepeat = actionToRepeat;
        this.waitCondition = exitCondition;
        this.pollingRateSupplier = pollingRateSupplier;
        this.timeoutMs = timeoutMs;
        this.sleepDurationSupplier = null;
        log.debug("Created repeat until action with timeout: {}ms", timeoutMs);
    }

    @Override
    public boolean execute() {
        log.info("Executing {} action", sleepType);
        long startTime = System.currentTimeMillis();

        try {
            boolean result;
            if (concurrentAction == null) {
                log.debug("Executing without concurrent action");
                result = executeWithoutConcurrentAction();
            } else {
                log.debug("Executing with concurrent action");
                result = executeWithConcurrentAction();
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("{} action completed in {}ms: {}", sleepType, duration, result ? "SUCCESS" : "FAILED");
            return result;

        } catch (Exception e) {
            log.warn("Error during {} action execution", sleepType, e);
            return false;
        }
    }

    private boolean executeWithoutConcurrentAction() {
        switch (sleepType) {
            case SIMPLE_SLEEP:
                long sleepDuration = sleepDurationSupplier.getAsLong();
                log.debug("Simple sleep for {}ms", sleepDuration);
                return TimingUtils.sleep(sleepDuration);
            case WAIT_UNTIL:
                log.debug("Waiting until condition with timeout: {}ms", timeoutMs);
                return TimingUtils.waitUntil(waitCondition, pollingRateSupplier, timeoutMs);
            case REPEAT_UNTIL:
                log.debug("Repeating action until condition with timeout: {}ms", timeoutMs);
                return TimingUtils.repeatUntil(
                        actionToRepeat, waitCondition, pollingRateSupplier, timeoutMs);
            default:
                log.warn("Unknown sleep type: {}", sleepType);
                return false;
        }
    }

    private boolean executeWithConcurrentAction() {
        switch (sleepType) {
            case SIMPLE_SLEEP:
                return executeSimpleSleepWithConcurrentAction();
            case WAIT_UNTIL:
                return executeWaitWithConcurrentAction();
            case REPEAT_UNTIL:
                return executeRepeatWithConcurrentAction();
            default:
                log.warn("Unknown sleep type with concurrent action: {}", sleepType);
                return false;
        }
    }

    private boolean executeSimpleSleepWithConcurrentAction() {
        long totalSleepTime = sleepDurationSupplier.getAsLong();
        long actionRate = actionRateSupplier.getAsLong();
        log.debug("Simple sleep with concurrent action: {}ms total, action rate: {}ms", totalSleepTime, actionRate);

        long remainingTime = totalSleepTime;
        int actionCount = 0;

        while (remainingTime > 0) {
            log.debug("Executing concurrent action (count: {})", ++actionCount);
            concurrentAction.execute();

            long sleepTime = Math.min(actionRate, remainingTime);
            if (!TimingUtils.sleep(sleepTime)) {
                log.warn("Sleep interrupted during concurrent action execution");
                return false;
            }

            remainingTime -= sleepTime;
        }

        log.debug("Completed simple sleep with {} concurrent actions", actionCount);
        return true;
    }

    private boolean executeWaitWithConcurrentAction() {
        log.debug("Wait until condition with concurrent action, timeout: {}ms", timeoutMs);
        long startTime = System.currentTimeMillis();
        long lastActionTime = startTime;
        int actionCount = 0;

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (waitCondition.getAsBoolean()) {
                log.debug("Wait condition met after {} concurrent actions", actionCount);
                return true;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastActionTime >= actionRateSupplier.getAsLong()) {
                log.debug("Executing concurrent action (count: {})", ++actionCount);
                concurrentAction.execute(); // Ignore failure for now
                lastActionTime = currentTime;
            }

            long pollingRate = pollingRateSupplier.getAsLong();
            if (pollingRate > 0 && !TimingUtils.sleep(pollingRate)) {
                log.warn("Sleep interrupted during wait with concurrent action");
                return false;
            }
        }

        log.warn("Wait with concurrent action timed out after {}ms, {} actions executed", timeoutMs, actionCount);
        return false;
    }

    private boolean executeRepeatWithConcurrentAction() {
        log.debug("Repeat until condition with concurrent action, timeout: {}ms", timeoutMs);
        long startTime = System.currentTimeMillis();
        long lastActionTime = startTime;
        int mainActionCount = 0;
        int concurrentActionCount = 0;

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            // Check exit condition first
            if (waitCondition.getAsBoolean()) {
                log.debug("Exit condition met after {} main actions, {} concurrent actions",
                        mainActionCount, concurrentActionCount);
                return true;
            }

            // Execute the main action
            log.debug("Executing main action (count: {})", ++mainActionCount);
            if (!actionToRepeat.get()) {
                log.warn("Main action failed after {} attempts", mainActionCount);
                return false; // Main action failed
            }

            // Check exit condition again after execution
            if (waitCondition.getAsBoolean()) {
                log.debug("Exit condition met after main action execution");
                return true;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastActionTime >= actionRateSupplier.getAsLong()) {
                log.debug("Executing concurrent action (count: {})", ++concurrentActionCount);
                concurrentAction.execute(); // Ignore failure for now
                lastActionTime = currentTime;
            }

            long pollingRate = pollingRateSupplier.getAsLong();
            if (pollingRate > 0 && !TimingUtils.sleep(pollingRate)) {
                log.warn("Sleep interrupted during repeat with concurrent action");
                return false;
            }
        }

        log.warn("Repeat with concurrent action timed out after {}ms, {} main actions, {} concurrent actions",
                timeoutMs, mainActionCount, concurrentActionCount);
        return false;
    }

    @Override
    public SleepAction whileDoing(Action concurrentAction) {
        log.debug("Setting concurrent action with default rate: {}ms", actionRateSupplier.getAsLong());
        this.concurrentAction = concurrentAction;
        return this;
    }

    @Override
    public SleepAction whileDoing(Action concurrentAction, LongSupplier actionRateSupplier) {
        log.debug("Setting concurrent action with dynamic rate supplier");
        this.concurrentAction = concurrentAction;
        this.actionRateSupplier = actionRateSupplier;
        return this;
    }

    @Override
    public SleepAction whileDoing(Action concurrentAction, long actionRateMs) {
        log.debug("Setting concurrent action with rate: {}ms", actionRateMs);
        return whileDoing(concurrentAction, () -> actionRateMs);
    }
}
