package net.runelite.client.plugins.microbot.shank.api.fluent.api;

import net.runelite.api.GameObject;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.general.FluentItemStore;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.function.Predicate;

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
public interface FluentInventory extends FluentItemStore {

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

    /**
     * Creates an action to use (activate) an item matching the given predicate.
     *
     * <p>This method finds the first item that matches the predicate and uses it.
     * The "use" action is typically the primary action for consumable items like
     * food, potions, or tools. This is equivalent to right-clicking and selecting
     * the first option, or left-clicking for most items.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Use any food item when health is low
     * when(() -> player().getHealthPercent() < 50)
     *     .then(inventory().use(Rs2ItemModel::isFood))
     *     .onSuccess(log("Used food item"));
     *
     * // Use the most expensive potion
     * inventory().use(item -> item.getName().contains("potion") && item.getPrice() > 1000)
     *     .onSuccess(log("Used expensive potion"));
     *
     * // Use teleport items
     * inventory().use(item -> item.getName().contains("teleport"))
     *     .onSuccess(log("Used teleport"));
     * }</pre>
     *
     * @param target The predicate to match the item to use
     * @return An Action that uses the matching item
     * @see #use(int)
     * @see #use(String)
     * @see Rs2ItemModel
     */
    Action use(Predicate<Rs2ItemModel> target);

    /**
     * Creates an action to use (activate) an item with the specified ID.
     *
     * <p>This method finds an item with the given ID and uses it. For consumable
     * items, this typically consumes one item from the stack.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Use a shark (ID: 385)
     * inventory().use(385)
     *     .onSuccess(log("Ate shark"))
     *     .onFailure(log("No shark available"));
     *
     * // Use a prayer potion (ID: 2434)
     * inventory().use(2434)
     *     .onSuccess(log("Drank prayer potion"));
     *
     * // Use a teleport tablet
     * inventory().use(8007) // Varrock teleport tablet ID
     *     .onSuccess(log("Teleported to Varrock"));
     * }</pre>
     *
     * @param id The item ID to use
     * @return An Action that uses the item with the specified ID
     * @see #use(String)
     * @see #use(Predicate)
     * @see Rs2ItemModel#getId()
     */
    Action use(int id);

    /**
     * Creates an action to use (activate) an item with the specified name.
     *
     * <p>This method finds an item with the given name and uses it. The name
     * matching is case-sensitive and must match exactly.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Use a shark for healing
     * inventory().use("Shark")
     *     .onSuccess(log("Ate shark"))
     *     .onFailure(log("No shark available"));
     *
     * // Use a specific potion
     * inventory().use("Super strength(4)")
     *     .onSuccess(log("Drank super strength potion"));
     *
     * // Use teleport items
     * inventory().use("House teleport")
     *     .onSuccess(log("Teleported home"));
     * }</pre>
     *
     * @param name The exact item name to use
     * @return An Action that uses the item with the specified name
     * @see #use(int)
     * @see #use(Predicate)
     * @see Rs2ItemModel#getName()
     */
    Action use(String name);

    /**
     * Creates an action to use one item on another item in the inventory.
     *
     * <p>This method finds the first item matching each predicate and uses the first
     * item on the second item. This is commonly used for combining items, such as
     * using thread on a needle, or using one item to create another.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Use thread on needle for crafting
     * inventory().useOn(
     *     item -> item.getName().equals("Thread"),
     *     item -> item.getName().equals("Needle")
     * ).onSuccess(log("Combined thread with needle"));
     *
     * // Use feathers on arrow shafts
     * inventory().useOn(
     *     item -> item.getName().contains("Feather"),
     *     item -> item.getName().contains("Arrow shaft")
     * ).onSuccess(log("Added feathers to arrow shafts"));
     *
     * // Use gem on jewelry
     * inventory().useOn(
     *     item -> item.getName().contains("Ruby"),
     *     item -> item.getName().contains("Gold ring")
     * ).onSuccess(log("Created ruby ring"));
     * }</pre>
     *
     * @param first The predicate to match the first item (the one being used)
     * @param second The predicate to match the second item (the target)
     * @return An Action that uses the first item on the second item
     * @see #combine(int, int)
     * @see #combine(String, String)
     * @see Rs2ItemModel
     */
    Action combine(Predicate<Rs2ItemModel> first, Predicate<Rs2ItemModel> second);

