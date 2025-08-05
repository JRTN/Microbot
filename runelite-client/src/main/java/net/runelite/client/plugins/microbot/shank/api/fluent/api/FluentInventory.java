package net.runelite.client.plugins.microbot.shank.api.fluent.api;

import net.runelite.api.ItemContainer;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Fluent API for performing actions on the player's inventory in Old School RuneScape.
 *
 * <p>The inventory is the core storage container where players carry items, limited to 28 slots.
 * This interface provides methods to query inventory contents and perform common inventory
 * operations such as dropping, using, and manipulating items.</p>
 *
 * <h2>Basic Usage</h2>
 * <pre>{@code
 * // Drop items when inventory is full
 * when(() -> inventory().count("Trout") > 10)
 *     .then(inventory().drop("Trout"))
 *     .onSuccess(log("Dropped a trout"));
 *
 * // Drop all items except essentials
 * when(() -> inventory().count() == 28)
 *     .then(inventory().dropAllExcept("Food"))
 *     .onSuccess(log("Cleared inventory space"));
 * }</pre>
 *
 * <h2>Item Identification</h2>
 * <p>Items can be identified in multiple ways:</p>
 * <ul>
 *   <li><strong>By ID:</strong> Using the unique numeric identifier (e.g., 995 for coins)</li>
 *   <li><strong>By Name:</strong> Using the item's display name (e.g., "Shark", "Rune sword")</li>
 *   <li><strong>By Predicate:</strong> Using custom logic to match items based on any property</li>
 * </ul>
 *
 * <h2>Common Patterns</h2>
 *
 * <h3>Food Management</h3>
 * <pre>{@code
 * // Eat food when health is low
 * when(() -> Rs2Player.getHealthPercent() < 50)
 *     .then(inventory().use("Shark"))
 *     .onFailure(log("No food available"));
 * }</pre>
 *
 * <h3>Inventory Cleanup</h3>
 * <pre>{@code
 * // Drop all fish except sharks
 * when(() -> inventory().count(item -> item.name.contains("fish")) > 5)
 *     .then(inventory().dropAllExcept("Shark"))
 *     .onSuccess(log("Kept only sharks"));
 * }</pre>
 *
 * <h3>Item Counting</h3>
 * <pre>{@code
 * // Check if we have enough supplies
 * when(() -> inventory().count("Logs") >= 27)
 *     .then(walker().webWalk(FIREMAKING_LOCATION))
 *     .onSuccess(log("Ready for firemaking"));
 * }</pre>
 *
 * <h2>Stack Behavior</h2>
 * <p>Items in Old School RuneScape can be stackable or non-stackable:</p>
 * <ul>
 *   <li><strong>Stackable items</strong> (coins, runes, arrows) occupy one inventory slot regardless of quantity</li>
 *   <li><strong>Non-stackable items</strong> (weapons, armor, food) each occupy one inventory slot</li>
 *   <li>The {@code count()} methods respect stack quantities for stackable items</li>
 * </ul>
 *
 * @see Action
 * @see Rs2ItemModel
 */
public interface FluentInventory {

    /**
     * Gets the underlying RuneLite ItemContainer for direct access to inventory data.
     *
     * <p>This provides low-level access to the inventory's raw data structure. Most users
     * should prefer the higher-level methods provided by this interface.</p>
     *
     * @return The ItemContainer representing the player's inventory, or null if not available
     */
    ItemContainer getContainer();

    /**
     * Returns a stream of all items currently in the inventory.
     *
     * <p>Each item is represented as an {@link Rs2ItemModel} containing information
     * about the item's ID, name, quantity, and other properties. Empty inventory slots
     * are not included in the stream.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Find all food items
     * List<Rs2ItemModel> foods = inventory().items()
     *     .filter(item -> item.name.contains("Shark") || item.name.contains("Lobster"))
     *     .collect(Collectors.toList());
     *
     * // Count total value of inventory
     * int totalValue = inventory().items()
     *     .mapToInt(item -> item.price * item.quantity)
     *     .sum();
     * }</pre>
     *
     * @return A stream of all items in the inventory
     * @see #items(Predicate)
     */
    Stream<Rs2ItemModel> items();

    /**
     * Returns a stream of items in the inventory that match the given predicate.
     *
     * <p>This is a filtered version of {@link #items()} that only includes items
     * satisfying the provided condition. The predicate receives an {@link Rs2ItemModel}
     * and should return true for items that should be included in the stream.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Find all weapons
     * inventory().items(item -> item.name.contains("sword") || item.name.contains("bow"))
     *     .forEach(weapon -> System.out.println("Found weapon: " + weapon.name));
     *
     * // Find items worth more than 1000 gp
     * List<Rs2ItemModel> valuableItems = inventory().items(item -> item.price > 1000)
     *     .collect(Collectors.toList());
     *
     * // Find stackable items with high quantities
     * inventory().items(item -> item.quantity > 100)
     *     .forEach(item -> System.out.println(item.name + " x" + item.quantity));
     * }</pre>
     *
     * @param predicate The condition that items must satisfy to be included
     * @return A stream of items matching the predicate
     * @see #items()
     * @see #getItems(Predicate)
     */
    Stream<Rs2ItemModel> items(Predicate<Rs2ItemModel> predicate);

