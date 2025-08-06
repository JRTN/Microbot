package net.runelite.client.plugins.microbot.shank.api.fluent.api;

import net.runelite.api.GameObject;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface FluentGameObject {
    Stream<GameObject> gameObjects();

    List<GameObject> getGameObjects();
    List<GameObject> getGameObjects(int id);
    List<GameObject> getGameObjects(Predicate<GameObject> predicate);

    Optional<GameObject> getGameObject(int id);
    Optional<GameObject> getGameObject(Predicate<GameObject> predicate);

    Optional<GameObject> getNearestGameObject(int id);
    Optional<GameObject> getNearestGameObject(Predicate<GameObject> predicate);
    Optional<GameObject> getNearestGameObject(int id, int distance);
    Optional<GameObject> getNearestGameObject(Predicate<GameObject> predicate, int distance);

    boolean canWalkNear(GameObject gameObject);
    boolean canWalkNextTo(GameObject gameObject);
    boolean canInteractWith(GameObject gameObject);

    Action walkNear(GameObject gameObject);
    Action walkNextTo(GameObject gameObject);
    Action interactWith(GameObject gameObject, String action);
}
