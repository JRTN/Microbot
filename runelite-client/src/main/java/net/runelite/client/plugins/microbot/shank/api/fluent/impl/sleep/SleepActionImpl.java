package net.runelite.client.plugins.microbot.shank.api.fluent.impl.sleep;

import net.runelite.client.plugins.microbot.shank.api.fluent.api.sleep.SleepAction;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.ActionChain;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.util.TimingUtils;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

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
    }

    public SleepActionImpl(
            BooleanSupplier waitCondition, LongSupplier pollingRateSupplier, long timeoutMs) {
        this.sleepType = SleepType.WAIT_UNTIL;
        this.waitCondition = waitCondition;
        this.pollingRateSupplier = pollingRateSupplier;
        this.timeoutMs = timeoutMs;
        this.sleepDurationSupplier = null;
        this.actionToRepeat = null;
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
    }

    @Override
    public boolean execute() {
        if (concurrentAction == null) {
            return executeWithoutConcurrentAction();
        } else {
            return executeWithConcurrentAction();
        }
    }

    private boolean executeWithoutConcurrentAction() {
        switch (sleepType) {
            case SIMPLE_SLEEP:
                return TimingUtils.sleep(sleepDurationSupplier.getAsLong());
            case WAIT_UNTIL:
                return TimingUtils.waitUntil(waitCondition, pollingRateSupplier, timeoutMs);
            case REPEAT_UNTIL:
                return TimingUtils.repeatUntil(
                        actionToRepeat, waitCondition, pollingRateSupplier, timeoutMs);
            default:
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
                return false;
        }
    }

    private boolean executeSimpleSleepWithConcurrentAction() {
        long totalSleepTime = sleepDurationSupplier.getAsLong();
        long actionRate = actionRateSupplier.getAsLong();
        long remainingTime = totalSleepTime;

        while (remainingTime > 0) {
            concurrentAction.execute();

            long sleepTime = Math.min(actionRate, remainingTime);
            if (!TimingUtils.sleep(sleepTime)) {
                return false;
            }

            remainingTime -= sleepTime;
        }

        return true;
    }

    private boolean executeWaitWithConcurrentAction() {
        long startTime = System.currentTimeMillis();
        long lastActionTime = startTime;

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (waitCondition.getAsBoolean()) {
                return true;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastActionTime >= actionRateSupplier.getAsLong()) {
                concurrentAction.execute(); // Ignore failure for now
                lastActionTime = currentTime;
            }

            long pollingRate = pollingRateSupplier.getAsLong();
            if (pollingRate > 0 && !TimingUtils.sleep(pollingRate)) {
                return false;
            }
        }

        return false;
    }

    private boolean executeRepeatWithConcurrentAction() {
        long startTime = System.currentTimeMillis();
        long lastActionTime = startTime;

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            // Check exit condition first
            if (waitCondition.getAsBoolean()) {
                return true;
            }

            // Execute the main action
            if (!actionToRepeat.get()) {
                return false; // Main action failed
            }

            // Check exit condition again after execution
            if (waitCondition.getAsBoolean()) {
                return true;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastActionTime >= actionRateSupplier.getAsLong()) {
                concurrentAction.execute(); // Ignore failure for now
                lastActionTime = currentTime;
            }

            long pollingRate = pollingRateSupplier.getAsLong();
            if (pollingRate > 0 && !TimingUtils.sleep(pollingRate)) {
                return false;
            }
        }

        return false;
    }

    @Override
    public SleepAction whileDoing(Action concurrentAction) {
        this.concurrentAction = concurrentAction;
        return this;
    }

    @Override
    public SleepAction whileDoing(Action concurrentAction, LongSupplier actionRateSupplier) {
        this.concurrentAction = concurrentAction;
        this.actionRateSupplier = actionRateSupplier;
        return this;
    }

    @Override
    public SleepAction whileDoing(Action concurrentAction, long actionRateMs) {
        return whileDoing(concurrentAction, () -> actionRateMs);
    }

    @Override
    public SleepAction whileDoing(ActionChain concurrentActions) {
        this.concurrentAction = concurrentActions;
        return this;
    }

    @Override
    public SleepAction whileDoing(ActionChain concurrentActions, LongSupplier actionRateSupplier) {
        this.concurrentAction = concurrentActions;
        this.actionRateSupplier = actionRateSupplier;
        return this;
    }

    @Override
    public SleepAction whileDoing(ActionChain concurrentActions, long actionRateMs) {
        return whileDoing(concurrentActions, () -> actionRateMs);
    }
}
