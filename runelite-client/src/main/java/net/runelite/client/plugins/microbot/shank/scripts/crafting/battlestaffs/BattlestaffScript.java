package net.runelite.client.plugins.microbot.shank.scripts.crafting.battlestaffs;

import static net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent.*;

import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.microbot.shank.api.fluent.AbstractFluentScript;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentAntiban;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;
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
    protected void configureAntiban(FluentAntiban.Config config) {
        config.resetSettings();

        config.enable();
        config.enableNaturalMouse();
        config.enablePlayStyle();
        config.enableNonLinearIntervals();
        config.enableRandomMouseMovement();

        config.setMouseRandomChance(0.63);

        config.setActivityIntensity(ActivityIntensity.CUSTOM);
        config.setActivity(Activity.HIGH_INTENSITY_SKILLING);
    }

    @Override
    protected void onLoop() {
        var airOrb = ItemID.AIR_ORB;
        var battlestaff = ItemID.BATTLESTAFF;

        when(needToBank()
                && bank().isOpen())
                .then(bank().deposit().all())
                .then(bank().withdraw().x(battlestaff, 14))
                .then(bank().withdraw().x(airOrb, 14))
                .then(bank().close())
                .then(timing().sleepUntil(this::haveNecessaryMaterials, pollingRate(), 3000));

        when(!needToBank()
                && bank().isClosed())
                .then(inventory().combine(battlestaff, airOrb))
                .then(timing().sleepUntil(this::isCraftingInterfaceOpened))
                .then(this::craftAllBattlestaffs)
                .then(timing().sleep(300, 20))
                .then(antiban().moveMouseRandomly())
                .then(antiban().moveMouseOffScreen())
                .then(timing().sleepUntil(this::needToBank));

        when(needToBank()
                && bank().isClosed())
                .then(bank().open())
                .then(timing().sleepUntil(bank()::isOpen));
    }

    private boolean needToBank() {
        return !haveNecessaryMaterials();
    }

    private boolean haveNecessaryMaterials() {
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
    protected int pollingRate() {
        return 60;
    }
}
