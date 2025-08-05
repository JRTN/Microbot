package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentAntiban;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;

import java.util.function.Consumer;

public class FluentAntibanImpl implements FluentAntiban {

    @Override
    public Action actionCooldown() {
        return () -> {
            try {
                Rs2Antiban.actionCooldown();
                return true;
            } catch (Exception e) {
                return false;
            }
        };
    }

    @Override
    public Action microBreak() {
        return () -> {
            try {
                return Rs2Antiban.takeMicroBreakByChance();
            } catch (Exception e) {
                return false;
            }
        };
    }

    @Override
    public Action moveMouseOffScreen() {
        return () -> {
            try {
                Rs2Antiban.moveMouseOffScreen();
                return true;
            } catch (Exception e) {
                return false;
            }
        };
    }

    @Override
    public Action moveMouseOffScreenWithChance(double chance) {
        return () -> {
            try {
                Rs2Antiban.moveMouseOffScreen(chance);
                return true;
            } catch (Exception e) {
                return false;
            }
        };
    }

    @Override
    public Action moveMouseRandomly() {
        return () -> {
            try {
                Rs2Antiban.moveMouseRandomly();
                return true;
            } catch (Exception e) {
                return false;
            }
        };
    }

    @Override
    public Action setActivity(Activity activity) {
        return () -> {
            try {
                Rs2Antiban.setActivity(activity);
                return true;
            } catch (Exception e) {
                return false;
            }
        };
    }

    @Override
    public Action setActivityIntensity(ActivityIntensity intensity) {
        return () -> {
            try {
                Rs2Antiban.setActivityIntensity(intensity);
                return true;
            } catch (Exception e) {
                return false;
            }
        };
    }

    @Override
    public boolean isActive() {
        return Rs2AntibanSettings.antibanEnabled;
    }

    @Override
    public boolean isCooldownActive() {
        return Rs2AntibanSettings.actionCooldownActive;
    }

    @Override
    public boolean isMicroBreakActive() {
        return Rs2AntibanSettings.microBreakActive;
    }

    @Override
    public Action configure(Consumer<Config> configurer) {
        return () -> {
            try {
                FluentAntiban.Config config = new ConfigImpl();
                configurer.accept(config);
                return true;
            } catch (Exception e) {
                return false;
            }
        };
    }

    private static class ConfigImpl implements FluentAntiban.Config {
        @Override
        public void enable() {
            Rs2AntibanSettings.antibanEnabled = true;
        }

        @Override
        public void disable() {
            Rs2AntibanSettings.antibanEnabled = false;
        }

        @Override
        public void enableRandomIntervals() {
            Rs2AntibanSettings.randomIntervals = true;
        }

        @Override
        public void disableRandomIntervals() {
            Rs2AntibanSettings.randomIntervals = false;
        }

        @Override
        public void enableFatigueSimulation() {
            Rs2AntibanSettings.simulateFatigue = true;
        }

        @Override
        public void disableFatigueSimulation() {
            Rs2AntibanSettings.simulateFatigue = false;
        }

        @Override
        public void enableAttentionSpanSimulation() {
            Rs2AntibanSettings.simulateAttentionSpan = true;
        }

        @Override
        public void disableAttentionSpanSimulation() {
            Rs2AntibanSettings.simulateAttentionSpan = false;
        }

        @Override
        public void enableBehavioralVariability() {
            Rs2AntibanSettings.behavioralVariability = true;
        }

        @Override
        public void disableBehavioralVariability() {
            Rs2AntibanSettings.behavioralVariability = false;
        }

        @Override
        public void enableNonLinearIntervals() {
            Rs2AntibanSettings.nonLinearIntervals = true;
        }

        @Override
        public void disableNonLinearIntervals() {
            Rs2AntibanSettings.nonLinearIntervals = false;
        }

        @Override
        public void enableNaturalMouse() {
            Rs2AntibanSettings.naturalMouse = true;
        }

        @Override
        public void disableNaturalMouse() {
            Rs2AntibanSettings.naturalMouse = false;
        }

        @Override
        public void enableRandomMouseMovement() {
            Rs2AntibanSettings.moveMouseRandomly = true;
        }

        @Override
        public void disableRandomMouseMovement() {
            Rs2AntibanSettings.moveMouseRandomly = false;
        }

        @Override
        public void enableOffScreenMouseMovement() {
            Rs2AntibanSettings.moveMouseOffScreen = true;
        }

        @Override
        public void disableOffScreenMouseMovement() {
            Rs2AntibanSettings.moveMouseOffScreen = false;
        }

        @Override
        public void enableUniversalAntiban() {
            Rs2AntibanSettings.universalAntiban = true;
        }

        @Override
        public void disableUniversalAntiban() {
            Rs2AntibanSettings.universalAntiban = false;
        }

        @Override
        public void enablePlayStyle() {
            Rs2AntibanSettings.usePlayStyle = true;
        }

        @Override
        public void disablePlayStyle() {
            Rs2AntibanSettings.usePlayStyle = false;
        }

        @Override
        public void enableMicroBreaks() {
            Rs2AntibanSettings.takeMicroBreaks = true;
        }

        @Override
        public void disableMicroBreaks() {
            Rs2AntibanSettings.takeMicroBreaks = false;
        }

        @Override
        public void setMicroBreakChance(double chance) {
            Rs2AntibanSettings.microBreakChance = chance;
        }

        @Override
        public void setActionCooldownChance(double chance) {
            Rs2AntibanSettings.actionCooldownChance = chance;
        }

        @Override
        public void setMouseRandomChance(double chance) {
            Rs2AntibanSettings.moveMouseRandomlyChance = chance;
        }

        @Override
        public void setMouseOffScreenChance(double chance) {
            Rs2AntibanSettings.moveMouseOffScreenChance = chance;
        }

        @Override
        public void setMicroBreakDurationMax(int maxMinutes) {
            Rs2AntibanSettings.microBreakDurationHigh = maxMinutes;
        }

        @Override
        public void setMicroBreakDurationMin(int minMinutes) {
            Rs2AntibanSettings.microBreakDurationLow = minMinutes;
        }

        @Override
        public void resetSettings() {
            Rs2AntibanSettings.reset();
        }
    }
}
