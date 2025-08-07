package net.runelite.client.plugins.microbot.shank.api.fluent.impl.bank;

import static net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent.inventory;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.bank.FluentBankDeposit;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.function.Predicate;

@Slf4j
public class FluentBankDepositImpl implements FluentBankDeposit {

    @Override
    public Action one(Predicate<Rs2ItemModel> item) {
        log.debug("Creating deposit one action with predicate");
        var inventoryItem = inventory().getItems(item).stream().findFirst();

        return inventoryItem.<Action>map(itemModel -> () -> {
            try {
                log.info("Depositing one of item ID: {}", itemModel.getId());
                boolean result = Rs2Bank.depositOne(itemModel.getId());
                if (result) {
                    log.info("Successfully deposited one of item ID: {}", itemModel.getId());
                } else {
                    log.warn("Failed to deposit one of item ID: {}", itemModel.getId());
                }
                return result;
            } catch (Exception ex) {
                log.warn("Error depositing one of item ID: {}", itemModel.getId(), ex);
                return false;
            }
        }).orElseGet(() -> () -> {
            log.warn("Item not found in inventory for deposit one");
            return false;
        });
    }

    @Override
    public Action one(int id) {
        log.debug("Creating deposit one action for ID: {}", id);
        return one(item -> item.getId() == id);
    }

    @Override
    public Action one(String name) {
        log.debug("Creating deposit one action for name: {}", name);
        return one(item -> name.equals(item.getName()));
    }

    @Override
    public Action x(Predicate<Rs2ItemModel> item, int amount) {
        log.debug("Creating deposit {} action with predicate", amount);
        var inventoryItem = inventory().getItems(item).stream().findFirst();

        return inventoryItem.<Action>map(itemModel -> () -> {
            try {
                log.info("Depositing {} of item ID: {}", amount, itemModel.getId());
                boolean result = Rs2Bank.depositX(itemModel.getId(), amount);
                if (result) {
                    log.info("Successfully deposited {} of item ID: {}", amount, itemModel.getId());
                } else {
                    log.warn("Failed to deposit {} of item ID: {}", amount, itemModel.getId());
                }
                return result;
            } catch (Exception ex) {
                log.warn("Error depositing {} of item ID: {}", amount, itemModel.getId(), ex);
                return false;
            }
        }).orElseGet(() -> () -> {
            log.warn("Item not found in inventory for deposit {}", amount);
            return false;
        });
    }

    @Override
    public Action x(int id, int amount) {
        log.debug("Creating deposit {} action for ID: {}", amount, id);
        return x(item -> item.getId() == id, amount);
    }

    @Override
    public Action x(String name, int amount) {
        log.debug("Creating deposit {} action for name: {}", amount, name);
        return x(item -> name.equals(item.getName()), amount);
    }

    @Override
    public Action all(Predicate<Rs2ItemModel> item) {
        log.debug("Creating deposit all action with predicate");
        var inventoryItem = inventory().getItems(item).stream().findFirst();

        return inventoryItem.<Action>map(itemModel -> () -> {
            try {
                log.info("Depositing all of item ID: {}", itemModel.getId());
                boolean result = Rs2Bank.depositAll(itemModel.getId());
                if (result) {
                    log.info("Successfully deposited all of item ID: {}", itemModel.getId());
                } else {
                    log.warn("Failed to deposit all of item ID: {}", itemModel.getId());
                }
                return result;
            } catch (Exception ex) {
                log.warn("Error depositing all of item ID: {}", itemModel.getId(), ex);
                return false;
            }
        }).orElseGet(() -> () -> {
            log.warn("Item not found in inventory for deposit all");
            return false;
        });
    }

    @Override
    public Action all(int id) {
        log.debug("Creating deposit all action for ID: {}", id);
        return all(item -> item.getId() == id);
    }

    @Override
    public Action all(String name) {
        log.debug("Creating deposit all action for name: {}", name);
        return all(item -> name.equals(item.getName()));
    }

    @Override
    public Action all() {
        return () -> {
            try {
                log.info("Depositing all items");
                boolean result = Rs2Bank.depositAll();
                if (result) {
                    log.info("Successfully deposited all items");
                } else {
                    log.warn("Failed to deposit all items");
                }
                return result;
            } catch (Exception ex) {
                log.warn("Error depositing all items", ex);
                return false;
            }
        };
    }
}
