package net.runelite.client.plugins.microbot.shank.scripts.mining.m1d1;

import static net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent.*;

import lombok.extern.slf4j.Slf4j;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameObject;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shank.api.fluent.AbstractFluentScript;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.List;

import javax.inject.Inject;

@Slf4j
public class M1D1Script extends AbstractFluentScript {
    private final M1D1Config config;
    private static final int IRON_ORE_ID = 440;
    private static final int IRON_ORE_ROCK_ID = 11364;
    private static final int MAX_IRON_ORE_COUNT = 3;

    // Track the next rock to mine to maintain consistency
    private TileObject nextRockToMine = null;

    @Inject
    public M1D1Script(M1D1Config config) {
        this.config = config;
    }

    @Override
    protected void onLoop() {
        when(doesNotHavePickaxe())
                .throwException("Player does not have pickaxe - cannot continue mining!");

        when(combat().specialAttack().canUse())
                .then(combat().specialAttack().use())
                .waitUntil(combat().specialAttack()::isLow);

        when(inventory().count(IRON_ORE_ID) >= MAX_IRON_ORE_COUNT)
                .then(inventory().dropAll(IRON_ORE_ID))
                .then(timing()
                        .sleepUntil(() -> !inventory().contains(IRON_ORE_ID), this::pollingRate, 5000)
                        .whileDoing(antiban().moveMouseOffScreen()))
                .then(antiban().actionCooldown());

        when(inventory().hasSpace() && !Rs2Player.isAnimating())
                .then(this::mineRock)
                .then(antiban().actionCooldown());
    }

    @Override
    protected long pollingRate() {
        return 75;
    }

    boolean mineRock() {
        var startXp = Microbot.getClient().getSkillExperience(Skill.MINING);
        var ironRock = getCurrentRockToMine();

        return when(ironRock != null)
                .then(() -> hoverOverRock(ironRock))
                .then(() -> clickRockIfNotMining(ironRock))
                .then(() -> selectAndHoverNextRock(ironRock))
                .waitUntil(() -> hasReceivedXpDrop(startXp), 50, 2000)
                .succeeded();
    }

    private static boolean doesNotHavePickaxe() {
        return !equipment()
                        .isWearingInSlot(
                                item -> item.getName().endsWith("pickaxe"),
                                EquipmentInventorySlot.WEAPON)
                && !inventory().getItems(item -> item.getName().endsWith("pickaxe")).isEmpty();
    }

    // Condition methods
    boolean hasReceivedXpDrop(int startXp) {
        int currentXp = Microbot.getClient().getSkillExperience(Skill.MINING);
        return currentXp > startXp;
    }

    List<GameObject> getAllAvailableRocks() {
        return Rs2GameObject.getGameObjects(obj -> obj.getId() == IRON_ORE_ROCK_ID, 1);
    }

    // Rock selection methods
    TileObject getCurrentRockToMine() {
        // If we have a next rock queued up and it's still valid, use it
        if (nextRockToMine != null && isRockValid(nextRockToMine)) {
            TileObject currentRock = nextRockToMine;
            nextRockToMine = null; // Clear it so we select a new next rock
            return currentRock;
        }

        // Otherwise, get any available iron ore rock
        return Rs2GameObject.getGameObject(IRON_ORE_ROCK_ID, 1);
    }

    boolean isRockValid(TileObject rock) {
        if (rock == null || rock.getId() != IRON_ORE_ROCK_ID) return false;

        List<GameObject> currentRocks = getAllAvailableRocks();
        return currentRocks.contains(rock);
    }

    TileObject selectNextRock(TileObject currentRock) {

        var allRocks = getAllAvailableRocks();

        for (TileObject rock : allRocks) {
            if (!rock.equals(currentRock)) {
                return rock;
            }
        }

        return currentRock;
    }

    // Action methods
    boolean hoverOverRock(TileObject rock) {
        log.debug("Hovering over iron ore rock at {}", rock.getWorldLocation());
        return Rs2GameObject.hoverOverObject(rock);
    }

    boolean clickRockIfNotMining(TileObject rock) {
        // Only click if player is not already mining
        if (!Rs2Player.isAnimating()) {
            log.debug("Clicking iron ore rock at {}", rock.getWorldLocation());
            return Rs2GameObject.interact(rock, "Mine");
        }
        log.debug("Player already mining, skipping click");
        return true; // Already mining, no need to click
    }

    boolean selectAndHoverNextRock(TileObject currentRock) {
        // Next action will be drop
        if (Rs2Inventory.count("iron ore") == (MAX_IRON_ORE_COUNT - 1)) {
            Rs2Inventory.hover(Rs2Inventory.get("iron ore"));

            return true;
        }
        // Select the next rock and hover over it for the next iteration
        nextRockToMine = selectNextRock(currentRock);
        if (nextRockToMine != null) {
            log.debug(
                    "Hovering over next rock at {} for next iteration",
                    nextRockToMine.getWorldLocation());
            Rs2GameObject.hoverOverObject(nextRockToMine);
        }
        return true;
    }

    boolean activateSpecialAttack() {
        log.debug("Using special attack");
        return Rs2Combat.setSpecState(true);
    }

    boolean logMiningFailure() {
        log.warn("Failed to mine rock, will retry next loop");
        // Clear the next rock selection on failure to get a fresh selection
        nextRockToMine = null;
        return true;
    }
}
