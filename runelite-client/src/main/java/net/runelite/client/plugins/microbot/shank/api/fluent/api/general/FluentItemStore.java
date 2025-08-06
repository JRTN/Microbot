package net.runelite.client.plugins.microbot.shank.api.fluent.api.general;

import net.runelite.api.ItemContainer;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Base interface for interacting with item storage containers.
 *
 * <p>This interface provides a unified API for working with any container that can store items,
 * such as the player's inventory, bank, equipment slots, or other storage interfaces. It abstracts
 * common operations like querying items, checking capacity, and performing interactions across
 * different types of item containers.</p>
 *
 * <p>All item identification methods support multiple approaches: by item ID (numeric), by name
 * (string matching), or by custom predicate for complex filtering logic. This flexibility allows
 * for both simple and sophisticated item management strategies.</p>
 *
 * <h2>Container Types</h2>
 * <p>This interface is implemented by various storage systems including:</p>
 * <ul>
 *   <li>Player inventory (28 slots)</li>
 *   <li>Bank storage (variable capacity)</li>
 *   <li>Equipment slots (fixed slots per equipment type)</li>
 *   <li>Shop inventories</li>
 *   <li>Other specialized containers</li>
 * </ul>
 *
 * <h2>Stack Behavior</h2>
 * <p>Item counting and capacity calculations respect the stacking behavior of items:</p>
 * <ul>
 *   <li>Stackable items (coins, runes, arrows) count their full quantity</li>
 *   <li>Non-stackable items count as individual units</li>
 *   <li>Capacity checks consider both occupied slots and stack limits</li>
 * </ul>
 *
 * @see Rs2ItemModel
 * @see ItemContainer
 */
public interface FluentItemStore {

    /**
     * Gets the underlying RuneLite ItemContainer for direct access to container data.
     *
     * <p>This provides low-level access to the container's raw data structure. Most users
     * should prefer the higher-level methods provided by this interface.</p>
     *
     * @return The ItemContainer representing this storage, or null if not available
     */
    ItemContainer getContainer();

    /**
     * Returns a stream of all items currently in this container.
     *
     * <p>Each item is represented as an {@link Rs2ItemModel} containing information
     * about the item's ID, name, quantity, and other properties. Empty container slots
     * are not included in the stream.</p>
     *
     * @return A stream of all items in this container
     * @see #items(Predicate)
     */
    Stream<Rs2ItemModel> items();

    /**
     * Returns a stream of items in this container that match the given predicate.
     *
     * <p>This is a filtered version of {@link #items()} that only includes items
     * satisfying the provided condition. The predicate receives an {@link Rs2ItemModel}
     * and should return true for items that should be included in the stream.</p>
     *
     * @param predicate The condition that items must satisfy to be included
     * @return A stream of items matching the predicate
     * @see #items()
     * @see #getItems(Predicate)
     */
    Stream<Rs2ItemModel> items(Predicate<Rs2ItemModel> predicate);

    /**
     * Returns a list of all items currently in this container.
     *
     * <p>This is a convenience method that collects all items from {@link #items()}
     * into a List. Use this when you need random access to items or when working
     * with APIs that require Lists instead of Streams.</p>
     *
     * @return A list containing all items in this container
     * @see #items()
     * @see #getItems(Predicate)
     */
    List<Rs2ItemModel> getItems();

    /**
     * Returns a list of items in this container that match the given predicate.
     *
     * <p>This is a convenience method that filters items using the predicate and
     * collects them into a List. Use this when you need random access to filtered
     * items or when working with APIs that require Lists.</p>
     *
     * @param item The condition that items must satisfy to be included
     * @return A list of items matching the predicate
     * @see #items(Predicate)
     * @see #getItems()
     */
    List<Rs2ItemModel> getItems(Predicate<Rs2ItemModel> item);

