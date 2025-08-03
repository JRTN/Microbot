package net.runelite.client.plugins.microbot.shank.api.fluent.impl.action.wait;

import net.runelite.client.plugins.microbot.shank.api.fluent.impl.flow.Action;

import java.util.function.BooleanSupplier;

/**
 * Action that waits for a condition to become true
 */
public class WaitAction implements Action {
    private final BooleanSupplier condition;
    private final long timeoutMs;
    private final long pollIntervalMs;

    public WaitAction(BooleanSupplier condition) {
        this(condition, 5000); // 5 second default timeout
    }

    public WaitAction(BooleanSupplier condition, long timeoutMs) {
        this(condition, timeoutMs, 50); // 50ms poll interval
    }

    public WaitAction(BooleanSupplier condition, long timeoutMs, long pollIntervalMs) {
        this.condition = condition;
        this.timeoutMs = timeoutMs;
        this.pollIntervalMs = pollIntervalMs;
    }

    @Override
    public boolean execute() {
        long startTime = System.currentTimeMillis();

        while (!condition.getAsBoolean()) {
            if (System.currentTimeMillis() - startTime > timeoutMs) {
                return false; // Timeout
            }

            try {
                Thread.sleep(pollIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return true;
    }
}
