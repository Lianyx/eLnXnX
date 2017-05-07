package uiyj;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
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


        root.setStyle("-fx-background-color: #B0B0B0");
        root.setPrefWidth(1200/2);
        root.setPrefHeight(800/2);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        ArrayList<Integer> list1 = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        ArrayList<Integer> list2 = new ArrayList<>(Arrays.asList(5, 6, 7, 8, 9));
        ArrayList<Integer> list3 = new ArrayList<>(Arrays.asList(51, 61, 71, 81, 91));


//        list1.addAll(list2);
//        list1.remove(new Integer(5));
//        list1.remove(new Integer(5));
//
//        System.out.println(list1);

        ArrayList<ArrayList<Integer>> jewelsToBeDeleted = new ArrayList<ArrayList<Integer>>();
        jewelsToBeDeleted.add(list1);
        jewelsToBeDeleted.add(list2);
        jewelsToBeDeleted.add(list3);


        ArrayList<Integer> toBeMergedList1 = new ArrayList<>();
        System.out.println(jewelsToBeDeleted);


        Integer tempJewel = 5;
        for(ArrayList<Integer> tempList: jewelsToBeDeleted.stream().collect(Collectors.toList())) {
            if (tempList.contains(tempJewel) && toBeMergedList1.isEmpty()) {
                toBeMergedList1 = tempList;
                jewelsToBeDeleted.remove(tempList);
            } else if (tempList.contains(tempJewel)) {
                System.out.println();
                jewelsToBeDeleted.remove(tempList);
                tempList.addAll(toBeMergedList1);
                tempList.remove(tempJewel);
                tempList.remove(tempJewel);
                jewelsToBeDeleted.add(tempList);
            }
        }
        System.out.println();
        System.out.println(jewelsToBeDeleted);
//        jewelsToBeDeleted.forEach(e->{
//            System.out.println(e);
//        });
//
//        IntegerProperty width = new SimpleIntegerProperty(10);
//        IntegerProperty height = new SimpleIntegerProperty(10);
//
//        NumberBinding area = width.multiply(height);
//
//        System.out.println(area.getValue());
    }
}
