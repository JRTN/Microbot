package net.runelite.client.plugins.microbot.shank.api.fluent.api;

import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;

import java.util.function.Consumer;

/**
 * Fluent API for anti-ban system operations in Old School RuneScape automation.
 *
 * <p>This interface provides methods to integrate sophisticated anti-detection measures into bot
 * scripts. The anti-ban system simulates natural human behavior patterns to reduce the likelihood
 * of detection by automated systems or manual review.
 *
 * <h2>Core Philosophy</h2>
 *
 * <p>Real players exhibit inconsistent, varied behavior with natural pauses, mouse movements, and
 * timing variations. This system replicates those patterns through:
 *
 * <ul>
 *   <li><strong>Timing Variability:</strong> Non-uniform delays and action spacing
 *   <li><strong>Mouse Behavior:</strong> Natural movements, off-screen positioning, fidgeting
 *   <li><strong>Attention Simulation:</strong> Periodic breaks and focus changes
 *   <li><strong>Fatigue Modeling:</strong> Gradual efficiency changes over time
 *   <li><strong>Behavioral Patterns:</strong> Activity-specific response patterns
 * </ul>
 *
 * <h2>Basic Integration</h2>
 *
 * <pre>{@code
 * // Configure anti-ban system for mining
 * antiban().configure(config -> {
 *     config.setActivity(Activity.GENERAL_MINING);
 *     config.setActivityIntensity(ActivityIntensity.HIGH);
 *     config.setActionCooldownChance(0.75);
 *     config.setMicroBreakChance(0.10);
 *     config.enableFatigueSimulation();
 *     config.enableNaturalMouse();
 * });
 *
 * // Integrate into mining loop
 * when(canMine())
 *     .then(gameObject().interact("Rock", "Mine"))
 *     .then(antiban().actionCooldown())
 *     .then(timing().sleepUntil(() -> !player().isAnimating(), () -> 100, 10000))
 *     .then(antiban().microBreak());
 * }</pre>
 *
 * <h2>Action Integration Patterns</h2>
 *
 * <h3>Skill Training Enhancement</h3>
 *
 * <pre>{@code
 * // Enhanced fishing with natural behavior
 * timing().repeatUntil(
 *     () -> npc().interact("Fishing spot", "Net")
 *         .then(antiban().actionCooldown())
 *         .then(antiban().moveMouseRandomly()),
 *     () -> inventory().count() == 28,
 *     () -> 1500 + random.nextInt(1000),
 *     600000
 * ).then(antiban().microBreak());
 * }</pre>
 *
 * <h3>Banking with Natural Pauses</h3>
 *
 * <pre>{@code
 * // Banking sequence with anti-ban measures
 * when(needToBank())
 *     .then(antiban().moveMouseOffScreenWithChance(0.3))
 *     .then(walker().webWalk(BANK_LOCATION))
 *     .then(bank().open())
 *     .then(antiban().actionCooldown())
 *     .then(bank().depositAll("Fish"))
 *     .then(antiban().moveMouseRandomly())
 *     .then(bank().close());
 * }</pre>
 *
 * <h3>Combat with Attention Simulation</h3>
 *
 * <pre>{@code
 * // Combat loop with micro-breaks and mouse movement
 * when(inCombat())
 *     .then(monster().attack("Goblin"))
 *     .then(antiban().moveMouseOffScreenWithChance(0.15))
 *     .then(timing().sleepUntil(() -> !player().isInCombat(), () -> 600, 30000))
 *     .then(antiban().microBreak());
 * }</pre>
 *
 * <h2>Configuration System</h2>
 *
 * <p>The anti-ban system is highly configurable through the {@link Config} interface:
 *
 * <h3>Activity-Specific Settings</h3>
 *
 * <ul>
 *   <li><strong>Activity Type:</strong> Optimizes patterns for specific skills (mining, fishing,
 *       etc.)
 *   <li><strong>Intensity Level:</strong> Controls how aggressive or conservative the measures are
 *   <li><strong>Timing Patterns:</strong> Adjusts delay distributions and cooldown frequencies
 * </ul>
 *
 * <h3>Behavioral Features</h3>
 *
 * <ul>
 *   <li><strong>Fatigue Simulation:</strong> Gradually changes behavior over extended periods
 *   <li><strong>Attention Span:</strong> Models focus loss and recovery cycles
 *   <li><strong>Natural Mouse:</strong> Implements human-like cursor movement patterns
 *   <li><strong>Micro-breaks:</strong> Short pauses that simulate momentary distractions
 * </ul>
 *
 * <h2>Best Practices</h2>
 *
 * <h3>Configuration Guidelines</h3>
 *
 * <ul>
 *   <li><strong>Match Activity:</strong> Always set the correct activity type for optimal patterns
 *   <li><strong>Reasonable Chances:</strong> Keep probability values realistic (5-85%)
 *   <li><strong>Progressive Intensity:</strong> Start conservative, increase based on needs
 *   <li><strong>Test Thoroughly:</strong> Verify behavior appears natural in practice
 * </ul>
 *
 * <h3>Integration Strategies</h3>
 *
 * <pre>{@code
 * // Good: Varied integration points
 * when(shouldPerformAction())
 *     .then(primaryAction())
 *     .then(antiban().actionCooldown())        // After main actions
 *     .then(supportingAction())
 *     .then(antiban().moveMouseRandomly())     // During transitions
 *     .then(antiban().microBreak());           // At natural break points
 *
 * // Avoid: Excessive or predictable patterns
 * when(condition)
 *     .then(antiban().actionCooldown())        // Don't always use same measures
 *     .then(antiban().microBreak())           // Don't stack without purpose
 *     .then(antiban().moveMouseOffScreen());  // Don't create obvious patterns
 * }</pre>
 *
 * <h2>Performance Considerations</h2>
 *
 * <ul>
 *   <li><strong>Minimal Overhead:</strong> Anti-ban actions are lightweight and fast
 *   <li><strong>Probability-Based:</strong> Many measures only trigger based on chance
 *   <li><strong>Adaptive Timing:</strong> Delays adjust based on current game state
 *   <li><strong>State Awareness:</strong> System respects existing cooldowns and breaks
 * </ul>
 *
 * <h2>Monitoring and Debugging</h2>
 *
 * <pre>{@code
 * // Check anti-ban system status
 * if (antiban().isActive()) {
 *     log.info("Anti-ban system is running");
 *
 *     if (antiban().isCooldownActive()) {
 *         log.debug("Action cooldown currently active");
 *     }
 *
 *     if (antiban().isMicroBreakActive()) {
 *         log.debug("Micro-break in progress");
 *     }
 * }
 * }</pre>
 *
 * @see Config
 * @see Activity
 * @see ActivityIntensity
 * @see Action
 */
