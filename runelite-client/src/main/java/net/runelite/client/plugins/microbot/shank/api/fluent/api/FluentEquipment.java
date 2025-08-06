package net.runelite.client.plugins.microbot.shank.api.fluent.api;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.general.FluentItemStore;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.Optional;
import java.util.function.Predicate;

public interface FluentEquipment extends FluentItemStore {
    Optional<Rs2ItemModel> getWearingInSlot(EquipmentInventorySlot slot);
    boolean isWearingInSlot(Predicate<Rs2ItemModel> target, EquipmentInventorySlot slot);
    boolean isWearingInSlot(int id, EquipmentInventorySlot slot);
    boolean isWearingInSlot(String name, EquipmentInventorySlot slot);
}
