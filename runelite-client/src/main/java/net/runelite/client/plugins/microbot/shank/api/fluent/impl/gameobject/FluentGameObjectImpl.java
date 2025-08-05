package net.runelite.client.plugins.microbot.shank.api.fluent.impl.gameobject;

import net.runelite.api.GameObject;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.gameobject.FluentGameObject;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FluentGameObjectImpl implements FluentGameObject {

    @Override
    public Stream<GameObject> gameObjects() {
        return getGameObjects().stream();
    }

    @Override
    public List<GameObject> getGameObjects() {
        return Rs2GameObject.getGameObjects();
    }

    @Override
    public List<GameObject> getGameObjects(int id) {
        return gameObjects()
                .filter(gameObject -> gameObject.getId() == id)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameObject> getGameObjects(Predicate<GameObject> predicate) {
        return gameObjects().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public Optional<GameObject> getGameObject(int id) {
        return getGameObjects().stream().findAny();
    }

    @Override
    public Optional<GameObject> getGameObject(Predicate<GameObject> predicate) {
        return gameObjects().filter(predicate).findAny();
    }

    @Override
    public Optional<GameObject> getNearestGameObject(int id, int distance) {
        return Optional.empty();
    }

    @Override
    public Optional<GameObject> getNearestGameObject(Predicate<GameObject> predicate, int distance) {
        return Optional.empty();
    }

    @Override
    public Optional<GameObject> getNearestGameObject(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<GameObject> getNearestGameObject(Predicate<GameObject> predicate) {
        return Optional.empty();
    }

    @Override
    public boolean canWalkNear(GameObject gameObject) {
        return false;
    }

    @Override
    public boolean canWalkNextTo(GameObject gameObject) {
        return false;
    }

    @Override
    public boolean canInteractWith(GameObject gameObject) {
        return false;
    }

    @Override
    public Action walkNear(GameObject gameObject) {
        return null;
    }

    @Override
    public Action walkNextTo(GameObject gameObject) {
        return null;
    }

    @Override
    public Action interactWith(GameObject gameObject, String action) {
        return null;
    }

    private int distanceTo(Predicate<GameObject> predicate) {
        return 0;
    }
}
