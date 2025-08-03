package net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas;

import lombok.Setter;
import net.runelite.client.plugins.microbot.shank.scripts.runecrafting.lavas.info.ScriptInfo;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import org.apache.commons.lang3.time.DurationFormatUtils;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;

public class LavasRcOverlay extends OverlayPanel {
    @Setter private ScriptInfo scriptInfo;

    @Inject
    LavasRcOverlay(LavasRcPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        var title = TitleComponent.builder()
                .text("Lava Test Script")
                .color(Color.GREEN)
                .build();

        var blankLine = LineComponent.builder().build();

        var runtime = LineComponent.builder()
                .left("Runtime: " + formatDuration(scriptInfo.getRunTime()))
                .build();

        var level = LineComponent.builder()
                .left(String.format("Level: %d (+%d)", scriptInfo.getCurrentLevel(), scriptInfo.getLevelsGained()))
                .build();

        var expGained = LineComponent.builder()
                .left("Experience gained: " + scriptInfo.getExpGained())
                .build();

        double hoursElapsed = scriptInfo.getRunTime().toMillis() / (1000.0 * 60.0 * 60.0);
        var expHr = LineComponent.builder()
                .left("Xp/hr: " + (int)(scriptInfo.getExpGained() / hoursElapsed))
                .build();

        var state = LineComponent.builder()
                .left("STATE: " + scriptInfo.getCurrentState().getTitle())
                .build();


        addToLayout(title);
        addToLayout(blankLine);
        addToLayout(runtime);
        addToLayout(level);
        addToLayout(expGained);
        addToLayout(expHr);
        addToLayout(state);

        return super.render(graphics);
    }

    String formatDuration(Duration duration) {
        return DurationFormatUtils.formatDuration(duration.toMillis(), "HH:mm:ss");
    }

    void addToLayout(LayoutableRenderableEntity entity) {
        panelComponent.getChildren().add(entity);
    }
}
