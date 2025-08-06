package net.runelite.client.plugins.microbot.shank.api.fluent.impl.bank;

import static net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent.inventory;

import net.runelite.client.plugins.microbot.shank.api.fluent.api.bank.FluentBankDeposit;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.function.Predicate;

public class FluentBankDepositImpl implements FluentBankDeposit {

    @Override
    public Action one(Predicate<Rs2ItemModel> item) {
        var inventoryItem = inventory().getItems(item).stream().findFirst();

        return inventoryItem.<Action>map(itemModel -> () -> {
            try {
                return Rs2Bank.depositOne(itemModel.getId());
            } catch (Exception ex) {
                return false;
            }
        }).orElseGet(() -> () -> false);
    }

    @Override
    public Action one(int id) {
        return one(item -> item.getId() == id);
    }

    @Override
    public Action one(String name) {
        return one(item -> name.equals(item.getName()));
    }

    @Override
    public Action x(Predicate<Rs2ItemModel> item, int amount) {
        var inventoryItem = inventory().getItems(item).stream().findFirst();

        return inventoryItem.<Action>map(itemModel -> () -> {
            try {
                return Rs2Bank.depositX(itemModel.getId(), amount);
            } catch (Exception ex) {
                return false;
            }
        }).orElseGet(() -> () -> false);
    }

    @Override
    public Action x(int id, int amount) {
        return x(item -> item.getId() == id, amount);
    }

    @Override
    public Action x(String name, int amount) {
        return x(item -> name.equals(item.getName()), amount);
    }

    @Override
    public Action all(Predicate<Rs2ItemModel> item) {
        var inventoryItem = inventory().getItems(item).stream().findFirst();

        return inventoryItem.<Action>map(itemModel -> () -> {
            try {
                return Rs2Bank.depositAll(itemModel.getId());
            } catch (Exception ex) {
                return false;
            }
        }).orElseGet(() -> () -> false);
    }

    @Override
    public Action all(int id) {
        return all(item -> item.getId() == id);
    }

    @Override
    public Action all(String name) {
        return all(item -> name.equals(item.getName()));
    }
}
