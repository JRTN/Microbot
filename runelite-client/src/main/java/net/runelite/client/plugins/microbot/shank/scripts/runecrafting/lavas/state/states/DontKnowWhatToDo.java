package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.states;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.AbstractState;

@Slf4j
public class DontKnowWhatToDo extends AbstractState {
    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public void execute() {

    }

    @Override
    public boolean evaluate() {
        log.info("This is the default state. Always true unless another evaluates first");
        return true;
    }
}
