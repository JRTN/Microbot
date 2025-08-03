package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.info.ScriptInfo;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
        name = "Lavas RC",
        description = "Runecrafts lavas",
        tags = {"lava", "lavas", "rc", "runecraft", "runecrafting"}
)
public class LavasRcPlugin extends Plugin {

    @Inject private LavasRcScript script;
    @Inject private OverlayManager overlayManager;
    @Inject private LavasRcOverlay lavasRcOverlay;

    @Provides
    public LavasRcConfig providesConfig(ConfigManager configManager) {
        return configManager.getConfig(LavasRcConfig.class);
    }

    @Override
    protected void startUp() {
        if (overlayManager != null) {
            overlayManager.add(lavasRcOverlay);
        }

        ScriptInfo scriptInfo = new ScriptInfo();

        lavasRcOverlay.setScriptInfo(scriptInfo);
        script.run(scriptInfo);
    }

    @Override
    protected void shutDown() {
        if (overlayManager != null) {
            overlayManager.remove(lavasRcOverlay);
        }

        script.shutdown();
    }
}
