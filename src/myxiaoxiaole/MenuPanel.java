package myxiaoxiaole;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Created by tiberius on 2017/5/14.
 */
public class MenuPanel extends Pane {
	public MenuPanel() {
		this.setPrefWidth(GamePanel.WIDTH);
		this.setPrefHeight(GamePanel.HEIGHT);
		BackgroundImage myBI = new BackgroundImage(Images.entering, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		this.setBackground(new Background(myBI));
		Button btOnePlayer = new Button("1 Player");
		Button btTwoPlayers = new Button("2 Players");// 这个要突出一点
		Button btOnline = new Button("Online");

		btOnePlayer.getStyleClass().addAll("start-bt1", "start-bt:hover","bt-circle");
		btTwoPlayers.getStyleClass().addAll("start-bt2", "start-bt:hover","bt-circle");
		btOnline.getStyleClass().addAll("start-bt3", "start-bt:hover","bt-circle");

		btOnePlayer.setTextFill(Color.WHITE);
		btOnePlayer.setLayoutY(GamePanel.HEIGHT / 2 - 100);
		btTwoPlayers.setLayoutY(GamePanel.HEIGHT / 2);
		btOnline.setLayoutY(GamePanel.HEIGHT / 2 + 100);

		btOnePlayer.setPrefWidth(300);
		btTwoPlayers.setPrefWidth(300);
		btOnline.setPrefWidth(300);

		btOnePlayer.setOnMouseEntered((MouseEvent e) -> {
			btOnePlayer.setTextFill(Color.BLACK);
			btOnePlayer.setScaleX(1.1);
			btOnePlayer.setScaleY(1.1);
		});
		
		btTwoPlayers.setOnMouseEntered((MouseEvent e) -> {
			btTwoPlayers.setScaleX(1.1);
			btTwoPlayers.setScaleY(1.1);
		});

		btOnline.setOnMouseEntered((MouseEvent e) -> {
			btOnline.setScaleX(1.1);
			btOnline.setScaleY(1.1);
		});
		
		btOnePlayer.setOnMouseExited((MouseEvent e) -> {
			btOnePlayer.setTextFill(Color.WHITE);
			btOnePlayer.setScaleX(1);
			btOnePlayer.setScaleY(1);
		});

		btTwoPlayers.setOnMouseExited((MouseEvent e) -> {
			btTwoPlayers.setScaleX(1);
			btTwoPlayers.setScaleY(1);
		});
		
		btOnline.setOnMouseExited((MouseEvent e) -> {
			btOnline.setScaleX(1);
			btOnline.setScaleY(1);
		});

		// btTwoPlayers.setOnMouseEntered(e->));

		btOnePlayer.setLayoutX((GamePanel.WIDTH - btOnePlayer.getPrefWidth()) * 0.5);
		btTwoPlayers.setLayoutX((GamePanel.WIDTH - btTwoPlayers.getPrefWidth()) * 0.5);
		btOnline.setLayoutX((GamePanel.WIDTH - btOnline.getPrefWidth()) * 0.5);

		this.getChildren().addAll(btOnePlayer, btTwoPlayers, btOnline);

		btOnePlayer.setOnAction(e -> {
			StackPane root = (StackPane) this.getParent();
			root.getChildren().add(new GamePanel(2));
		});

		btTwoPlayers.setOnAction(e -> {
			StackPane root = (StackPane) this.getParent();
			root.getChildren().add(new GamePanel(1));
		});

	}
}