public interface FluentAntiban {
    /**
     * Creates an action that triggers an action cooldown based on current anti-ban settings.
     *
     * <p>An action cooldown introduces a delay between bot actions to simulate human-like pacing.
     * The duration and probability of the cooldown are determined by the current anti-ban
     * configuration. This helps prevent the bot from performing actions too rapidly, which could
     * appear robotic.
     *
     * <h3>Example Usage</h3>
     *
     * <pre>{@code
     * // Add cooldown between mining actions
     * gameObject().interact("Rock", "Mine")
     *     .then(antiban().actionCooldown())
     *     .then(inventory().drop("Ore"));
     *
     * // Use in skill training loops
     * timing().repeatUntil(
     *     () -> gameObject().interact("Tree", "Chop")
     *         .then(antiban().actionCooldown()),
     *     () -> inventory().count() == 28,
     *     () -> 1000,
     *     300000
     * );
     * }</pre>
     *
     * <h3>Behavior</h3>
     *
     * <ul>
     *   <li>Cooldown duration varies based on activity type and intensity settings
     *   <li>May not trigger every time (controlled by action cooldown chance)
     *   <li>Adapts to fatigue simulation if enabled
     *   <li>Uses non-linear timing patterns when configured
     * </ul>
     *
     * @return An Action that may introduce an action cooldown delay
     * @see Config#setActionCooldownChance(double)
     * @see Config#setActivity(Activity)
     * @see Config#setActivityIntensity(ActivityIntensity)
     */
    Action actionCooldown();