    /**
     * Returns a list of items in this container that match any of the specified IDs.
     *
     * <p>This method searches for items with IDs that match any of the provided values.
     * It's useful for finding multiple types of related items or checking for alternative
     * item variants.</p>
     *
     * @param id The item IDs to search for
     * @return A list of items with matching IDs
     * @see #getItems(String...)
     * @see #getItem(int)
     */
    List<Rs2ItemModel> getItems(int... id);

    /**
     * Returns a list of items in this container that match any of the specified names.
     *
     * <p>This method searches for items with names that match any of the provided values.
     * Name matching is typically case-sensitive and requires exact matches.</p>
     *
     * @param names The item names to search for
     * @return A list of items with matching names
     * @see #getItems(int...)
     * @see #getItem(String)
     */
    List<Rs2ItemModel> getItems(String... names);

    /**
     * Returns the first item in this container that matches the given predicate.
     *
     * <p>If multiple items match the predicate, only the first one found is returned.
     * The order depends on the container's internal organization (typically by slot position).</p>
     *
     * @param item The condition that the item must satisfy
     * @return An Optional containing the first matching item, or empty if none found
     * @see #getItem(int)
     * @see #getItem(String)
     */
    Optional<Rs2ItemModel> getItem(Predicate<Rs2ItemModel> item);

    /**
     * Returns the first item in this container with the specified ID.
     *
     * <p>If multiple items have the same ID (rare for most containers), only the first
     * one found is returned. For stackable items, this returns the stack.</p>
     *
     * @param id The unique item ID to search for
     * @return An Optional containing the first matching item, or empty if none found
     * @see #getItem(String)
     * @see #getItem(Predicate)
     */
    Optional<Rs2ItemModel> getItem(int id);

    /**
     * Returns the first item in this container with the specified name.
     *
     * <p>Name matching is typically case-sensitive and requires an exact match.
     * If multiple items have the same name, only the first one found is returned.</p>
     *
     * @param name The exact item name to search for
     * @return An Optional containing the first matching item, or empty if none found
     * @see #getItem(int)
     * @see #getItem(Predicate)
     */
    Optional<Rs2ItemModel> getItem(String name);

    /**
     * Returns the maximum number of items this container can hold.
     *
     * <p>For containers with fixed slot counts (like inventory), this returns the slot limit.
     * For containers with variable capacity (like some bank implementations), this may
     * return the current maximum or -1 if unlimited.</p>
     *
     * @return The maximum capacity of this container, or -1 if unlimited
     * @see #hasSpace()
     * @see #isFull()
     */
    int capacity();

    /**
     * Counts the total quantity of items matching the given predicate.
     *
     * <p>For stackable items, this counts the total quantity (stack size).
     * For non-stackable items, this counts the number of individual items.
     * This method is useful for checking if you have enough of certain items
     * before performing actions.</p>
     *
     * @param item The predicate to match items against
     * @return The total count/quantity of matching items
     * @see #countItems(int)
     * @see #countItems(String)
     */
    int countItems(Predicate<Rs2ItemModel> item);

    /**
     * Counts the total quantity of items with the specified item ID.
     *
     * <p>For stackable items, this returns the stack quantity. For non-stackable
     * items, this returns the number of individual items with that ID.</p>
     *
     * @param id The unique item ID to count
     * @return The total count/quantity of items with the specified ID
     * @see #countItems(String)
     * @see #countItems(Predicate)
     */
    int countItems(int id);

    /**
     * Counts the total quantity of items with the specified name.
     *
     * <p>The name matching is typically case-sensitive and must match exactly.
     * For stackable items, this returns the stack quantity. For non-stackable
     * items, this returns the number of individual items with that name.</p>
     *
     * @param name The exact item name to count
     * @return The total count/quantity of items with the specified name
     * @see #countItems(int)
     * @see #countItems(Predicate)
     */
    int countItems(String name);

    /**
     * Checks if this container contains any items matching the given predicate.
     *
     * <p>This is equivalent to checking if {@link #countItems(Predicate)} returns
     * a value greater than zero, but may be more efficient for simple existence checks.</p>
     *
     * @param item The predicate to match items against
     * @return true if at least one matching item exists, false otherwise
     * @see #containsItem(int)
     * @see #containsItem(String)
     */
    boolean containsItem(Predicate<Rs2ItemModel> item);

