package net.runelite.client.plugins.microbot.shank.api.fluent.api;

import net.runelite.api.Player;
import net.runelite.client.plugins.microbot.util.player.Rs2PlayerModel;

public interface FluentPlayer {
    Rs2PlayerModel getLocalPlayer();
    boolean isIdle();
}
