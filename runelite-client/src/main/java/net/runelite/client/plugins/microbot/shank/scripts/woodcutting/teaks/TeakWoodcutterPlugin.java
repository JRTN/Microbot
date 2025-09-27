package net.runelite.client.plugins.microbot.shank.scripts.woodcutting.teaks;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
        name = "[shank] Teak Woodcutter",
        description = "A 1.5-tick teak woodcutting script using the Shank framework.",
        tags = {"shank", "woodcutting", "teaks", "tick manipulation"},
        enabledByDefault = false
)
public class TeakWoodcutterPlugin extends Plugin {

    @Inject
    private TeakWoodcutterConfig config;

    @Inject
    private OverlayManager overlayManager;

    private TeakWoodcutterScript script;

    @Provides
    TeakWoodcutterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TeakWoodcutterConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        Injector injector = Guice.createInjector(new TeakWoodcutterModule());
        script = injector.getInstance(TeakWoodcutterScript.class);
        script.run(config);
    }

    @Override
    protected void shutDown() throws Exception {
        if (script != null) {
            script.shutdown();
        }
    }
}
