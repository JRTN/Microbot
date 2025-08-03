package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.states;

import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.AbstractState;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

public class NeedToTeleportNearBank extends AbstractState {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void execute() {

    }

    @Override
    public boolean evaluate() {
        boolean isNearBank = Rs2Bank.isNearBank(50);
        if (!isNearBank) {

        }
        boolean inventoryIsNotSet = !Rs2Inventory.isFull();


        return true;
    }
}
