package net.runelite.client.plugins.microbot.shank.api.fluent.api.sleep;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Fluent API for timing and sleep operations in Old School RuneScape automation.
 *
 * <p>This interface provides methods to introduce delays, wait for conditions, and repeat
 * actions with various timing strategies. Proper timing is crucial for creating natural-looking
 * bot behavior and avoiding detection by anti-bot systems.</p>
 *
 * <h2>Basic Usage</h2>
 * <pre>{@code
 * // Simple sleep with jitter for natural timing
 * timing().sleep(1000, 200)
 *     .then(inventory().drop("Trout"))
 *     ;
 *
 * // Wait for a condition with timeout
 * timing().sleepUntil(() -> player().isIdle(), () -> 100, 5000)
 *     .then(log("Player finished moving"))
 *     ;
 * }</pre>
 *
 * <h2>Timing Strategies</h2>
 * <ul>
 *   <li><strong>Fixed Sleep:</strong> Simple delays for consistent timing</li>
 *   <li><strong>Jitter Sleep:</strong> Random variation to appear more human-like</li>
 *   <li><strong>Conditional Sleep:</strong> Wait until specific game states are reached</li>
 *   <li><strong>Repeated Actions:</strong> Perform actions until conditions are met</li>
 * </ul>
 *
 * <h2>Anti-Detection Considerations</h2>
 * <p>Using varied timing patterns helps avoid detection:</p>
 * <ul>
 *   <li>Always use jitter for non-deterministic delays</li>
 *   <li>Vary polling rates to avoid predictable patterns</li>
 *   <li>Use realistic timeouts based on expected game response times</li>
 *   <li>Consider using suppliers for dynamic timing calculations</li>
 * </ul>
 *
 * <h2>Common Patterns</h2>
 *
 * <h3>Action Delays</h3>
 * <pre>{@code
 * // Wait between inventory actions
 * inventory().drop("Logs")
 *     .then(timing().sleep(600, 200))
 *     .then(inventory().drop("Ore"))
 *     ;
 * }</pre>
 *
 * <h3>Animation Waiting</h3>
 * <pre>{@code
 * // Wait for player to finish current action
 * player().interact("Mine")
 *     .then(timing().sleepUntil(() -> !player().isAnimating(), () -> 100, 10000))
 *     .then(log("Mining complete"))
 *     ;
 * }</pre>
 *
 * <h3>Skill Training Loops</h3>
 * <pre>{@code
 * // Repeat fishing until inventory is full
 * timing().repeatUntil(
 *     () -> npc().interact("Fishing spot", "Net"),
 *     () -> inventory().count() == 28,
 *     () -> 1000 + random.nextInt(500),
 *     60000
 * );
 * }</pre>
 *
 * @see SleepAction
 */
public interface FluentTiming {

    /**
     * Creates a sleep action with a fixed duration plus random jitter.
     *
     * <p>This method creates a delay with random variation to make timing appear more
     * human-like. The actual sleep time will be between {@code milliseconds} and
     * {@code milliseconds + jitter}.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Sleep for 1000-1200ms (1000ms + 0-200ms jitter)
     * timing().sleep(1000, 200)
     *     .then(inventory().drop("Logs"))
     *     ;
     *
     * // Short delay with small jitter for rapid actions
     * inventory().use("Food")
     *     .then(timing().sleep(300, 100))
     *     .then(inventory().use("Potion"))
     *     ;
     *
     * // Longer delay with larger jitter for less frequent actions
     * timing().sleep(5000, 2000)
     *     .then(bank().open())
     *     ;
     * }</pre>
     *
     * <h3>Anti-Detection Benefits</h3>
     * <ul>
     *   <li>Prevents perfectly consistent timing patterns</li>
     *   <li>Mimics human reaction time variation</li>
     *   <li>Reduces predictability in action sequences</li>
     * </ul>
     *
     * @param milliseconds The base sleep duration in milliseconds
     * @param jitter The maximum additional random delay in milliseconds
     * @return A SleepAction that sleeps for the specified duration with jitter
     * @see #sleep(long)
     * @see #sleep(LongSupplier)
     */
    SleepAction sleep(long milliseconds, long jitter);

