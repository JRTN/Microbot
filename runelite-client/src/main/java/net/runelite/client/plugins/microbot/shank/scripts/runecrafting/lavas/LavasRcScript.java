package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.info.ScriptInfo;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.State;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.states.*;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LavasRcScript extends Script {

    @Getter private ScriptInfo scriptInfo;
    final LavasRcConfig config;

    static final List<State> states;

    static {
        states = new ArrayList<>();

        states.add(new NeedToOpenBank());
        states.add(new NeedToDoBanking());
        states.add(new NeedToRepairPouches());
        states.add(new NeedToTeleportToDesert());
        states.add(new NeedToEnterAltar());
        states.add(new NeedToCraftRunes());
    }

    @Inject
    public LavasRcScript(LavasRcConfig config) {
        this.config = config;
    }


    public boolean run(ScriptInfo scriptInfo) {
        if (!Microbot.isLoggedIn()) {
            return false;
        }

        if (!super.run()) {
            return false;
        }

        Microbot.enableAutoRunOn = false;
        Rs2Antiban.resetAntibanSettings();

        Rs2Antiban.setActivity(Activity.KILLING_ZULRAH_MAX_EFFICIENCY);

        this.scriptInfo = scriptInfo;

        mainScheduledFuture =
                scheduledExecutorService
                        .scheduleWithFixedDelay(this::scriptLoop, 0, 300, TimeUnit.MILLISECONDS);
        return true;
    }

    void scriptLoop() {
        scriptInfo.setCurrentState(evaluateCurrentState());
        scriptInfo.getCurrentState().execute();
    }

    State evaluateCurrentState() {
        State newState = states.stream()
                .sorted()
                .filter(State::evaluate).findFirst()
                .orElse(State.DEFAULT);

        if (State.is(scriptInfo.getCurrentState(), newState)) {
            return scriptInfo.getCurrentState();
        }

        log.info("Script state updated. Previous: [{}] New: [{}]", scriptInfo.getCurrentState(), newState);

        return newState;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }


}
