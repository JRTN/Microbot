package net.runelite.client.plugins.microbot.shank.scripts.woodcutting.teaks.context;

import lombok.Getter;
import lombok.Setter;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shank.base.ScriptContext;
import net.runelite.client.plugins.microbot.shank.scripts.woodcutting.teaks.models.CycleStep;
import net.runelite.client.plugins.microbot.shank.scripts.woodcutting.teaks.models.DropStrategy;
import net.runelite.client.plugins.microbot.shank.scripts.woodcutting.teaks.models.TeakTree;
import net.runelite.client.plugins.microbot.util.coords.Rs2WorldPoint;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A unified context holding all state for the 1.5-tick Teak Woodcutting script.
 * This class is self-updating by listening to game events.
 */
@Singleton
@Getter
@Setter
@Slf4j
public class TeakWoodcutterContext implements ScriptContext {

    public static final Set<Integer> WOODCUTTING_ANIMATIONS = Stream.of(
            879, 877, 875, 873, 871, 869, 867, 8303, 2846, 24, 2117, 7264, 8324, 8778,
            10064, 10065, 10066, 10067, 10068, 10069, 10070, 10071, 10072, 10073, 10074,
            3291, 3290, 3289, 3288, 3287, 3286, 3285, 8305, 3292, 23, 2116, 8777, 7266, 8323, 8327, 8780,
            10517, 10518, 10519, 10520, 10521, 10522, 10523, 10524, 10525, 10526, 10527
    ).collect(Collectors.toUnmodifiableSet());

    private final EventBus eventBus;

    // == Player State ==
    private Rs2WorldPoint currentLocation;
    private int currentAnimationId = -1;

    // == Inventory State ==
    private boolean isInventoryFull;
    private boolean hasNewLogToDrop;
    private int teakLogCount = 0;

    // == Tick Manipulation State ==
    private long skillingTick = 0;
    private CycleStep currentCycleStep = CycleStep.PERFORM_ACTION_AND_MOVE;

    Map<TeakTree, Boolean> treeStatuses = Map.of(
            TeakTree.NORTH_WEST, true,
            TeakTree.NORTH_EAST, true,
            TeakTree.SOUTH_WEST, true
    );

    public TeakWoodcutterContext() {
        this.eventBus = Microbot.getEventBus();
        this.eventBus.register(this);
    }

    public void shutdown() {
        this.eventBus.unregister(this);
    }

    // == Helper Methods ==

    private void markTreeAsValid(TeakTree tree) {
        if (tree == null) {
            return;
        }

        treeStatuses.put(tree, true);
        log.info("[STATE_CHANGE] Tree status updated: {} -> VALID", tree.name());
    }

    private void markTreeAsInvalid(TeakTree tree) {
        if (tree == null) {
            return;
        }

        treeStatuses.put(tree, false);
        log.info("[STATE_CHANGE] Tree status updated: {} -> INVALID", tree.name());
    }

    public boolean shouldDropLogs(DropStrategy strategy) {
        if (strategy == DropStrategy.DROP_ONE_BY_ONE && hasNewLogToDrop) {
            this.hasNewLogToDrop = false; // Consume the flag
            return true;
        }
        if (strategy == DropStrategy.DROP_WHEN_FULL && isInventoryFull) {
            return true;
        }
        return false;
    }

    public boolean isWoodcutting() {
        return WOODCUTTING_ANIMATIONS.contains(this.currentAnimationId);
    }

    public long getSkillingTimer(long globalTickCounter) {
        return skillingTick - globalTickCounter;
    }

    // == Event Listeners ==

    @Subscribe
    public void onGameTick(GameTick event) {
        this.currentLocation = Rs2Player.getRs2WorldPoint();
        var trees =
                Arrays.stream(TeakTree.values())
                        .map(TeakTree::getLocation)
                        .map(Rs2GameObject::getGameObject)
                        .collect(Collectors.toList());
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        var actor = event.getActor();
        if (actor != Microbot.getClient().getLocalPlayer()) {
            return;
        }
        this.currentAnimationId = actor.getAnimation();
        log.info("[STATE_CHANGE] Animation updated: {} isWoodcutting: {}", this.currentAnimationId, this.isWoodcutting());
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        var gameObject = event.getGameObject();
        if (TeakTree.isTeakTree(gameObject.getId())) {
            markTreeAsValid(TeakTree.atWorldPoint(gameObject.getWorldLocation()));
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        var gameObject = event.getGameObject();
        if (TeakTree.isTeakTree(gameObject.getId())) {
            markTreeAsInvalid(TeakTree.atWorldPoint(gameObject.getWorldLocation()));
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() != InventoryID.INV) {
            return;
        }

        boolean wasInventoryFull = this.isInventoryFull;
        int previousLogCount = this.teakLogCount;

        this.isInventoryFull = Rs2Inventory.isFull();
        this.teakLogCount = Rs2Inventory.count(ItemID.TEAK_LOGS);
        this.hasNewLogToDrop = this.teakLogCount > previousLogCount;

        if (wasInventoryFull != this.isInventoryFull || this.hasNewLogToDrop) {
            log.info("[STATE_CHANGE] Inventory updated: isFull={}, hasNewLog={}, logCount={}", this.isInventoryFull, this.hasNewLogToDrop, this.teakLogCount);
        }
    }
}