    /**
     * Checks if this container contains any items with the specified ID.
     *
     * <p>This returns true if at least one item with the given ID exists,
     * regardless of quantity.</p>
     *
     * @param id The unique item ID to check for
     * @return true if at least one item with the specified ID exists, false otherwise
     * @see #containsItem(String)
     * @see #containsItem(Predicate)
     */
    boolean containsItem(int id);

    /**
     * Checks if this container contains any items with the specified name.
     *
     * <p>Name matching is typically case-sensitive and requires an exact match.
     * This returns true if at least one item with the given name exists.</p>
     *
     * @param name The exact item name to check for
     * @return true if at least one item with the specified name exists, false otherwise
     * @see #containsItem(int)
     * @see #containsItem(Predicate)
     */
    boolean containsItem(String name);

    /**
     * Checks if this container has at least one empty slot available.
     *
     * <p>This considers both occupied slots and the container's capacity.
     * For stackable items, existing stacks may still accept more items
     * without requiring additional slots.</p>
     *
     * @return true if at least one slot is available, false if container is full
     * @see #hasSpace(int)
     * @see #isFull()
     */
    boolean hasSpace();

    /**
     * Checks if this container has space for the specified number of items.
     *
     * <p>This calculation considers the container's current state, capacity limits,
     * and the stacking behavior of items. The exact logic may vary by container type.</p>
     *
     * @param amount The number of additional items/slots needed
     * @return true if the container can accommodate the specified amount, false otherwise
     * @see #hasSpace()
     * @see #capacity()
     */
    boolean hasSpace(int amount);

    /**
     * Checks if this container is completely full and cannot accept any more items.
     *
     * <p>This considers both slot occupancy and stack limits. A container may be
     * considered full even if some slots contain stackable items that haven't
     * reached their maximum stack size.</p>
     *
     * @return true if the container is full, false if space is available
     * @see #hasSpace()
     * @see #isEmpty()
     */
    boolean isFull();

    /**
     * Checks if this container is completely empty with no items.
     *
     * <p>This returns true only if the container contains no items at all.</p>
     *
     * @return true if the container is empty, false if it contains any items
     * @see #isFull()
     * @see #hasSpace()
     */
    boolean isEmpty();

    /**
     * Creates an action to perform the specified interaction on the first item matching the predicate.
     *
     * <p>The action will search for the first item that satisfies the predicate and attempt
     * to perform the specified interaction on it. Available actions depend on the item type
     * and context.</p>
     *
     * @param target The predicate to match the item for interaction
     * @param action The name of the action to perform (e.g., "Use", "Drop", "Eat")
     * @return An Action that performs the interaction on the matching item
     * @see #interact(String, String)
     * @see #interact(int, String)
     */
    Action interact(Predicate<Rs2ItemModel> target, String action);

    /**
     * Creates an action to perform the specified interaction on the first item with the given name.
     *
     * <p>The action will search for the first item with the specified name and attempt
     * to perform the specified interaction on it. Name matching is typically case-sensitive.</p>
     *
     * @param name The exact item name to interact with
     * @param action The name of the action to perform (e.g., "Use", "Drop", "Eat")
     * @return An Action that performs the interaction on the matching item
     * @see #interact(int, String)
     * @see #interact(Predicate, String)
     */
    Action interact(String name, String action);

    /**
     * Creates an action to perform the specified interaction on the first item with the given ID.
     *
     * <p>The action will search for the first item with the specified ID and attempt
     * to perform the specified interaction on it.</p>
     *
     * @param id The unique item ID to interact with
     * @param action The name of the action to perform (e.g., "Use", "Drop", "Eat")
     * @return An Action that performs the interaction on the matching item
     * @see #interact(String, String)
     * @see #interact(Predicate, String)
     */
    Action interact(int id, String action);
}