    /**
     * Returns a list of all items currently in the inventory.
     *
     * <p>This is a convenience method that collects all items from {@link #items()}
     * into a List. Use this when you need random access to items or when working
     * with APIs that require Lists instead of Streams.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * List<Rs2ItemModel> allItems = inventory().getAllItems();
     * if (!allItems.isEmpty()) {
     *     Rs2ItemModel firstItem = allItems.get(0);
     *     System.out.println("First item: " + firstItem.name);
     * }
     *
     * // Check if inventory has any items
     * boolean hasItems = !inventory().getAllItems().isEmpty();
     * }</pre>
     *
     * @return A list containing all items in the inventory
     * @see #items()
     * @see #getItems(Predicate)
     */
    List<Rs2ItemModel> getAllItems();

    /**
     * Returns a list of items in the inventory that match the given predicate.
     *
     * <p>This is a convenience method that filters items using the predicate and
     * collects them into a List. Use this when you need random access to filtered
     * items or when working with APIs that require Lists.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Get all runes for spellcasting
     * List<Rs2ItemModel> runes = inventory().getItems(item ->
     *     item.name.contains("rune") && !item.name.contains("pickaxe"));
     *
     * // Get all food items
     * List<Rs2ItemModel> food = inventory().getItems(item ->
     *     item.name.equals("Shark") || item.name.equals("Lobster"));
     *
     * if (!food.isEmpty()) {
     *     System.out.println("Have " + food.size() + " different food types");
     * }
     * }</pre>
     *
     * @param predicate The condition that items must satisfy to be included
     * @return A list of items matching the predicate
     * @see #items(Predicate)
     * @see #getAllItems()
     */
    List<Rs2ItemModel> getItems(Predicate<Rs2ItemModel> predicate);

    /**
     * Counts the number of items matching the given predicate.
     *
     * <p>For stackable items, this counts the total quantity (stack size).
     * For non-stackable items, this counts the number of individual items.
     * This method is useful for checking if you have enough of certain items
     * before performing actions.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Count all food items
     * int foodCount = inventory().count(item ->
     *     item.name.contains("Shark") || item.name.contains("Lobster"));
     *
     * // Count valuable items
     * int valuableCount = inventory().count(item -> item.price > 10000);
     *
     * // Check if we have enough supplies
     * when(() -> inventory().count(item -> item.name.contains("Potion")) >= 5)
     *     .then(walker().webWalk(COMBAT_AREA));
     * }</pre>
     *
     * @param target The predicate to match items against
     * @return The total count/quantity of matching items
     * @see #count(int)
     * @see #count(String)
     */
    int count(Predicate<Rs2ItemModel> target);

    /**
     * Counts the number of items with the specified item ID.
     *
     * <p>For stackable items, this returns the stack quantity. For non-stackable
     * items, this returns the number of individual items with that ID.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Count coins (ID: 995)
     * int coinCount = inventory().count(995);
     *
     * // Check if we have enough arrows (ID: 882 for bronze arrows)
     * when(() -> inventory().count(882) < 100)
     *     .then(bank().withdraw(882, 1000))
     *     .onSuccess(log("Restocked arrows"));
     * }</pre>
     *
     * <h3>Finding Item IDs</h3>
     * <p>Item IDs can be found using the RuneLite client's item lookup feature
     * or by checking the Old School RuneScape Wiki.</p>
     *
     * @param id The unique item ID to count
     * @return The total count/quantity of items with the specified ID
     * @see #count(String)
     * @see #count(Predicate)
     */
    int count(int id);

    /**
     * Counts the number of items with the specified name.
     *
     * <p>The name matching is typically case-sensitive and must match exactly.
     * For stackable items, this returns the stack quantity. For non-stackable
     * items, this returns the number of individual items with that name.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Count sharks in inventory
     * int sharkCount = inventory().count("Shark");
     *
     * // Check if we have enough logs for firemaking
     * when(() -> inventory().count("Yew logs") >= 27)
     *     .then(walker().webWalk(FIREMAKING_LOCATION))
     *     .onSuccess(log("Ready to make fires"));
     *
     * // Verify we have required items for a recipe
     * boolean canCook = inventory().count("Raw shark") > 0 &&
     *                   inventory().count("Cooking gauntlets") > 0;
     * }</pre>
     *
     * @param name The exact item name to count
     * @return The total count/quantity of items with the specified name
     * @see #count(int)
     * @see #count(Predicate)
     */
    int count(String name);

