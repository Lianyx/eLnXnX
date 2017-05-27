package myxiaoxiaole;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class EndScene extends Pane {
	public static final int WIDTH = GamePanel.WIDTH;
	public static final int HEIGHT = GamePanel.HEIGHT;
	Button reStartOnePlayer = new Button("Restart 1player");
	Button reStartTwoPlayers = new Button("Restart 2player");
	Button reStartOnline = new Button("Restart online");
	
	public EndScene(Image image) {
		BackgroundImage myBI = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		this.setBackground(new Background(myBI));
		
		reStartOnePlayer.setPrefWidth(400);
		reStartTwoPlayers.setPrefWidth(400);
		reStartOnline.setPrefWidth(400);
		
		reStartOnePlayer.setPrefHeight(30);
		reStartTwoPlayers.setPrefHeight(30);
		reStartOnline.setPrefHeight(30);
		
		reStartOnePlayer.setLayoutY((HEIGHT)/2-100-reStartOnePlayer.getPrefHeight());
		reStartTwoPlayers.setLayoutY((HEIGHT)/2-reStartTwoPlayers.getPrefHeight());
		reStartOnline.setLayoutY((HEIGHT/2+100-reStartOnline.getPrefHeight()));
		
		reStartOnePlayer.setLayoutX((WIDTH-reStartOnePlayer.getPrefWidth())/2);
		reStartTwoPlayers.setLayoutX((WIDTH-reStartTwoPlayers.getPrefWidth())/2);
		reStartOnline.setLayoutX((WIDTH-reStartTwoPlayers.getPrefWidth())/2);
		
		reStartOnePlayer.setTextFill(Color.WHITE);
		reStartTwoPlayers.setTextFill(Color.WHITE);
		reStartOnline.setTextFill(Color.WHITE);
		
		reStartOnePlayer.getStyleClass().addAll("start-bt4", "start-bt:hover","bt-circle");
		reStartTwoPlayers.getStyleClass().addAll("start-bt4", "start-bt:hover","bt-circle");
		reStartOnline.getStyleClass().addAll("start-bt4", "start-bt:hover","bt-circle");
		
		this.getChildren().addAll(reStartOnePlayer,reStartTwoPlayers,reStartOnline);
		
		reStartOnePlayer.setOnMouseEntered((MouseEvent e) -> {
			reStartOnePlayer.setTextFill(Color.BLUE);
			reStartOnePlayer.setScaleX(1.03);
			reStartOnePlayer.setScaleY(1.03);
		});
		
		reStartOnePlayer.setOnMouseExited((MouseEvent e) -> {
			reStartOnePlayer.setTextFill(Color.WHITE);
			reStartOnePlayer.setScaleX(1);
			reStartOnePlayer.setScaleY(1);
		});
		
		reStartTwoPlayers.setOnMouseEntered((MouseEvent e) -> {
			reStartTwoPlayers.setTextFill(Color.BLUE);
			reStartTwoPlayers.setScaleX(1.03);
			reStartTwoPlayers.setScaleY(1.03);
		});
		
		reStartTwoPlayers.setOnMouseExited((MouseEvent e) -> {
			reStartTwoPlayers.setTextFill(Color.WHITE);
			reStartTwoPlayers.setScaleX(1);
			reStartTwoPlayers.setScaleY(1);
		});
		
		reStartOnline.setOnMouseEntered((MouseEvent e) -> {
			reStartOnline.setTextFill(Color.BLUE);
			reStartOnline.setScaleX(1.03);
			reStartOnline.setScaleY(1.03);
		});
		
		reStartOnline.setOnMouseExited((MouseEvent e) -> {
			reStartOnline.setTextFill(Color.WHITE);
			reStartOnline.setScaleX(1);
			reStartOnline.setScaleY(1);
		});
		
		reStartOnePlayer.setOnAction(e -> {
			StackPane root = (StackPane) this.getParent();
			root.getChildren().remove(this);
			root.getChildren().add(new GamePanel(2));
		});
		
		reStartTwoPlayers.setOnAction(e -> {
			StackPane root = (StackPane) this.getParent();
			root.getChildren().remove(this);
			root.getChildren().add(new GamePanel(1));
		});
		
	}
}
