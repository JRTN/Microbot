package net.runelite.client.plugins.microbot.shank.scripts.mining.m1d1;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "[shank] M1D1 Iron",
        description = "Mines iron rocks, drops iron ore if inventory contains one",
        tags = {"mining"},
        enabledByDefault = false
)
public class M1D1Plugin extends Plugin {

    @Inject M1D1Script script;

    @Provides
    M1D1Config provideConfig(ConfigManager configManager) {
        return configManager.getConfig(M1D1Config.class);
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
