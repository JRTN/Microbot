package net.runelite.client.plugins.microbot.shank.api.fluent.impl.flow;

import net.runelite.client.plugins.microbot.shank.api.fluent.impl.util.TimingUtils;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

/**
 * Represents the result of executing an action, with support for chaining more actions.
 *
 * <p>ActionResult is returned when actions execute and provides methods for:</p>
 * <ul>
 *   <li>Chaining additional actions with {@code then()}</li>
 *   <li>Adding wait conditions with {@code waitUntil()}</li>
 *   <li>Repeating until conditions with {@code repeatUntil()}</li>
 *   <li>Success/failure handling</li>
 * </ul>
 *
 * <h2>Action Chaining</h2>
 * <p>Actions can be chained together in sequences where each action only executes
 * if the previous action succeeded:</p>
 * <pre>{@code
 * when(needToBank())
 *     .then(bank().open())
 *     .then(bank().depositAll("Fish"))     // Only executes if open() succeeded
 *     .then(bank().withdraw("Logs", 27))   // Only executes if depositAll() succeeded
 *     .onSuccess(log("Banking completed"))
 *     .onFailure(log("Banking failed"));
 * }</pre>
 *
 * <h2>Wait Operations</h2>
 * <p>Wait conditions can be inserted into action chains to pause execution
 * until specific conditions become true:</p>
 * <pre>{@code
 * when(needToFight())
 *     .then(combat().attack("Goblin"))
 *     .waitUntil(() -> Rs2Player.isInCombat())      // Wait for combat to start
 *     .waitUntil(() -> !Rs2Player.isInCombat())     // Wait for combat to end
 *     .then(inventory().eat("Food"));
 * }</pre>
 *
 * <h2>Repeat Operations</h2>
 * <p>Actions can be repeated until exit conditions are met, useful for
 * resource gathering or repetitive tasks:</p>
 * <pre>{@code
 * when(canMine())
 *     .then(mining().clickRock("Iron ore"))
 *     .repeatUntil(() -> Rs2Inventory.isFull(), 1000, 30000);
 * }</pre>
 */
public class ActionResult {
    private final boolean successful;
    private final Action lastAction;

    /**
     * Creates a new ActionResult representing the outcome of an action execution.
     *
     * @param successful Whether the action executed successfully
     * @param lastAction The action that was executed, used for potential repeating operations
     */
    public ActionResult(boolean successful, Action lastAction) {
        this.successful = successful;
        this.lastAction = lastAction;
    }

    /**
     * Chains another action to execute if the previous action succeeded.
     *
     * <p>The next action will only execute if all previous actions in the chain
     * have succeeded. This implements fail-fast behavior where the first failure
     * stops the entire chain.</p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * when(needHealing())
     *     .then(inventory().open())           // Execute first
     *     .then(inventory().eat("Shark"))     // Only if open() succeeded
     *     .then(inventory().close())          // Only if eat() succeeded
     *     .onSuccess(log("Healed successfully"));
     * }</pre>
     *
     * @param nextAction The action to execute if the previous action succeeded. Must not be null.
     * @return A new ActionResult representing the outcome of the next action
     * @throws NullPointerException if {@code nextAction} is null
     */
    public ActionResult then(Action nextAction) {
        if (successful) {
            boolean nextSuccessful = nextAction.execute();
            return new ActionResult(nextSuccessful, nextAction);
        } else {
            return new ActionResult(false, nextAction);
        }
    }

    /**
     * Adds a wait condition to the action chain with default timeout and polling rate.
     *
     * <p>Execution will pause until the condition becomes {@code true} or the default
     * timeout (5 seconds) is reached. Uses a default polling rate of 100ms to check
     * the condition repeatedly.</p>
     *
     * <p>The wait will only execute if all previous actions in the chain succeeded.
     * If any previous action failed, the wait is skipped and the chain remains failed.</p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * when(needToAttack())
     *     .then(combat().attack("Monster"))
     *     .waitUntil(() -> Rs2Player.isInCombat())  // Wait up to 5 seconds for combat to start
     *     .then(inventory().eat("Food"));
     * }</pre>
     *
     * @param condition The condition to wait for. Must not be null.
     * @return A new ActionResult representing the outcome of the wait operation
     * @throws NullPointerException if {@code condition} is null
     */
    public ActionResult waitUntil(BooleanSupplier condition) {
        return waitUntil(condition, 100, 5000);
    }

