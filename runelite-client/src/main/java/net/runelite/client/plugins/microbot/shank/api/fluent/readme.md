# RuneLite Fluent Flow API

A fluent, declarative API for writing RuneScape automation scripts that are easy to read, maintain, and debug.

## Motivation

Traditional bot scripting often results in complex, nested if-else statements and imperative code that's hard to follow:

```java
// Traditional approach - hard to read and maintain
if (Rs2Inventory.isFull()) {
    if (Rs2Bank.openBank()) {
        if (Rs2Bank.depositAll("Fish")) {
            if (Rs2Bank.withdraw("Logs", 27)) {
                if (Rs2Walker.webWalk(FLETCHING_AREA)) {
                    System.out.println("Banking completed successfully");
                } else {
                    System.out.println("Failed to walk to fletching area");
                }
            } else {
                System.out.println("Failed to withdraw logs");
            }
        } else {
            System.out.println("Failed to deposit fish");
        }
    } else {
        System.out.println("Banking failed, trying backup bank");
        Rs2Walker.webWalk(BACKUP_BANK);
    }
}
```

The Fluent Flow API transforms this into readable, declarative code:

```java
// Fluent approach - clear intent and flow
SituationResult bankingResult = when(Rs2Inventory::isFull)
    .then(bank().open()
        .then(bank().depositAll("Fish"))
        .then(bank().withdraw("Logs", 27)))
    .onSuccess(log("Banking completed successfully")
        .then(walker().webWalk(FLETCHING_AREA)))
    .onFailure(log("Banking failed, trying backup bank")
        .then(walker().webWalk(BACKUP_BANK)));
```

## Key Benefits

- **Readable**: Code reads like natural language describing what should happen
- **Declarative**: Focus on *what* should happen, not *how* to implement it
- **Composable**: Chain actions together and reuse common patterns
- **Self-Documenting**: The flow structure makes the bot's logic immediately clear
- **Robust Error Handling**: Built-in success/failure paths eliminate forgotten edge cases
- **Immediate Execution**: No need to remember to call `.execute()` - situations are evaluated as they're built

## Core Concepts

### Situations
A **situation** is a condition that may or may not be true, with actions to take when it is true:

```java
when(condition).then(action)
```

### Action Chaining
Actions can be chained together to create sequences:

```java
bank().open()
    .then(bank().depositAll("Fish"))
    .then(bank().withdraw("Logs", 27))
```

### Success/Failure Handling
Every situation can define what happens on success or failure:

```java
when(needToBank())
    .then(bank().open().then(bank().depositAll()))
    .onSuccess(log("Banking completed"))
    .onFailure(log("Banking failed").then(tryAlternativeBank()))
```

### Result Inspection
After a situation is evaluated, you can inspect what happened:

```java
SituationResult result = when(condition).then(action);

if (result.didNotHappen()) {
    // Condition was never true
}
if (result.failed()) {
    // Condition was true but action failed
}
if (result.succeeded()) {
    // Everything worked perfectly
}
```

## Basic Examples

### Simple Banking
```java
when(() -> Rs2Inventory.isFull())
    .then(bank().open()
        .then(bank().depositAll()))
    .onSuccess(log("Inventory cleared"))
    .onFailure(log("Could not bank items"));
```

### Combat with Food Management
```java
when(() -> Rs2Player.getHealthPercent() < 50)
    .then(inventory().use("Shark"))
    .onSuccess(log("Ate food"))
    .onFailure(log("No food available")
        .then(walker().webWalk(BANK_LOCATION)));
```

### Skill Training Loop
```java
// Get supplies
when(() -> !Rs2Inventory.contains("Raw fish"))
    .then(walker().webWalk(FISHING_SPOT)
        .then(inventory().use("Fishing rod").on("Fishing spot")))
    .onSuccess(log("Started fishing"))
    .onFailure(log("Could not start fishing"));

// Process supplies  
when(() -> Rs2Inventory.isFull())
    .then(walker().webWalk(COOKING_RANGE)
        .then(inventory().use("Raw fish").on("Cooking range")))
    .onSuccess(log("Started cooking"))
    .onFailure(log("Could not cook fish"));
```

### Complex Questing Logic
```java
SituationResult gearCheck = when(() -> !Rs2Equipment.isWearing("Combat bracelet"))
    .then(bank().open()
        .then(bank().withdraw("Combat bracelet", 1))
        .then(inventory().wear("Combat bracelet")))
    .onSuccess(log("Equipped combat bracelet"))
    .onFailure(log("Could not get combat bracelet"));

when(() -> gearCheck.succeeded() && Rs2Player.isInCombat())
    .then(magic().cast("Teleport to house"))
    .onSuccess(log("Escaped combat"))
    .onFailure(log("Teleport failed")
        .then(inventory().eat("Shark")));
```

## Advanced Patterns

### Conditional Chaining
```java
// Chain multiple situations together
when(needSupplies())
    .then(goToBank())
    .onSuccess(log("Got supplies"))
    .when(readyToTrain())  // Chain next situation
    .then(startTraining())
    .onSuccess(log("Training started"));
```

### Waiting for State Changes
```java
when(() -> Rs2Inventory.contains("Logs"))
    .then(inventory().use("Tinderbox").on("Logs")
        .waitUntil(() -> Rs2Player.isAnimating())  // Wait for fire lighting animation
        .waitUntil(() -> !Rs2Player.isAnimating())) // Wait for animation to finish
    .onSuccess(log("Fire lit successfully"));
```

### Result-Based Decision Making
```java
SituationResult miningResult = when(() -> Rs2GameObject.exists("Copper ore"))
    .then(gameObject().interact("Copper ore", "Mine"));

if (miningResult.didNotHappen()) {
    // No copper ore available - try iron instead
    when(() -> Rs2GameObject.exists("Iron ore"))
        .then(gameObject().interact("Iron ore", "Mine"));
}
```