    /**
     * Creates an action to drop one item matching the given predicate.
     *
     * <p>This action will find the first item that matches the predicate and drop it
     * to the ground. The action succeeds if an item was found and dropped, and fails
     * if no matching item exists or the drop operation fails.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Drop the first food item when inventory is full
     * when(() -> inventory().count() == 28)
     *     .then(inventory().drop(item -> item.name.contains("Trout")))
     *     .onSuccess(log("Dropped food to make space"));
     *
     * // Drop the lowest value item
     * inventory().drop(item -> item.price < 100)
     *     .onFailure(log("No cheap items to drop"));
     * }</pre>
     *
     * <h3>Drop Mechanics</h3>
     * <ul>
     *   <li>Dropped items appear on the ground at the player's location</li>
     *   <li>Items are visible to other players after 60 seconds (or immediately in PvP areas)</li>
     *   <li>Items disappear after 3 minutes if not picked up</li>
     *   <li>Some items (like untradeables) may be destroyed instead of dropped</li>
     * </ul>
     *
     * @param target The predicate to match the item to drop
     * @return An Action that drops one matching item
     * @see #drop(int)
     * @see #drop(String)
     * @see #dropAll(Predicate)
     */
    Action drop(Predicate<Rs2ItemModel> target);

    /**
     * Creates an action to drop one item with the specified ID.
     *
     * <p>For stackable items, this drops the entire stack. For non-stackable items,
     * this drops one item with the specified ID.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Drop bronze arrows (ID: 882) when inventory is full
     * when(() -> inventory().count(882) > 1000)
     *     .then(inventory().drop(882))
     *     .onSuccess(log("Dropped some arrows"));
     *
     * // Drop an unwanted item
     * inventory().drop(1234)  // Replace with actual item ID
     *     .then(log("Dropped unwanted item"));
     * }</pre>
     *
     * @param id The item ID to drop
     * @return An Action that drops one item with the specified ID
     * @see #drop(String)
     * @see #drop(Predicate)
     * @see #dropAll(int)
     */
    Action drop(int id);

    /**
     * Creates an action to drop one item with the specified name.
     *
     * <p>For stackable items, this drops the entire stack. For non-stackable items,
     * this drops one item with the specified name.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Drop trout when inventory gets full
     * when(() -> inventory().count() == 28)
     *     .then(inventory().drop("Trout"))
     *     .onSuccess(log("Dropped a trout"));
     *
     * // Drop unwanted quest items
     * inventory().drop("Burnt fish")
     *     .then(log("Cleaned up inventory"));
     * }</pre>
     *
     * @param name The exact item name to drop
     * @return An Action that drops one item with the specified name
     * @see #drop(int)
     * @see #drop(Predicate)
     * @see #dropAll(String)
     */
    Action drop(String name);

    /**
     * Creates an action to drop items matching the predicate until only the specified number remain.
     *
     * <p>This action will repeatedly drop items that match the predicate until the total
     * count of matching items is reduced to the specified remaining amount. This is useful
     * for maintaining a specific quantity of items while dropping excess.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Keep only 5 sharks, drop the rest
     * inventory().dropUntilRemain(item -> item.name.equals("Shark"), 5)
     *     .onSuccess(log("Kept 5 sharks, dropped excess"));
     *
     * // Keep only 1000 arrows, drop extras
     * inventory().dropUntilRemain(item -> item.name.contains("arrow"), 1000)
     *     .onSuccess(log("Maintained arrow supply"));
     *
     * // Keep only cheap food items, up to 10 total
     * inventory().dropUntilRemain(item ->
     *     item.name.contains("Bread") && item.price < 50, 10);
     * }</pre>
     *
     * <h3>Behavior Notes</h3>
     * <ul>
     *   <li>If current count is already at or below the target, no items are dropped</li>
     *   <li>For stackable items, partial stacks may be dropped if needed</li>
     *   <li>The action succeeds if the target count is achieved or no more items can be dropped</li>
     * </ul>
     *
     * @param target The predicate to match items for dropping
     * @param remaining The desired number of matching items to keep
     * @return An Action that drops excess items until the target count is reached
     * @see #drop(Predicate)
     * @see #dropAll(Predicate)
     */
    Action dropUntilRemain(Predicate<Rs2ItemModel> target, int remaining);

