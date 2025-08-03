package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.states;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.AbstractState;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.util.EssencePouch;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.magic.Rs2Spells;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

@Slf4j
public class NeedToRepairPouches extends AbstractState {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void execute() {
        if (!Rs2Magic.cast(MagicAction.NPC_CONTACT, "dark mage", 2)) {
            log.warn("Failed to cast NPC Contact, will try again in a moment");
        }

        Rs2Dialogue.sleepUntilHasContinue();
        Rs2Dialogue.clickContinue();

        Rs2Dialogue.sleepUntilHasDialogueOption("Can you repair my pouches?");
        Rs2Dialogue.keyPressForDialogueOption(2);

        Rs2Dialogue.sleepUntilHasContinue();
        Rs2Dialogue.clickContinue();

        boolean arePouchesRepaired = Global.sleepUntilTrue(() -> !EssencePouch.hasDegradedPouch(), 150, 5000);

        if (arePouchesRepaired) {
            log.info("Pouches repaired");
        } else {
            log.info("Inventory still contains degraded pouch. Repair unsuccessful.");
        }
    }

    @Override
    public boolean evaluate() {

        if (!Rs2Spells.NPC_CONTACT.hasRequirements()) {
            log.debug("Player does not have the requirements for NPC contact");
            return false;
        }

        if (!Rs2Magic.hasRequiredRunes(Rs2Spells.NPC_CONTACT)) {
            log.debug("Player does not have the required runes for NPC contact");
            return false;
        }

        if (Rs2Inventory.hasDegradedPouch()) {
            log.debug("Player has degraded pouch");
            return true;
        }

        log.debug("Unknown state in RepairDamagedPouches -- returning false");
        return false;
    }
}
