package net.runelite.client.plugins.microbot.shank.api.fluent.impl.flow;

import net.runelite.client.plugins.microbot.shank.api.fluent.impl.util.TimingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

/**
 * Represents a sequence of actions that execute in order until one fails or all succeed.
 *
 * <p>ActionChain implements fail-fast execution: if any action in the chain returns {@code false},
 * execution stops immediately and the entire chain is considered failed. Only if all actions
 * succeed does the chain succeed.
 *
 * <h2>Usage</h2>
 *
 * <p>ActionChains are typically created implicitly when chaining actions:
 *
 * <pre>{@code
 * bank().open()
 *     .then(bank().depositAll("Fish"))     // Creates ActionChain
 *     .then(bank().withdraw("Logs", 27))   // Adds to existing chain
 * }</pre>
 *
 * <h2>Execution Behavior</h2>
 *
 * <ul>
 *   <li><strong>Sequential:</strong> Actions execute in the order they were added
 *   <li><strong>Fail-fast:</strong> First action that returns {@code false} stops the chain
 *   <li><strong>All-or-nothing:</strong> Chain succeeds only if every action succeeds
 * </ul>
 *
 * <h2>Wait Operations</h2>
 *
 * <p>Chains can include wait conditions that pause execution until a condition becomes true:
 *
 * <pre>{@code
 * inventory().drop("Logs")
 *     .waitUntil(inventory().contains("Logs))
 *     .waitUntil(() -> !Rs2Player.isAnimating())
 * }</pre>
 */
public class ActionChain implements Action {
    private final List<Action> actions;

    /**
     * Creates a new action chain starting with the given action.
     *
     * @param firstAction The first action in the chain. Must not be null.
     * @throws NullPointerException if {@code firstAction} is null
     */
    public ActionChain(Action firstAction) {
        this.actions = new ArrayList<>();
        this.actions.add(firstAction);
    }

    public static ActionChain start(Action firstAction) {
        return new ActionChain(firstAction);
    }

    /**
     * Adds another action to execute after the current chain.
     *
     * <p>The new action will only execute if all previous actions in the chain have succeeded. If
     * any previous action failed, this action will not execute.
     *
     * @param nextAction The action to add to the chain. Must not be null.
     * @return This ActionChain for continued chaining
     * @throws NullPointerException if {@code nextAction} is null
     */
    @Override
    public ActionChain then(Action nextAction) {
        this.actions.add(nextAction);
        return this;
    }

    /**
     * Adds a wait condition to the chain with dynamic polling rate and custom timeout.
     *
     * <p>The polling rate supplier is called before each wait, allowing for dynamic timing
     * strategies like exponential backoff or adaptive polling based on game state.
     *
     * <h3>Example with exponential backoff</h3>
     *
     * <pre>{@code
     * AtomicLong backoff = new AtomicLong(50);
     * inventory().use("Tinderbox").on("Logs")
     *     .waitUntil(
     *         () -> Rs2Player.isAnimating(),
     *         () -> backoff.getAndUpdate(v -> Math.min(v * 2, 1000)), // 50ms, 100ms, 200ms...
     *         5000
     *     );
     * }</pre>
     *
     * @param condition The condition to wait for. Must not be null.
     * @param pollingRateSupplier Supplier that returns wait time between checks in milliseconds
     * @param timeoutMs Maximum time to wait in milliseconds. Must be positive.
     * @return This ActionChain for continued chaining
     * @throws NullPointerException if {@code condition} or {@code pollingRateSupplier} is null
     * @throws IllegalArgumentException if {@code timeoutMs} is not positive
     */
    public ActionChain waitUntil(
            BooleanSupplier condition, LongSupplier pollingRateSupplier, long timeoutMs) {
        return then(() -> TimingUtils.waitUntil(condition, pollingRateSupplier, timeoutMs));
    }

    /**
     * Adds a wait condition to the chain with dynamic polling rate and default timeout.
     *
     * <p>Uses a default timeout of 5 seconds.
     *
     * @param condition The condition to wait for. Must not be null.
     * @param pollingRateSupplier Supplier that returns wait time between checks in milliseconds
     * @return This ActionChain for continued chaining
     * @throws NullPointerException if {@code condition} or {@code pollingRateSupplier} is null
     */
    public ActionChain waitUntil(BooleanSupplier condition, LongSupplier pollingRateSupplier) {
        return this.waitUntil(condition, pollingRateSupplier, 5000);
    }

    /**
     * Adds a wait condition to the chain with default timeout and polling rate.
     *
     * <p>Execution will pause until the condition becomes {@code true} or the default timeout (5
     * seconds) is reached. Uses a default polling rate of 100ms.
     *
     * @param condition The condition to wait for. Must not be null.
     * @return This ActionChain for continued chaining
     * @throws NullPointerException if {@code condition} is null
     */
    public ActionChain waitUntil(BooleanSupplier condition) {
        return this.waitUntil(condition, 5000);
    }

