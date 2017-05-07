package myxiaoxiaole;

import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.Set;


public class GamePanel extends Pane {
	public static final int WIDTH = 900/2;
	public static final int HEIGHT = 1600/2;

	private final GridPanel gridPanel;
	private Timeline scoreFall;

	public PlayerPanel playerA, playerB;

	public GamePanel() {
		this.setPrefWidth(WIDTH);
		this.setPrefHeight(HEIGHT);
		gridPanel = new GridPanel(this);

		playerA = new PlayerPanel();
		playerA.setLayoutX(GridPanel.GRIDPANEL_WIDTH);
		playerA.setLayoutY(GamePanel.HEIGHT/2);
		playerA.setScaleY(-1);//翻转，playerA是下面的那个
		playerA.lblHPPoint.setScaleY(-1);
		playerA.lblACPoint.setScaleY(-1);

		playerB = new PlayerPanel();
		playerB.setLayoutX(GridPanel.GRIDPANEL_WIDTH);
		playerB.setLayoutY((GamePanel.HEIGHT - GridPanel.GRIDPANEL_HEIGHT)/2);
//		playerB.lblHPPoint.setScaleY(-1);
//		playerB.lblACPoint.setScaleY(-1);
//		playerB.lblHPPoint.setScaleX(-1);
//		playerB.lblACPoint.setScaleX(-1);
//		为了方便看先去掉，如果用rotate?

		Line divideLine = new Line();
		divideLine.setStartX(0);
		divideLine.endXProperty().bind(this.widthProperty());
		divideLine.setStartY(HEIGHT/2);
		divideLine.setEndY(HEIGHT/2);
		divideLine.setStroke(Color.ORANGE);
		divideLine.setStrokeWidth(6);

//		VBox vGame = new VBox();
//
//		HBox hTop = new HBox();
//		hTop.setPadding(new Insets(10));
//		HBox HBottom = new HBox();
//
//		HBox hFill = new HBox();
//		HBox.setHgrow(hFill, Priority.ALWAYS);
//
//		VBox vPlayer1 = new VBox();
//		Label lblPlayer1 = new Label("Player1");
//		lblPlayer1.setAlignment(Pos.CENTER);
//		lblPlayer1Score.setAlignment(Pos.CENTER);
//
//		vPlayer1.getChildren().addAll(lblPlayer1, lblPlayer1Score);

		Button bMenu = new Button("menu");
		bMenu.setLayoutX(10);
		bMenu.setLayoutY(10);
//		bMenu.getStyleClass().add("game-menu");
//		hTop.getChildren().addAll(bMenu, hFill, vPlayer1);

//		vGame.getChildren().addAll(hTop, gridPanel);

//		this.getChildren().add(vGame);
		this.getChildren().addAll(bMenu, gridPanel, playerA, playerB, divideLine);

	}

	public void processActions(Set<Integer> actionsTobeMade, PlayerPanel player){
		actionsTobeMade.forEach(i -> processActions(i, player));
	}
	public void processActions(int actionNum, PlayerPanel player){
		switch(actionNum){
			case 0:
//				oppositePlayer(player).
				physicalAttack(oppositePlayer(player), 100);
				break;
			case 1:
				increaseAC(player, 50);
				break;
			case 2:
				magicAttack(oppositePlayer(player), 80);
				break;
			case 3:
				increaseHP(player, 50);
				break;
			case 4:break;
			case 5:
			case 10:
				physicalAttack(oppositePlayer(player), 200);//因为目前这种写法之前做过100了
				break;
			case 6:
			case 11:
				increaseAC(player, 70);
				break;
			case 7:
			case 12:
				magicAttack(oppositePlayer(player), 160);
				break;
			case 8:
			case 13:
				increaseHP(player, 70);
				break;
			case 9:break;
//			case 10:break;
//			case 11:break;
//			case 12:break;
//			case 13:break;
//			case 14:break;

		}
	}

	private PlayerPanel oppositePlayer(PlayerPanel player){
		//TODO 这个方法好像不行
		System.out.println(player == playerA);
		return (player == playerA)? playerB : playerA;
	}

	private void physicalAttack(PlayerPanel player, int harm){
		int currentHP = player.getHP();
		int currentAC = player.getAC();

		System.out.println("physicalAttack");
		if(currentAC > harm){
			player.setAC(currentAC - harm);
			System.out.println("AC -" + harm);
		} else if(currentAC > 0){
			player.setAC(0);
			harm = harm - currentAC;
			System.out.print("AC =0; ");
		} else if(currentHP > harm){
			player.setHP(currentHP - harm);
			System.out.println("HP -" + harm);
		} else {
			player.setHP(0);//TODO加一个BooleanProperty判断是否胜利
			System.out.println("kill");
		}
		System.out.println();
	}
	private void increaseAC(PlayerPanel player, int increment){
		int currentHP = player.getHP();
		int currentAC = player.getAC();

		System.out.println("AC");
		if(currentAC + increment > 500){
			player.setAC(500);
			System.out.println("AC =500");
		} else {
			player.setAC(currentAC + increment);
			System.out.println("AC +" + increment);
		}
		System.out.println();
	}
	private void magicAttack(PlayerPanel player, int harm){
		int currentHP = player.getHP();

		System.out.println("magicAttack");
		if(currentHP > harm){
			player.setHP(currentHP - harm);
			System.out.println("HP -" + harm);
		} else {
			player.setHP(0);
			System.out.println("magic kill");
		}
		System.out.println();
	}
	private void increaseHP(PlayerPanel player, int increment){
		int currentHP = player.getHP();

		System.out.println("HP");
		if(currentHP + increment > 1000){
			player.setHP(1000);
			System.out.println("HP =1000");
		} else {
			player.setHP(currentHP + increment);
			System.out.println("HP +" + increment);
		}
		System.out.println();
	}
}
