package myxiaoxiaole;

import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javax.swing.text.html.ImageView;
import java.util.Set;

/**
 * Created by tiberius on 2017/5/6.
 */
public class PlayerPanel extends Pane {
    //TODO 应该下面是正着看。但是这样的话问题很多啊。。怎么才能清楚地知道自己和对方的血量？还是有个大长条？
    public static final int WIDTH = 100;
    public static final int HEIGHT = 300;
    public static final int REC_HEIGHT = 270;
    public static final double RATIO1 = 0.28;        //初值0.28
    public static final double RATIO2 = 0.56;

    public Label lblHPPoint;
    public Label lblACPoint;
//    private ImageView playerView;

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
        lblHPPoint = new Label("1000");
        lblACPoint = new Label("500");
//        lblHPPoint.textProperty().bind(HPProperty.asString());
//        lblACPoint.textProperty().bind(ACProperty.asString());

        //TODO 数字的动画

        Rectangle recHP = new Rectangle();
        Rectangle recAC = new Rectangle();
        recHP.setFill(Color.web("#FF6C6C"));//可以设置血条变色
        recAC.setFill(Color.web("#94F283"));

        recHP.setHeight(20);
        recHP.setArcHeight(20);
        recHP.setArcWidth(20);
        recAC.setHeight(20);
        recAC.setArcHeight(20);
        recAC.setArcWidth(20);

        recHP.setLayoutY(20);
        recAC.setLayoutY(50);

//        无力。multiply里只能写0.28，不能写280/1000。因为这是整除啊。。
//        recHP.heightProperty().bind(HPProperty.multiply(0.28));
//        recAC.heightProperty().bind(ACProperty.multiply(0.28));
        recHP.setWidth(1000*RATIO1);
        recAC.setWidth(500*RATIO2);

        this.getChildren().addAll(recHP, recAC);

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
            int oldValue = (int) o;
            int newValue = (int) n;
            int gap = Math.abs(oldValue - newValue);
            int change = (newValue > oldValue) ? 1 : -1;

            //这里应该考虑到如果一起修改的话
            if(oldValue > newValue){
                //TODO 受伤的动画
            } else {
                //TODO 加血的动画
            }

            if(newValue == 0){
                //TODO 死了动画就要停了。
                gamePanel.setLayerOn();
            } else {
                lblHPPoint.setText(String.valueOf(oldValue));
                Timeline tl = new Timeline(
                        new KeyFrame(Duration.millis(20), e->lblHPPoint.setText(String.valueOf(Integer.parseInt(lblHPPoint.getText()) + change)))
                );
                tl.setCycleCount(gap);
                tl.play();

                Timeline tl2 = new Timeline(
                        new KeyFrame(Duration.millis(20), e->recHP.setWidth((recHP.getWidth()/RATIO1 + change)*RATIO1))
                );
                tl2.setCycleCount(gap);
                tl2.play();
            }
        });

        ACProperty.addListener((observable, o, n) -> {
            int oldValue = (int) o;
            int newValue = (int) n;
            int gap = Math.abs(oldValue - newValue);
            int change = (newValue > oldValue) ? 1 : -1;

            lblACPoint.setText(String.valueOf(oldValue));
            Timeline tl = new Timeline(
                    new KeyFrame(Duration.millis(20), e-> lblACPoint.setText(String.valueOf(Integer.parseInt(lblACPoint.getText()) + change)))
            );
            tl.setCycleCount(gap);
            tl.play();

            Timeline tl2 = new Timeline(
                    new KeyFrame(Duration.millis(20), e->recAC.setWidth((recAC.getWidth()/RATIO2 + change)*RATIO2))
            );
            tl2.setCycleCount(gap);
            tl2.play();
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