    /**
     * Creates an action that attempts to trigger a micro-break based on configured chance.
     *
     * <p>Micro-breaks are short pauses that simulate a player briefly stopping their activity, such
     * as checking other windows, adjusting position, or momentary distraction. These breaks make
     * bot behavior appear more natural and less mechanical.
     *
     * <h3>Example Usage</h3>
     *
     * <pre>{@code
     * // Occasional micro-breaks during fishing
     * timing().repeatUntil(
     *     () -> npc().interact("Fishing spot", "Net")
     *         .then(antiban().microBreak()),
     *     () -> inventory().count() == 28,
     *     () -> 2000,
     *     600000
     * );
     *
     * // Micro-break after completing an inventory
     * inventory().dropAll("Logs")
     *     .then(antiban().microBreak())
     *     .then(log("Inventory cleared"));
     * }</pre>
     *
     * <h3>Behavior</h3>
     *
     * <ul>
     *   <li>Triggers based on configured micro-break chance
     *   <li>Duration varies between configured min/max values
     *   <li>May include mouse movements or other natural behaviors
     *   <li>Adapts to attention span simulation if enabled
     *   <li>Can be influenced by fatigue levels
     * </ul>
     *
     * @return An Action that may trigger a micro-break
     * @see Config#setMicroBreakChance(double)
     * @see Config#setMicroBreakDurationMin(int)
     * @see Config#setMicroBreakDurationMax(int)
     * @see Config#enableMicroBreaks()
     */
    Action microBreak();

    /**
     * Creates an action that moves the mouse off-screen to simulate taking a break.
     *
     * <p>Moving the mouse off-screen is a common human behavior when taking a break from the game,
     * checking other applications, or during moments of inactivity. This action helps simulate
     * natural player behavior patterns.
     *
     * <h3>Example Usage</h3>
     *
     * <pre>{@code
     * // Move mouse off-screen before a longer break
     * timing().sleep(30000, 5000)
     *     .then(antiban().moveMouseOffScreen())
     *     .then(log("Taking a break"));
     *
     * // Combine with banking routine
     * bank().depositAll()
     *     .then(antiban().moveMouseOffScreen())
     *     .then(timing().sleep(2000, 500))
     *     .then(bank().withdraw("Logs", 28));
     * }</pre>
     *
     * <h3>Behavior</h3>
     *
     * <ul>
     *   <li>Always moves the mouse off-screen when executed
     *   <li>Uses natural mouse movement patterns if enabled
     *   <li>Mouse position is randomized to avoid predictable patterns
     *   <li>May include slight delays to simulate realistic movement
     * </ul>
     *
     * @return An Action that moves the mouse off-screen
     * @see #moveMouseOffScreenWithChance(double)
     * @see Config#enableNaturalMouse()
     * @see Config#enableOffScreenMouseMovement()
     */
    Action moveMouseOffScreen();

    /**
     * Creates an action that moves the mouse off-screen based on a specified chance.
     *
     * <p>This provides probabilistic off-screen mouse movement, allowing for more varied behavior
     * patterns. The mouse will only move off-screen if the random chance condition is met.
     *
     * <h3>Example Usage</h3>
     *
     * <pre>{@code
     * // 30% chance to move mouse off-screen between actions
     * inventory().drop("Logs")
     *     .then(antiban().moveMouseOffScreenWithChance(0.3))
     *     .then(inventory().drop("Ore"));
     *
     * // Higher chance during longer activities
     * timing().repeatUntil(
     *     () -> gameObject().interact("Anvil", "Smith")
     *         .then(antiban().moveMouseOffScreenWithChance(0.15)),
     *     () -> inventory().count("Bar") == 0,
     *     () -> 3000,
     *     600000
     * );
     * }</pre>
     *
     * <h3>Probability Guidelines</h3>
     *
     * <ul>
     *   <li><strong>0.05-0.10 (5-10%):</strong> Subtle, occasional movements
     *   <li><strong>0.15-0.25 (15-25%):</strong> Moderate frequency for longer activities
     *   <li><strong>0.30+ (30%+):</strong> Frequent movements for highly repetitive tasks
     * </ul>
     *
     * @param chance The probability (0.0 to 1.0) of moving the mouse off-screen
     * @return An Action that may move the mouse off-screen based on chance
     * @see #moveMouseOffScreen()
     * @see Config#setMouseOffScreenChance(double)
     */
    Action moveMouseOffScreenWithChance(double chance);

