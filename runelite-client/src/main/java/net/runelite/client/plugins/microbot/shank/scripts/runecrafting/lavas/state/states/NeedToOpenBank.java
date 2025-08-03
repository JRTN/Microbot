package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.states;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.AbstractState;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

@Slf4j
public class NeedToOpenBank extends AbstractState {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void execute() {
        log.info("Opening bank...");
        Rs2Bank.openBank();
        Global.sleepUntil(Rs2Bank::isOpen);

        if (Rs2Bank.isOpen()) {
            log.info("Successfully opened bank");
        } else {
            log.info("Failed to open bank...");
        }
    }

    @Override
    public boolean evaluate() {
        var isAtFarmingGuild = Rs2Npc.getNpc("guildmaster jane") != null;
        var inventoryIsNotFull = !Rs2Inventory.isFull();
        var bankIsNotOpen = !Rs2Bank.isOpen();

        return isAtFarmingGuild && inventoryIsNotFull && bankIsNotOpen;
    }
}
