package net.runelite.client.plugins.microbot.shank.api.fluent.api.bank;

import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.function.Predicate;

/**
 * Base interface for bank operations that involve changing item quantities between containers.
 *
 * <p>This interface provides a unified API for operations that move items with specific quantities,
 * such as withdrawing from bank to inventory or depositing from inventory to bank. It abstracts
 * common quantity patterns used in banking operations.</p>
 *
 * <h2>Quantity Options</h2>
 * <p>All operations support three quantity patterns:</p>
 * <ul>
 *   <li><strong>One:</strong> Move exactly one item (or one stack for stackable items)</li>
 *   <li><strong>X Amount:</strong> Move a specific quantity, respecting stack limits</li>
 *   <li><strong>All:</strong> Move the maximum possible quantity of the item</li>
 * </ul>
 *
 * <h2>Item Identification</h2>
 * <p>Items can be identified using multiple approaches:</p>
 * <ul>
 *   <li>By item ID (numeric identifier)</li>
 *   <li>By name (exact string matching)</li>
 *   <li>By predicate (custom filtering logic)</li>
 * </ul>
 *
 * <h2>Stack Behavior</h2>
 * <p>The behavior of quantity operations depends on item stacking properties:</p>
 * <ul>
 *   <li>For stackable items (coins, runes), operations work with the total stack quantity</li>
 *   <li>For non-stackable items (weapons, armor), operations work with individual item counts</li>
 *   <li>Partial operations may occur if insufficient quantity exists or container limits are reached</li>
 * </ul>
 *
 * @see FluentBankWithdraw
 * @see FluentBankDeposit
 * @see Rs2ItemModel
 */
public interface FluentBankItemQuantityChange {

    /**
     * Creates an action to transfer one unit of the first item matching the given predicate.
     *
     * <p>For stackable items, this transfers one complete stack. For non-stackable items,
     * this transfers exactly one individual item. The action will search for the first
     * item that satisfies the predicate condition.</p>
     *
     * @param item The predicate to match the target item
     * @return An Action that transfers one unit of the matching item
     * @see #one(int)
     * @see #one(String)
     */
    Action one(Predicate<Rs2ItemModel> item);

    /**
     * Creates an action to transfer one unit of the item with the specified ID.
     *
     * <p>For stackable items, this transfers one complete stack. For non-stackable items,
     * this transfers exactly one individual item.</p>
     *
     * @param id The unique item ID to transfer
     * @return An Action that transfers one unit of the specified item
     * @see #one(String)
     * @see #one(Predicate)
     */
    Action one(int id);

    /**
     * Creates an action to transfer one unit of the item with the specified name.
     *
     * <p>For stackable items, this transfers one complete stack. For non-stackable items,
     * this transfers exactly one individual item. Name matching is typically case-sensitive
     * and requires exact matches.</p>
     *
     * @param name The exact item name to transfer
     * @return An Action that transfers one unit of the specified item
     * @see #one(int)
     * @see #one(Predicate)
     */
    Action one(String name);

    /**
     * Creates an action to transfer a specific quantity of the first item matching the given predicate.
     *
     * <p>The actual amount transferred may be less than requested if insufficient quantity exists
     * in the source container or if the destination container cannot accommodate the full amount.
     * For non-stackable items, this represents the number of individual items to transfer.</p>
     *
     * @param item The predicate to match the target item
     * @param amount The specific quantity to transfer
     * @return An Action that transfers the specified amount of the matching item
     * @see #x(int, int)
     * @see #x(String, int)
     */
    Action x(Predicate<Rs2ItemModel> item, int amount);

    /**
     * Creates an action to transfer a specific quantity of the item with the specified ID.
     *
     * <p>The actual amount transferred may be less than requested if insufficient quantity exists
     * in the source container or if the destination container cannot accommodate the full amount.</p>
     *
     * @param id The unique item ID to transfer
     * @param amount The specific quantity to transfer
     * @return An Action that transfers the specified amount of the item
     * @see #x(String, int)
     * @see #x(Predicate, int)
     */
    Action x(int id, int amount);

    /**
     * Creates an action to transfer a specific quantity of the item with the specified name.
     *
     * <p>The actual amount transferred may be less than requested if insufficient quantity exists
     * in the source container or if the destination container cannot accommodate the full amount.
     * Name matching is typically case-sensitive and requires exact matches.</p>
     *
     * @param name The exact item name to transfer
     * @param amount The specific quantity to transfer
     * @return An Action that transfers the specified amount of the item
     * @see #x(int, int)
     * @see #x(Predicate, int)
     */
    Action x(String name, int amount);

    /**
     * Creates an action to transfer all available quantity of the first item matching the given predicate.
     *
     * <p>This transfers the maximum possible amount, limited by the source container's contents
     * and the destination container's available capacity. For stackable items, this includes
     * the entire stack. For non-stackable items, this includes all individual items of that type.</p>
     *
     * @param item The predicate to match the target item
     * @return An Action that transfers all available quantity of the matching item
     * @see #all(int)
     * @see #all(String)
     */
    Action all(Predicate<Rs2ItemModel> item);

    /**
     * Creates an action to transfer all available quantity of the item with the specified ID.
     *
     * <p>This transfers the maximum possible amount, limited by the source container's contents
     * and the destination container's available capacity.</p>
     *
     * @param id The unique item ID to transfer
     * @return An Action that transfers all available quantity of the item
     * @see #all(String)
     * @see #all(Predicate)
     */
    Action all(int id);

    /**
     * Creates an action to transfer all available quantity of the item with the specified name.
     *
     * <p>This transfers the maximum possible amount, limited by the source container's contents
     * and the destination container's available capacity. Name matching is typically case-sensitive
     * and requires exact matches.</p>
     *
     * @param name The exact item name to transfer
     * @return An Action that transfers all available quantity of the item
     * @see #all(int)
     * @see #all(Predicate)
     */
    Action all(String name);
}
