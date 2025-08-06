package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentEquipment;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FluentEquipmentImpl implements FluentEquipment {
    @Override
    public ItemContainer getContainer() {
        return Microbot.getClient().getItemContainer(InventoryID.WORN);
    }

    @Override
    public Stream<Rs2ItemModel> items() {
        return Rs2Equipment.items().stream();
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
    public List<Rs2ItemModel> getItems(Predicate<Rs2ItemModel> item) {
        return null;
    }

    @Override
    public List<Rs2ItemModel> getItems(int... id) {
        return null;
    }

    @Override
    public List<Rs2ItemModel> getItems(String... names) {
        return null;
    }

    @Override
    public Optional<Rs2ItemModel> getItem(Predicate<Rs2ItemModel> item) {
        return Optional.empty();
    }

    @Override
    public Optional<Rs2ItemModel> getItem(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Rs2ItemModel> getItem(String name) {
        return Optional.empty();
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public int countItems(Predicate<Rs2ItemModel> item) {
        return 0;
    }

    @Override
    public int countItems(int id) {
        return 0;
    }

    @Override
    public int countItems(String name) {
        return 0;
    }

    @Override
    public boolean containsItem(Predicate<Rs2ItemModel> item) {
        return false;
    }

    @Override
    public boolean containsItem(int id) {
        return false;
    }

    @Override
    public boolean containsItem(String name) {
        return false;
    }

    @Override
    public boolean hasSpace() {
        return false;
    }

    @Override
    public boolean hasSpace(int amount) {
        return false;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Action interact(Predicate<Rs2ItemModel> target, String action) {
        return null;
    }

    @Override
    public Action interact(String name, String action) {
        return null;
    }

    @Override
    public Action interact(int id, String action) {
        return null;
    }

    @Override
    public Optional<Rs2ItemModel> getWearingInSlot(EquipmentInventorySlot slot) {
        return items(equipment -> equipment.getSlot() == slot.getSlotIdx()).findFirst();
    }

    @Override
    public boolean isWearingInSlot(Predicate<Rs2ItemModel> target, EquipmentInventorySlot slot) {
        return getWearingInSlot(slot).stream().anyMatch(target);
    }

    @Override
    public boolean isWearingInSlot(int id, EquipmentInventorySlot slot) {
        return getWearingInSlot(slot).stream().anyMatch(worn -> worn.getId() == id);
    }

    @Override
    public boolean isWearingInSlot(String name, EquipmentInventorySlot slot) {
        return getWearingInSlot(slot).stream().anyMatch(worn -> name.equals(worn.getName()));
    }
}
