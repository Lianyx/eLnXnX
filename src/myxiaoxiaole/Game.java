package myxiaoxiaole;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Game extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		StackPane root = new StackPane();
		Scene scene = new Scene(root);
		MenuPanel menuPanel = new MenuPanel();

		root.getChildren().add(menuPanel);

		scene.getStylesheets().add(Game.class.getResource("game.css").toExternalForm());
//		System.out.println(Game.class.getResource("game.css").toExternalForm());
		root.getStyleClass().add("game-root");



		stage.setScene(scene);
//		stage.initStyle(StageStyle.UNDECORATED);
		stage.setResizable(true);
		stage.show();
	}

}