    /**
     * Adds a wait condition to the action chain with custom timeout and default polling rate.
     *
     * <p>Execution will pause until the condition becomes {@code true} or the specified
     * timeout is reached. Uses a default polling rate of 100ms to check the condition
     * repeatedly.</p>
     *
     * <p>The wait will only execute if all previous actions in the chain succeeded.
     * If the timeout is reached before the condition becomes true, the wait is
     * considered failed and the entire chain fails.</p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * when(needToBank())
     *     .then(bank().open())
     *     .waitUntil(() -> Rs2Bank.isOpen(), 10000)  // Wait up to 10 seconds for bank to open
     *     .then(bank().depositAll("Fish"));
     * }</pre>
     *
     * @param condition The condition to wait for. Must not be null.
     * @param timeoutMs Maximum time to wait in milliseconds. Must be positive.
     * @return A new ActionResult representing the outcome of the wait operation
     * @throws NullPointerException if {@code condition} is null
     * @throws IllegalArgumentException if {@code timeoutMs} is not positive
     */
    public ActionResult waitUntil(BooleanSupplier condition, long timeoutMs) {
        return waitUntil(condition, 100, timeoutMs);
    }

    /**
     * Adds a wait condition to the action chain with custom timeout and polling rate.
     *
     * <p>Execution will pause until the condition becomes {@code true} or the specified
     * timeout is reached. The condition is checked at the specified polling interval.</p>
     *
     * <p>The wait will only execute if all previous actions in the chain succeeded.
     * If the timeout is reached before the condition becomes true, the wait is
     * considered failed and the entire chain fails.</p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * when(needToHeal())
     *     .then(inventory().eat("Shark"))
     *     .waitUntil(() -> Rs2Player.getHealthPercent() > 80, 500, 15000)  // Check every 500ms for 15s
     *     .then(combat().resumeFighting());
     * }</pre>
     *
     * @param condition The condition to wait for. Must not be null.
     * @param pollingRateMs Time between condition checks in milliseconds. Must be positive.
     * @param timeoutMs Maximum time to wait in milliseconds. Must be positive.
     * @return A new ActionResult representing the outcome of the wait operation
     * @throws NullPointerException if {@code condition} is null
     * @throws IllegalArgumentException if {@code pollingRateMs} or {@code timeoutMs} is not positive
     */
    public ActionResult waitUntil(BooleanSupplier condition, long pollingRateMs, long timeoutMs) {
        return waitUntil(condition, () -> pollingRateMs, timeoutMs);
    }

    /**
     * Adds a wait condition to the action chain with dynamic polling rate and custom timeout.
     *
     * <p>The polling rate supplier is called before each wait, allowing for dynamic timing
     * strategies like exponential backoff or adaptive polling based on game state.</p>
     *
     * <p>The wait will only execute if all previous actions in the chain succeeded.
     * If the timeout is reached before the condition becomes true, the wait is
     * considered failed and the entire chain fails.</p>
     *
     * <h3>Example with exponential backoff</h3>
     * <pre>{@code
     * AtomicLong backoff = new AtomicLong(50);
     * when(needToConnect())
     *     .then(network().connect())
     *     .waitUntil(
     *         () -> network().isConnected(),
     *         () -> backoff.getAndUpdate(v -> Math.min(v * 2, 1000)), // 50ms, 100ms, 200ms...
     *         30000
     *     )
     *     .then(game().login());
     * }</pre>
     *
     * @param condition The condition to wait for. Must not be null.
     * @param pollingRateSupplier Supplier that returns wait time between checks in milliseconds. Must not be null.
     * @param timeoutMs Maximum time to wait in milliseconds. Must be positive.
     * @return A new ActionResult representing the outcome of the wait operation
     * @throws NullPointerException if {@code condition} or {@code pollingRateSupplier} is null
     * @throws IllegalArgumentException if {@code timeoutMs} is not positive
     */
    public ActionResult waitUntil(BooleanSupplier condition, LongSupplier pollingRateSupplier, long timeoutMs) {
        if (successful) {
            boolean waitSuccessful = TimingUtils.waitUntil(condition, pollingRateSupplier, timeoutMs);
            return new ActionResult(waitSuccessful, () -> waitSuccessful);
        } else {
            return new ActionResult(false, () -> false);
        }
    }

