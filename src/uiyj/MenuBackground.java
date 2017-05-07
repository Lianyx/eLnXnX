package uiyj;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import uiyj.rsces.Sun;

import java.util.ArrayList;

/**
 * Created by tiberius on 2017/5/2.
 */
public class MenuBackground extends Application{
    public final static int WIDTH = 1200;
    public final static int HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        root.setPrefWidth(WIDTH);
        root.setPrefHeight(HEIGHT);
        root.setStyle("-fx-background-color: #B0B0B0");

        Sun sun = new Sun();
        root.getChildren().add(sun);
//        Rotate sunRo = new Rotate();
//        sunRo.pivotXProperty().bind(sun.centerXProperty());
//        sunRo.pivotYProperty().bind(sun.centerYProperty().add(1290));
//        sunRo.setAngle(-20);
//        sun.getTransforms().add(sunRo);


        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        final long startNanoTime = System.nanoTime();
        AnimationTimer at = new AnimationTimer() {
            int i = 1;
            @Override
            public void handle(long n) {
                System.out.println(i);
                i++;
            }
        };
        at.start();
    }
}
