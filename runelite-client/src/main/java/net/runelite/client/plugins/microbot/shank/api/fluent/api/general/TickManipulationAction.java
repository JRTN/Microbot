package net.runelite.client.plugins.microbot.shank.api.fluent.api.general;

import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

/**
 * Fluent API for performing tick manipulation techniques.
 *
 * <p>Tick manipulation involves performing precise timing sequences to optimize certain activities.
 * This interface provides methods to define the timing actions and their execution order.
 */
public interface TickManipulationAction extends Action {

    TickManipulationAction action(Action mainAction);

    /**
     * Defines the action to perform before the main activity.
     *
     * <p>This is typically the tick manipulation action (like making tar, dropping items, etc.)
     * that needs to happen at precise timing before the main action.
     *
     * @param beforeAction The action to execute before the main activity
     * @return This TickManipulationAction for further configuration
     */
    TickManipulationAction before(Action beforeAction);

    /**
     * Defines the action to perform after the main activity completes.
     *
     * @param afterAction The action to execute after the main activity
     * @return This TickManipulationAction for further configuration
     */
    TickManipulationAction after(Action afterAction);

    /**
     * Defines the condition that must be met for the main activity to be considered complete.
     *
     * @param completionCondition The condition that indicates the main activity is done
     * @return This TickManipulationAction for further configuration
     */
    TickManipulationAction waitUntil(BooleanSupplier completionCondition);

    /**
     * Sets the maximum time to wait for the main activity to complete.
     *
     * @param timeoutMs The timeout in milliseconds
     * @return This TickManipulationAction for further configuration
     */
    TickManipulationAction timeout(long timeoutMs);

    /**
     * Sets a dynamic timeout supplier for the main activity.
     *
     * @param timeoutSupplier Supplier that provides the timeout value
     * @return This TickManipulationAction for further configuration
     */
    TickManipulationAction timeout(LongSupplier timeoutSupplier);

    /**
     * Sets the polling rate for checking conditions.
     *
     * @param pollingRateMs The polling rate in milliseconds
     * @return This TickManipulationAction for further configuration
     */
    TickManipulationAction pollingRate(long pollingRateMs);

    /**
     * Sets a dynamic polling rate supplier.
     *
     * @param pollingRateSupplier Supplier that provides the polling rate
     * @return This TickManipulationAction for further configuration
     */
    TickManipulationAction pollingRate(LongSupplier pollingRateSupplier);

    TickManipulationAction maxDuration(LongSupplier maxDuration);
    TickManipulationAction maxDuration(long maxDuration);

    /**
     * Configures the action to repeat the entire sequence.
     *
     * <p>When enabled, after completing one full cycle (before -> main -> after), the action will
     * immediately start the next cycle.
     *
     * @return This TickManipulationAction for further configuration
     */
    TickManipulationAction repeating();

    /**
     * Configures the action to repeat until a condition is met.
     *
     * @param stopCondition The condition that will stop the repetition
     * @return This TickManipulationAction for further configuration
     */
    TickManipulationAction repeatUntil(BooleanSupplier stopCondition);
}
