package net.runelite.client.plugins.microbot.shank.api.fluent.impl.combat;

import net.runelite.client.plugins.microbot.shank.api.fluent.api.combat.FluentSpecialAttack;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;

public class FluentSpecialAttackImpl implements FluentSpecialAttack {

    @Override
    public boolean canUse() {
        return false;
    }

    @Override
    public boolean canUse(int threshold) {
        return false;
    }

    @Override
    public boolean isFull() {
        return getEnergy() == 1000;
    }

    @Override
    public boolean isLow() {
        return !isFull();
    }

    @Override
    public int getEnergy() {
        return Rs2Combat.getSpecEnergy();
    }

    @Override
    public Action use() {
        return () -> {
            try {
                return Rs2Combat.setSpecState(true);
            } catch (Exception ex) {
                return false;
            }
        };
    }
}
