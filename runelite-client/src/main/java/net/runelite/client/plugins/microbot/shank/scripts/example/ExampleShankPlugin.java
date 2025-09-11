package net.runelite.client.plugins.microbot.shank.scripts.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.shank.scripts.example.config.ExampleShankConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;

@PluginDescriptor(
    name = "[shank] Example Script",
    description = "An example script using the Shank task framework",
    tags = {"example", "shank", "microbot"},
    enabledByDefault = false
)
public class ExampleShankPlugin extends Plugin {

    @Inject
    private ExampleShankConfig config;

    private ExampleShankScript exampleShankScript;

    @Provides
    ExampleShankConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ExampleShankConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        Injector injector = Guice.createInjector(new ExampleShankModule());
        exampleShankScript = injector.getInstance(ExampleShankScript.class);
        exampleShankScript.run(config);
    }

    @Override
    protected void shutDown() throws Exception {
        if (exampleShankScript != null) {
            exampleShankScript.shutdown();
        }
    }
}