    /**
     * Repeats the last action until a condition becomes true with dynamic polling rate.
     *
     * <p>The last action in the chain will be executed repeatedly until the exit condition
     * becomes {@code true} or the timeout is reached. The polling rate supplier is called
     * before each wait, allowing for dynamic timing strategies.</p>
     *
     * <p>This method will only execute if all previous actions in the chain succeeded.
     * If any previous action failed, this method returns {@code false} immediately.</p>
     *
     * <h3>Example with exponential backoff</h3>
     * <pre>{@code
     * AtomicLong delay = new AtomicLong(100);
     * boolean success = when(canMine())
     *     .then(mining().clickRock("Iron ore"))
     *     .repeatUntil(
     *         () -> Rs2Inventory.isFull(),
     *         () -> delay.getAndUpdate(v -> Math.min(v * 2, 2000)), // Exponential backoff
     *         60000  // 1 minute timeout
     *     );
     * }</pre>
     *
     * <h3>Example with adaptive polling</h3>
     * <pre>{@code
     * boolean success = when(needHealing())
     *     .then(inventory().eat("Shark"))
     *     .repeatUntil(
     *         () -> Rs2Player.getHealthPercent() >= 80,
     *         () -> Rs2Player.isInCombat() ? 200 : 1000,  // Faster polling in combat
     *         30000
     *     );
     * }</pre>
     *
     * @param exitCondition Condition that stops the loop when it becomes {@code true}. Must not be null.
     * @param pollingRateSupplier Supplier that returns the wait time in milliseconds for each iteration. Must not be null.
     * @param timeoutMs Maximum time to repeat before giving up, in milliseconds. Must be positive.
     * @return {@code true} if the exit condition was met, {@code false} if timeout, action failure, or previous chain failure
     * @throws NullPointerException if {@code exitCondition} or {@code pollingRateSupplier} is null
     * @throws IllegalArgumentException if {@code timeoutMs} is not positive
     */
    public boolean repeatUntil(BooleanSupplier exitCondition, LongSupplier pollingRateSupplier, long timeoutMs) {
        if (!successful) return false;
        return TimingUtils.repeatUntil(lastAction::execute, exitCondition, pollingRateSupplier, timeoutMs);
    }

    /**
     * Repeats the last action until a condition becomes true with fixed polling rate.
     *
     * <p>The last action in the chain will be executed repeatedly until the exit condition
     * becomes {@code true} or the timeout is reached. Uses a fixed polling rate between
     * iterations.</p>
     *
     * <p>This method will only execute if all previous actions in the chain succeeded.
     * If any previous action failed, this method returns {@code false} immediately.</p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * boolean success = when(canFish())
     *     .then(fishing().clickSpot("Salmon"))
     *     .repeatUntil(
     *         () -> Rs2Inventory.isFull(),
     *         1500,  // Check every 1.5 seconds
     *         300000 // 5 minute timeout
     *     );
     * }</pre>
     *
     * @param exitCondition Condition that stops the loop when it becomes {@code true}. Must not be null.
     * @param pollingRateMs Time to wait between iterations in milliseconds. Must be positive.
     * @param timeoutMs Maximum time to repeat before giving up, in milliseconds. Must be positive.
     * @return {@code true} if the exit condition was met, {@code false} if timeout, action failure, or previous chain failure
     * @throws NullPointerException if {@code exitCondition} is null
     * @throws IllegalArgumentException if {@code pollingRateMs} or {@code timeoutMs} is not positive
     */
    public boolean repeatUntil(BooleanSupplier exitCondition, long pollingRateMs, long timeoutMs) {
        return this.repeatUntil(exitCondition, () -> pollingRateMs, timeoutMs);
    }

    /**
     * Repeats the last action until a condition becomes true with dynamic polling rate and default timeout.
     *
     * <p>Uses a default timeout of 30 seconds. The polling rate supplier allows for
     * dynamic timing strategies like exponential backoff or adaptive polling based
     * on game state.</p>
     *
     * <p>This method will only execute if all previous actions in the chain succeeded.
     * If any previous action failed, this method returns {@code false} immediately.</p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * boolean success = when(canCraft())
     *     .then(crafting().makePotions())
     *     .repeatUntil(
     *         () -> !Rs2Inventory.contains("Herb"),
     *         TimingUtils.randomJitter(800, 200)  // 600-1000ms random intervals
     *     );
     * }</pre>
     *
     * @param exitCondition Condition that stops the loop when it becomes {@code true}. Must not be null.
     * @param pollingRateSupplier Supplier that returns the wait time in milliseconds for each iteration. Must not be null.
     * @return {@code true} if the exit condition was met, {@code false} if timeout, action failure, or previous chain failure
     * @throws NullPointerException if {@code exitCondition} or {@code pollingRateSupplier} is null
     */
    public boolean repeatUntil(BooleanSupplier exitCondition, LongSupplier pollingRateSupplier) {
        return this.repeatUntil(exitCondition, pollingRateSupplier, 30000);
    }

