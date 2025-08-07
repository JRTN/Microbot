package net.runelite.client.plugins.microbot.shank.api.fluent.impl.bank;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.bank.FluentBankWithdraw;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.function.Predicate;

import static net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent.bank;

@Slf4j
public class FluentBankWithdrawImpl implements FluentBankWithdraw {

    @Override
    public Action one(Predicate<Rs2ItemModel> item) {
        log.debug("Creating withdraw one action with predicate");
        var bankItem = bank().getItem(item);

        return bankItem.<Action>map(itemModel -> () -> {
            try {
                log.info("Withdrawing one of item ID: {}", itemModel.getId());
                boolean result = Rs2Bank.withdrawOne(itemModel.getId());
                if (result) {
                    log.info("Successfully withdrew one of item ID: {}", itemModel.getId());
                } else {
                    log.warn("Failed to withdraw one of item ID: {}", itemModel.getId());
                }
                return result;
            } catch (Exception ex) {
                log.warn("Error withdrawing one of item ID: {}", itemModel.getId(), ex);
                return false;
            }
        }).orElseGet(() -> () -> {
            log.warn("Item not found in bank for withdraw one");
            return false;
        });
    }

    @Override
    public Action one(int id) {
        log.debug("Creating withdraw one action for ID: {}", id);
        return one(item -> item.getId() == id);
    }

    @Override
    public Action one(String name) {
        log.debug("Creating withdraw one action for name: {}", name);
        return one(item -> name.equals(item.getName()));
    }

    @Override
    public Action x(Predicate<Rs2ItemModel> item, int amount) {
        log.debug("Creating withdraw {} action with predicate", amount);
        var bankItem = bank().getItem(item);

        return bankItem.<Action>map(itemModel -> () -> {
            try {
                log.info("Withdrawing {} of item ID: {}", amount, itemModel.getId());
                boolean result = Rs2Bank.withdrawX(itemModel.getId(), amount);
                if (result) {
                    log.info("Successfully withdrew {} of item ID: {}", amount, itemModel.getId());
                } else {
                    log.warn("Failed to withdraw {} of item ID: {}", amount, itemModel.getId());
                }
                return result;
            } catch (Exception ex) {
                log.warn("Error withdrawing {} of item ID: {}", amount, itemModel.getId(), ex);
                return false;
            }
        }).orElseGet(() -> () -> {
            log.warn("Item not found in bank for withdraw {}", amount);
            return false;
        });
    }

    @Override
    public Action x(int id, int amount) {
        log.debug("Creating withdraw {} action for ID: {}", amount, id);
        return x(item -> item.getId() == id, amount);
    }

    @Override
    public Action x(String name, int amount) {
        log.debug("Creating withdraw {} action for name: {}", amount, name);
        return x(item -> name.equals(item.getName()), amount);
    }

    @Override
    public Action all(Predicate<Rs2ItemModel> item) {
        log.debug("Creating withdraw all action with predicate");
        var bankItem = bank().getItem(item);

        return bankItem.<Action>map(itemModel -> () -> {
            try {
                log.info("Withdrawing all of item ID: {}", itemModel.getId());
                boolean result = Rs2Bank.withdrawAll(itemModel.getId());
                if (result) {
                    log.info("Successfully withdrew all of item ID: {}", itemModel.getId());
                } else {
                    log.warn("Failed to withdraw all of item ID: {}", itemModel.getId());
                }
                return result;
            } catch (Exception ex) {
                log.warn("Error withdrawing all of item ID: {}", itemModel.getId(), ex);
                return false;
            }
        }).orElseGet(() -> () -> {
            log.warn("Item not found in bank for withdraw all");
            return false;
        });
    }

    @Override
    public Action all(int id) {
        log.debug("Creating withdraw all action for ID: {}", id);
        return all(item -> item.getId() == id);
    }

    @Override
    public Action all(String name) {
        log.debug("Creating withdraw all action for name: {}", name);
        return all(item -> name.equals(item.getName()));
    }
}
