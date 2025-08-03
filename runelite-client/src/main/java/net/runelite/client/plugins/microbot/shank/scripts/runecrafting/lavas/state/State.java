package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state;

import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.states.DontKnowWhatToDo;

public interface State {
    State DEFAULT = new DontKnowWhatToDo();

    int getOrder();
    void execute();
    boolean evaluate();
    String getTitle();

    static boolean is(State state1, State state2) {
        return state1 == state2;
    }
}
