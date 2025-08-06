package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import net.runelite.api.gameval.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentBank;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.bank.FluentBankDeposit;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.bank.FluentBankWithdraw;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.shank.api.fluent.impl.bank.FluentBankDepositImpl;
import net.runelite.client.plugins.microbot.shank.api.fluent.impl.bank.FluentBankWithdrawImpl;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FluentBankImpl implements FluentBank {

    @Override
    public boolean isClosed() {
        return !isOpen();
    }

    @Override
    public boolean isOpen() {
        return Rs2Bank.isOpen();
    }

    @Override
    public Action close() {
        return isClosed() ? () -> true : Rs2Bank::closeBank;
    }

    @Override
    public Action open() {
        return isOpen() ? () -> true : Rs2Bank::openBank;
    }

    @Override
    public FluentBankDeposit deposit() {
        return new FluentBankDepositImpl();
    }

    @Override
    public FluentBankWithdraw withdraw() {
        return new FluentBankWithdrawImpl();
    }

    @Override
    public ItemContainer getContainer() {
        return Microbot.getClient().getItemContainer(InventoryID.BANK);
    }

    @Override
    public Stream<Rs2ItemModel> items() {
        return Rs2Bank.getAll();
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
        return items(item).collect(Collectors.toList());
    }

    @Override
    public List<Rs2ItemModel> getItems(int... id) {
        var idSet = Arrays.stream(id).boxed().collect(Collectors.toSet());

        return getItems(item -> idSet.contains(item.getId()));
    }

    @Override
    public List<Rs2ItemModel> getItems(String... names) {
        var nameSet = Arrays.stream(names).collect(Collectors.toSet());

        return getItems(item -> nameSet.contains(item.getName()));
    }

    @Override
    public Optional<Rs2ItemModel> getItem(Predicate<Rs2ItemModel> item) {
        return items(item).findAny();
    }

    @Override
    public Optional<Rs2ItemModel> getItem(int id) {
        return getItems(id).stream().findAny();
    }

    @Override
    public Optional<Rs2ItemModel> getItem(String name) {
        return getItems(name).stream().findAny();
    }

    @Override
    public int capacity() {
        return 800; //TODO -- implement capacity
    }

    @Override
    public int countItems(Predicate<Rs2ItemModel> item) {
        return getItem(item).map(Rs2ItemModel::getQuantity).orElse(0);
    }

    @Override
    public int countItems(int id) {
        return countItems(item -> item.getId() == id);
    }

    @Override
    public int countItems(String name) {
        return countItems(item -> name.equals(item.getName()));
    }

    @Override
    public boolean containsItem(Predicate<Rs2ItemModel> item) {
        return getItem(item).isPresent();
    }

    @Override
    public boolean containsItem(int id) {
        return containsItem(item -> item.getId() == id);
    }

    @Override
    public boolean containsItem(String name) {
        return containsItem(item -> name.equals(item.getName()));
    }

    @Override
    public boolean hasSpace() {
        return hasSpace(1);
    }

    @Override
    public boolean hasSpace(int amount) {
        return getItems().size() <= capacity() - amount;
    }

    @Override
    public boolean isFull() {
        return !isEmpty();
    }

    @Override
    public boolean isEmpty() {
        return hasSpace(capacity());
    }

    @Override
    public Action interact(Predicate<Rs2ItemModel> target, String action) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Action interact(String name, String action) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Action interact(int id, String action) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
