package net.runelite.client.plugins.microbot.shank.api.fluent.impl.bank;

import net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.bank.FluentBankDeposit;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.function.Predicate;

public class FluentBankDepositImpl implements FluentBankDeposit {

    @Override
    public Action one(Predicate<Rs2ItemModel> item) {
        var bankItem = Rs2Fluent.bank().getBankItem(item);

        return bankItem.<Action>map(itemModel -> () -> {
            try {
                return Rs2Bank.withdrawOne(itemModel);
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
    public Action ten(Predicate<Rs2ItemModel> item) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Action ten(int id) {
        return ten(item -> item.getId() == id);
    }

    @Override
    public Action ten(String name) {
        return ten(item -> name.equals(item.getName()));
    }

    @Override
    public Action x(Predicate<Rs2ItemModel> item) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Action x(int id) {
        return x(item -> item.getId() == id);
    }

    @Override
    public Action x(String name) {
        return x(item -> name.equals(item.getName()));
    }

    @Override
    public Action all(Predicate<Rs2ItemModel> item) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Action all(int id) {
        return all(item -> item.getId() == id);
    }

    @Override
    public Action all(String name) {
        return all(item -> name.equals(item.getName()));
    }

    @Override
    public Action allButOne(Predicate<Rs2ItemModel> item) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Action allButOne(int id) {
        return allButOne(item -> item.getId() == id);
    }

    @Override
    public Action allButOne(String name) {
        return allButOne(item -> name.equals(item.getName()));
    }
}
