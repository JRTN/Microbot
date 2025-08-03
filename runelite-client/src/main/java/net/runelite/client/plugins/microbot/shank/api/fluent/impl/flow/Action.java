package net.runelite.client.plugins.microbot.shank.api.fluent.impl.flow;

/**
 * Base interface for all executable actions in the fluent API.
 *
 * <p>Actions represent discrete operations that can be performed in the game, such as
 * clicking buttons, moving items, or interacting with objects. All actions follow a
 * simple contract: they attempt to perform their operation and report success or failure.</p>
 *
 * <p>As a functional interface, Action can be implemented with lambda expressions:</p>
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
 * <p>Implementations must:</p>
 * <ul>
 *   <li><strong>Be idempotent when possible:</strong> Multiple calls should be safe</li>
 *   <li><strong>Return quickly:</strong> Avoid long-running operations in {@code execute()}</li>
 *   <li><strong>Handle errors gracefully:</strong> Return {@code false} instead of throwing exceptions</li>
 *   <li><strong>Be stateless:</strong> Each {@code execute()} call should be independent</li>
 * </ul>
 *
 * <h2>Success vs Failure</h2>
 * <p>Actions should return:</p>
 * <ul>
 *   <li><strong>{@code true}:</strong> The intended operation completed successfully</li>
 *   <li><strong>{@code false}:</strong> The operation failed, was not possible, or timed out</li>
 * </ul>
 *
 * <h2>Chaining</h2>
 * <p>Actions can be chained together using the default {@code then()} method:</p>
 * <pre>{@code
 * bank().open()
 *     .then(bank().depositAll("Fish"))     // Creates ActionChain
 *     .then(bank().withdraw("Logs", 27))   // Adds to existing chain
 * }</pre>
 *
 * <p>Chained actions execute sequentially and use fail-fast semantics: if any action
 * in the chain returns {@code false}, execution stops and the entire chain fails.</p>
 *
 * @see ActionChain
 */
@FunctionalInterface
public interface Action {

    /**
     * Executes this action and reports the result.
     *
     * <p>This method should attempt to perform the action's intended operation
     * and return {@code true} if successful, {@code false} if the operation
     * failed or could not be completed.</p>
     *
     * <h3>Implementation Guidelines</h3>
     * <ul>
     *   <li><strong>Be quick:</strong> Avoid blocking operations; use timeouts for waits</li>
     *   <li><strong>Be safe:</strong> Handle game state changes gracefully</li>
     *   <li><strong>Be clear:</strong> Return {@code false} for any failure condition</li>
     *   <li><strong>Be consistent:</strong> Same inputs should produce same results</li>
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
     * <h3>Lambda Examples</h3>
     * <pre>{@code
     * // Simple lambda action
     * Action eatFood = () -> Rs2Inventory.interact("Shark", "Eat");
     *
     * // Lambda with custom logic
     * Action customHealing = () -> {
     *     if (Rs2Player.getHealthPercent() < 30) {
     *         return Rs2Inventory.interact("Saradomin brew", "Drink");
     *     } else {
     *         return Rs2Inventory.interact("Shark", "Eat");
     *     }
     * };
     *
     * // Using directly in chains
     * when(needHealing())
     *     .then(() -> Rs2Inventory.interact("Shark", "Eat"))
     *     .onSuccess(() -> { System.out.println("Healed!"); return true; });
     * }</pre>
     *
     * @return {@code true} if the action completed successfully, {@code false} if it
     *         failed for any reason
     */
    boolean execute();

    /**
     * Creates a chain with this action followed by another action.
     *
     * <p>This is a convenience method that creates an {@link ActionChain} containing
     * this action and the provided next action. The resulting chain will execute
     * this action first, and only execute the next action if this one succeeds.</p>
     *
     * <h3>Execution Semantics</h3>
     * <ul>
     *   <li>This action executes first</li>
     *   <li>If this action returns {@code false}, the chain fails immediately</li>
     *   <li>If this action returns {@code true}, the next action executes</li>
     *   <li>The chain succeeds only if both actions succeed</li>
     * </ul>
     *
     * <h3>Example</h3>
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
