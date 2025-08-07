package net.runelite.client.plugins.microbot.shank.scripts.fishing.tickbarbarian;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "[shank] 3t Barbarian Fish",
        description = "3t Fishing attempt at barbarian outpost",
        tags = {"fishing", "3t", "tick"},
        enabledByDefault = false
)
public class ThreeTickBarbarianPlugin extends Plugin {
    @Inject ThreeTickBarbarianScript script;

    @Provides
    ThreeTickBarbarianConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ThreeTickBarbarianConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        script.run();
    }

    @Override
    protected void shutDown() {
        script.shutdown();
    }
}