    /**
     * Creates an action that moves the mouse to a random position on screen.
     *
     * <p>Random mouse movements simulate fidgeting, accidental movements, or natural cursor
     * adjustments that occur during normal gameplay. This helps break up predictable mouse
     * positioning patterns.
     *
     * <h3>Example Usage</h3>
     *
     * <pre>{@code
     * // Random mouse movement during idle periods
     * timing().sleepUntil(() -> player().isIdle(), () -> 100, 10000)
     *     .then(antiban().moveMouseRandomly())
     *     .then(log("Player finished action"));
     *
     * // Add randomness during repetitive clicking
     * timing().repeatUntil(
     *     () -> gameObject().interact("Rock", "Mine")
     *         .then(antiban().moveMouseRandomly()),
     *     () -> inventory().count() == 28,
     *     () -> 2500,
     *     300000
     * );
     * }</pre>
     *
     * <h3>Behavior</h3>
     *
     * <ul>
     *   <li>Moves to a random position within the game screen bounds
     *   <li>Uses natural mouse movement patterns if enabled
     *   <li>Avoids clicking on interactive elements unless intended
     *   <li>Movement distance and speed vary to appear natural
     * </ul>
     *
     * @return An Action that moves the mouse to a random screen position
     * @see Config#enableRandomMouseMovement()
     * @see Config#setMouseRandomChance(double)
     * @see Config#enableNaturalMouse()
     */
    Action moveMouseRandomly();

    /**
     * Checks if the anti-ban system is currently active and operational.
     *
     * <p>This method returns {@code true} when the anti-ban system has been properly
     * configured and is actively monitoring and influencing bot behavior. An inactive
     * system will not trigger any anti-ban measures, regardless of configuration.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Verify anti-ban system status before starting activities
     * when(!antiban().isActive())
     *     .throwException("Anti-ban system must be active for this script");
     *
     * // Conditional logic based on anti-ban status
     * if (antiban().isActive()) {
     *     log.info("Running with anti-ban protection");
     *     startEnhancedLoop();
     * } else {
     *     log.warn("Running without anti-ban measures");
     *     startBasicLoop();
     * }
     *
     * // Status monitoring in script loops
     * when(() -> scriptRunning && !antiban().isActive())
     *     .then(log("Warning: Anti-ban system became inactive"))
     *     .then(() -> reinitializeAntiban());
     * }</pre>
     *
     * <h3>Activation Conditions</h3>
     * <p>The anti-ban system becomes active when:</p>
     * <ul>
     *   <li>Configuration has been applied via {@link #configure(Consumer)}</li>
     *   <li>At least one anti-ban feature is enabled</li>
     *   <li>The system has initialized successfully</li>
     *   <li>No critical errors prevent operation</li>
     * </ul>
     *
     * <h3>Deactivation Scenarios</h3>
     * <p>The system may become inactive due to:</p>
     * <ul>
     *   <li>Explicit disabling via {@link Config#disable()}</li>
     *   <li>All features being individually disabled</li>
     *   <li>Configuration reset without re-enabling</li>
     *   <li>System errors or resource constraints</li>
     * </ul>
     *
     * @return {@code true} if the anti-ban system is active and operational,
     *         {@code false} if disabled or not configured
     * @see #configure(Consumer)
     * @see Config#enable()
     * @see Config#disable()
     */
    boolean isActive();

