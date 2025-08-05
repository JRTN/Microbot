package net.runelite.client.plugins.microbot.shank.api.fluent;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentAntiban;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;

import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent.antiban;
import static net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent.when;

@Slf4j
public abstract class AbstractFluentScript extends Script {

    @Override
    public boolean run() {
        try {
            when(!super.run()).throwException("Script failed to initialize");

            initialize();
            configureAntiban();
            schedule();
        } catch (Exception ex) {
            log.error("Cannot start script: {}", ex.getMessage(), ex);
            return false;
        }

        return true;
    }

    protected boolean initialize() {
        when(!Microbot.isLoggedIn()).throwException("Player is not logged in");

        return true;
    }

    protected boolean schedule() {
        mainScheduledFuture =
                scheduledExecutorService.scheduleWithFixedDelay(
                        this::onLoop, 0, pollingRate(), TimeUnit.MILLISECONDS);

        return true;
    }

    protected boolean configureAntiban() {
        antiban().configure(config -> {
            config.setActivityIntensity(ActivityIntensity.EXTREME);
            config.setActivity(Activity.GENERAL_MINING);

            config.setActionCooldownChance(0.85);
            config.setMicroBreakChance(0.05);
            config.setMicroBreakDurationMax(1);
            config.setMicroBreakDurationMin(1);
            config.setMouseRandomChance(0.75);
            config.setMouseOffScreenChance(0.00);
        });

        return true;
    }

    protected abstract void onLoop();
    protected abstract long pollingRate();
}
