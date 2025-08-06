package net.runelite.client.plugins.microbot.shank.api.fluent.api.combat;

import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;

/**
 * Fluent API for managing special attack energy and usage.
 *
 * <p>This interface provides methods to check special attack energy levels, availability,
 * and to execute special attacks. Energy values range from 0 to 1000.</p>
 */
public interface FluentSpecialAttack {

    /**
     * Checks if a special attack can be used with the current weapon and energy level.
     *
     * @return true if a special attack can be used, false otherwise
     * @see #canUse(int)
     */
    boolean canUse();

    /**
     * Checks if a special attack can be used and the current energy meets the specified threshold.
     *
     * @param threshold The minimum energy level required (0-1000)
     * @return true if special attack can be used and energy is at least the threshold, false otherwise
     * @see #canUse()
     */
    boolean canUse(int threshold);

    /**
     * Checks if the special attack energy is at maximum (1000).
     *
     * @return true if special attack energy is at 1000, false otherwise
     * @see #isLow()
     * @see #getEnergy()
     */
    boolean isFull();

    /**
     * Checks if the special attack energy is not at maximum capacity.
     *
     * @return true if special attack energy is below 1000, false if at maximum
     * @see #isFull()
     * @see #getEnergy()
     */
    boolean isLow();

    /**
     * Gets the current special attack energy level.
     *
     * @return The current special attack energy (0-1000)
     * @see #isFull()
     * @see #isLow()
     */
    int getEnergy();

    /**
     * Creates an action to activate the special attack for the currently equipped weapon.
     *
     * <p>The action succeeds if the special attack is successfully activated, and fails
     * if requirements are not met (insufficient energy, no weapon equipped, etc.).</p>
     *
     * @return An Action that activates the special attack
     * @see #canUse()
     */
    Action use();
}
