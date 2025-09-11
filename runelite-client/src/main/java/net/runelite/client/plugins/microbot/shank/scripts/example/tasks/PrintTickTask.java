package net.runelite.client.plugins.microbot.shank.scripts.example.tasks;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shank.base.ScriptTask;
import net.runelite.client.plugins.microbot.shank.base.ScriptTaskPriority;
import net.runelite.client.plugins.microbot.shank.base.ScriptTaskResult;
import net.runelite.client.plugins.microbot.shank.scripts.example.context.ExampleShankContext;

import javax.inject.Inject;

public class PrintTickTask implements ScriptTask<ExampleShankContext> {

    private final ExampleShankContext context;

    @Inject
    public PrintTickTask(ExampleShankContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return "Print Tick Task";
    }

    @Override
    public boolean ready() {
        // This task is ready to run only if it hasn't ticked yet.
        return !context.isHasTicked();
    }

    @Override
    public ScriptTaskResult run() {
        Microbot.log("Shank framework is running its first tick!");
        context.setHasTicked(true); // Update the state in the context
        Microbot.log("Context has been updated. The task will not run again.");
        return ScriptTaskResult.success(this);
    }

    @Override
    public ScriptTaskPriority priority() {
        return ScriptTaskPriority.MEDIUM;
    }
}