    /**
     * Creates a sleep action with a dynamically calculated duration.
     *
     * <p>This method uses a supplier to calculate the sleep duration each time the
     * action is executed. This allows for complex timing strategies based on game
     * state, player statistics, or other dynamic factors.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Dynamic sleep based on player energy
     * timing().sleep(() -> player().getEnergy() < 50 ? 2000 : 800)
     *     .then(player().walk(nextLocation))
     *     ;
     *
     * // Sleep time based on inventory fullness
     * timing().sleep(() -> {
     *     int items = inventory().count();
     *     return 500 + (items * 50); // Slower as inventory fills
     * }).then(inventory().drop("Logs"));
     *
     * // Random sleep within a range
     * Random random = new Random();
     * timing().sleep(() -> 1000 + random.nextInt(500))
     *     .then(bank().depositAll())
     *     ;
     * }</pre>
     *
     * <h3>Use Cases</h3>
     * <ul>
     *   <li>Adaptive timing based on game conditions</li>
     *   <li>Complex random distribution patterns</li>
     *   <li>Performance-based delay adjustments</li>
     *   <li>Context-sensitive timing strategies</li>
     * </ul>
     *
     * @param millisecondsSupplier A supplier that provides the sleep duration in milliseconds
     * @return A SleepAction that sleeps for the duration provided by the supplier
     * @see #sleep(long, long)
     * @see #sleep(long)
     */
    SleepAction sleep(LongSupplier millisecondsSupplier);

    /**
     * Creates a sleep action with a fixed duration.
     *
     * <p>This method creates a simple delay with no variation. While useful for
     * testing or specific timing requirements, consider using {@link #sleep(long, long)}
     * with jitter for more natural behavior in production bots.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Fixed delay for precise timing requirements
     * timing().sleep(1000)
     *     .then(interface().click("Accept"))
     *     ;
     *
     * // Short fixed delay between rapid actions
     * inventory().use("Logs")
     *     .then(timing().sleep(100))
     *     .then(inventory().use("Tinderbox"))
     *     ;
     * }</pre>
     *
     * <h3>Warning</h3>
     * <p>Fixed timing can create detectable patterns. For production use,
     * prefer {@link #sleep(long, long)} with jitter.</p>
     *
     * @param milliseconds The exact sleep duration in milliseconds
     * @return A SleepAction that sleeps for the specified duration
     * @see #sleep(long, long)
     * @see #sleep(LongSupplier)
     */
    SleepAction sleep(long milliseconds);

    /**
     * Creates a sleep action that waits until a condition becomes true.
     *
     * <p>This method repeatedly checks a condition at specified intervals until
     * either the condition becomes true or a timeout is reached. This is essential
     * for waiting for game state changes before proceeding with actions.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Wait for player to finish walking
     * player().walkTo(destination)
     *     .then(timing().sleepUntil(() -> player().isIdle(), () -> 100, 10000))
     *     .then(log("Arrived at destination"))
     *     ;
     *
     * // Wait for interface to appear
     * bank().open()
     *     .then(timing().sleepUntil(() -> interface().isVisible("Bank"), () -> 50, 5000))
     *     .then(bank().depositAll())
     *     ;
     *
     * // Wait for animation to complete
     * inventory().use("Logs on Tinderbox")
     *     .then(timing().sleepUntil(() -> !player().isAnimating(), () -> 200, 15000))
     *     .then(log("Firemaking complete"))
     *     ;
     *
     * // Dynamic polling rate based on expected wait time
     * timing().sleepUntil(
     *     () -> inventory().contains("Cooked fish"),
     *     () -> player().isAnimating() ? 100 : 500, // Poll faster while cooking
     *     30000
     * );
     * }</pre>
     *
     * <h3>Best Practices</h3>
     * <ul>
     *   <li>Use appropriate polling rates - faster for short waits, slower for long waits</li>
     *   <li>Set realistic timeouts based on expected game response times</li>
     *   <li>Consider dynamic polling rates for efficiency</li>
     *   <li>Always have timeout handling for unexpected situations</li>
     * </ul>
     *
     * @param condition The condition to wait for (should return true when ready to proceed)
     * @param pollingRateSupplier Supplier for the delay between condition checks in milliseconds
     * @param timeoutMs Maximum time to wait before giving up (in milliseconds)
     * @return A SleepAction that waits until the condition is met or timeout occurs
     * @see #repeatUntil(Supplier, BooleanSupplier, LongSupplier, long)
     */
    SleepAction sleepUntil(BooleanSupplier condition, LongSupplier pollingRateSupplier, long timeoutMs);
    SleepAction sleepUntil(BooleanSupplier condition, LongSupplier pollingRateSupplier);
    SleepAction sleepUntil(BooleanSupplier condition, long pollingRate, long timeoutMs);
    SleepAction sleepUntil(BooleanSupplier condition, long pollingRate);
    SleepAction sleepUntil(BooleanSupplier condition);

