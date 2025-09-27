package net.runelite.client.plugins.microbot.shank.scripts.woodcutting.teaks;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.microbot.shank.scripts.woodcutting.teaks.models.DropStrategy;

@ConfigGroup("shank-teak-woodcutter")
public interface TeakWoodcutterConfig extends Config {



    @ConfigItem(
            keyName = "dropStrategy",
            name = "Drop Strategy",
            description = "Choose when to drop the logs.",
            position = 0
    )
    default DropStrategy dropStrategy() {
        return DropStrategy.DROP_WHEN_FULL;
    }
}
