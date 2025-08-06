package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import net.runelite.api.AnimationID;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentPlayer;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.player.Rs2PlayerModel;

public class FluentPlayerImpl implements FluentPlayer {
    @Override
    public Rs2PlayerModel getLocalPlayer() {
        return Rs2Player.getLocalPlayer();
    }

    @Override
    public boolean isIdle() {
        return getLocalPlayer().getAnimation() == AnimationID.IDLE;
    }
}
