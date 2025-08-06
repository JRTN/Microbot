package net.runelite.client.plugins.microbot.shank.api.fluent.api.bank;

import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.function.Predicate;

public interface FluentBankItemQuantityChange {
    Action one(Predicate<Rs2ItemModel> item);
    Action one(int id);
    Action one(String name);

    Action ten(Predicate<Rs2ItemModel> item);
    Action ten(int id);
    Action ten(String name);

    Action x(Predicate<Rs2ItemModel> item);
    Action x(int id);
    Action x(String name);

    Action all(Predicate<Rs2ItemModel> item);
    Action all(int id);
    Action all(String name);

    Action allButOne(Predicate<Rs2ItemModel> item);
    Action allButOne(int id);
    Action allButOne(String name);
}
