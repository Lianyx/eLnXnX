package myxiaoxiaole;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Created by tiberius on 2017/5/14.
 */
public class MenuPanel extends Pane {
    public MenuPanel(){
        this.setPrefWidth(GamePanel.WIDTH);
        this.setPrefHeight(GamePanel.HEIGHT);

        Button btOnePlayer = new Button("1 Player");
        Button btTwoPlayers = new Button("2 Players");//这个要突出一点
        Button btOnline = new Button("Online");

        btOnePlayer.getStyleClass().add("game-btMenu");
        btTwoPlayers.getStyleClass().add("game-btMenu");
        btOnline.getStyleClass().add("game-btMenu");

        btOnePlayer.setLayoutY(GamePanel.HEIGHT/2 - 100);
        btTwoPlayers.setLayoutY(GamePanel.HEIGHT/2);
        btOnline.setLayoutY(GamePanel.HEIGHT/2 + 100);

        btOnePlayer.setPrefWidth(300);
        btTwoPlayers.setPrefWidth(300);
        btOnline.setPrefWidth(300);

//        btTwoPlayers.setOnMouseEntered(e->));

        btOnePlayer.setLayoutX((GamePanel.WIDTH - btOnePlayer.getPrefWidth()) * 0.5);
        btTwoPlayers.setLayoutX((GamePanel.WIDTH - btTwoPlayers.getPrefWidth()) * 0.5);
        btOnline.setLayoutX((GamePanel.WIDTH - btOnline.getPrefWidth()) * 0.5);


        this.getChildren().addAll(btOnePlayer, btTwoPlayers, btOnline);

        btOnePlayer.setOnAction(e->{
           StackPane root = (StackPane) this.getParent();
           root.getChildren().add(new GamePanel(2));
        });

        btTwoPlayers.setOnAction(e->{
            StackPane root = (StackPane) this.getParent();
            root.getChildren().add(new GamePanel(1));
        });




    }
}
