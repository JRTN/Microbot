package net.runelite.client.plugins.microbot.shank.api.fluent.api.bank;

import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.function.Predicate;

public interface FluentBankItemQuantityChange {
    Action one(Predicate<Rs2ItemModel> item);
    Action one(int id);
    Action one(String name);

    Action x(Predicate<Rs2ItemModel> item, int amount);
    Action x(int id, int amount);
    Action x(String name, int amount);

    Action all(Predicate<Rs2ItemModel> item);
    Action all(int id);
    Action all(String name);
}
