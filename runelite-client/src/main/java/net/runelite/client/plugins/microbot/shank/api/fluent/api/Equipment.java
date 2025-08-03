package net.runelite.client.plugins.microbot.shank.api.fluent.api;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Equipment {
    ItemContainer getContainer();
    Stream<Rs2ItemModel> items();
    Stream<Rs2ItemModel> items(Predicate<Rs2ItemModel> predicate);

    List<Rs2ItemModel> getAllWorn();
    List<Rs2ItemModel> getWorn(Predicate<Rs2ItemModel> predicate);

    Optional<Rs2ItemModel> getWearingInSlot(EquipmentInventorySlot slot);
    boolean isWearingInSlot(Predicate<Rs2ItemModel> target, EquipmentInventorySlot slot);
    boolean isWearingInSlot(int id, EquipmentInventorySlot slot);
    boolean isWearingInSlot(String name, EquipmentInventorySlot slot);
}
