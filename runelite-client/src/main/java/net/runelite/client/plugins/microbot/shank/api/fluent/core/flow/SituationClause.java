package net.runelite.client.plugins.microbot.shank.api.fluent.core.flow;

/**
 * Represents a conditional situation that can trigger actions when its condition is met.
 *
 * <p>A SituationClause is the intermediate step between defining a condition with {@code when()}
 * and specifying actions with {@code then()}. It holds the condition value but does not act on it
 * until {@code then()} is called.
 *
 * <h2>Usage Pattern</h2>
 *
 * <p>SituationClause is typically not instantiated directly, but created through the fluent API:
 *
 * <pre>{@code
 * // Creates SituationClause internally
 * when(Rs2Inventory.isFull())       // Returns SituationClause
 *     .then(bank().open());         // Uses condition and executes â†’ returns ActionResult
 * }</pre>
 *
 * <h2>Entry Point Only</h2>
 *
 * <p>{@code when()} is the only entry point to the fluent API. SituationClause cannot be chained
 * from other situations - each {@code when()} creates a new, independent situation evaluation.
 *
 * <h2>Condition Handling</h2>
 *
 * <p>The condition value is stored when the SituationClause is created. When {@code then()} is
 * called, it immediately:
 *
 * <ol>
 *   <li>Checks the stored condition value
 *   <li>If true, executes the action immediately
 *   <li>Returns an {@link ActionResult} with the outcome
 * </ol>
 *
 * <h2>Immediate Evaluation</h2>
 *
 * <p>Since conditions are simple boolean values, they are evaluated at the point where {@code
 * when()} is called:
 *
 * <pre>{@code
 * // Condition is evaluated here, when when() is called
 * SituationClause clause = when(Rs2Inventory.isFull());
 *
 * // Action executes here only if the stored condition was true
 * ActionResult result = clause.then(someAction());
 * }</pre>
 */
public class SituationClause {
    private final boolean condition;

    /**
     * Creates a new situation clause with the given condition value.
     *
     * <p>The condition value is stored and will be used when {@link #then(Action)} is called.
     *
     * @param condition The boolean condition that determines whether actions should execute
     */
    public SituationClause(boolean condition) {
        this.condition = condition;
    }

    /**
     * Defines actions to execute if the condition is true, and immediately evaluates the situation.
     *
     * <p><strong>This method triggers immediate execution:</strong>
     *
     * <ol>
     *   <li>The stored condition value is checked
     *   <li>If the condition is {@code true}, the action executes immediately
     *   <li>An {@link ActionResult} is returned with the outcome
     *   <li>Further actions can be chained to the ActionResult
     * </ol>
     *
     * <h3>Example</h3>
     *
     * <pre>{@code
     * ActionResult result = when(needToBank())
     *     .then(bank().open())                     // Executes immediately if needToBank() was true
     *     .then(bank().depositAll("Fish"))         // Chains additional actions
     *     .onSuccess(log("Banking completed"))     // Handle success
     *     .onFailure(log("Banking failed"));       // Handle failure
     * }</pre>
     *
     * @param action The action or action chain to execute if the condition is true. Must not be
     *     null.
     * @return An {@link ActionResult} containing the outcome, which can be used for chaining
     *     additional actions or success/failure handling
     * @throws NullPointerException if {@code action} is null
     * @see ActionResult
     */
    public ActionResult then(Action action) {
        if (condition) {
            boolean successful = action.execute();
            return new ActionResult(successful, action);
        } else {
            // Condition was false, so action was not executed
            return new ActionResult(false, action);
        }
    }

