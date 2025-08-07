package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentAntiban;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;

import java.util.function.Consumer;

@Slf4j
public class FluentAntibanImpl implements FluentAntiban {

    @Override
    public Action actionCooldown() {
        return () -> {
            try {
                log.debug("Executing action cooldown");
                Rs2Antiban.actionCooldown();
                log.debug("Action cooldown completed successfully");
                return true;
            } catch (Exception e) {
                log.warn("Action cooldown failed", e);
                return false;
            }
        };
    }

    @Override
    public Action microBreak() {
        return () -> {
            try {
                log.debug("Attempting micro break");
                boolean tookBreak = Rs2Antiban.takeMicroBreakByChance();
                if (tookBreak) {
                    log.info("Micro break taken");
                } else {
                    log.debug("Micro break not taken (by chance)");
                }
                return tookBreak;
            } catch (Exception e) {
                log.warn("Micro break failed", e);
                return false;
            }
        };
    }

    @Override
    public Action moveMouseOffScreen() {
        return () -> {
            try {
                log.debug("Moving mouse off screen");
                Rs2Antiban.moveMouseOffScreen();
                log.debug("Mouse moved off screen successfully");
                return true;
            } catch (Exception e) {
                log.warn("Failed to move mouse off screen", e);
                return false;
            }
        };
    }

    @Override
    public Action moveMouseOffScreenWithChance(double chance) {
        return () -> {
            try {
                log.debug("Moving mouse off screen with chance: {}", chance);
                Rs2Antiban.moveMouseOffScreen(chance);
                log.debug("Mouse off screen action completed");
                return true;
            } catch (Exception e) {
                log.warn("Failed to move mouse off screen with chance: {}", chance, e);
                return false;
            }
        };
    }

    @Override
    public Action moveMouseRandomly() {
        return () -> {
            try {
                log.debug("Moving mouse randomly");
                Rs2Antiban.moveMouseRandomly();
                log.debug("Random mouse movement completed");
                return true;
            } catch (Exception e) {
                log.warn("Failed to move mouse randomly", e);
                return false;
            }
        };
    }

    @Override
    public boolean isActive() {
        boolean active = Rs2AntibanSettings.antibanEnabled;
        log.debug("Antiban active status: {}", active);
        return active;
    }

    @Override
    public boolean isCooldownActive() {
        boolean active = Rs2AntibanSettings.actionCooldownActive;
        log.debug("Action cooldown active status: {}", active);
        return active;
    }

    @Override
    public boolean isMicroBreakActive() {
        boolean active = Rs2AntibanSettings.microBreakActive;
        log.debug("Micro break active status: {}", active);
        return active;
    }

    @Override
    public void configure(Consumer<Config> configurer) {
        log.info("Configuring antiban settings");
        FluentAntiban.Config config = new ConfigImpl();
        configurer.accept(config);
        log.info("Antiban configuration completed");
    }

    private static class ConfigImpl implements FluentAntiban.Config {
        @Override
        public void enable() {
            log.info("Enabling antiban");
            Rs2AntibanSettings.antibanEnabled = true;
        }

        @Override
        public void disable() {
            log.info("Disabling antiban");
            Rs2AntibanSettings.antibanEnabled = false;
        }

        @Override
        public void enableRandomIntervals() {
            log.debug("Enabling random intervals");
            Rs2AntibanSettings.randomIntervals = true;
        }

        @Override
        public void disableRandomIntervals() {
            log.debug("Disabling random intervals");
            Rs2AntibanSettings.randomIntervals = false;
        }

        @Override
        public void enableFatigueSimulation() {
            log.debug("Enabling fatigue simulation");
            Rs2AntibanSettings.simulateFatigue = true;
        }

        @Override
        public void disableFatigueSimulation() {
            log.debug("Disabling fatigue simulation");
            Rs2AntibanSettings.simulateFatigue = false;
        }

        @Override
        public void enableAttentionSpanSimulation() {
            log.debug("Enabling attention span simulation");
            Rs2AntibanSettings.simulateAttentionSpan = true;
        }

        @Override
        public void disableAttentionSpanSimulation() {
            log.debug("Disabling attention span simulation");
            Rs2AntibanSettings.simulateAttentionSpan = false;
        }

        @Override
        public void enableBehavioralVariability() {
            log.debug("Enabling behavioral variability");
            Rs2AntibanSettings.behavioralVariability = true;
        }