    /**
     * Creates an action to use one item on another item in the inventory by their IDs.
     *
     * <p>This method finds items with the specified IDs and uses the first item on
     * the second item. This is useful when you know the exact item IDs.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Use feathers (ID: 314) on arrow shafts (ID: 52)
     * inventory().useOn(314, 52)
     *     .onSuccess(log("Created headless arrows"));
     *
     * // Use thread (ID: 1734) on needle (ID: 1733)
     * inventory().useOn(1734, 1733)
     *     .onSuccess(log("Ready for crafting"));
     *
     * // Use gem on ring
     * inventory().useOn(1603, 1635) // Ruby on gold ring
     *     .onSuccess(log("Created ruby ring"));
     * }</pre>
     *
     * @param first The ID of the first item (the one being used)
     * @param second The ID of the second item (the target)
     * @return An Action that uses the first item on the second item
     * @see #combine(String, String)
     * @see #combine(Predicate, Predicate)
     * @see Rs2ItemModel#getId()
     */
    Action combine(int first, int second);

    /**
     * Creates an action to use one item on another item in the inventory by their names.
     *
     * <p>This method finds items with the specified names and uses the first item on
     * the second item. The name matching is case-sensitive and must match exactly.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Use feathers on arrow shafts for fletching
     * inventory().useOn("Feather", "Arrow shaft")
     *     .onSuccess(log("Created headless arrows"));
     *
     * // Use thread on needle for crafting
     * inventory().useOn("Thread", "Needle")
     *     .onSuccess(log("Ready for crafting"));
     *
     * // Combine items for cooking
     * inventory().useOn("Raw chicken", "Fire")
     *     .onSuccess(log("Started cooking chicken"));
     * }</pre>
     *
     * @param first The exact name of the first item (the one being used)
     * @param second The exact name of the second item (the target)
     * @return An Action that uses the first item on the second item
     * @see #combine(int, int)
     * @see #combine(Predicate, Predicate)
     * @see Rs2ItemModel#getName()
     */
    Action combine(String first, String second);

    /**
     * Creates an action to use an inventory item on a game object in the world.
     *
     * <p>This method finds the first item matching the predicate and the first game object
     * matching the predicate, then uses the item on the object. This is commonly used for
     * activities like using items on furnaces, anvils, altars, or other interactive objects.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Use ore on furnace for smelting
     * inventory().useOnGameObject(
     *     item -> item.getName().contains("Iron ore"),
     *     obj -> obj.getName().equals("Furnace")
     * ).onSuccess(log("Started smelting iron ore"));
     *
     * // Use logs on fire for cooking
     * inventory().useOnGameObject(
     *     item -> item.getName().contains("Raw"),
     *     obj -> obj.getName().equals("Fire")
     * ).onSuccess(log("Started cooking"));
     *
     * // Use bones on altar for prayer
     * inventory().useOnGameObject(
     *     item -> item.getName().contains("bones"),
     *     obj -> obj.getName().contains("Altar")
     * ).onSuccess(log("Offered bones at altar"));
     * }</pre>
     *
     * @param item The predicate to match the inventory item to use
     * @param obj The predicate to match the game object to use the item on
     * @return An Action that uses the item on the game object
     * @see #useOnGameObject(int, int)
     * @see GameObject
     * @see Rs2ItemModel
     */
    Action useOnGameObject(Predicate<Rs2ItemModel> item, Predicate<GameObject> obj);

    /**
     * Creates an action to use an inventory item on a game object in the world by their IDs.
     *
     * <p>This method finds an item with the specified item ID and a game object with the
     * specified object ID, then uses the item on the object.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Use iron ore (ID: 440) on furnace (ID: 16469)
     * inventory().useOnGameObject(440, 16469)
     *     .onSuccess(log("Started smelting iron ore"));
     *
     * // Use raw fish on fire for cooking
     * inventory().useOnGameObject(317, 2732) // Raw shrimp on fire
     *     .onSuccess(log("Started cooking shrimp"));
     *
     * // Use bones on altar for prayer
     * inventory().useOnGameObject(526, 409) // Bones on altar
     *     .onSuccess(log("Offered bones at altar"));
     * }</pre>
     *
     * @param item The ID of the inventory item to use
     * @param obj The ID of the game object to use the item on
     * @return An Action that uses the item on the game object
     * @see #useOnGameObject(Predicate, Predicate)
     * @see GameObject#getId()
     * @see Rs2ItemModel#getId()
     */
    Action useOnGameObject(int item, int obj);
}