    /**
     * Repeats the last action until a condition becomes true with default timeout and polling rate.
     *
     * <p>Uses default values of 1 second polling rate and 30 second timeout. This is
     * a convenience method for simple repetition scenarios.</p>
     *
     * <p>This method will only execute if all previous actions in the chain succeeded.
     * If any previous action failed, this method returns {@code false} immediately.</p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * boolean success = when(canChop())
     *     .then(woodcutting().clickTree("Oak"))
     *     .repeatUntil(() -> Rs2Inventory.isFull());  // Use defaults: 1s polling, 30s timeout
     * }</pre>
     *
     * @param exitCondition Condition that stops the loop when it becomes {@code true}. Must not be null.
     * @return {@code true} if the exit condition was met, {@code false} if timeout, action failure, or previous chain failure
     * @throws NullPointerException if {@code exitCondition} is null
     */
    public boolean repeatUntil(BooleanSupplier exitCondition) {
        return this.repeatUntil(exitCondition, 1000, 30000);
    }

    /**
     * Defines an action to execute if all previous actions in the chain succeeded.
     *
     * <p>The success action executes immediately when this method is called, but only if
     * the entire action chain up to this point has succeeded. If any action in the chain
     * failed, the success action is ignored.</p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * when(needToBank())
     *     .then(bank().open())
     *     .then(bank().depositAll("Fish"))
     *     .onSuccess(() -> {
     *         log.info("Successfully banked all fish");
     *         return true;
     *     })
     *     .onFailure(() -> {
     *         log.warn("Failed to bank fish");
     *         return true;
     *     });
     * }</pre>
     *
     * @param successAction The action to execute on success. Must not be null.
     * @return This ActionResult for method chaining
     * @throws NullPointerException if {@code successAction} is null
     */
    public ActionResult onSuccess(Action successAction) {
        if (successful) {
            successAction.execute();
        }
        return this;
    }

    /**
     * Defines an action to execute if any action in the chain failed.
     *
     * <p>The failure action executes immediately when this method is called, but only if
     * any action in the chain up to this point has failed. If all actions succeeded,
     * the failure action is ignored.</p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * when(needToCook())
     *     .then(cooking().useItemOn("Raw fish", "Fire"))
     *     .waitUntil(() -> Rs2Player.isAnimating())
     *     .onSuccess(() -> {
     *         log.info("Started cooking successfully");
     *         return true;
     *     })
     *     .onFailure(() -> {
     *         log.warn("Failed to start cooking - retrying next cycle");
     *         return true;
     *     });
     * }</pre>
     *
     * @param failureAction The action to execute on failure. Must not be null.
     * @return This ActionResult for method chaining
     * @throws NullPointerException if {@code failureAction} is null
     */
    public ActionResult onFailure(Action failureAction) {
        if (!successful) {
            failureAction.execute();
        }
        return this;
    }

    /**
     * Checks if all actions in the chain completed successfully.
     *
     * <p>This method returns {@code true} when all actions that were executed
     * in the chain returned {@code true}. If any action failed or returned
     * {@code false}, this method returns {@code false}.</p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * ActionResult result = when(canTrade())
     *     .then(trading().openTrade("Player123"))
     *     .then(trading().offer("Gold", 1000));
     *
     * if (result.succeeded()) {
     *     log.info("Trade setup successful");
     * } else {
     *     log.warn("Trade setup failed");
     * }
     * }</pre>
     *
     * @return {@code true} if all actions succeeded, {@code false} if any action failed
     */
    public boolean succeeded() {
        return successful;
    }

    /**
     * Checks if any action in the chain failed.
     *
     * <p>This method returns {@code true} when any action that was executed
     * in the chain returned {@code false}. If all actions succeeded, this
     * method returns {@code false}.</p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * ActionResult result = when(needSupplies())
     *     .then(shop().open("General Store"))
     *     .then(shop().buy("Bread", 10));
     *
     * if (result.failed()) {
     *     log.warn("Shopping failed - will try different approach");
     *     // Handle failure case
     * }
     * }</pre>
     *
     * @return {@code true} if any action failed, {@code false} if all actions succeeded
     */
    public boolean failed() {
        return !successful;
    }
}
