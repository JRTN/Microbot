package net.runelite.client.plugins.microbot.shank.api.fluent.impl.flow;

/**
 * Represents the immutable result of an evaluated situation, with support for success/failure
 * handling.
 *
 * <p>SituationResult is created when a {@link SituationClause} evaluates its condition and executes
 * its actions. The result captures the outcome and provides methods for conditional response
 * handling and result inspection.
 *
 * <h2>Immediate Execution Model</h2>
 *
 * <p><strong>All evaluation and execution happens during construction:</strong>
 *
 * <ol>
 *   <li>Condition value is checked once
 *   <li>If condition is {@code true}, the action executes immediately
 *   <li>The result state is captured and becomes immutable
 *   <li>Success/failure handlers execute immediately when added
 * </ol>
 *
 * <h2>Result States</h2>
 *
 * <p>A situation can end in one of three states:
 *
 * <ul>
 *   <li><strong>Did not happen:</strong> Condition was {@code false}, action never attempted
 *   <li><strong>Failed:</strong> Condition was {@code true} but action returned {@code false}
 *   <li><strong>Succeeded:</strong> Condition was {@code true} and action returned {@code true}
 * </ul>
 *
 * <h2>Success/Failure Handling</h2>
 *
 * <p>Handlers execute immediately when attached, based on the already-determined result:
 *
 * <pre>{@code
 * SituationResult result = when(condition)
 *     .then(action)                    // Executes immediately
 *     .onSuccess(successHandler)       // Executes immediately if action succeeded
 *     .onFailure(failureHandler);      // Executes immediately if action failed
 * }</pre>
 *
 * <h2>Handler Execution Rules</h2>
 *
 * <ul>
 *   <li>{@code onSuccess()} executes only if: condition was {@code true} AND action succeeded
 *   <li>{@code onFailure()} executes only if: condition was {@code true} AND action failed
 *   <li>If condition was {@code false}, neither handler executes
 * </ul>
 */
public class SituationResult {
    private final boolean condition;
    private final Action action;
    private final boolean conditionMet;
    private final boolean successful;

    /**
     * Creates a new situation result by immediately checking the condition and executing the
     * action.
     *
     * <p><strong>This constructor triggers immediate execution:</strong>
     *
     * <ol>
     *   <li>The condition value is checked
     *   <li>If the condition is {@code true}, {@code action.execute()} is called immediately
     *   <li>If the condition is {@code false}, the action is not executed
     *   <li>The results are stored as immutable state
     * </ol>
     *
     * @param condition The boolean condition value to check.
     * @param action The action to execute if the condition is true. Must not be null.
     * @throws NullPointerException if {@code action} is null
     */
    public SituationResult(boolean condition, Action action) {
        this.condition = condition;
        this.action = action;

        // Execute immediately during construction
        this.conditionMet = condition;
        this.successful = conditionMet ? action.execute() : false;
    }

    /**
     * Defines an action to execute if the situation succeeded, and executes it immediately if
     * applicable.
     *
     * <p>The success action executes immediately when this method is called, but only if both:
     *
     * <ul>
     *   <li>The original condition was {@code true}, AND
     *   <li>The original action succeeded (returned {@code true})
     * </ul>
     *
     * <p>If the situation did not succeed, the success action is ignored.
     *
     * <h3>Example</h3>
     *
     * <pre>{@code
     * when(needHealing())
     *     .then(inventory().eat("Shark"))
     *     .onSuccess(log("Healed successfully"))     // Executes immediately if eating succeeded
     *     .onFailure(log("No food available"));      // Executes immediately if eating failed
     * }</pre>
     *
     * @param successAction The action to execute on success. Must not be null.
     * @return This SituationResult for method chaining
     * @throws NullPointerException if {@code successAction} is null
     */
    public SituationResult onSuccess(Action successAction) {
        if (successful && conditionMet) {
            successAction.execute();
        }
        return this;
    }

    /**
     * Defines an action to execute if the situation failed, and executes it immediately if
     * applicable.
     *
     * <p>The failure action executes immediately when this method is called, but only if:
     *
     * <ul>
     *   <li>The original condition was {@code true}, AND
     *   <li>The original action failed (returned {@code false})
     * </ul>
     *
     * <p>If the condition was {@code false} (situation did not happen), the failure action is NOT
     * executed. Use {@link #didNotHappen()} to check for this case separately.
     *
     * @param failureAction The action to execute on failure. Must not be null.
     * @return This SituationResult for method chaining
     * @throws NullPointerException if {@code failureAction} is null
     */
    public SituationResult onFailure(Action failureAction) {
        if (conditionMet && !successful) {
            failureAction.execute();
        }
        return this;
    }

    /**
     * Checks if the situation never occurred because its condition was false.
     *
     * <p>This method returns {@code true} when the original condition was {@code false}, meaning
     * the action was never attempted.
     *
     * @return {@code true} if the condition was false, {@code false} otherwise
     */
    public boolean didNotHappen() {
        return !conditionMet;
    }

    /**
     * Checks if the situation's condition was met but the action failed.
     *
     * <p>This method returns {@code true} when:
     *
     * <ul>
     *   <li>The condition was {@code true}, AND
     *   <li>The action was executed but returned {@code false}
     * </ul>
     *
     * @return {@code true} if the condition was met but action failed, {@code false} otherwise
     */
    public boolean failed() {
        return conditionMet && !successful;
    }

    /**
     * Checks if the situation completed successfully.
     *
     * <p>This method returns {@code true} when:
     *
     * <ul>
     *   <li>The condition was {@code true}, AND
     *   <li>The action was executed and returned {@code true}
     * </ul>
     *
     * @return {@code true} if both condition and action succeeded, {@code false} otherwise
     */
    public boolean succeeded() {
        return conditionMet && successful;
    }

    /**
     * Creates a new situation clause for chaining additional conditional logic.
     *
     * <p>This allows for sequential situation handling:
     *
     * <pre>{@code
     * SituationResult banking = when(needToBank()).then(bankItems());
     * SituationClause nextSituation = banking.when(readyToTrain());  // Returns clause, not result
     * }</pre>
     *
     * <p>The new situation is independent of this result - it does not depend on whether this
     * situation succeeded or failed.
     *
     * @param nextCondition The boolean condition for the next situation.
     * @return A new {@link SituationClause} for the next conditional logic
     */
    public SituationClause when(boolean nextCondition) {
        return new SituationClause(nextCondition);
    }

    /**
     * Continues the action chain with another action if this action succeeded.
     *
     * <p>This method allows chaining additional actions after success/failure handlers:
     *
     * <pre>{@code
     * when(condition)
     *     .then(action1)
     *     .onSuccess(log("Action1 succeeded"))
     *     .then(action2)              // Continue chaining
     *     .then(action3);
     * }</pre>
     *
     * <p><strong>Important:</strong> The next action only executes if the original situation
     * succeeded. If the original condition was false or the original action failed, the chained
     * action will not execute.
     *
     * @param nextAction The action to execute if this situation succeeded. Must not be null.
     * @return A new {@link ActionResult} for the chained action
     * @throws NullPointerException if {@code nextAction} is null
     */
    public ActionResult then(Action nextAction) {
        if (succeeded()) {
            boolean nextSuccessful = nextAction.execute();
            return new ActionResult(nextSuccessful, nextAction);
        } else {
            return new ActionResult(false, nextAction);
        }
    }
}
