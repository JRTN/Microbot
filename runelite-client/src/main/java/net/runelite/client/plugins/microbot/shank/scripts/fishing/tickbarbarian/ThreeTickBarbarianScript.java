package net.runelite.client.plugins.microbot.shank.scripts.fishing.tickbarbarian;

import static net.runelite.api.AnimationID.FISHING_BARBARIAN_ROD;
import static net.runelite.api.AnimationID.HERBLORE_MAKE_TAR;
import static net.runelite.api.gameval.ItemID.GUAM_LEAF;
import static net.runelite.api.gameval.ItemID.SWAMP_TAR;
import static net.runelite.client.plugins.microbot.shank.api.fluent.Rs2Fluent.*;

import lombok.extern.slf4j.Slf4j;

import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.plugins.microbot.shank.api.fluent.AbstractFluentScript;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentAntiban;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcModel;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class ThreeTickBarbarianScript extends AbstractFluentScript {

    @Inject
    public ThreeTickBarbarianScript(EventBus eventBus) {
        eventBus.register(this);
    }

    @Override
    protected void configureAntiban(FluentAntiban.Config config) {
        config.setActivity(Activity.HIGH_INTENSITY_SKILLING);
        config.setActivityIntensity(ActivityIntensity.EXTREME);

        config.enablePlayStyle();
        config.disableNaturalMouse();

        config.enableNonLinearIntervals();
        config.enableRandomMouseMovement();
        config.setMouseRandomChance(0.62);

        config.disableMicroBreaks();
    }

    @Override
    protected void onLoop() {
        when(true)
                .then(tickManipulation()
                        .action(fishAtSpot())
                        .waitUntil(this::hasLeapingFish)
                        .timeout(650)
                        .before(inventory()
                                .combine(GUAM_LEAF, SWAMP_TAR))
                        .repeating()
                ).then(inventory().drop(this::leapingFish));
    }

    @Override
    protected int pollingRate() {
        return 60;
    }

    private long tickActionTimeout() {
        return 650;
    }

    private boolean leapingFish(Rs2ItemModel item) {
        return StringUtils.startsWith(item.getName().toLowerCase(), "leaping");
    }

    private boolean doesNotHaveLeapingFish() {
        return !hasLeapingFish();
    }

    private boolean hasLeapingFish() {
        return inventory().containsItem(this::leapingFish);
    }

    private Action doTickFishing() {
        return doTickAction().then(doFishAction());
    }

    private Action fishAtSpot() {
        return () -> findFishingSpot()
                .filter(rs2NpcModel -> Rs2Npc.interact(rs2NpcModel, "Use-rod"))
                .isPresent();
    }

    private boolean isFishActionCompleted() {
        return player().getLocalPlayer().getAnimation() == FISHING_BARBARIAN_ROD;
    }

    private Action doFishAction() {
        return fishAtSpot();
    }

    private boolean isTickActionCompleted() {
        return player().getLocalPlayer().getAnimation() == HERBLORE_MAKE_TAR;
    }

    private Action doTickAction() {
        return inventory()
                .combine(GUAM_LEAF, SWAMP_TAR)
                .then(timing().sleep(590, 12));
    }

    private Optional<Rs2NpcModel> findFishingSpot() {
        return Arrays.stream(FishingSpot.BARB_FISH.getIds())
                .mapToObj(Rs2Npc::getNpc)
                .filter(Objects::nonNull)
                .findFirst();
    }
}
