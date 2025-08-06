package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import static net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent.gameObject;

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

public class FluentInventoryImpl implements FluentInventory {

    @Override
    public ItemContainer getContainer() {
        return Microbot.getClient().getItemContainer(InventoryID.INV);
    }

    @Override
    public Stream<Rs2ItemModel> items() {
        return Rs2Inventory.items();
    }

    @Override
    public Stream<Rs2ItemModel> items(Predicate<Rs2ItemModel> predicate) {
        return items().filter(predicate);
    }

    @Override
    public List<Rs2ItemModel> getItems() {
        return items().collect(Collectors.toList());
    }

    @Override
    public List<Rs2ItemModel> getItems(Predicate<Rs2ItemModel> predicate) {
        return items(predicate).collect(Collectors.toList());
    }

    @Override
    public List<Rs2ItemModel> getItems(int... id) {
        return getItems(
                item -> Arrays.stream(id)
                        .boxed()
                        .collect(Collectors.toSet())
                        .contains(item.getId()));
    }

    @Override
    public List<Rs2ItemModel> getItems(String... names) {
        return getItems(
                item -> Arrays.stream(names)
                        .collect(Collectors.toSet())
                        .contains(item.getName()));
    }

    @Override
    public Optional<Rs2ItemModel> getItem(Predicate<Rs2ItemModel> item) {
        return items(item).findAny();
    }

    @Override
    public Optional<Rs2ItemModel> getItem(int id) {
        return getItem(item -> item.getId() == id);
    }

    @Override
    public Optional<Rs2ItemModel> getItem(String name) {
        return getItem(item -> name.equals(item.getName()));
    }

    @Override
    public int capacity() {
        return 28;
    }

    @Override
    public int countItems(Predicate<Rs2ItemModel> target) {
        return Rs2Inventory.count(target);
    }

    @Override
    public int countItems(int id) {
        return countItems(model -> model.getId() == id);
    }

    @Override
    public int countItems(String name) {
        return countItems(model -> name.equals(model.getName()));
    }

    @Override
    public boolean containsItem(Predicate<Rs2ItemModel> predicate) {
        return countItems(predicate) > 0;
    }

    @Override
    public boolean containsItem(int id) {
        return countItems(id) > 0;
    }

    @Override
    public boolean containsItem(String name) {
        return countItems(name) > 0;
    }

    @Override
    public boolean isEmpty() {
        return hasSpace(capacity());
    }

    @Override
    public boolean isFull() {
        return !hasSpace();
    }

    @Override
    public boolean hasSpace(int amount) {
        return getItems().size() <= capacity() - amount;
    }

    @Override
    public boolean hasSpace() {
        return hasSpace(1);
    }

    @Override
    public Action interact(Predicate<Rs2ItemModel> target, String action) {
        return () -> Rs2Inventory.interact(target, action);
    }

    @Override
    public Action interact(int id, String action) {
        return interact(item -> item.getId() == id, action);
    }

    @Override
    public Action interact(String name, String action) {
        return interact(item -> name.equals(item.getName()), action);
    }

    @Override
    public Action drop(Predicate<Rs2ItemModel> target) {
        return interact(target, "Drop");
    }

    @Override
    public Action drop(int id) {
        return drop(model -> model.getId() == id);
    }

    @Override
    public Action drop(String name) {
        return drop(model -> name.equals(model.getName()));
    }

    @Override
    public Action dropUntilRemain(Predicate<Rs2ItemModel> target, int remaining) {
        return ActionChain.start(drop(target))
                .repeatUntil(
                        () -> countItems(target) <= remaining, TimingUtils.randomJitter(30, 12), 30000);
    }

    @Override
    public Action dropAll(Predicate<Rs2ItemModel> target) {
        return dropUntilRemain(target, 0);
    }

    @Override
    public Action dropAll(int id) {
        return dropAll(model -> model.getId() == id);
    }

    @Override
    public Action dropAll(String name) {
        return dropAll(model -> name.equals(model.getName()));
    }

    @Override
    public Action dropAllExcept(Predicate<Rs2ItemModel> exclude) {
        return dropAll(exclude.negate());
    }

    @Override
    public Action dropAllExcept(int id) {
        return dropAll(model -> model.getId() == id);
    }

    @Override
    public Action dropAllExcept(String name) {
        return dropAll(model -> name.equals(model.getName()));
    }

    @Override
    public Action use(Predicate<Rs2ItemModel> target) {
        return interact(target, "Use");
    }

    @Override
    public Action use(int id) {
        return use(item -> item.getId() == id);
    }

    @Override
    public Action use(String name) {
        return use(item -> name.equals(item.getName()));
    }

    @Override
    public Action useOn(Predicate<Rs2ItemModel> first, Predicate<Rs2ItemModel> second) {
        return use(first).then(use(second));
    }

    @Override
    public Action useOn(int first, int second) {
        return use(first).then(use(second));
    }

    @Override
    public Action useOn(String first, String second) {
        return use(first).then(use(second));
    }

    @Override
    public Action useOnGameObject(Predicate<Rs2ItemModel> item, Predicate<GameObject> obj) {
        var object = gameObject().getNearestGameObject(obj);

        if (object.isEmpty()) {
            return () -> false;
        }

        return use(item).then(gameObject().interactWith(object.get(), "Use"));
    }

    @Override
    public Action useOnGameObject(int itemId, int objId) {
        return useOnGameObject(item -> item.getId() == itemId, obj -> obj.getId() == objId);
    }
}
