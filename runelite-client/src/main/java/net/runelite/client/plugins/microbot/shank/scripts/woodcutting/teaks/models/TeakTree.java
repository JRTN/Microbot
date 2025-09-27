package net.runelite.client.plugins.microbot.shank.scripts.woodcutting.teaks.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.coords.Rs2WorldPoint;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum TeakTree {
    NORTH_WEST(new WorldPoint(3702, 3837, 0)),
    NORTH_EAST(new WorldPoint(3715, 3835, 0)),
    SOUTH_WEST(new WorldPoint(3708, 3833, 0));

    private final WorldPoint location;

    private static final int TREE_OBJECT_ID = 30445;
    private static final int STUMP_OBJECT_ID = 30446;
    public static final int PATCH_TILE_ID = 7517;

    private static final Map<WorldPoint, TeakTree> TEAK_TREE_BY_WORLDPOINT = Map.of(
            NORTH_WEST.getLocation(), NORTH_WEST,
            NORTH_EAST.getLocation(), NORTH_EAST,
            SOUTH_WEST.getLocation(), SOUTH_WEST
    );

    public static boolean isTeakTree(int objectId) {
        return TREE_OBJECT_ID == objectId;
    }

    public static boolean isTeakStump(int objectId) {
        return STUMP_OBJECT_ID == objectId;
    }

    public static TeakTree atWorldPoint(Rs2WorldPoint worldPoint) {
        return atWorldPoint(worldPoint.getWorldPoint());
    }

    public static TeakTree atWorldPoint(WorldPoint worldPoint) {
        return  TEAK_TREE_BY_WORLDPOINT.get(worldPoint);
    }
}