    /**
     * Checks if an action cooldown is currently active and preventing immediate action execution.
     *
     * <p>Action cooldowns are temporary delays that space out bot actions to create more
     * natural timing patterns. When a cooldown is active, the system may introduce
     * additional delays or modify timing behavior.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Conditional action execution based on cooldown status
     * when(() -> canPerformAction() && !antiban().isCooldownActive())
     *     .then(performRapidAction())
     *     .then(log("Executed action during cooldown gap"));
     *
     * // Status-aware timing adjustments
     * long baseDelay = 1000;
     * long adjustedDelay = antiban().isCooldownActive() ? baseDelay / 2 : baseDelay;
     * timing().sleep(adjustedDelay);
     *
     * // Debugging cooldown behavior
     * if (antiban().isCooldownActive()) {
     *     log.debug("Action cooldown active - natural delay in progress");
     * }
     *
     * // Avoid redundant cooldown calls
     * when(() -> actionNeeded && !antiban().isCooldownActive())
     *     .then(primaryAction())
     *     .then(antiban().actionCooldown()); // Only add if not already active
     * }</pre>
     *
     * <h3>Cooldown Lifecycle</h3>
     * <ul>
     *   <li><strong>Triggered:</strong> When {@link #actionCooldown()} executes successfully</li>
     *   <li><strong>Duration:</strong> Varies based on activity type, intensity, and fatigue simulation</li>
     *   <li><strong>Probability:</strong> Controlled by action cooldown chance setting</li>
     *   <li><strong>Completion:</strong> Automatically clears when delay period expires</li>
     * </ul>
     *
     * <h3>Integration Patterns</h3>
     * <pre>{@code
     * // Smart cooldown management
     * Action smartAction = () -> {
     *     if (antiban().isCooldownActive()) {
     *         log.debug("Skipping redundant cooldown - already active");
     *         return true;
     *     }
     *     return antiban().actionCooldown().execute();
     * };
     *
     * // Adaptive timing based on cooldown state
     * LongSupplier adaptiveDelay = () -> {
     *     return antiban().isCooldownActive() ?
     *         500 + random.nextInt(300) :    // Shorter delay during cooldown
     *         1000 + random.nextInt(500);    // Normal delay otherwise
     * };
     * }</pre>
     *
     * @return {@code true} if an action cooldown is currently active,
     *         {@code false} if no cooldown is in progress
     * @see #actionCooldown()
     * @see Config#setActionCooldownChance(double)
     * @see Config#setActivityIntensity(ActivityIntensity)
     */
    boolean isCooldownActive();

