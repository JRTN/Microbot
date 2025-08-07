package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class FluentBankImpl implements FluentBank {

    @Override
    public boolean isClosed() {
        boolean closed = !isOpen();
        log.debug("Bank closed status: {}", closed);
        return closed;
    }

    @Override
    public boolean isOpen() {
        boolean open = Rs2Bank.isOpen();
        log.debug("Bank open status: {}", open);
        return open;
    }

    @Override
    public Action close() {
        return isClosed() ?
                () -> {
                    log.debug("Bank already closed, skipping close action");
                    return true;
                } :
                () -> {
                    try {
                        log.info("Closing bank");
                        boolean result = Rs2Bank.closeBank();
                        if (result) {
                            log.info("Bank closed successfully");
                        } else {
                            log.warn("Failed to close bank");
                        }
                        return result;
                    } catch (Exception e) {
                        log.warn("Error closing bank", e);
                        return false;
                    }
                };
    }

    @Override
    public Action open() {
        return isOpen() ?
                () -> {
                    log.debug("Bank already open, skipping open action");
                    return true;
                } :
                () -> {
                    try {
                        log.info("Opening bank");
                        boolean result = Rs2Bank.openBank();
                        if (result) {
                            log.info("Bank opened successfully");
                        } else {
                            log.warn("Failed to open bank");
                        }
                        return result;
                    } catch (Exception e) {
                        log.warn("Error opening bank", e);
                        return false;
                    }
                };
    }

    @Override
    public FluentBankDeposit deposit() {
        log.debug("Creating bank deposit interface");
        return new FluentBankDepositImpl();
    }

    @Override
    public FluentBankWithdraw withdraw() {
        log.debug("Creating bank withdraw interface");
        return new FluentBankWithdrawImpl();
    }

    @Override
    public ItemContainer getContainer() {
        log.debug("Getting bank container");
        return Microbot.getClient().getItemContainer(InventoryID.BANK);
    }

    @Override
    public Stream<Rs2ItemModel> items() {
        log.debug("Getting all bank items as stream");
        return Rs2Bank.getAll();
    }

    @Override
    public Stream<Rs2ItemModel> items(Predicate<Rs2ItemModel> predicate) {
        log.debug("Getting filtered bank items as stream");
        return items().filter(predicate);
    }

    @Override
    public List<Rs2ItemModel> getItems() {
        log.debug("Getting all bank items as list");
        List<Rs2ItemModel> items = items().collect(Collectors.toList());
        log.debug("Found {} items in bank", items.size());
        return items;
    }

    @Override
    public List<Rs2ItemModel> getItems(Predicate<Rs2ItemModel> item) {
        log.debug("Getting filtered bank items as list");
        List<Rs2ItemModel> items = items(item).collect(Collectors.toList());
        log.debug("Found {} matching items in bank", items.size());
        return items;
    }

    @Override
    public List<Rs2ItemModel> getItems(int... id) {
        log.debug("Getting bank items by IDs: {}", Arrays.toString(id));
        var idSet = Arrays.stream(id).boxed().collect(Collectors.toSet());
        List<Rs2ItemModel> items = getItems(item -> idSet.contains(item.getId()));
        log.debug("Found {} items matching IDs in bank", items.size());
        return items;
    }

    @Override
    public List<Rs2ItemModel> getItems(String... names) {
        log.debug("Getting bank items by names: {}", Arrays.toString(names));
        var nameSet = Arrays.stream(names).collect(Collectors.toSet());
        List<Rs2ItemModel> items = getItems(item -> nameSet.contains(item.getName()));
        log.debug("Found {} items matching names in bank", items.size());
        return items;
    }

    @Override
    public Optional<Rs2ItemModel> getItem(Predicate<Rs2ItemModel> item) {
        log.debug("Getting single bank item with predicate");
        Optional<Rs2ItemModel> result = items(item).findAny();
        log.debug("Item found: {}", result.isPresent());
        return result;
    }

    @Override
    public Optional<Rs2ItemModel> getItem(int id) {
        log.debug("Getting single bank item by ID: {}", id);
        Optional<Rs2ItemModel> result = getItems(id).stream().findAny();
        log.debug("Item with ID {} found: {}", id, result.isPresent());
        return result;
    }

    @Override
    public Optional<Rs2ItemModel> getItem(String name) {
        log.debug("Getting single bank item by name: {}", name);
        Optional<Rs2ItemModel> result = getItems(name).stream().findAny();
        log.debug("Item with name '{}' found: {}", name, result.isPresent());
        return result;
    }

    @Override
    public int capacity() {
        log.debug("Getting bank capacity (hardcoded 800)");
        return 800; //TODO -- implement capacity
    }

    @Override
    public int countItems(Predicate<Rs2ItemModel> item) {
        log.debug("Counting bank items with predicate");
        int count = getItem(item).map(Rs2ItemModel::getQuantity).orElse(0);
        log.debug("Item count: {}", count);
        return count;
    }

    @Override
    public int countItems(int id) {
        log.debug("Counting bank items with ID: {}", id);
        int count = countItems(item -> item.getId() == id);
        log.debug("Count for ID {}: {}", id, count);
        return count;
    }

    @Override
    public int countItems(String name) {
        log.debug("Counting bank items with name: {}", name);
        int count = countItems(item -> name.equals(item.getName()));
        log.debug("Count for name '{}': {}", name, count);
        return count;
    }

    @Override
    public boolean containsItem(Predicate<Rs2ItemModel> item) {
        log.debug("Checking if bank contains item with predicate");
        boolean contains = getItem(item).isPresent();
        log.debug("Bank contains item: {}", contains);
        return contains;
    }

    @Override
    public boolean containsItem(int id) {
        log.debug("Checking if bank contains item with ID: {}", id);
        boolean contains = containsItem(item -> item.getId() == id);
        log.debug("Bank contains ID {}: {}", id, contains);
        return contains;
    }

    @Override
    public boolean containsItem(String name) {
        log.debug("Checking if bank contains item with name: {}", name);
        boolean contains = containsItem(item -> name.equals(item.getName()));
        log.debug("Bank contains name '{}': {}", name, contains);
        return contains;
    }

    @Override
    public boolean hasSpace() {
        log.debug("Checking if bank has space for 1 item");
        return hasSpace(1);
    }

    @Override
    public boolean hasSpace(int amount) {
        log.debug("Checking if bank has space for {} items", amount);
        boolean hasSpace = getItems().size() <= capacity() - amount;
        log.debug("Bank has space for {} items: {}", amount, hasSpace);
        return hasSpace;
    }

    @Override
    public boolean isFull() {
        log.debug("Checking if bank is full");
        boolean full = !isEmpty();
        log.debug("Bank is full: {}", full);
        return full;
    }

    @Override
    public boolean isEmpty() {
        log.debug("Checking if bank is empty");
        boolean empty = hasSpace(capacity());
        log.debug("Bank is empty: {}", empty);
        return empty;
    }

    @Override
    public Action interact(Predicate<Rs2ItemModel> target, String action) {
        log.warn("Bank interact with predicate not implemented");
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Action interact(String name, String action) {
        log.warn("Bank interact with name '{}' and action '{}' not implemented", name, action);
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Action interact(int id, String action) {
        log.warn("Bank interact with ID {} and action '{}' not implemented", id, action);
        throw new UnsupportedOperationException("Not implemented");
    }
}
