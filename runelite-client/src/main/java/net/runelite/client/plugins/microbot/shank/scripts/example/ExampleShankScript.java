package net.runelite.client.plugins.microbot.shank.scripts.example;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.shank.scripts.example.config.ExampleShankConfig;
import net.runelite.client.plugins.microbot.shank.base.ScriptTaskManager;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ExampleShankScript extends Script {

    @Inject
    private ScriptTaskManager taskManager;

    public void run(ExampleShankConfig config) {
        log.info("run() method called. Starting script.");
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) {
                return;
            }
            taskManager.tick();
        }, 0, 600, TimeUnit.MILLISECONDS);
    }

    @Override
    public void shutdown() {
        log.info("shutdown() method called. Shutting down script.");
        super.shutdown();
    }
}