    /**
     * Adds a wait condition to the chain with custom timeout.
     *
     * <p>Execution will pause until the condition becomes {@code true} or the specified timeout is
     * reached. Uses a default polling rate of 100ms.
     *
     * @param condition The condition to wait for. Must not be null.
     * @param timeoutMs Maximum time to wait in milliseconds. Must be positive.
     * @return This ActionChain for continued chaining
     * @throws NullPointerException if {@code condition} is null
     * @throws IllegalArgumentException if {@code timeoutMs} is not positive
     */
    public ActionChain waitUntil(BooleanSupplier condition, long timeoutMs) {
        return this.waitUntil(condition, 100, timeoutMs);
    }

    /**
     * Adds a wait condition to the chain with custom timeout and polling rate.
     *
     * <p>This is a convenience method that uses a fixed polling rate.
     *
     * @param condition The condition to wait for. Must not be null.
     * @param pollingRateMs Time between condition checks in milliseconds. Must be positive.
     * @param timeoutMs Maximum time to wait in milliseconds. Must be positive.
     * @return This ActionChain for continued chaining
     * @throws NullPointerException if {@code condition} is null
     * @throws IllegalArgumentException if {@code pollingRateMs} or {@code timeoutMs} is not
     *     positive
     */
    public ActionChain waitUntil(BooleanSupplier condition, long pollingRateMs, long timeoutMs) {
        return this.waitUntil(condition, () -> pollingRateMs, timeoutMs);
    }

    /**
     * Repeats the entire action chain until a condition becomes true with dynamic polling rate.
     *
     * <p>The polling rate supplier is called before each wait, allowing for dynamic timing
     * strategies like exponential backoff or adaptive polling based on game state.
     *
     * <h3>Example with exponential backoff</h3>
     *
     * <pre>{@code
     * AtomicLong backoff = new AtomicLong(100);
     * inventory().use("Tinderbox").on("Logs")
     *     .repeatUntil(
     *         () -> !Rs2Inventory.contains("Logs"),
     *         () -> backoff.getAndUpdate(v -> Math.min(v * 2, 5000)), // Double up to 5s max
     *         30000
     *     );
     * }</pre>
     *
     * @param exitCondition Condition that stops the loop when it becomes {@code true}
     * @param pollingRateSupplier Supplier that returns the wait time in milliseconds for each
     *     iteration
     * @param timeoutMs Maximum time to repeat before giving up, in milliseconds
     * @return {@code true} if the exit condition was met, {@code false} if timeout or action
     *     failure
     */
    public ActionChain repeatUntil(
            BooleanSupplier exitCondition, LongSupplier pollingRateSupplier, long timeoutMs) {
        return then(
                () ->
                        TimingUtils.repeatUntil(
                                this::execute, exitCondition, pollingRateSupplier, timeoutMs));
    }

    /**
     * Repeats the entire action chain until a condition becomes true or timeout is reached.
     *
     * <p>This is a convenience method that uses a fixed polling rate.
     *
     * @param exitCondition Condition that stops the loop when it becomes {@code true}
     * @param pollingRateMs Time to wait between iterations in milliseconds
     * @param timeoutMs Maximum time to repeat before giving up, in milliseconds
     * @return {@code true} if the exit condition was met, {@code false} if timeout or action
     *     failure
     */
    public ActionChain repeatUntil(
            BooleanSupplier exitCondition, long pollingRateMs, long timeoutMs) {
        return this.repeatUntil(exitCondition, () -> pollingRateMs, timeoutMs);
    }

    /**
     * Repeats the entire action chain until a condition becomes true with dynamic polling rate and
     * default timeout.
     *
     * <p>Uses a default timeout of 30 seconds.
     *
     * @param exitCondition Condition that stops the loop when it becomes {@code true}
     * @param pollingRateSupplier Supplier that returns the wait time in milliseconds for each
     *     iteration
     * @return {@code true} if the exit condition was met, {@code false} if timeout or action
     *     failure
     */
    public ActionChain repeatUntil(
            BooleanSupplier exitCondition, LongSupplier pollingRateSupplier) {
        return this.repeatUntil(exitCondition, pollingRateSupplier, 30000);
    }

    /**
     * Repeats the entire action chain until a condition becomes true with default timeout.
     *
     * <p>Uses a default timeout of 30 seconds.
     *
     * @param exitCondition Condition that stops the loop when it becomes {@code true}
     * @param pollingRateMs Time to wait between iterations in milliseconds
     * @return {@code true} if the exit condition was met, {@code false} if timeout or action
     *     failure
     */
    public ActionChain repeatUntil(BooleanSupplier exitCondition, long pollingRateMs) {
        return this.repeatUntil(exitCondition, pollingRateMs, 30000);
    }

    /**
     * Executes all actions in the chain sequentially.
     *
     * <p>Actions are executed in the order they were added to the chain. If any action returns
     * {@code false}, execution stops immediately and this method returns {@code false}. Only if all
     * actions return {@code true} does this method return {@code true}.
     *
     * <p><strong>Note:</strong> In the fluent API, this method is typically called automatically by
     * the situation framework. Manual calls are rarely needed.
     *
     * @return {@code true} if all actions in the chain succeeded, {@code false} if any action
     *     failed or if the chain is empty
     */
    @Override
    public boolean execute() {
        for (Action action : actions) {
            if (!action.execute()) {
                return false;
            }
        }
        return true;
    }
}
