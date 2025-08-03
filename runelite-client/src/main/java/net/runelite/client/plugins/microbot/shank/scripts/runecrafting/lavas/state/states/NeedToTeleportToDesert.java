package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.states;

import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.AbstractState;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

public class NeedToTeleportToDesert extends AbstractState {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void execute() {
        Rs2Inventory.interact("ring of the elements", "last destination");
        Global.sleepUntil(() -> Rs2Player.getWorldLocation().getRegionID() == 13107); //Is at desert teleport spot
    }

    @Override
    public boolean evaluate() {
        var isAtFarmGuild = Rs2Player.getWorldLocation().getRegionID() == 4922;
        var isBankClosed = !Rs2Bank.isOpen();
        var isInventoryFull = Rs2Inventory.isFull();
        var hasPureEssence = Rs2Inventory.hasItem(ItemID.BLANKRUNE_HIGH);
        var hasEarthRune = Rs2Inventory.hasItem(ItemID.EARTHRUNE);
        var hasTiara = Rs2Equipment.isWearing(ItemID.TIARA_ELEMENTAL);

        return isAtFarmGuild && isBankClosed && isInventoryFull && hasPureEssence && hasEarthRune && hasTiara;
    }
}
