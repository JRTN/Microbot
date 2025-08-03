package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.info;

import lombok.Data;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.state.State;

import java.time.Duration;
import java.time.Instant;

@Data
public class ScriptInfo {
    private State currentState;
    private final Instant startTime;
    private final int startXp;
    private final int startLevel;

    public ScriptInfo() {
        currentState = State.DEFAULT;
        startTime = Instant.now();
        startLevel = getCurrentLevel();
        startXp = getCurrentExperience();
    }

    public Duration getRunTime() {
        return Duration.between(startTime, Instant.now());
    }

    public int getLevelsGained() {
        return Microbot.getClient().getRealSkillLevel(Skill.RUNECRAFT) - startLevel;
    }

    public int getExpGained() {
        return Microbot.getClient().getSkillExperience(Skill.RUNECRAFT) - startXp;
    }

    public int getCurrentLevel() {
        return Microbot.getClient().getRealSkillLevel(Skill.RUNECRAFT);
    }

    public int getCurrentExperience() {
        return Microbot.getClient().getSkillExperience(Skill.RUNECRAFT);
    }
}
