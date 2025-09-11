package net.runelite.client.plugins.microbot.shank.base;

import lombok.Data;

/**
 * Encapsulates the outcome of a {@link ScriptTask} execution.
 * <p>
 * This class provides a standard way to represent whether a task succeeded or failed,
 * along with a descriptive message. It is immutable.
 */
@Data
public class ScriptTaskResult {

    /**
     * A null-safe result representing the absence of an executed task for a given tick.
     */
    public static final ScriptTaskResult NONE = new ScriptTaskResult(ScriptTask.NONE, true, "No task executed");

    /**
     * The task that was executed.
     */
    private final ScriptTask<?> task;

    /**
     * Whether the task execution was successful.
     */
    private final boolean success;

    /**
     * A human-readable message describing the outcome.
     */
    private final String message;

    /**
     * Creates a new successful result for the given task.
     *
     * @param task The task that was successfully executed.
     * @return A new {@link ScriptTaskResult} indicating success.
     */
    public static ScriptTaskResult success(ScriptTask<?> task) {
        return new ScriptTaskResult(task, true, task.name() + " executed successfully");
    }

    /**
     * Creates a new failed result for the given task.
     *
     * @param task The task that failed to execute.
     * @return A new {@link ScriptTaskResult} indicating failure.
     */
    public static ScriptTaskResult failure(ScriptTask<?> task) {
        return new ScriptTaskResult(task, false, task.name() + " execution failed");
    }
}
