package uiyj;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import myxiaoxiaole.Jewel;
import myxiaoxiaole.PlayerPanel;
import uiyj.rsces.Moon;
import uiyj.rsces.Star;
import uiyj.rsces.StarCircle;
import uiyj.rsces.Sun;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 * 1.这个是为了试验解决path与translateX/Y的问题
 * 另外即使我不getChildren add，动画也会做（当然看不到）
 * <p>
 * 2.试验了linearGradient
 * 3.Moon, Sun, star,
 */
public class RectangleUiyj extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        Label lblPlayer = new Label("gads");
        root.getChildren().add(lblPlayer);
        lblPlayer.setOpacity(0);

        SequentialTransition returnST = new SequentialTransition();
        Timeline animatelbl = new Timeline(
                new KeyFrame(Duration.millis(1000), new KeyValue(lblPlayer.scaleXProperty(), 3, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1000), new KeyValue(lblPlayer.scaleYProperty(), 3, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1000), new KeyValue(lblPlayer.opacityProperty(), 1, Interpolator.EASE_OUT))
        );
        animatelbl.setOnFinished(e->root.getChildren().remove(lblPlayer));
        SequentialTransition st = new SequentialTransition(animatelbl);
        st.play();


        root.setStyle("-fx-background-color: #B0B0B0");
        root.setPrefWidth(1200/2);
        root.setPrefHeight(800/2);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
