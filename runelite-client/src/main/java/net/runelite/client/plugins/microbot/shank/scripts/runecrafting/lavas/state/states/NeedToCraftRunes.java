package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.states;

import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.microbot.runecrafting.chillRunecraft.Altars;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.AbstractState;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

public class NeedToCraftRunes extends AbstractState {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void execute() {
        var fireAltar = Rs2GameObject.getGameObject(Altars.FIRE_ALTAR.getAltarID());
        Rs2Inventory.useItemOnObject(ItemID.EARTHRUNE, Altars.FIRE_ALTAR.getAltarID());
        Global.sleepGaussian(100, 11);

        Rs2Magic.cast(MagicAction.MAGIC_IMBUE);

        Rs2Inventory.useItemOnObject(ItemID.EARTHRUNE, Altars.FIRE_ALTAR.getAltarID());
        Global.sleepUntil(() -> Rs2Inventory.contains(ItemID.LAVARUNE), 7000);

        //Rs2Inventory.emptyPouches();
        Rs2Inventory.interact("small pouch", "empty");
        Global.sleepGaussian(83, 9);
        Rs2Inventory.interact("medium pouch", "empty");
        Global.sleepGaussian(83, 9);
        Rs2Inventory.interact("large pouch", "empty");
        Global.sleepGaussian(83, 9);

        Rs2Inventory.useItemOnObject(ItemID.EARTHRUNE, Altars.FIRE_ALTAR.getAltarID());
        Global.sleepUntil(() -> !Rs2Inventory.hasItem(ItemID.BLANKRUNE_HIGH));

        Rs2Inventory.interact("farming cape", "teleport");
        Global.sleepUntil(() -> !evaluate(), 5000);
    }

    @Override
    public boolean evaluate() {
        var isInFireAltar = Rs2Player.getWorldLocation().getRegionID() == 10315;
        return isInFireAltar;
    }
}
