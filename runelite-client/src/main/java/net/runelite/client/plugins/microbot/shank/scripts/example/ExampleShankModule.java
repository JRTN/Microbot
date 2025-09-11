package net.runelite.client.plugins.microbot.shank.scripts.example;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.runelite.client.plugins.microbot.shank.base.ScriptTask;
import net.runelite.client.plugins.microbot.shank.scripts.example.context.ExampleShankContext;
import net.runelite.client.plugins.microbot.shank.scripts.example.tasks.PrintTickTask;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

public class ExampleShankModule extends AbstractModule {
    @Override
    protected void configure() {
        // Bind the context as a singleton. This ensures that the script and all tasks
        // get the exact same instance of the context.
        bind(ExampleShankContext.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    Set<ScriptTask<?>> provideTasks(PrintTickTask printTickTask) {
        // Manually create the set of tasks. Guice will provide the instance
        // of PrintTickTask with its dependencies (the context) already injected.
        final Set<ScriptTask<?>> tasks = new HashSet<>();
        tasks.add(printTickTask);
        // To add more tasks, you would declare them as parameters in this method
        // and add them to the set here.
        return tasks;
    }
}
