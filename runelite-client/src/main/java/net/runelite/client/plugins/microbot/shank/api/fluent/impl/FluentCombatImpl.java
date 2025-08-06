package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentCombat;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.combat.FluentSpecialAttack;
import net.runelite.client.plugins.microbot.shank.api.fluent.impl.combat.FluentSpecialAttackImpl;

public class FluentCombatImpl implements FluentCombat {

    @Override
    public FluentSpecialAttack specialAttack() {
        return new FluentSpecialAttackImpl();
    }
}
