package net.runelite.client.plugins.microbot.shank.scripts.crafting.battlestaffs;

import static net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent.*;

import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.microbot.shank.api.fluent.AbstractFluentScript;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import javax.inject.Inject;

public class BattlestaffScript extends AbstractFluentScript {

    private final BattlestaffConfig config;

    @Inject
    public BattlestaffScript(BattlestaffConfig config) {
        this.config = config;
    }

    @Override
    protected void onLoop() {
        var airOrb = ItemID.AIR_ORB;
        var battlestaff = ItemID.BATTLESTAFF;

        when(hasIngredients() && bank().isClosed())
                .then(inventory().useOn(battlestaff, airOrb))
                .then(timing().sleepUntil(this::isCraftingInterfaceOpened))
                .then(this::craftAllBattlestaffs)
                .then(timing().sleepUntil(() -> !hasIngredients(), pollingRate(), 30_000));

        when(!hasIngredients() && bank().isClosed())
                .then(bank().open())
                .then(timing().sleepUntil(bank()::isOpen));

        when(!hasIngredients() && bank().isOpen())
                .then(bank().deposit().all())
                .then(timing().sleepUntil(inventory()::isEmpty))
                .then(bank().withdraw().x(battlestaff, 14))
                .then(timing().sleepUntil(() -> inventory().containsItem(battlestaff)))
                .then(bank().withdraw().x(airOrb, 14))
                .then(timing().sleepUntil(() -> inventory().containsItem(airOrb)));

        when(hasIngredients() && bank().isOpen())
                .then(bank().close());
    }

    private boolean hasIngredients() {
        var airOrb = ItemID.AIR_ORB;
        var battlestaff = ItemID.BATTLESTAFF;

        return inventory().containsItem(airOrb)
                && inventory().containsItem(battlestaff);
    }

    private boolean isCraftingInterfaceOpened() {
        return Rs2Widget.getWidget(17694734) != null;
    }

    private boolean craftAllBattlestaffs() {
        Rs2Keyboard.keyPress('1');
        return true;
    }

    @Override
    protected long pollingRate() {
        return 60;
    }
}
