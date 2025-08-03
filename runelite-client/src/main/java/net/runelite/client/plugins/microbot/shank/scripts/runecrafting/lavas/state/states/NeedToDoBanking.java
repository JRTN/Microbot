package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.states;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.microbot.qualityoflife.scripts.pouch.Pouch;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.AbstractState;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.util.Banking;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class NeedToDoBanking extends AbstractState {

    private final Set<Integer> CRITICAL_ITEMS = Set.of(
            ItemID.EARTHRUNE,
            ItemID.SKILLCAPE_FARMING,
            ItemID.RING_OF_ELEMENTS_CHARGED,
            ItemID.BH_RUNE_POUCH,
            ItemID.BLANKRUNE_HIGH
    );

    private final Set<Integer> CRITICAL_EQUIPMENT = Set.of(
            ItemID.TIARA_ELEMENTAL,
            ItemID.MAGIC_EMERALD_NECKLACE
    );

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void execute() {

        if (!handleItemsToDeposit()) {
            log.error("Couldn't deposit items. Returning...");
            return;
        }

        if (!handleCriticalEquipment()) {
            log.error("Couldn't equip critical equipment. Returning...");
            return;
        }

        if (!handleCriticalItems()) {
            log.error("Couldn't withdraw critical items. Returning...");
            return;
        }

        if (!handleFillAllPouches()) {
            log.error("Couldn't fill essence pouches. Returning...");
            return;
        }

        if (!handleEssence()) {

        }

        Global.sleepUntil(Rs2Inventory::isFull);
        log.info("Banking completed. Closing bank...");
        var bankClosed = Rs2Bank.closeBank();

        if (bankClosed) {
            log.info("Successfully closed bank.");
        } else {
            log.info("Failed to close bank.");
        }
    }

    private boolean handleItemsToDeposit() {
        log.info("Depositing all items that are not necessary");

        var excludedItemsFromDeposit =
                Stream.concat(CRITICAL_ITEMS.stream(), CRITICAL_EQUIPMENT.stream())
                        .collect(Collectors.toSet());

        Set<Integer> pouchItemIds = Arrays.stream(Pouch.values())
                .flatMapToInt(pouch -> Arrays.stream(pouch.getItemIds()))
                .boxed()
                .collect(Collectors.toSet());

        excludedItemsFromDeposit.addAll(pouchItemIds);

        var itemsToDeposit =
                Rs2Inventory.items(item -> !excludedItemsFromDeposit.contains(item.getId()))
                        .collect(Collectors.toList());
        log.info("Depositing {} items", itemsToDeposit.size());

        var allItemsDeposited = true;
        for (Rs2ItemModel itemToDeposit : itemsToDeposit) {
            log.info("Depositing all {}", itemToDeposit.getName());
            Banking.deposit(itemToDeposit, Banking.Quantity.ALL);

            var successfullyDepositedItem = !Rs2Inventory.hasItem(itemToDeposit.getId());
            if (successfullyDepositedItem) {
                log.info("Successfully deposited all {}", itemToDeposit.getName());
            } else {
                log.warn("Failed to deposit all {}", itemToDeposit.getName());
            }
            allItemsDeposited &= successfullyDepositedItem;
        }

        log.info("Finished depositing all items. All succeeded?: {}", allItemsDeposited);
        return allItemsDeposited;
    }

    private boolean handleCriticalEquipment() {
        log.info("Ensuring all necessary equipment is equipped");
        for (int equipment : CRITICAL_EQUIPMENT) {
            if (Rs2Equipment.isWearing(equipment)) {
                log.info("{} already equipped!", Rs2Equipment.get(equipment).getName());
                continue;
            }

            if (!Rs2Bank.hasItem(equipment)) {
                log.error("Need to withdraw item {} but cannot find it in bank", equipment);
                return false;
            }

            var itemModel = Rs2Bank.getBankItem(equipment);

            log.info("{} not yet equipped. Equipping...", itemModel.getName());
            Banking.withdrawAndWear(itemModel);
        }

        if (Rs2Equipment.isWearing(CRITICAL_EQUIPMENT.stream().mapToInt(Integer::intValue).toArray())) {
            log.info("All necessary equipment is now worn.");
            return true;
        } else {
            log.error("Failed to equip the necessary items.");
            return false;
        }
    }

    private boolean handleCriticalItems() {
        log.info("Ensuring all necessary items are withdrawn");
        for (int item : CRITICAL_ITEMS) {
            if (Rs2Inventory.hasItem(item)) {
                log.info("{} already withdrawn!", Rs2Inventory.get(item).getName());
                continue;
            }

            if (!Rs2Bank.hasItem(item)) {
                log.error("Need to withdraw item {} but cannot find it in bank", item);
                return false;
            }

            var itemModel = Rs2Bank.getBankItem(item);
            log.info("{} not yet withdrawn. Withdrawing...", itemModel.getName());

            Banking.withdraw(itemModel, Banking.Quantity.ALL);
        }

        if (Rs2Inventory.hasItem(CRITICAL_ITEMS.stream().mapToInt(Integer::intValue).toArray())) {
            log.info("All necessary equipment is now withdrawn.");
            return true;
        } else {
            log.error("Failed to withdraw the necessary items.");
            return false;
        }


    }

    private boolean handleFillAllPouches() {

        //var giantPouchFilled = handleFillSinglePouch("giant pouch");
        var smallPouchFilled = handleFillSinglePouch("small pouch");
        var mediumPouchFilled = handleFillSinglePouch("medium pouch");
        var largePouchFilled = handleFillSinglePouch("large pouch");

        return smallPouchFilled && mediumPouchFilled && largePouchFilled;
    }

    private boolean handleFillSinglePouch(String pouchName) {
        if (!Rs2Inventory.hasItem(pouchName)) {
            return false;
        }



        if (!Rs2Inventory.hasItem(ItemID.BLANKRUNE_HIGH)) {
            log.info("Do not have the items to fill pouch {}. Withdrawing essence...", pouchName);
            boolean withdrawEssResult = handleEssence();

            if (!withdrawEssResult) {
                return false;
            }
        }

        Rs2Inventory.interact(pouchName, "fill");

        Global.sleepGaussian(87, 11);
        return true;
    }

    private boolean handleEssence() {
        if (Rs2Inventory.isFull()) {
            log.info("Inventory is full. Cannot withdraw essence at this time...");
            return true;
        }

        if (!Rs2Bank.hasItem(ItemID.BLANKRUNE_HIGH)) {
            log.error("No pure essence to withdraw!!");
            return false;
        }

        log.info("Withdrawing all essence...");
        return Rs2Bank.withdrawAll(ItemID.BLANKRUNE_HIGH);
    }

    @Override
    public boolean evaluate() {
        if (!Rs2Bank.isOpen()) {
            return false;
        }

        return true;
    }
}