        @Override
        public void disableBehavioralVariability() {
            log.debug("Disabling behavioral variability");
            Rs2AntibanSettings.behavioralVariability = false;
        }

        @Override
        public void enableNonLinearIntervals() {
            log.debug("Enabling non-linear intervals");
            Rs2AntibanSettings.nonLinearIntervals = true;
        }

        @Override
        public void disableNonLinearIntervals() {
            log.debug("Disabling non-linear intervals");
            Rs2AntibanSettings.nonLinearIntervals = false;
        }

        @Override
        public void enableNaturalMouse() {
            log.debug("Enabling natural mouse");
            Rs2AntibanSettings.naturalMouse = true;
        }

        @Override
        public void disableNaturalMouse() {
            log.debug("Disabling natural mouse");
            Rs2AntibanSettings.naturalMouse = false;
        }

        @Override
        public void enableRandomMouseMovement() {
            log.debug("Enabling random mouse movement");
            Rs2AntibanSettings.moveMouseRandomly = true;
        }

        @Override
        public void disableRandomMouseMovement() {
            log.debug("Disabling random mouse movement");
            Rs2AntibanSettings.moveMouseRandomly = false;
        }

        @Override
        public void enableOffScreenMouseMovement() {
            log.debug("Enabling off-screen mouse movement");
            Rs2AntibanSettings.moveMouseOffScreen = true;
        }

        @Override
        public void disableOffScreenMouseMovement() {
            log.debug("Disabling off-screen mouse movement");
            Rs2AntibanSettings.moveMouseOffScreen = false;
        }

        @Override
        public void enableUniversalAntiban() {
            log.debug("Enabling universal antiban");
            Rs2AntibanSettings.universalAntiban = true;
        }

        @Override
        public void disableUniversalAntiban() {
            log.debug("Disabling universal antiban");
            Rs2AntibanSettings.universalAntiban = false;
        }

        @Override
        public void enablePlayStyle() {
            log.debug("Enabling play style");
            Rs2AntibanSettings.usePlayStyle = true;
        }

        @Override
        public void disablePlayStyle() {
            log.debug("Disabling play style");
            Rs2AntibanSettings.usePlayStyle = false;
        }

        @Override
        public void enableMicroBreaks() {
            log.debug("Enabling micro breaks");
            Rs2AntibanSettings.takeMicroBreaks = true;
        }

        @Override
        public void disableMicroBreaks() {
            log.debug("Disabling micro breaks");
            Rs2AntibanSettings.takeMicroBreaks = false;
        }

        @Override
        public void setActivity(Activity activity) {
            log.info("Setting activity: {}", activity);
            Rs2Antiban.setActivity(activity);
        }

        @Override
        public void setActivityIntensity(ActivityIntensity intensity) {
            log.info("Setting activity intensity: {}", intensity);
            Rs2Antiban.setActivityIntensity(intensity);
        }

        @Override
        public void setMicroBreakChance(double chance) {
            log.debug("Setting micro break chance: {}", chance);
            Rs2AntibanSettings.microBreakChance = chance;
        }

        @Override
        public void setActionCooldownChance(double chance) {
            log.debug("Setting action cooldown chance: {}", chance);
            Rs2AntibanSettings.actionCooldownChance = chance;
        }

        @Override
        public void setMouseRandomChance(double chance) {
            log.debug("Setting mouse random chance: {}", chance);
            Rs2AntibanSettings.moveMouseRandomlyChance = chance;
        }

        @Override
        public void setMouseOffScreenChance(double chance) {
            log.debug("Setting mouse off-screen chance: {}", chance);
            Rs2AntibanSettings.moveMouseOffScreenChance = chance;
        }

        @Override
        public void setMicroBreakDurationMax(int maxMinutes) {
            log.debug("Setting micro break max duration: {} minutes", maxMinutes);
            Rs2AntibanSettings.microBreakDurationHigh = maxMinutes;
        }

        @Override
        public void setMicroBreakDurationMin(int minMinutes) {
            log.debug("Setting micro break min duration: {} minutes", minMinutes);
            Rs2AntibanSettings.microBreakDurationLow = minMinutes;
        }

        @Override
        public void resetSettings() {
            log.info("Resetting antiban settings to defaults");
            Rs2AntibanSettings.reset();
        }
    }
}
