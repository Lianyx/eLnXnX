package uiyj;/**
 * Created by tiberius on 2017/5/1.
 */

import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class ImageUiyj extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();

        File file = new File(System.getProperty("user.dir") + "/resources/anger.png");
        ImageView j = new ImageView(file.toURI().toString());
        root.getChildren().add(j);

        j.setFitWidth(50);
        j.setPreserveRatio(true);
        j.setSmooth(true);
        j.setCache(true);
        j.setOnMouseClicked(event -> System.out.println("click"));

        Rectangle rec = new Rectangle(0, 0, 50, 25);
        root.getChildren().add(rec);
        Stop[] stops = new Stop[]{new Stop(0, Color.web("#D3F4F9")), new Stop(0.55, Color.web("#F7F6F0")), new Stop(1, Color.web("#F7F6F0"))};

//        rec.setFill(stops);
        rec.setX(100);
        rec.setY(100);
        Rectangle rec1 = new Rectangle(0, 0, 50, 25);
        root.getChildren().add(rec1);

        rec1.setFill(Color.BLUE);
        rec1.setX(100);
        rec1.setY(100);

        j.setX(100);
        j.setY(100);


        Timeline tl1 = make(j, 200);
        tl1.setOnFinished(e->{
            rec.toFront();
            Timeline tl2 = make(j, 100);
            tl2.setOnFinished(e1->{
                rec.setFill(Color.TRANSPARENT);
                rec.toFront();
            });
            tl2.play();
        });
        tl1.play();

        rec.setOnMouseClicked(e->{
            System.out.println("click");
        });


        primaryStage.setScene(new Scene(root, 300, 300));
        primaryStage.show();
    }
    public static Timeline make(ImageView j, int x){
        KeyValue kvX = new KeyValue(j.xProperty(), x, Interpolator.EASE_OUT);
        KeyValue kvY = new KeyValue(j.yProperty(), x, Interpolator.EASE_OUT);
        KeyFrame kf = new KeyFrame(Duration.millis(2000), kvX, kvY);
        Timeline tl = new Timeline(kf);
        return tl;
    }

}
