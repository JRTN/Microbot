package net.runelite.client.plugins.microbot.shank.api.fluent.api.general;

import net.runelite.api.ItemContainer;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Methods to interact with some form of an item store -- some place which the
 * player can store and withdraw items from such as inventory, bank, equipment, etc.
 */
public interface FluentItemStore {
    ItemContainer getContainer();
    Stream<Rs2ItemModel> items();
    Stream<Rs2ItemModel> items(Predicate<Rs2ItemModel> predicate);

    List<Rs2ItemModel> getItems();
    List<Rs2ItemModel> getItems(Predicate<Rs2ItemModel> item);
    List<Rs2ItemModel> getItems(int... id);
    List<Rs2ItemModel> getItems(String... names);

    Optional<Rs2ItemModel> getItem(Predicate<Rs2ItemModel> item);
    Optional<Rs2ItemModel> getItem(int id);
    Optional<Rs2ItemModel> getItem(String name);

    int capacity();

    int countItems(Predicate<Rs2ItemModel> item);
    int countItems(int id);
    int countItems(String name);

    boolean containsItem(Predicate<Rs2ItemModel> item);
    boolean containsItem(int id);
    boolean containsItem(String name);

    boolean hasSpace();
    boolean hasSpace(int amount);
    boolean isFull();
    boolean isEmpty();

    Action interact(Predicate<Rs2ItemModel> target, String action);
    Action interact(String name, String action);
    Action interact(int id, String action);
}
