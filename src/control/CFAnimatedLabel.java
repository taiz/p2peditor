package control;

import javafx.animation.FadeTransitionBuilder;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransitionBuilder;
import javafx.animation.Transition;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class CFAnimatedLabel extends Label {
    protected static final double DEFAULT_TIME = 2d;
    protected double time = DEFAULT_TIME;
    protected final Transition animation;

    public CFAnimatedLabel() { this(""); }

    public CFAnimatedLabel(String text) {
        super(text);
        setOpacity(0d);
        setWrapText(true);
        animation = buildAnimation();
    }

    public void showText(String text) {
        setText(text);
        animation.play();
    }

    protected Transition buildAnimation() {
        return SequentialTransitionBuilder.create()
            .node(this)
            .children(
                FadeTransitionBuilder.create()
                    .duration(Duration.seconds(time * 0.5))
                    .fromValue(0.0)
                    .toValue(1.0)
                    .interpolator(Interpolator.EASE_IN)
                    .build(),
                FadeTransitionBuilder.create()
                    .duration(Duration.seconds(time * 0.5))
                    .fromValue(1.0)
                    .toValue(0.0)
                    .interpolator(Interpolator.EASE_OUT)
                    .build()
            )
            .build();
    }
}