package myxiaoxiaole;

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

        Button btOnePlayer = new Button("1 Players");
        Button btTwoPlayers = new Button("2 Players");//这个要突出一点
        Button btOnline = new Button("Online");

        btOnePlayer.setLayoutY(GamePanel.HEIGHT/2 - 100);
        btTwoPlayers.setLayoutY(GamePanel.HEIGHT/2);
        btOnline.setLayoutY(GamePanel.HEIGHT/2 + 100);

        this.getChildren().addAll(btOnePlayer, btTwoPlayers, btOnline);

        btTwoPlayers.setOnAction(e->{
            StackPane root = (StackPane) this.getParent();
            root.getChildren().add(new GamePanel());
        });
    }
}
