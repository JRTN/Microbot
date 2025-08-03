package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state;

public abstract class AbstractState implements State, Comparable<State> {
    @Override
    public int compareTo(State other) {
        return Integer.compare(this.getOrder(), other.getOrder());
    }

    public String getTitle() {
        return String.format("%s (order %d)", this.getClass().getSimpleName(), this.getOrder());
    }
}
