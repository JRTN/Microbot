package net.runelite.client.plugins.microbot.shank.base;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * The central engine that manages and executes a collection of {@link ScriptTask}s.
 * <p>
 * This manager is responsible for the main script loop. On each {@link #tick()}, it
 * identifies the highest-priority task that is ready for execution, runs it, and
 * returns the result. It uses a thread-safe {@link PriorityBlockingQueue} to manage
 * tasks, ensuring that they are always evaluated in order of their priority.
 * <p>
 * This class is designed to be a singleton within a script's Guice module.
 */
@Singleton
public class ScriptTaskManager {

    /**
     * A thread-safe priority queue that holds all the tasks for the script.
     * Tasks are ordered based on their {@link ScriptTaskPriority}.
     */
    private final PriorityBlockingQueue<ScriptTask<?>> tasks;

    /**
     * A reference to the task that is currently being executed.
     * Defaults to {@link ScriptTask#NONE}.
     */
    private ScriptTask<?> currentTask = ScriptTask.NONE;

    /**
     * Constructs the task manager.
     * <p>
     * This constructor is called by Guice, which injects the set of all tasks
     * that have been bound in the script's module.
     *
     * @param tasks The set of tasks to be managed, provided by Guice.
     */
    @Inject
    public ScriptTaskManager(Set<ScriptTask<?>> tasks) {
        this.tasks = new PriorityBlockingQueue<>(Math.max(1, tasks.size()), ScriptTaskPriority::compare);
        this.tasks.addAll(tasks);
    }

    /**
     * Dynamically adds a new task to the manager at runtime.
     *
     * @param task The {@link ScriptTask} to add.
     */
    public void addTask(ScriptTask<?> task) {
        tasks.add(task);
    }

    /**
     * Dynamically removes a task from the manager at runtime using its class.
     *
     * @param taskClass The class of the task to remove (e.g., {@code MyTask.class}).
     */
    public void removeTask(Class<? extends ScriptTask<?>> taskClass) {
        tasks.removeIf(task -> task.getClass().equals(taskClass));
    }

    /**
     * Executes a single cycle of the script's logic.
     * <p>
     * This method polls tasks from the priority queue until it finds one that is
     * {@link ScriptTask#ready()}. It then executes that task. Any tasks that were polled
     * but were not ready are temporarily held and re-added to the queue to ensure they
     * are not lost. The executed task is also re-added to be considered for the next tick.
     * <p>
     * This method is synchronized to ensure atomicity of the poll-check-execute-re-add cycle,
     * preventing race conditions in a multi-threaded environment.
     *
     * @return The {@link ScriptTaskResult} from the executed task, or {@link ScriptTaskResult#NONE}
     *         if no tasks were ready to run.
     */
    public synchronized ScriptTaskResult tick() {
        List<ScriptTask<?>> polledTasks = new ArrayList<>();
        ScriptTask<?> taskToRun = null;

        while (true) {
            ScriptTask<?> task = tasks.poll();
            if (task == null) {
                // Queue is empty
                break;
            }

            if (task.ready()) {
                taskToRun = task;
                break;
            } else {
                polledTasks.add(task);
            }
        }

        // Re-add all the non-ready tasks that were polled
        tasks.addAll(polledTasks);

        if (taskToRun != null) {
            currentTask = taskToRun;
            try {
                return taskToRun.run();
            } finally {
                // Ensure the executed task is returned to the queue for the next tick
                tasks.add(taskToRun);
            }
        }

        // No task was ready to run
        currentTask = ScriptTask.NONE;
        return ScriptTaskResult.NONE;
    }
}
