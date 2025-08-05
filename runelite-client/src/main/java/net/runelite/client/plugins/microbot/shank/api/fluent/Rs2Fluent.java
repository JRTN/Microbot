package net.runelite.client.plugins.microbot.shank.api.fluent;

import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentAntiban;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentEquipment;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentInventory;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.sleep.FluentTiming;
import net.runelite.client.plugins.microbot.shank.api.fluent.impl.FluentAntibanImpl;
import net.runelite.client.plugins.microbot.shank.api.fluent.impl.FluentEquipmentImpl;
import net.runelite.client.plugins.microbot.shank.api.fluent.impl.FluentInventoryImpl;
import net.runelite.client.plugins.microbot.shank.api.fluent.impl.sleep.FluentTimingImpl;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.SituationClause;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.SituationResult;

/**
 * Main entry point for the RuneScape fluent scripting API.
 *
 * <p>This API provides a declarative, readable way to write automation scripts using a fluent
 * interface that mirrors natural language. Scripts are built around <strong>situations</strong> -
 * conditions that trigger actions when true.
 *
 * <h2>Basic Usage</h2>
 *
 * <pre>{@code
 * // Simple conditional action
 * when(Rs2Inventory.isFull())
 *     .then(bank().open().then(bank().depositAll()))
 *     .onSuccess(log("Banking completed"))
 *     .onFailure(log("Banking failed"));
 * }</pre>
 *
 * <h2>Execution Model</h2>
 *
 * <p><strong>All situations execute immediately</strong> when constructed - there is no deferred
 * execution. The flow is:
 *
 * <ol>
 *   <li>Condition is evaluated when {@code .then()} is called
 *   <li>If condition is true, the action chain executes immediately
 *   <li>{@code .onSuccess()} executes immediately if the action chain succeeded
 *   <li>{@code .onFailure()} executes immediately if the action chain failed
 * </ol>
 *
 * <h2>Success/Failure Hierarchy</h2>
 *
 * <p>Success and failure handlers apply to the <strong>entire situation</strong>, not individual
 * actions within the chain:
 *
 * <pre>{@code
 * when(condition)
 *     .then(action1().then(action2()).then(action3()))  // All actions must succeed
 *     .onSuccess(successAction)   // Executes if ALL actions in the chain succeeded
 *     .onFailure(failureAction);  // Executes if ANY action in the chain failed
 * }</pre>
 *
 * <h2>Result Inspection</h2>
 *
 * <pre>{@code
 * SituationResult result = when(condition).then(action);
 *
 * if (result.didNotHappen()) {
 *     // Condition was false - action never attempted
 * }
 * if (result.failed()) {
 *     // Condition was true but action failed
 * }
 * if (result.succeeded()) {
 *     // Condition was true and action succeeded
 * }
 * }</pre>
 *
 * @see SituationClause
 * @see SituationResult
 */
public interface Rs2Fluent {

    /**
     * Start a conditional flow that executes actions when a condition is true.
     *
     * <p><strong>Execution is immediate:</strong> The condition is used as soon as {@code .then()}
     * is called, and if true, the action chain executes immediately.
     *
     * <h3>Execution Order</h3>
     *
     * <ol>
     *   <li>{@code condition} is checked when {@code .then()} is called
     *   <li>If condition is {@code true}, the action executes immediately
     *   <li>Success/failure handlers execute based on the action result
     *   <li>The {@link SituationResult} is returned with final state
     * </ol>
     *
     * @param condition A boolean value that determines whether the situation should trigger. If
     *     {@code true}, actions will execute when {@code .then()} is called.
     * @return A {@link SituationClause} for chaining actions with {@code .then()}
     */
    static SituationClause when(boolean condition) {
        return new SituationClause(condition);
    }

    static FluentAntiban antiban() {
        return new FluentAntibanImpl();
    }

    /**
     * Creates a fluent interface for performing actions on the player's equipment.
     *
     * <p>This factory method returns an {@link FluentEquipment} instance that provides access to all
     * equipment-related operations in a fluent, chainable manner. The equipment interface allows
     * querying equipment loadout contents and performing common operations like checking, equipping, and
     * unequipping items.
     *
     * @return A new {@link FluentEquipment} instance for fluent inventory operations
     * @see FluentEquipment
     */
    static FluentEquipment equipment() {
        return new FluentEquipmentImpl();
    }

    /**
     * Creates a fluent interface for performing actions on the player's inventory.
     *
     * <p>This factory method returns an {@link FluentInventory} instance that provides access to all
     * inventory-related operations in a fluent, chainable manner. The inventory interface allows
     * querying inventory contents and performing common operations like dropping, counting, and
     * manipulating items.
     *
     * @return A new {@link FluentInventory} instance for fluent inventory operations
     * @see FluentInventory
     */
    static FluentInventory inventory() {
        return new FluentInventoryImpl();
    }

    static FluentTiming timing() {
        return new FluentTimingImpl();
    }
}
