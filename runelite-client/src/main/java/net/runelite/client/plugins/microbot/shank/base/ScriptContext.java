package net.runelite.client.plugins.microbot.shank.base;

/**
 * A marker interface for a script's state container.
 * <p>
 * The ScriptContext is the canonical place for a script’s data and derived facts.
 * Its purpose is to centralize all the state that tasks need to read from or write to,
 * such as inventory status, current location, or simple counters.
 * <p>
 * By keeping state localized to a specific script instance, it avoids global clutter
 * and allows tasks to be stateless and reusable. The context should be the single
 * source of truth for a script's state; tasks may update it, but should not maintain
 * their own parallel state.
 * <p>
 * Implementations of this interface are typically bound as singletons within a script's
 * dedicated Guice module, ensuring all tasks within that script share the same context instance.
 */
public interface ScriptContext {
}
