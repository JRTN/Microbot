package net.runelite.client.plugins.microbot.shank.scripts.crafting.battlestaffs;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "[shank] Air Battlestaffs",
        description = "Makes air battlestaffs",
        tags = {"crafting"},
        enabledByDefault = false
)
public class BattlestaffPlugin extends Plugin {
    @Inject BattlestaffScript script;

    @Provides
    BattlestaffConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BattlestaffConfig.class);
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