    /**
     * Executes a complex action chain as a single unit if the condition is true, making the entire
     * chain repeatable.
     *
     * <p><strong>This method is designed for multi-step operations that need to be repeated as a
     * unit.</strong> Unlike {@link #then(Action)}, which returns an {@link ActionResult} for further
     * chaining, {@code performAll()} treats the provided action chain as a complete, atomic operation
     * that can be repeated in its entirety.
     *
     * <h2>Key Differences from {@code then()}</h2>
     *
     * <ul>
     *   <li><strong>Returns {@link SituationResult}:</strong> Provides access to repeat operations
     *       that work on the entire chain
     *   <li><strong>Atomic repetition:</strong> {@code repeatUntil()} repeats the complete action
     *       chain, not just the last action
     *   <li><strong>Visual grouping:</strong> Clearly indicates that the action chain is a single
     *       logical operation
     * </ul>
     *
     * <h2>Perfect for Resource Gathering</h2>
     *
     * <p>This method excels at repetitive multi-step operations like mining, fishing, woodcutting, or
     * any scenario where you need to repeat an entire sequence:
     *
     * <pre>{@code
     * // Mining example - repeats entire sequence until inventory full
     * when(canMine())
     *     .performAll(
     *         mining().walkToRock()
     *             .then(mining().clickRock("Iron ore"))
     *             .then(wait().until(() -> !Rs2Player.isAnimating()))
     *             .then(mining().checkRockDepleted())
     *     )
     *     .repeatUntil(() -> Rs2Inventory.isFull(), 1000, 30000)
     *     .onSuccess(log("Mining completed - inventory full"))
     *     .onFailure(log("Mining failed or timed out"));
     *
     * // Fishing example - repeats entire fishing cycle
     * when(canFish())
     *     .performAll(
     *         fishing().walkToSpot()
     *             .then(fishing().clickSpot("Salmon"))
     *             .then(wait().until(() -> Rs2Player.isAnimating()))
     *             .then(wait().until(() -> !Rs2Player.isAnimating()))
     *     )
     *     .repeatUntil(() -> Rs2Inventory.isFull(), 2000, 300000);
     * }</pre>
     *
     * <h2>Execution Model</h2>
     *
     * <p><strong>Immediate execution on first call:</strong>
     *
     * <ol>
     *   <li>The stored condition value is checked
     *   <li>If condition is {@code true}, the entire action chain executes once immediately
     *   <li>A {@link SituationResult} is returned containing the outcome and the complete chain
     *   <li>The chain can then be repeated using {@code repeatUntil()} methods
     * </ol>
     *
     * <p>If the condition is {@code false}, the action chain is not executed, but the
     * {@link SituationResult} still contains the chain for potential future operations.
     *
     * <h2>Comparison with {@code then()}</h2>
     *
     * <table border="1">
     *   <tr><th>Aspect</th><th>{@code then()}</th><th>{@code performAll()}</th></tr>
     *   <tr><td>Return Type</td><td>ActionResult</td><td>SituationResult</td></tr>
     *   <tr><td>Repeat Behavior</td><td>Last action only</td><td>Entire chain</td></tr>
     *   <tr><td>Use Case</td><td>Linear action chains</td><td>Repetitive multi-step operations</td></tr>
     *   <tr><td>Visual Intent</td><td>Continuous flow</td><td>Grouped logical unit</td></tr>
     * </table>
     *
     * <h3>Example: Banking Sequence</h3>
     *
     * <pre>{@code
     * // Complex banking that might need to be repeated if it fails
     * when(needToBank())
     *     .performAll(
     *         walker().webWalk(BANK_LOCATION)
     *             .then(bank().open())
     *             .waitUntil(() -> Rs2Bank.isOpen(), 5000)
     *             .then(bank().depositAll("Fish"))
     *             .then(bank().withdraw("Logs", 27))
     *             .then(bank().close())
     *     )
     *     .onSuccess(log("Banking sequence completed"))
     *     .onFailure(retryBanking());  // Could repeat the entire sequence
     * }</pre>
     *
     * @param actionChain The complete action sequence to execute as a unit. This can be a single
     *     action or a complex chain built with {@code .then()} calls. Must not be null.
     * @return A {@link SituationResult} that can repeat the entire action chain and provides
     *     success/failure handling for the complete operation
     * @throws NullPointerException if {@code actionChain} is null
     * @see #then(Action)
     */
    public ActionResult performAll(Action actionChain) {
        if (condition) {
            boolean successful = actionChain.execute();
            return new ActionResult(successful, actionChain);
        } else {
            // Condition was false, so action was not executed
            return new ActionResult(false, actionChain);
        }
    }

    /**
     * Throws a RuntimeException if the condition is true, indicating a critical failure situation.
     *
     * <p>This method is useful for handling conditions that represent unrecoverable errors or
     * situations that should never occur during normal operation. If the condition is {@code
     * false}, no exception is thrown and execution continues normally.
     *
     * <p><strong>Important:</strong> Unlike {@code then()}, this method does not return an
     * ActionResult for chaining. It either throws an exception or does nothing, making it suitable
     * for assertion-style checks.
     *
     * <h3>Example</h3>
     *
     * <pre>{@code
     * // These will throw exceptions if conditions are true
     * when(Rs2Player.isDead())
     *     .throwException("Player died unexpectedly!");
     *
     * when(Rs2Inventory.isEmpty() && needItems())
     *     .throwException("No items available and unable to continue");
     *
     * when(timeoutExceeded())
     *     .throwException("Operation timed out after maximum retries");
     *
     * // Normal execution continues here if no exceptions were thrown
     * when(canContinue())
     *     .then(continueScript());
     * }</pre>
     *
     * <h3>Usage in Script Loops</h3>
     *
     * <pre>{@code
     * void onLoop() {
     *     // Critical checks that stop execution
     *     when(playerLoggedOut())
     *         .throwException("Player logged out during script execution");
     *     when(outOfSupplies())
     *         .throwException("Script cannot continue without supplies");
     *
     *     // Normal script logic
     *     when(needToBank())
     *         .then(performBanking());
     * }
     * }</pre>
     *
     * @param message The error message to include in the exception. Must not be null.
     * @throws RuntimeException if the condition is {@code true}
     * @throws NullPointerException if {@code message} is null
     */
    public void throwException(String message) {
        if (condition) {
            throw new RuntimeException(message);
        }
    }

