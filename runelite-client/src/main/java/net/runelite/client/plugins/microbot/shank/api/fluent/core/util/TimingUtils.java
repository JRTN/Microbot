package net.runelite.client.plugins.microbot.shank.api.fluent.core.util;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Utility class for timing operations, polling loops, and waiting conditions.
 *
 * <p>This class provides reusable timing patterns commonly needed in automation scripts, such as
 * polling until conditions are met, repeating actions with timeouts, and handling dynamic wait
 * intervals.
 */
public class TimingUtils {

    /**
     * Repeats an action until a condition becomes true or timeout is reached.
     *
     * <p>This is the core polling loop implementation used throughout the fluent API. It supports
     * dynamic polling rates and proper timeout handling.
     *
     * <h3>Execution Flow</h3>
     *
     * <ol>
     *   <li>Check exit condition (early return if already met)
     *   <li>Execute the action
     *   <li>Check exit condition again (in case action changed state)
     *   <li>Wait for the polling interval
     *   <li>Repeat until condition is met or timeout
     * </ol>
     *
     * @param action The action to repeatedly execute. Should return {@code true} on success.
     * @param exitCondition Condition that stops the loop when it becomes {@code true}
     * @param pollingRateSupplier Supplier that returns wait time in milliseconds for each iteration
     * @param timeoutMs Maximum time to repeat before giving up, in milliseconds
     * @return {@code true} if the exit condition was met, {@code false} if timeout or action
     *     failure
     */
    public static boolean repeatUntil(
            Supplier<Boolean> action,
            BooleanSupplier exitCondition,
            LongSupplier pollingRateSupplier,
            long timeoutMs) {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            // Check exit condition first
            if (exitCondition.getAsBoolean()) {
                return true;
            }

            // Execute the action
            if (!action.get()) {
                return false; // Action failed
            }

            // Check exit condition again after execution
            if (exitCondition.getAsBoolean()) {
                return true;
            }

            // Get dynamic polling rate and wait
            long waitTime = pollingRateSupplier.getAsLong();
            if (waitTime > 0) {
                if (!sleep(waitTime)) {
                    return false; // Interrupted
                }
            }
        }

        return false; // Timeout reached
    }

    /**
     * Waits until a condition becomes true or timeout is reached.
     *
     * <p>This is useful for waiting for game state changes without repeatedly executing actions.
     *
     * @param condition Condition to wait for
     * @param pollingRateSupplier Supplier that returns wait time in milliseconds for each check
     * @param timeoutMs Maximum time to wait in milliseconds
     * @return {@code true} if condition became true, {@code false} if timeout
     */
    public static boolean waitUntil(
            BooleanSupplier condition, LongSupplier pollingRateSupplier, long timeoutMs) {
        Supplier<Boolean> doNothing = () -> true;
        return repeatUntil(doNothing, condition, pollingRateSupplier, timeoutMs);
    }

    /**
     * Sleeps for the specified duration, handling interruption gracefully.
     *
     * @param milliseconds Time to sleep in milliseconds
     * @return {@code true} if sleep completed normally, {@code false} if interrupted
     */
    public static boolean sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Creates a polling rate supplier for exponential backoff.
     *
     * <p>Starts with an initial delay and doubles it each iteration up to a maximum value.
     *
     * @param initialDelayMs Starting delay in milliseconds
     * @param maxDelayMs Maximum delay in milliseconds
     * @return A supplier that provides exponentially increasing delays
     */
    public static LongSupplier exponentialBackoff(long initialDelayMs, long maxDelayMs) {
        return new LongSupplier() {
            private long currentDelay = initialDelayMs;

            @Override
            public long getAsLong() {
                long result = currentDelay;
                currentDelay = Math.min(currentDelay * 2, maxDelayMs);
                return result;
            }
        };
    }

    /**
     * Creates a polling rate supplier with random jitter.
     *
     * <p>Returns the base delay plus a random amount up to the jitter value.
     *
     * @param baseDelayMs Base delay in milliseconds
     * @param jitterMs Maximum additional random delay in milliseconds
     * @return A supplier that provides delays with random jitter
     */
    public static LongSupplier randomJitter(long baseDelayMs, long jitterMs) {
        return () -> baseDelayMs + (long) (Math.random() * jitterMs);
    }
}