    /**
     * Creates an action to drop all items matching the given predicate.
     *
     * <p>This action will drop every item in the inventory that satisfies the predicate.
     * It's useful for clearing inventory space or removing unwanted items.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Drop all low-value items
     * inventory().dropAll(item -> item.price < 100)
     *     .onSuccess(log("Cleaned inventory of junk"));
     *
     * // Drop all food after finishing combat
     * inventory().dropAll(item -> item.name.contains("Shark") || item.name.contains("Lobster"))
     *     .onSuccess(log("Dropped all food"));
     *
     * // Drop all noted items (can't be used)
     * inventory().dropAll(item -> item.noted)
     *     .onSuccess(log("Dropped all noted items"));
     * }</pre>
     *
     * @param target The predicate to match items for dropping
     * @return An Action that drops all matching items
     * @see #dropAll(int)
     * @see #dropAll(String)
     * @see #drop(Predicate)
     */
    Action dropAll(Predicate<Rs2ItemModel> target);

    /**
     * Creates an action to drop all items with the specified ID.
     *
     * <p>For stackable items, this drops the entire stack. For non-stackable items,
     * this drops all items with the specified ID.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Drop all bronze arrows (ID: 882)
     * inventory().dropAll(882)
     *     .onSuccess(log("Dropped all bronze arrows"));
     *
     * // Clear inventory of specific unwanted items
     * inventory().dropAll(1234)  // Replace with actual item ID
     *     .then(log("Cleared unwanted items"));
     * }</pre>
     *
     * @param id The item ID to drop all of
     * @return An Action that drops all items with the specified ID
     * @see #dropAll(String)
     * @see #dropAll(Predicate)
     * @see #drop(int)
     */
    Action dropAll(int id);

    /**
     * Creates an action to drop all items with the specified name.
     *
     * <p>For stackable items, this drops the entire stack. For non-stackable items,
     * this drops all items with the specified name.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Drop all trout after fishing
     * inventory().dropAll("Trout")
     *     .onSuccess(log("Dropped all trout"));
     *
     * // Clear inventory of specific food
     * inventory().dropAll("Burnt fish")
     *     .then(log("Removed all burnt food"));
     * }</pre>
     *
     * @param name The exact item name to drop all of
     * @return An Action that drops all items with the specified name
     * @see #dropAll(int)
     * @see #dropAll(Predicate)
     * @see #drop(String)
     */
    Action dropAll(String name);

    /**
     * Creates an action to drop all items except those matching the given predicate.
     *
     * <p>This action will drop every item in the inventory that does NOT satisfy the
     * predicate, effectively keeping only the items that match the condition. This is
     * useful for inventory cleanup while preserving essential items.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Keep only food and drop everything else
     * inventory().dropAllExcept(item ->
     *     item.name.contains("Shark") || item.name.contains("Lobster"))
     *     .onSuccess(log("Kept only food"));
     *
     * // Keep only valuable items (worth more than 1000 gp)
     * inventory().dropAllExcept(item -> item.price > 1000)
     *     .onSuccess(log("Kept only valuable items"));
     *
     * // Keep only combat equipment
     * inventory().dropAllExcept(item ->
     *     item.name.contains("sword") ||
     *     item.name.contains("armor") ||
     *     item.name.contains("potion"))
     *     .onSuccess(log("Kept only combat gear"));
     * }</pre>
     *
     * @param exclude The predicate defining which items to keep (exclude from dropping)
     * @return An Action that drops all items except those matching the predicate
     * @see #dropAllExcept(int)
     * @see #dropAllExcept(String)
     * @see #dropAll(Predicate)
     */
    Action dropAllExcept(Predicate<Rs2ItemModel> exclude);

    /**
     * Creates an action to drop all items except those with the specified ID.
     *
     * <p>This action will drop every item in the inventory except items with the
     * specified ID. Useful for keeping only one type of item while clearing everything else.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Keep only coins (ID: 995), drop everything else
     * inventory().dropAllExcept(995)
     *     .onSuccess(log("Kept only coins"));
     *
     * // Keep only a specific type of food
     * inventory().dropAllExcept(385)  // Shark ID
     *     .onSuccess(log("Kept only sharks"));
     * }</pre>
     *
     * @param id The item ID to keep (exclude from dropping)
     * @return An Action that drops all items except those with the specified ID
     * @see #dropAllExcept(String)
     * @see #dropAllExcept(Predicate)
     * @see #dropAll(int)
     */
    Action dropAllExcept(int id);

    /**
     * Creates an action to drop all items except those with the specified name.
     *
     * <p>This action will drop every item in the inventory except items with the
     * specified name. Useful for keeping only one type of item while clearing everything else.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Keep only sharks, drop everything else
     * inventory().dropAllExcept("Shark")
     *     .onSuccess(log("Kept only sharks"));
     *
     * // Keep only a specific tool
     * inventory().dropAllExcept("Dragon pickaxe")
     *     .onSuccess(log("Kept only pickaxe"));
     * }</pre>
     *
     * @param name The exact item name to keep (exclude from dropping)
     * @return An Action that drops all items except those with the specified name
     * @see #dropAllExcept(int)
     * @see #dropAllExcept(Predicate)
     * @see #dropAll(String)
     */
    Action dropAllExcept(String name);
}
