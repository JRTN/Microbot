package net.runelite.client.plugins.microbot.shank.base;

/**
 * Defines the priority levels for a {@link ScriptTask}.
 * <p>
 * Priority is used by the {@link ScriptTaskManager} to resolve which task to execute
 * when multiple tasks are ready in the same tick. Tasks with a higher priority (e.g.,
 * {@code VERY_HIGH}) will be chosen over tasks with a lower priority (e.g., {@code LOW}).
 */
public enum ScriptTaskPriority {
    /**
     * The highest priority, reserved for critical, must-run tasks (e.g., emergency teleport).
     */
    VERY_HIGH,
    /**
     * High priority, for important tasks that should generally run before others (e.g., combat).
     */
    HIGH,
    /**
     * The default priority for most standard tasks.
     */
    MEDIUM,
    /**
     * Low priority, for background or less critical tasks (e.g., checking for buffs).
     */
    LOW,
    /**
     * The lowest priority, for trivial or optional tasks.
     */
    VERY_LOW
    ;

    /**
     * Compares two priorities for sorting in descending order.
     * <p>
     * This allows a collection of tasks to be sorted from highest priority to lowest.
     *
     * @param a The first priority.
     * @param b The second priority.
     * @return A negative integer, zero, or a positive integer as the first argument
     *         is greater than, equal to, or less than the second.
     */
    public static int compare(ScriptTaskPriority a, ScriptTaskPriority b) {
        // Invert the comparison to sort from highest to lowest ordinal
        return Integer.compare(b.ordinal(), a.ordinal());
    }

    /**
     * Compares two tasks based on their priorities for sorting in descending order.
     * <p>
     * This is a convenience method used by the {@link ScriptTaskManager}'s priority queue.
     *
     * @param a The first task.
     * @param b The second task.
     * @return A negative integer, zero, or a positive integer as the first argument
     *         is greater than, equal to, or less than the second.
     */
    public static int compare(ScriptTask<?> a, ScriptTask<?> b) {
        return compare(a.priority(), b.priority());
    }
}
