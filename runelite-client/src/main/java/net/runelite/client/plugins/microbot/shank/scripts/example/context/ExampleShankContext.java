package net.runelite.client.plugins.microbot.shank.scripts.example.context;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.plugins.microbot.shank.base.ScriptContext;

import javax.inject.Singleton;

@Singleton
@Getter
@Setter
public class ExampleShankContext implements ScriptContext {
    private boolean hasTicked = false;
}
