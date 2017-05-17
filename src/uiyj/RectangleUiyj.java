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
import java.util.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 1.这个是为了试验解决path与translateX/Y的问题
 * 另外即使我不getChildren add，动画也会做（当然看不到）
 * <p>
 * 2.试验了linearGradient
 * 3.Moon, Sun, star,
 */
public class RectangleUiyj extends Application {
    private Timeline tl;
    long lastUpdate = System.currentTimeMillis();


    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();

        root.setStyle("-fx-background-color: #B0B0B0");

        Rectangle rec = new Rectangle(200,200,200,200);
        root.getChildren().add(rec);
        tl = new Timeline(
                new KeyFrame(Duration.millis(10000), new KeyValue(rec.scaleXProperty(), 1.5))
        );
        tl.play();
/**这个控制timer运行频率的方法注意一下*/
       AnimationTimer timer = new AnimationTimer() {
           private int frameSkip = 0;
           private final int SKIP = 60;
            @Override
            public void handle(long now) {
                frameSkip++;
                if(frameSkip <= SKIP){
                    return;
                }
                if(tl == null){
                    System.out.println("null");
                } else {
                    System.out.println("..");
                }
                frameSkip = 0;
             }
        };
       timer.start();
        root.setPrefWidth(1200);
        root.setPrefHeight(800);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }
}
