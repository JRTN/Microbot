package net.runelite.client.plugins.microbot.shank.scripts.mining.m1d1;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.PlayStyle;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent.*;

@Slf4j
public class M1D1Script extends Script {
    private final M1D1Config config;
    private static final int IRON_ORE_ID = -1;
    private static final int IRON_ORE_ROCK_ID = 11364;
    private static final int MAX_IRON_ORE_COUNT = 3;

    // Track the next rock to mine to maintain consistency
    private TileObject nextRockToMine = null;

    @Inject
    public M1D1Script(M1D1Config config) {
        this.config = config;
    }

    @Override
    public boolean run() {
        when(!Microbot.isLoggedIn())
                .throwException("Player is not logged in");

        when(!super.run())
                .throwException("Script failed to initialize");

        Rs2Antiban.setActivity(Activity.GENERAL_MINING);
        Rs2Antiban.setPlayStyle(PlayStyle.EXTREME_AGGRESSIVE);

        mainScheduledFuture = scheduledExecutorService
                .scheduleWithFixedDelay(this::onLoop, 0, 75, TimeUnit.MILLISECONDS);

        return true;
    }

    void onLoop() {
        when(playerDoesNotHavePickaxe())
                .throwException("Player does not have pickaxe - cannot continue mining!");

        when(canUseSpecialAttack())
                .then(this::activateSpecialAttack)
                .waitUntil(this::hasSpecialEnergyDecreased, 500, 3000);

        when(inventory().count(IRON_ORE_ID) >= MAX_IRON_ORE_COUNT)
                .then(inventory().dropAll(IRON_ORE_ID));

        when(shouldMineRock())
                .then(this::mineRock)
                .onFailure(this::logMiningFailure);
    }

    // Condition methods
    boolean playerDoesNotHavePickaxe() {
        return !Rs2Equipment.isWearing(item -> item.getName().endsWith("pickaxe"))
                && !Rs2Inventory.hasItem("pickaxe", false);
    }

    boolean canUseSpecialAttack() {
        return Rs2Combat.getSpecEnergy() == 1000 && !Rs2Combat.getSpecState();
    }

    boolean shouldDropIronOre() {
        return Rs2Inventory.count("iron ore") >= MAX_IRON_ORE_COUNT;
    }

    boolean shouldMineRock() {
        return !Rs2Inventory.isFull() && !Rs2Player.isAnimating();
    }

    boolean isPlayerAnimating() {
        return Rs2Player.isAnimating();
    }

    boolean hasReceivedXpDrop(int startXp) {
        int currentXp = Microbot.getClient().getSkillExperience(Skill.MINING);
        return currentXp > startXp;
    }

    boolean hasSpecialEnergyDecreased() {
        return Rs2Combat.getSpecEnergy() < 1000;
    }

    boolean hasNoIronOre() {
        return !Rs2Inventory.contains("iron ore");
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
        //Next action will be drop
        if (Rs2Inventory.count("iron ore") == (MAX_IRON_ORE_COUNT - 1)) {
            Rs2Inventory.hover(Rs2Inventory.get("iron ore"));

            return true;
        }
        // Select the next rock and hover over it for the next iteration
        nextRockToMine = selectNextRock(currentRock);
        if (nextRockToMine != null) {
            log.debug("Hovering over next rock at {} for next iteration", nextRockToMine.getWorldLocation());
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

    // Complex action methods
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
}
