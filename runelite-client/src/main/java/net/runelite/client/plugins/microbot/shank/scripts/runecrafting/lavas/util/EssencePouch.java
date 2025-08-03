package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.util;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.microbot.qualityoflife.scripts.pouch.Pouch;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import java.util.Set;

@Slf4j
public class EssencePouch {

    private static Set<Integer> STANDARD = Set.of(
            ItemID.RCU_POUCH_SMALL,
            ItemID.RCU_POUCH_MEDIUM,
            ItemID.RCU_POUCH_LARGE,
            ItemID.RCU_POUCH_GIANT
    );

    private static Set<Integer> DEGRADED = Set.of(
            ItemID.RCU_POUCH_MEDIUM_DEGRADE,
            ItemID.RCU_POUCH_LARGE_DEGRADE,
            ItemID.RCU_POUCH_GIANT_DEGRADE
    );

    public static boolean hasDegradedPouch() {
        return Rs2Inventory.items().anyMatch(item -> DEGRADED.contains(item.getId()));
    }

    public static boolean fillPouch(Pouch pouch) {
        if (pouch.isUnknown()) {
            pouch.check();
        }

        if (pouch.getHolding() != 0) {
            log.info("Pouch {} already contains essence. Not filling...", pouch.name());
            return true;
        }

        var startEssCount = Rs2Inventory.count(ItemID.BLANKRUNE_HIGH);
        log.info("Inventory essence count before: {}", startEssCount);

        log.info("Filling pouch: {}", pouch.name());
        var pouchItem = Rs2Inventory.get(pouch.getItemIds());
        Rs2Inventory.interact(pouchItem, "fill");
        var filled = Global.sleepUntil(() -> pouch.getHolding() > 0, 3000);
        if (!filled) {
            pouch.check();
        }

        return pouch.getHolding() > 0;
    }

}
