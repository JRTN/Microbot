package net.runelite.client.plugins.microbot.shank.api.fluent.core.flow;

/**
 * Base interface for all executable actions in the fluent API.
 *
 * <p>Actions represent discrete operations that can be performed in the game, such as clicking
 * buttons, moving items, or interacting with objects. All actions follow a simple contract: they
 * attempt to perform their operation and report success or failure.
 *
 * <p>As a functional interface, Action can be implemented with lambda expressions:
 *
 * <pre>{@code
 * // Using domain actions
 * when(Rs2Inventory.isFull())
 *     .then(bank().open())
 *     .then(bank().depositAll());
 *
 * // Using lambda actions directly
 * when(Rs2Player.getHealthPercent() < 50)
 *     .then(() -> Rs2Inventory.interact("Shark", "Eat"))
 *     .then(() -> {
 *         System.out.println("Ate food");
 *         return true;
 *     });
 *
 * // Mixed usage
 * when(condition)
 *     .then(bank().open())
 *     .then(() -> customLogic())
 *     .then(walker().webWalk(destination));
 * }</pre>
 *
 * <h2>Implementation Contract</h2>
 *
 * <p>Implementations must:
 *
 * <ul>
 *   <li><strong>Be idempotent when possible:</strong> Multiple calls should be safe
 *   <li><strong>Return quickly:</strong> Avoid long-running operations in {@code execute()}
 *   <li><strong>Handle errors gracefully:</strong> Return {@code false} instead of throwing
 *       exceptions
 *   <li><strong>Be stateless:</strong> Each {@code execute()} call should be independent
 * </ul>
 *
 * <h2>Success vs Failure</h2>
 *
 * <p>Actions should return:
 *
 * <ul>
 *   <li><strong>{@code true}:</strong> The intended operation completed successfully
 *   <li><strong>{@code false}:</strong> The operation failed, was not possible, or timed out
 * </ul>
 *
 * <h2>Chaining</h2>
 *
 * <p>Actions can be chained together using the default {@code then()} method:
 *
 * <pre>{@code
 * bank().open()
 *     .then(bank().depositAll("Fish"))     // Creates ActionChain
 *     .then(bank().withdraw("Logs", 27))   // Adds to existing chain
 * }</pre>
 *
 * <p>Chained actions execute sequentially and use fail-fast semantics: if any action in the chain
 * returns {@code false}, execution stops and the entire chain fails.
 *
 * @see ActionChain
 */
@FunctionalInterface
public interface Action {
    /**
     * <strong>INTERNAL USE ONLY:</strong> Executes this action and reports the result.
     *
     * <p><strong>This method is called automatically by the fluent API framework and should
     * not be called directly by user code.</strong> When you use the fluent API with
     * {@code when().then()}, the framework handles execution internally.</p>
     *
     * <h3>Framework Usage vs User Usage</h3>
     *
     * <pre>{@code
     * // CORRECT - User code, execution happens automatically
     * when(inventory().count() == 28)
     *     .then(inventory().drop("Logs"))
     *     .onSuccess(log("Dropped logs"));
     *
     * // INCORRECT - Don't call execute() directly
     * Action dropAction = inventory().drop("Logs");
     * boolean result = dropAction.execute(); // Don't do this!
     *
     * // INCORRECT - Don't add execute() to chains
     * when(condition)
     *     .then(inventory().drop("Logs"))
     *     .execute(); // This method doesn't exist on ActionResult!
     * }</pre>
     *
     * <h3>When Framework Calls Execute</h3>
     * <ul>
     *   <li>{@code SituationClause.then(action)} &rarr; calls {@code action.execute()}</li>
     *   <li>{@code ActionChain.execute()} &rarr; calls {@code execute()} on each action in sequence</li>
     *   <li>{@code ActionResult.repeatUntil(...)} &rarr; calls {@code execute()} repeatedly</li>
     *   <li>{@code SituationResult.repeatUntil(...)} &rarr; calls {@code execute()} repeatedly</li>
     * </ul>
     *
     * <h3>Implementation Requirements</h3>
     *
     * <p>When implementing custom actions, this method must:
     *
     * <ul>
     *   <li><strong>Be quick:</strong> Avoid blocking operations; use timeouts for waits</li>
     *   <li><strong>Be safe:</strong> Handle game state changes gracefully</li>
     *   <li><strong>Be clear:</strong> Return {@code false} for any failure condition</li>
     *   <li><strong>Be consistent:</strong> Same inputs should produce same results</li>
     *   <li><strong>Be idempotent when possible:</strong> Multiple calls should be safe</li>
     * </ul>
     *
     * <h3>Common Failure Scenarios</h3>
     * <ul>
     *   <li>Required game objects or interfaces are not available</li>
     *   <li>Player character cannot perform the action (e.g., insufficient items)</li>
     *   <li>Action times out waiting for expected game state changes</li>
     *   <li>Unexpected game state prevents the action from completing</li>
     * </ul>
     *
     * <h3>Lambda Implementation Example</h3>
     *
     * <pre>{@code
     * // When implementing custom actions as lambdas
     * Action customAction = () -> {
     *     try {
     *         // Perform the actual game operation
     *         boolean gameActionResult = Rs2Inventory.interact("Shark", "Eat");
     *
     *         // Wait for expected result with timeout
     *         boolean success = Rs2Player.waitForHealthIncrease(3000);
     *
     *         return gameActionResult && success;
     *     } catch (Exception e) {
     *         log.error("Action failed: {}", e.getMessage());
     *         return false; // Always return false on exceptions
     *     }
     * };
     *
     * // Framework will call customAction.execute() automatically
     * when(player().getHealthPercent() < 50)
     *     .then(customAction)
     *     .onSuccess(log("Successfully healed"));
     * }</pre>
     *
     * @return {@code true} if the action completed successfully, {@code false} if it failed for any reason
     * @apiNote This method is part of the internal framework API. User code should rely on the fluent
     *          API methods ({@code when().then()}) which handle execution automatically.
     */
    boolean execute();

    /**
     * Creates a chain with this action followed by another action.
     *
     * <p>This is a convenience method that creates an {@link ActionChain} containing this action
     * and the provided next action. The resulting chain will execute this action first, and only
     * execute the next action if this one succeeds.
     *
     * <h3>Execution Semantics</h3>
     *
     * <ul>
     *   <li>This action executes first
     *   <li>If this action returns {@code false}, the chain fails immediately
     *   <li>If this action returns {@code true}, the next action executes
     *   <li>The chain succeeds only if both actions succeed
     * </ul>
     *
     * <h3>Example</h3>
     *
     * <pre>{@code
     * // These two forms are equivalent:
     * Action chain1 = action1.then(action2);
     * ActionChain chain2 = new ActionChain(action1).then(action2);
     *
     * // Mixed lambda and domain actions
     * bank().open()
     *     .then(() -> customBankingLogic())
     *     .then(walker().webWalk(destination));
     * }</pre>
     *
     * @param nextAction The action to execute after this one succeeds. Must not be null.
     * @return A new {@link ActionChain} containing this action and the next action
     * @throws NullPointerException if {@code nextAction} is null
     * @see ActionChain
     */
    default ActionChain then(Action nextAction) {
        return new ActionChain(this).then(nextAction);
    }
}
