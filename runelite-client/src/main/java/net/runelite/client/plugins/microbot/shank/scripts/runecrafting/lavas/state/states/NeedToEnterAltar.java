package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.states;

import net.runelite.client.plugins.microbot.runecrafting.chillRunecraft.Altars;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.AbstractState;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

public class NeedToEnterAltar extends AbstractState {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void execute() {
        Altars fireAltar = Altars.FIRE_ALTAR;
        Rs2GameObject.interact(fireAltar.getAltarRuinsID(), "enter");

        Global.sleepUntil(() -> Rs2Player.getWorldLocation().getRegionID() == 10315, 15000);
    }

    @Override
    public boolean evaluate() {
        var isInDesert = Rs2Player.getWorldLocation().getRegionID() == 13107;
        return isInDesert;
    }
}
