package net.runelite.client.plugins.microbot.shank.api.fluent.impl.action.bank;

import net.runelite.client.plugins.microbot.shank.api.fluent.impl.flow.Action;

/**
 * Placeholder bank actions for the flow API
 */
public class BankActions {

    public Action open() {
        return () -> {
            System.out.println("Opening bank...");
            // TODO: Implement Rs2Bank.open()
            return true;
        };
    }

    public Action depositAll(String itemName) {
        return () -> {
            System.out.println("Depositing all " + itemName + "...");
            // TODO: Implement Rs2Bank.depositAll(itemName)
            return true;
        };
    }

    public Action withdraw(String itemName, int quantity) {
        return () -> {
            System.out.println("Withdrawing " + quantity + " " + itemName + "...");
            // TODO: Implement Rs2Bank.withdraw(itemName, quantity)
            return true;
        };
    }
}
