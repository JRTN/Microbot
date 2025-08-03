package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.Equipment;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EquipmentImpl implements Equipment {
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
    public List<Rs2ItemModel> getAllWorn() {
        return items().collect(Collectors.toList());
    }

    @Override
    public List<Rs2ItemModel> getWorn(Predicate<Rs2ItemModel> predicate) {
        return items(predicate).collect(Collectors.toList());
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
