package net.runelite.client.plugins.microbot.shank.api.fluent;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
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
            antiban().configure(this::configureAntiban);
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
                        this::_onLoop, 0, 5, TimeUnit.MILLISECONDS);

        return true;
    }

    private void _onLoop() {
        onLoop();
        sleep(pollingRate());
    }

    protected abstract void configureAntiban(FluentAntiban.Config config);
    protected abstract void onLoop();
    protected abstract int pollingRate();
}
