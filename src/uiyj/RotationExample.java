package uiyj;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class RotationExample extends Application {
    @Override
    public void start(Stage stage) {
        //Drawing Rectangle1
        Rectangle rectangle1 = new Rectangle(390, 320, 420, 240);
        rectangle1.setFill(Color.BLUE);
        rectangle1.setStroke(Color.BLACK);

        //Drawing Rectangle2
        Rectangle rectangle2 = new Rectangle(390, 320, 420, 240);
        Group group = new Group();
        group.setLayoutX(390);
        group.setLayoutY(320);

        Rectangle r1 = new Rectangle(120, 240);
        r1.setLayoutX(300);
        r1.setFill(Color.YELLOW);
        Rectangle r2 = new Rectangle(120, 240);
        r2.setLayoutX(150);
        r2.setFill(Color.AQUA);
        Rectangle r3 = new Rectangle(120, 240);
        r3.setLayoutX(0);
        r2.setFill(Color.BEIGE);
        group.getChildren().addAll(r1, r2, r3);

        //Setting the color of the rectangle
        rectangle2.setFill(Color.BURLYWOOD);

        //Setting the stroke color of the rectangle
        rectangle2.setStroke(Color.BLACK);

        //creating the rotation transformation
        Rotate rotate = new Rotate();

        //Setting the angle for the rotation
        rotate.setAngle(20);

        //Setting pivot points for the rotation
//        rotate.setPivotX(600);
//        rotate.setPivotY(1400);
        rotate.setPivotX(210);
        rotate.setPivotY(1160);

        //Adding the transformation to rectangle2
        group.getTransforms().addAll(rotate);

        //Creating a Group object
        Group root = new Group(/*rectangle1, */rectangle2, group);

        //Creating a scene object
        Scene scene = new Scene(root, 1200, 800);

        //Setting title to the Stage
        stage.setTitle("Rotation transformation example");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();
    }
    public static void main(String args[]){
        launch(args);
    }
}
