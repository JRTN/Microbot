package net.runelite.client.plugins.microbot.shank.base;

/**
 * Represents a small, independent, and stateless unit of work within a script.
 * <p>
 * Tasks are the fundamental building blocks of a script, where each task encapsulates a
 * specific action (e.g., "withdraw item," "teleport to bank"). They are designed to be
 * composable and reusable.
 *
 * @param <T> The type of {@link ScriptContext} this task depends on. This links the
 *            task to a specific script's state container.
 */
public interface ScriptTask<T extends ScriptContext> {

    /**
     * Returns a descriptive, human-readable name for the task.
     *
     * @return The name of the task, used for logging and debugging.
     */
    String name();

    /**
     * Determines if this task is currently eligible for execution.
     * <p>
     * This method is checked on every script tick. It should read from the script's
     * {@link ScriptContext} to decide if its conditions are met. For example, a
     * "deposit items" task would be ready only if the inventory is full.
     *
     * @return {@code true} if the task is ready to run, {@code false} otherwise.
     */
    boolean ready();

    /**
     * Executes the core logic of the task.
     * <p>
     * This method is only called if {@link #ready()} returns {@code true} and this task
     * has the highest priority among all other ready tasks for the current tick.
     *
     * @return A {@link ScriptTaskResult} indicating the outcome of the execution.
     */
    ScriptTaskResult run();

    /**
     * Returns the priority level of this task.
     * <p>
     * The priority is used by the {@link ScriptTaskManager} to decide which task to run
     * when multiple tasks are ready at the same time.
     *
     * @return The {@link ScriptTaskPriority} of this task.
     */
    ScriptTaskPriority priority();

    /**
     * A null-safe, singleton implementation of {@link ScriptTask} that represents the
     * absence of a task to run.
     * <p>
     * This task is never ready and serves as a default to prevent null pointer exceptions
     * when no other task is eligible for execution.
     */
    ScriptTask<?> NONE = new ScriptTask<>() {
        @Override
        public String name() {
            return "None";
        }

        @Override
        public boolean ready() {
            return false;
        }

        @Override
        public ScriptTaskResult run() {
            return ScriptTaskResult.NONE;
        }

        @Override
        public ScriptTaskPriority priority() {
            return ScriptTaskPriority.VERY_LOW;
        }
    };
}