    /**
     * Creates a sleep action that repeatedly executes an action until an exit condition is met.
     *
     * <p>This method is useful for implementing loops where you need to perform an action
     * repeatedly (like clicking, using items, or checking states) until a specific condition
     * is satisfied. The action is executed, then the system waits for the polling interval
     * before checking the exit condition and potentially repeating.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Repeatedly fish until inventory is full
     * timing().repeatUntil(
     *     () -> npc().interact("Fishing spot", "Net"),
     *     () -> inventory().count() == 28,
     *     () -> 1000 + random.nextInt(500),
     *     60000
     * ).then(bank().depositInventory());
     *
     * // Repeatedly eat food during combat until health is full
     * timing().repeatUntil(
     *     () -> inventory().use("Shark"),
     *     () -> player().getHealthPercent() >= 95,
     *     () -> 1200 + random.nextInt(300),
     *     10000
     * ).then(log("Health restored"));
     *
     * // Repeatedly mine until pickaxe breaks or inventory is full
     * timing().repeatUntil(
     *     () -> gameObject().interact("Rock", "Mine"),
     *     () -> inventory().count() == 28 || !inventory().contains("Pickaxe"),
     *     () -> 2000 + random.nextInt(1000),
     *     300000
     * );
     *
     * // Dynamic polling based on action success
     * timing().repeatUntil(
     *     () -> {
     *         boolean success = monster().attack("Goblin");
     *         return success;
     *     },
     *     () -> !monster().exists("Goblin") || player().getHealthPercent() < 30,
     *     () -> player().isInCombat() ? 600 : 1500, // Faster polling during combat
     *     120000
     * );
     * }</pre>
     *
     * <h3>Action Return Values</h3>
     * <p>The action supplier should return:</p>
     * <ul>
     *   <li><code>true</code> if the action was successful and the loop should continue</li>
     *   <li><code>false</code> if the action failed and the loop should exit early</li>
     * </ul>
     *
     * <h3>Use Cases</h3>
     * <ul>
     *   <li>Skill training loops (fishing, mining, woodcutting)</li>
     *   <li>Combat sequences with healing</li>
     *   <li>Crafting until resources are exhausted</li>
     *   <li>Repeated attempts at success-based actions</li>
     * </ul>
     *
     * <h3>Best Practices</h3>
     * <ul>
     *   <li>Always set reasonable timeouts to prevent infinite loops</li>
     *   <li>Use variable polling rates to avoid predictable patterns</li>
     *   <li>Handle action failures gracefully in the supplier</li>
     *   <li>Consider multiple exit conditions for robustness</li>
     * </ul>
     *
     * @param action The action to repeat (should return true to continue, false to stop)
     * @param exitCondition The condition that stops the repetition when true
     * @param pollingRateSupplier Supplier for delay between repetitions in milliseconds
     * @param timeoutMs Maximum time to continue repeating before giving up
     * @return A SleepAction that repeats the action until the exit condition or timeout
     * @see #sleepUntil(BooleanSupplier, LongSupplier, long)
     */
    SleepAction repeatUntil(Supplier<Boolean> action, BooleanSupplier exitCondition,
                            LongSupplier pollingRateSupplier, long timeoutMs);
}
