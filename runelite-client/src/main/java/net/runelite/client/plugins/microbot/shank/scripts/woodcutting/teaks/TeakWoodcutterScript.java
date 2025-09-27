package net.runelite.client.plugins.microbot.shank.scripts.woodcutting.teaks;

import com.google.inject.Inject;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.shank.base.ScriptTaskManager;

import java.util.concurrent.TimeUnit;

public class TeakWoodcutterScript extends Script {

    @Inject
    private ScriptTaskManager taskManager;

    public void run(TeakWoodcutterConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) {
                return;
            }
            taskManager.tick();
        }, 0, 100, TimeUnit.MILLISECONDS); // Using a faster tick for responsiveness
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