    /**
     * Throws a custom exception if the condition is true, indicating a critical failure situation.
     *
     * <p>This method allows throwing specific exception types for different failure scenarios,
     * enabling more granular error handling by calling code. If the condition is {@code false}, no
     * exception is thrown and execution continues normally.
     *
     * <h3>Example with Custom Exception Types</h3>
     *
     * <pre>{@code
     * // Define custom exception types
     * class PlayerDeathException extends RuntimeException {
     *     public PlayerDeathException(String message) { super(message); }
     * }
     *
     * class ConnectionException extends RuntimeException {
     *     public ConnectionException(String message) { super(message); }
     * }
     *
     * // Use in script
     * when(Rs2Player.isDead())
     *     .throwException(new PlayerDeathException("Player died during combat"));
     *
     * when(connectionLost())
     *     .throwException(new ConnectionException("Lost connection to game server"));
     *
     * when(invalidGameState())
     *     .throwException(new IllegalStateException("Game state is invalid"));
     * }</pre>
     *
     * <h3>Exception Handling</h3>
     *
     * <pre>{@code
     * try {
     *     runScriptLoop();
     * } catch (PlayerDeathException e) {
     *     log.error("Player died: {}", e.getMessage());
     *     // Handle death-specific recovery
     * } catch (ConnectionException e) {
     *     log.error("Connection issue: {}", e.getMessage());
     *     // Handle connection-specific recovery
     * }
     * }</pre>
     *
     * @param exception The exception to throw. Must not be null and must extend RuntimeException.
     * @throws RuntimeException the provided exception if the condition is {@code true}
     * @throws NullPointerException if {@code exception} is null
     */
    public void throwException(RuntimeException exception) {
        if (condition) {
            throw exception;
        }
    }

    /**
     * Throws a RuntimeException with a formatted message if the condition is true.
     *
     * <p>This method provides convenient string formatting for exception messages, allowing dynamic
     * error messages based on current game state or script variables. Uses {@link
     * String#format(String, Object...)} for formatting.
     *
     * <h3>Example with Dynamic Messages</h3>
     *
     * <pre>{@code
     * int maxAttempts = 5;
     * int currentAttempts = 3;
     * String lastError = "Timeout";
     *
     * when(attemptsExceeded())
     *     .throwException("Failed after %d/%d attempts, last error: %s",
     *                    currentAttempts, maxAttempts, lastError);
     *
     * when(Rs2Player.getHealthPercent() <= 10)
     *     .throwException("Player health critical: %d%% remaining",
     *                    Rs2Player.getHealthPercent());
     *
     * when(inventorySpace() < requiredSpace())
     *     .throwException("Insufficient inventory space: need %d, have %d",
     *                    requiredSpace(), inventorySpace());
     * }</pre>
     *
     * <h3>Common Formatting Patterns</h3>
     *
     * <pre>{@code
     * // Numeric values
     * when(tooManyFailures())
     *     .throwException("Failed %d times, maximum allowed: %d", failures, maxFailures);
     *
     * // String interpolation
     * when(missingItem())
     *     .throwException("Required item '%s' not found in %s", itemName, location);
     *
     * // Time-based messages
     * when(operationTimedOut())
     *     .throwException("Operation timed out after %d seconds", elapsedTime / 1000);
     * }</pre>
     *
     * @param messageFormat The format string for the error message. Must not be null.
     * @param args Arguments for the format string. Can be empty.
     * @throws RuntimeException if the condition is {@code true}
     * @throws NullPointerException if {@code messageFormat} is null
     */
    public void throwException(String messageFormat, Object... args) {
        if (condition) {
            throw new RuntimeException(String.format(messageFormat, args));
        }
    }
}
