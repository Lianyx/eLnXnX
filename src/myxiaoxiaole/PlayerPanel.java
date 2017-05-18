package myxiaoxiaole;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Set;

/**
 * Created by tiberius on 2017/5/6.
 */
public class PlayerPanel extends Pane {
    public static final int WIDTH = 100;
    public static final int HEIGHT = 300;
    public static final int REC_HEIGHT = 270;

    public Label lblHPPoint;
    public Label lblACPoint;

    private IntegerProperty HPProperty = new SimpleIntegerProperty(1000);
    private IntegerProperty ACProperty = new SimpleIntegerProperty(500);

    private int number;
    private GamePanel gamePanel;

    public PlayerPanel(int number, GamePanel gamePanel) {
        this.number = number;
        this.gamePanel = gamePanel;

        this.setPrefWidth(350);
        this.setPrefHeight(GamePanel.OFFSET_HEIGHT);

        Label lblHP = new Label("HP");
        Label lblAC = new Label("AC");
        //StringProperty和StringBinding有什么区别啊...
        lblHPPoint = new Label();
        lblACPoint = new Label();
        lblHPPoint.textProperty().bind(HPProperty.asString());
        lblACPoint.textProperty().bind(ACProperty.asString());

        //TODO 数字的动画

//        Rectangle recHP = new Rectangle();
//        Rectangle recAC = new Rectangle();
//        recHP.setFill(Color.web("#FF6C6C"));//可以设置血条变色
//        recAC.setFill(Color.web("#94F283"));

//        recHP.setWidth(20);
//        recHP.setArcHeight(20);
//        recHP.setArcWidth(20);
//        recAC.setWidth(20);
//        recAC.setArcHeight(20);
//        recAC.setArcWidth(20);
//
//        recHP.setLayoutX(20);
//        recAC.setLayoutX(40);

        //无力。multiply里只能写0.28，不能写280/1000。因为这是整除啊。。
//        recHP.heightProperty().bind(HPProperty.multiply(0.28));
//        recAC.heightProperty().bind(ACProperty.multiply(0.28));

        lblHPPoint.setLayoutX(50);lblHPPoint.setLayoutY(20);
        lblACPoint.setLayoutX(50);lblACPoint.setLayoutY(50);
        lblHP.setLayoutX(20);lblHP.setLayoutY(20);
        lblAC.setLayoutX(20);lblAC.setLayoutY(50);
//        lblHPPoint.setScaleY(-1);
//        lblACPoint.setScaleY(-1);
//        lblHPPoint.layoutYProperty().bind(recHP.heightProperty());
//        lblACPoint.layoutYProperty().bind(recAC.heightProperty());

        this.getChildren().addAll(lblHP, lblAC, lblHPPoint, lblACPoint);

        HPProperty.addListener((observable, o,n)->{
            if((int)n == 0){
                //TODO 这里可以考虑加动画
                gamePanel.setLayerOn();
            }
        });

    }

    public int getHP(){
        return HPProperty.get();
    }
    public int getAC(){
        return ACProperty.get();
    }
    public IntegerProperty HPProperty(){
        return this.HPProperty;
    }
    public IntegerProperty ACProperty(){
        return this.ACProperty;
    }
    public void setHP(int hp){
        this.HPProperty.set(hp);
    }
    public void setAC(int ac){
        this.ACProperty.set(ac);
    }

}