    /**
     * Checks if a micro-break is currently active and the bot is in a simulated pause state.
     *
     * <p>Micro-breaks are short periods of inactivity that simulate a player briefly stopping
     * their activity due to distractions, fatigue, or attention shifts. During an active
     * micro-break, the bot may delay or modify its normal behavior patterns.</p>
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Respect active micro-breaks in main loop
     * when(() -> !antiban().isMicroBreakActive())
     *     .then(continueNormalActivity())
     *     .onFailure(() -> log.debug("Waiting for micro-break to complete"));
     *
     * // Adaptive behavior during micro-breaks
     * if (antiban().isMicroBreakActive()) {
     *     log.debug("Micro-break active - reducing activity intensity");
     *     performLowIntensityActions();
     * } else {
     *     performNormalActions();
     * }
     *
     * // Status monitoring and reporting
     * when(() -> antiban().isMicroBreakActive())
     *     .then(log("Taking a short break..."))
     *     .then(timing().sleepUntil(() -> !antiban().isMicroBreakActive(), () -> 500, 30000))
     *     .then(log("Break completed, resuming activity"));
     * }</pre>
     *
     * <h3>Micro-break Characteristics</h3>
     * <ul>
     *   <li><strong>Trigger:</strong> Probabilistic activation via {@link #microBreak()}</li>
     *   <li><strong>Duration:</strong> Varies between configured min/max values (typically 10-60 seconds)</li>
     *   <li><strong>Frequency:</strong> Controlled by micro-break chance setting</li>
     *   <li><strong>Behavior:</strong> May include mouse movements, pauses, or other natural actions</li>
     * </ul>
     *
     * <h3>Integration Strategies</h3>
     * <pre>{@code
     * // Skill training loop with micro-break awareness
     * timing().repeatUntil(
     *     () -> {
     *         if (antiban().isMicroBreakActive()) {
     *             return true; // Continue loop but don't perform main action
     *         }
     *         return gameObject().interact("Tree", "Chop");
     *     },
     *     () -> inventory().count() == 28,
     *     () -> antiban().isMicroBreakActive() ? 2000 : 1000, // Longer polling during breaks
     *     600000
     * );
     *
     * // Conditional action chaining
     * Action conditionalAction = () -> {
     *     if (antiban().isMicroBreakActive()) {
     *         log.debug("Deferring action due to active micro-break");
     *         return false; // Don't execute, will retry later
     *     }
     *     return performMainAction();
     * };
     * }</pre>
     *
     * <h3>Break Interaction</h3>
     * <pre>{@code
     * // Respect both cooldowns and micro-breaks
     * when(() -> canAct() && !antiban().isCooldownActive() && !antiban().isMicroBreakActive())
     *     .then(performOptimalAction())
     *     .onFailure(() -> {
     *         if (antiban().isMicroBreakActive()) {
     *             log.debug("Action deferred - micro-break in progress");
     *         } else if (antiban().isCooldownActive()) {
     *             log.debug("Action deferred - cooldown active");
     *         }
     *     });
     * }</pre>
     *
     * @return {@code true} if a micro-break is currently active,
     *         {@code false} if no micro-break is in progress
     * @see #microBreak()
     * @see Config#setMicroBreakChance(double)
     * @see Config#setMicroBreakDurationMin(int)
     * @see Config#setMicroBreakDurationMax(int)
     * @see Config#enableMicroBreaks()
     */
    boolean isMicroBreakActive();

    /**
     * Configures the anti-ban system with custom settings using a fluent configuration interface.
     *
     * <p>This method provides access to the anti-ban configuration through a {@link Config} object
     * that can be modified using lambda expressions. The configuration is applied immediately and
     * affects all subsequent anti-ban operations.</p>
     *
     * <h3>Basic Configuration</h3>
     * <pre>{@code
     * // Simple mining configuration
     * antiban().configure(config -> {
     *     config.setActivity(Activity.GENERAL_MINING);
     *     config.setActivityIntensity(ActivityIntensity.HIGH);
     *     config.enableFatigueSimulation();
     *     config.enableNaturalMouse();
     * });
     * }</pre>
     *
     * @param configurer A consumer function that receives a {@link Config} object for modification.
     *                   The configurer should call appropriate methods on the config to set desired values.
     *                   Must not be null.
     * @throws NullPointerException if configurer is null
     * @throws IllegalArgumentException if configuration values are invalid or incompatible
     * @see Config
     * @see Activity
     * @see ActivityIntensity
     */
    void configure(Consumer<Config> configurer);

    interface Config {
        void enable();

        void disable();

        void enableRandomIntervals();

        void disableRandomIntervals();

        void enableFatigueSimulation();

        void disableFatigueSimulation();

        void enableAttentionSpanSimulation();

        void disableAttentionSpanSimulation();

        void enableBehavioralVariability();

        void disableBehavioralVariability();

        void enableNonLinearIntervals();

        void disableNonLinearIntervals();

        void enableNaturalMouse();

        void disableNaturalMouse();

        void enableRandomMouseMovement();

        void disableRandomMouseMovement();

        void enableOffScreenMouseMovement();

        void disableOffScreenMouseMovement();

        void enableUniversalAntiban();

        void disableUniversalAntiban();

        void enablePlayStyle();

        void disablePlayStyle();

        void enableMicroBreaks();

        void disableMicroBreaks();

        void setActivity(Activity activity);

        void setActivityIntensity(ActivityIntensity intensity);

        void setMicroBreakChance(double chance);

        void setActionCooldownChance(double chance);

        void setMouseRandomChance(double chance);

        void setMouseOffScreenChance(double chance);

        void setMicroBreakDurationMax(int maxMinutes);

        void setMicroBreakDurationMin(int minMinutes);

        void resetSettings();
    }
}
