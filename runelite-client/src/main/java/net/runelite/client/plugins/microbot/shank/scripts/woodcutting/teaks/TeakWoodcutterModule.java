package net.runelite.client.plugins.microbot.shank.scripts.woodcutting.teaks;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.runelite.client.plugins.microbot.shank.base.ScriptTask;
import net.runelite.client.plugins.microbot.shank.scripts.woodcutting.teaks.context.TeakWoodcutterContext;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.Set;

public class TeakWoodcutterModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TeakWoodcutterContext.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    Set<ScriptTask<?>> provideTasks() {
        // Tasks will be provided here later
        return Collections.emptySet();
    }
}
