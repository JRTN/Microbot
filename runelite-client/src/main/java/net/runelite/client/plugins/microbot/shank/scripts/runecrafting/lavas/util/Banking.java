package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class Banking {

    public enum Quantity {
        ONE, X, ALL, ALL_BUT_ONE
    }

    public static boolean withdraw(Rs2ItemModel itemModel, Quantity quantity) {
        return withdraw(itemModel, quantity, 1);
    }

    public static boolean withdraw(Rs2ItemModel itemModel, Quantity quantity, int amount) {
        log.info("Withdrawing {} {} in withdraw mode {}", itemModel.getName(), amount, quantity.name());

        if (!Rs2Bank.hasItem(new int[]{itemModel.getId()}, amount)) {
            log.warn("Bank does not have enough {} to withdraw", itemModel.getName());
            return false;
        }

        log.info("Starting inventory count: {}", Rs2Inventory.count(itemModel.getId()));

        boolean result;
        switch (quantity) {
            case X:
                result = Rs2Bank.withdrawX(itemModel.getId(), amount);
                break;
            case ALL:
                result = Rs2Bank.withdrawAll(itemModel.getId());
                break;
            case ALL_BUT_ONE:
                result = Rs2Bank.withdrawAllButOne(itemModel.getId());
                break;
            case ONE:
            default:
                result = Rs2Bank.withdrawOne(itemModel.getId());
        }
        log.info("Ending inventory count: {}", Rs2Inventory.count(itemModel.getId()));
        return result;
    }

    public static boolean deposit(Rs2ItemModel itemModel, Quantity quantity) {
        return deposit(itemModel, quantity, 1);
    }

    public static boolean deposit(Rs2ItemModel itemModel, Quantity quantity, int amount) {
        log.info("Depositing {} {} in deposit mode {}", itemModel.getName(), amount, quantity.name());
        log.info("Starting inventory count: {}", Rs2Inventory.count(itemModel.getId()));

        boolean result;
        switch (quantity) {
            case X:
                result = Rs2Bank.depositX(itemModel.getId(), amount);
                break;
            case ALL:
                result = Rs2Bank.depositAll(itemModel.getId());
                break;
            case ALL_BUT_ONE:
            case ONE:
            default:
                result = Rs2Bank.depositOne(itemModel.getId());
        }

        log.info("Ending inventory count: {}", Rs2Inventory.count(itemModel.getId()));
        return result;
    }

    public static boolean withdrawAndWear(Rs2ItemModel itemModel) {
        log.info("Withdrawing and equipping a {}", itemModel.getName());
        withdraw(itemModel, Quantity.ONE);
        boolean isItemInInventory = Global.sleepUntil(() -> Rs2Inventory.hasItem(itemModel.getId()));

        if (isItemInInventory) {
            log.info("{} now in inventory. Equipping...", itemModel.getName());
            Rs2Bank.wearItem(itemModel.getId());
            Global.sleepUntil(() -> Rs2Equipment.isWearing(itemModel.getId()));

            log.info("{} equipped: {}", itemModel.getName(), Rs2Equipment.isWearing(itemModel.getId()));
            return Rs2Equipment.isWearing(itemModel.getId());
        }

        log.info("Couldn't withdraw and equip a {}", itemModel.getName());
        return false;
    }
}
