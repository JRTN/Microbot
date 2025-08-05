package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import net.runelite.api.ItemContainer;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentInventory;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.ActionChain;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.util.TimingUtils;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FluentInventoryImpl implements FluentInventory {

    @Override
    public ItemContainer getContainer() {
        return Rs2Inventory.inventory();
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
    public List<Rs2ItemModel> getAllItems() {
        return items().collect(Collectors.toList());
    }

    @Override
    public List<Rs2ItemModel> getItems(Predicate<Rs2ItemModel> predicate) {
        return items(predicate).collect(Collectors.toList());
    }

    @Override
    public int count(Predicate<Rs2ItemModel> target) {
        return Rs2Inventory.count(target);
    }

    @Override
    public int count(int id) {
        return count(model -> model.getId() == id);
    }

    @Override
    public int count(String name) {
        return count(model -> name.equals(model.getName()));
    }

    @Override
    public Action drop(Predicate<Rs2ItemModel> target) {
        return () -> Rs2Inventory.drop(target);
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
                        () -> count(target) <= remaining, TimingUtils.randomJitter(30, 12), 30000);
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
}
