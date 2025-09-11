# The Shank Task Framework

## 1. Purpose & Philosophy

The Shank Task Framework provides a structured, modular, and reusable architecture for creating gameplay scripts. Its primary goal is to eliminate the complex, sprawling conditional logic (`if/else if/else`) that often plagues botting scripts, replacing it with a clean, priority-driven task system.

The core philosophy is **explicitness and isolation**. Instead of relying on magical classpath scanning, the framework uses Google's Guice for dependency injection in a very deliberate way. Each script operates in its own isolated "universe," preventing conflicts and making the entire system more robust and maintainable.

---

## 2. Core Concepts

The framework is built on five key components that work together:

### `ScriptTaskManager`: The Engine
This is the heart of the framework. It holds all the tasks for a script and, on every `tick()`, it is responsible for:
1. Finding all tasks whose conditions are met (`ready()` is true).
2. Selecting the single task with the highest `ScriptTaskPriority` from that group.
3. Executing the chosen task.

### `ScriptTask<T extends ScriptContext>`: The Unit of Work
A `ScriptTask` is a small, independent, and stateless action. Think of it as a single verb in your script's vocabulary, like "Walk to bank," "Withdraw essence," or "Cast spell."
- **`name()`**: A descriptive name for logging.
- **`ready()`**: The task's activation condition. It reads from the `ScriptContext` to decide if it should run.
- **`run()`**: The core logic that performs the action.
- **`priority()`**: Its importance relative to other tasks.

### `ScriptContext`: The State Container
This is a script's brain. It is a simple interface implemented by a class that holds all the dynamic data, state, and derived facts that tasks need to function. For example:
- `isInventoryFull(): boolean`
- `hasSufficientPrayer(): boolean`
- `lastMonsterKilledAt: Instant`

By centralizing state in the context, tasks themselves can remain stateless, making them highly reusable. Each script gets its own **singleton instance** of its context, shared among all its tasks.

### `ScriptTaskPriority`: The Decision Maker
An enum (`VERY_HIGH` to `VERY_LOW`) that resolves conflicts. When multiple tasks are `ready()` at the same time, the one with the highest priority wins. This provides a clear and declarative way to control the script's flow.

### `ScriptTaskResult`: The Outcome
A simple data class that wraps the result of a task's execution (`success` or `failure`), ensuring that every action has a clear, loggable outcome.

---

## 3. The "Why": Guice, Modules, and Isolation

A common question for those familiar with frameworks like Spring Boot is, "Why is this so explicit? Why can't I just annotate my tasks?"

The answer is **isolation**, which is critical in a plugin-based environment like RuneLite.

### The Two Injectors
1.  **RuneLite's Main Injector**: A global, top-level injector that manages core services like `Client`, `ConfigManager`, and the `Plugin` instances themselves.
2.  **The Script's Injector**: A private, isolated injector that **you create** inside your plugin's `startUp()` method. This injector only knows about the classes you explicitly tell it about in your script's `Module`.

This separation is powerful. A bug or bad configuration in one script's module cannot affect the core client or any other running script. It creates a firewall between plugins.

### The Role of the `Module`
Since the script's injector doesn't scan for components, you must explicitly register everything it needs to know about in a class that extends `AbstractModule`.

Your module is the **configuration recipe** for your script. It is responsible for:
1.  **Binding the `ScriptContext` as a singleton**, so all tasks share one instance.
2.  **Providing the `Set<ScriptTask<?>>`** that the `ScriptTaskManager` will manage.

This explicit registration makes the dependency graph for your script crystal clear and self-contained.

---

## 4. How to Use the Framework: A Practical Guide

Here is the step-by-step process for building a script with this framework:

**Step 1: Create Your `ScriptContext`**
Define a class that implements `ScriptContext` to hold your script's state.

```java
@Singleton // Mark as a singleton for Guice
public class MyScriptContext implements ScriptContext {
    @Getter @Setter
    private boolean needsToBank = false;
}
```

**Step 2: Create Your `ScriptTask`s**
Implement the `ScriptTask` interface for each action. Inject the context in the constructor.

```java
public class BankingTask implements ScriptTask<MyScriptContext> {
    private final MyScriptContext context;

    @Inject // Guice will provide the singleton context
    public BankingTask(MyScriptContext context) {
        this.context = context;
    }

    @Override
    public boolean ready() {
        return context.isNeedsToBank();
    }
    // ... implement run(), name(), priority()
}
```

**Step 3: Create Your Guice `Module`**
This is where you wire everything together.

```java
public class MyScriptModule extends AbstractModule {
    @Override
    protected void configure() {
        // 1. Bind the context for singleton access
        bind(MyScriptContext.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    // 2. Provide the set of tasks for this script
    Set<ScriptTask<?>> provideTasks(BankingTask bankingTask, AnotherTask anotherTask) {
        return Set.of(bankingTask, anotherTask);
    }
}
```

**Step 4: Create Your `Script`**
Your main script class becomes very simple. It just needs to start the task manager's loop.

```java
public class MyScript extends Script {
    @Inject // The script's injector will provide this
    private ScriptTaskManager taskManager;

    public void run(MyConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            taskManager.tick(); // The entire script logic is now just this one line
        }, 0, 600, TimeUnit.MILLISECONDS);
    }
}
```

**Step 5: Create Your `Plugin`**
This is the entry point that bridges RuneLite's world and your script's world.

```java
@PluginDescriptor(...)
public class MyPlugin extends Plugin {
    @Inject
    private MyConfig config;

    private MyScript script;

    @Override
    protected void startUp() {
        // Create the script's private, isolated injector
        Injector scriptInjector = Guice.createInjector(new MyScriptModule());
        // Get the fully-wired script instance from it
        this.script = scriptInjector.getInstance(MyScript.class);
        // Start the script, passing in the config from the main injector
        script.run(config);
    }
}
```
