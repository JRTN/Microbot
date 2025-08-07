package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import static net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent.gameObject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentInventory;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.ActionChain;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.util.TimingUtils;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FluentInventoryImpl implements FluentInventory {

    @Override
    public ItemContainer getContainer() {
        log.debug("Getting inventory container");
        return Microbot.getClient().getItemContainer(InventoryID.INV);
    }

    @Override
    public Stream<Rs2ItemModel> items() {
        log.debug("Getting all inventory items as stream");
        return Rs2Inventory.items();
    }

    @Override
    public Stream<Rs2ItemModel> items(Predicate<Rs2ItemModel> predicate) {
        log.debug("Getting filtered inventory items as stream");
        return items().filter(predicate);
    }

    @Override
    public List<Rs2ItemModel> getItems() {
        log.debug("Getting all inventory items as list");
        List<Rs2ItemModel> items = items().collect(Collectors.toList());
        log.debug("Found {} items in inventory", items.size());
        return items;
    }

    @Override
    public List<Rs2ItemModel> getItems(Predicate<Rs2ItemModel> predicate) {
        log.debug("Getting filtered inventory items as list");
        List<Rs2ItemModel> items = items(predicate).collect(Collectors.toList());
        log.debug("Found {} matching items in inventory", items.size());
        return items;
    }

    @Override
    public List<Rs2ItemModel> getItems(int... id) {
        log.debug("Getting inventory items by IDs: {}", Arrays.toString(id));
        List<Rs2ItemModel> items = getItems(
                item -> Arrays.stream(id)
                        .boxed()
                        .collect(Collectors.toSet())
                        .contains(item.getId()));
        log.debug("Found {} items matching IDs in inventory", items.size());
        return items;
    }

    @Override
    public List<Rs2ItemModel> getItems(String... names) {
        log.debug("Getting inventory items by names: {}", Arrays.toString(names));
        List<Rs2ItemModel> items = getItems(
                item -> Arrays.stream(names)
                        .collect(Collectors.toSet())
                        .contains(item.getName()));
        log.debug("Found {} items matching names in inventory", items.size());
        return items;
    }

    @Override
    public Optional<Rs2ItemModel> getItem(Predicate<Rs2ItemModel> item) {
        log.debug("Getting single inventory item with predicate");
        Optional<Rs2ItemModel> result = items(item).findAny();
        log.debug("Item found: {}", result.isPresent());
        return result;
    }

    @Override
    public Optional<Rs2ItemModel> getItem(int id) {
        log.debug("Getting single inventory item by ID: {}", id);
        Optional<Rs2ItemModel> result = getItem(item -> item.getId() == id);
        log.debug("Item with ID {} found: {}", id, result.isPresent());
        return result;
    }

    @Override
    public Optional<Rs2ItemModel> getItem(String name) {
        log.debug("Getting single inventory item by name: {}", name);
        Optional<Rs2ItemModel> result = getItem(item -> name.equals(item.getName()));
        log.debug("Item with name '{}' found: {}", name, result.isPresent());
        return result;
    }

    @Override
    public int capacity() {
        log.debug("Getting inventory capacity (28)");
        return 28;
    }

    @Override
    public int countItems(Predicate<Rs2ItemModel> target) {
        log.debug("Counting inventory items with predicate");
        int count = Rs2Inventory.count(target);
        log.debug("Item count: {}", count);
        return count;
    }

    @Override
    public int countItems(int id) {
        log.debug("Counting inventory items with ID: {}", id);
        int count = countItems(model -> model.getId() == id);
        log.debug("Count for ID {}: {}", id, count);
        return count;
    }

    @Override
    public int countItems(String name) {
        log.debug("Counting inventory items with name: {}", name);
        int count = countItems(model -> name.equals(model.getName()));
        log.debug("Count for name '{}': {}", name, count);
        return count;
    }

    @Override
    public boolean containsItem(Predicate<Rs2ItemModel> predicate) {
        log.debug("Checking if inventory contains item with predicate");
        boolean contains = countItems(predicate) > 0;
        log.debug("Inventory contains item: {}", contains);
        return contains;
    }

    @Override
    public boolean containsItem(int id) {
        log.debug("Checking if inventory contains item with ID: {}", id);
        boolean contains = countItems(id) > 0;
        log.debug("Inventory contains ID {}: {}", id, contains);
        return contains;
    }

    @Override
    public boolean containsItem(String name) {
        log.debug("Checking if inventory contains item with name: {}", name);
        boolean contains = countItems(name) > 0;
        log.debug("Inventory contains name '{}': {}", name, contains);
        return contains;
    }

    @Override
    public boolean isEmpty() {
        log.debug("Checking if inventory is empty");
        boolean empty = hasSpace(capacity());
        log.debug("Inventory is empty: {}", empty);
        return empty;
    }

    @Override
    public boolean isFull() {
        log.debug("Checking if inventory is full");
        boolean full = !hasSpace();
        log.debug("Inventory is full: {}", full);
        return full;
    }

    @Override
    public boolean hasSpace(int amount) {
        log.debug("Checking if inventory has space for {} items", amount);
        boolean hasSpace = getItems().size() <= capacity() - amount;
        log.debug("Inventory has space for {} items: {}", amount, hasSpace);
        return hasSpace;
    }

    @Override
    public boolean hasSpace() {
        log.debug("Checking if inventory has space for 1 item");
        return hasSpace(1);
    }

    @Override
    public Action interact(Predicate<Rs2ItemModel> target, String action) {
        return () -> {
            try {
                log.debug("Interacting with inventory item using predicate with action: {}", action);
                boolean result = Rs2Inventory.interact(target, action);
                if (result) {
                    log.debug("Inventory interaction '{}' completed successfully", action);
                } else {
                    log.warn("Inventory interaction '{}' failed", action);
                }
                return result;
            } catch (Exception e) {
                log.warn("Error during inventory interaction '{}'", action, e);
                return false;
            }
        };
    }

    @Override
    public Action interact(int id, String action) {
        log.debug("Creating inventory interaction for ID {} with action: {}", id, action);
        return interact(item -> item.getId() == id, action);
    }

    @Override
    public Action interact(String name, String action) {
        log.debug("Creating inventory interaction for name '{}' with action: {}", name, action);
        return interact(item -> name.equals(item.getName()), action);
    }

    @Override
    public Action drop(Predicate<Rs2ItemModel> target) {
        log.debug("Creating drop action with predicate");
        return interact(target, "Drop");
    }

    @Override
    public Action drop(int id) {
        log.debug("Creating drop action for ID: {}", id);
        return drop(model -> model.getId() == id);
    }

    @Override
    public Action drop(String name) {
        log.debug("Creating drop action for name: {}", name);
        return drop(model -> name.equals(model.getName()));
    }

    @Override
    public Action dropUntilRemain(Predicate<Rs2ItemModel> target, int remaining) {
        log.info("Creating dropUntilRemain action with {} items remaining", remaining);
        return ActionChain.start(drop(target))
                .repeatUntil(
                        () -> countItems(target) <= remaining, TimingUtils.randomJitter(30, 12), 30000);
    }

    @Override
    public Action dropAll(Predicate<Rs2ItemModel> target) {
        log.info("Creating dropAll action with predicate");
        return dropUntilRemain(target, 0);
    }

    @Override
    public Action dropAll(int id) {
        log.info("Creating dropAll action for ID: {}", id);
        return dropAll(model -> model.getId() == id);
    }

    @Override
    public Action dropAll(String name) {
        log.info("Creating dropAll action for name: {}", name);
        return dropAll(model -> name.equals(model.getName()));
    }

    @Override
    public Action dropAllExcept(Predicate<Rs2ItemModel> exclude) {
        log.info("Creating dropAllExcept action with predicate");
        return dropAll(exclude.negate());
    }

    @Override
    public Action dropAllExcept(int id) {
        log.info("Creating dropAllExcept action for ID: {}", id);
        return dropAll(model -> model.getId() == id);
    }

    @Override
    public Action dropAllExcept(String name) {
        log.info("Creating dropAllExcept action for name: {}", name);
        return dropAll(model -> name.equals(model.getName()));
    }

    @Override
    public Action use(Predicate<Rs2ItemModel> target) {
        log.debug("Creating use action with predicate");
        return interact(target, "Use");
    }

    @Override
    public Action use(int id) {
        log.debug("Creating use action for ID: {}", id);
        return use(item -> item.getId() == id);
    }

    @Override
    public Action use(String name) {
        log.debug("Creating use action for name: {}", name);
        return use(item -> name.equals(item.getName()));
    }

    @Override
    public Action combine(Predicate<Rs2ItemModel> first, Predicate<Rs2ItemModel> second) {
        log.info("Creating combine action with predicates");
        return use(first).then(use(second));
    }

    @Override
    public Action combine(int first, int second) {
        log.info("Creating combine action for IDs: {} + {}", first, second);
        return use(first).then(use(second));
    }

    @Override
    public Action combine(String first, String second) {
        log.info("Creating combine action for names: '{}' + '{}'", first, second);
        return use(first).then(use(second));
    }

    @Override
    public Action useOnGameObject(Predicate<Rs2ItemModel> item, Predicate<GameObject> obj) {
        return () -> {
            try {
                log.debug("Finding nearest game object for useOnGameObject");
                var object = gameObject().getNearestGameObject(obj);

                if (object.isEmpty()) {
                    log.warn("No matching game object found for useOnGameObject");
                    return false;
                }

                log.info("Executing useOnGameObject with found object");
                return use(item).then(gameObject().interactWith(object.get(), "Use")).execute();
            } catch (Exception e) {
                log.warn("Error during useOnGameObject", e);
                return false;
            }
        };
    }

    @Override
    public Action useOnGameObject(int itemId, int objId) {
        log.debug("Creating useOnGameObject for item ID {} and object ID {}", itemId, objId);
        return useOnGameObject(item -> item.getId() == itemId, obj -> obj.getId() == objId);
    }
}
