package myxiaoxiaole;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.Set;


public class GamePanel extends Pane {
	public static final int WIDTH = 900/2;
	public static final int HEIGHT = 1600/2;
	public static final int OFFSET_HEIGHT = (GamePanel.HEIGHT - GridPanel.GRIDPANEL_HEIGHT)/2;

	private final GridPanel gridPanel;

	private IntegerProperty gameAttackPoint = new SimpleIntegerProperty();
	private IntegerProperty gameSupplyPoint = new SimpleIntegerProperty();
	private Timeline animateAEffect;
	private Timeline animateBEffect;

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
		playerB.setLayoutY(OFFSET_HEIGHT);
//		playerB.lblHPPoint.setScaleY(-1);
//		playerB.lblACPoint.setScaleY(-1);
//		playerB.lblHPPoint.setScaleX(-1);
//		playerB.lblACPoint.setScaleX(-1);
//		为了方便看先去掉，下面的标签同理。如果用rotate?

		Line divideLine = new Line();
		divideLine.setStartX(0);
		divideLine.endXProperty().bind(this.widthProperty());
		divideLine.setStartY(HEIGHT/2);
		divideLine.setEndY(HEIGHT/2);
		divideLine.setStroke(Color.ORANGE);
		divideLine.setStrokeWidth(6);


		Button bMenu = new Button("menu");
		bMenu.setLayoutX(10);
		bMenu.setLayoutY(10);

//		lblPlayerA.textProperty().bind(Bindings.createStringBinding(()->
//				(gameAttackPoint.get() > 0 ? "+" + Integer.toString(gameAttackPoint.get()) : "")
//		));




//		//TODO 似乎只能每个都new一个，不能写成这样的
//		Label lblPlayerA = lblPlayer(true, "fads");
//		Label lblPlayerB = lblPlayer(false, "jihuu");
//		animateAEffect = new Timeline(
//				new KeyFrame(Duration.ZERO, new KeyValue(lblPlayerA.opacityProperty(), 1)),
//				new KeyFrame(Duration.ZERO, new KeyValue(lblPlayerA.scaleXProperty(), 1)),
//				new KeyFrame(Duration.ZERO, new KeyValue(lblPlayerA.scaleYProperty(), 1)),
//				new KeyFrame(Duration.millis(200), new KeyValue(lblPlayerA.scaleXProperty(), 1.5, Interpolator.EASE_BOTH)),
//				new KeyFrame(Duration.millis(200), new KeyValue(lblPlayerA.scaleYProperty(), 1.5, Interpolator.EASE_BOTH)),
//				new KeyFrame(Duration.millis(400), new KeyValue(lblPlayerA.opacityProperty(), 0, Interpolator.EASE_OUT))
//		);
//		animateBEffect = new Timeline(
//				new KeyFrame(Duration.ZERO, new KeyValue(lblPlayerB.opacityProperty(), 1)),
//				new KeyFrame(Duration.ZERO, new KeyValue(lblPlayerB.scaleXProperty(), 1)),
//				new KeyFrame(Duration.ZERO, new KeyValue(lblPlayerB.scaleYProperty(), 1)),
//				new KeyFrame(Duration.millis(200), new KeyValue(lblPlayerB.scaleXProperty(), 1.5, Interpolator.EASE_BOTH)),
//				new KeyFrame(Duration.millis(200), new KeyValue(lblPlayerB.scaleYProperty(), 1.5, Interpolator.EASE_BOTH)),
//				new KeyFrame(Duration.millis(400), new KeyValue(lblPlayerB.opacityProperty(), 0, Interpolator.EASE_OUT))
//		);




		this.getChildren().addAll(bMenu, gridPanel, playerA, playerB, divideLine);

	}

//	public void processActions(Set<Integer> actionsTobeMade, PlayerPanel player){
//		actionsTobeMade.forEach(i -> processActions(i, player));
//	}
	public Timeline processActions(boolean isA, int actionNum){
		PlayerPanel player = isA? playerA: playerB;
		switch(actionNum){
			case 0:
				return physicalAttack(!isA, 100);
			case 1:
				return increaseAC(isA, 50);
			case 2:
				return magicAttack(!isA, 80);
			case 3:
				return increaseHP(isA, 50);
			case 4:return new Timeline();
			case 5:
			case 10:
				return physicalAttack(!isA, 200);//因为目前这种写法之前做过100了
			case 6:
			case 11:
				return increaseAC(isA, 70);
			case 7:
			case 12:
				return magicAttack(!isA, 160);
			case 8:
			case 13:
				return increaseHP(isA, 70);
			case 9:return new Timeline();
//			case 10:break;
//			case 11:break;
//			case 12:break;
//			case 13:break;
//			case 14:break;

		}
		return null;
	}

	private PlayerPanel oppositePlayer(PlayerPanel player){
		System.out.println(player == playerA);
		return (player == playerA)? playerB : playerA;
	}

	private Timeline physicalAttack(boolean isA, int harm){
		Timeline returnTL;
		PlayerPanel player = isA? playerA:playerB;
		int currentHP = player.getHP();
		int currentAC = player.getAC();

		System.out.println("physicalAttack");
		if(currentAC > harm){

			System.out.println("AC -" + harm);
			returnTL = animateLbl(isA, "AC -" + harm);

			final int harmDuplicate = harm;
			returnTL.setOnFinished(e->{
				player.setAC(currentAC - harmDuplicate);
			});
			return returnTL;
		}

		if(currentAC > 0){
//			player.setAC(0);
			harm = harm - currentAC;
		}
		if(currentHP > harm){
//			player.setHP(currentHP - harm);
			System.out.println("AC = 0" + (harm > 0 ? "  HP -" + harm: ""));
			returnTL = animateLbl(isA, "AC = 0" + (harm > 0 ? "  HP -" + harm: ""));

			final int harmDuplicate = harm;
			returnTL.setOnFinished(e->{
				player.setAC(0);//反正到这一步了AC肯定要变成,或者本来就是0
				player.setHP(currentHP - harmDuplicate);
			});
			return returnTL;
		} else {
//			player.setHP(0);//TODO 加一个BooleanProperty判断是否胜利
			System.out.println("kill");
			returnTL = animateLbl(isA, "kill");
			returnTL.setOnFinished(e->{
				player.setHP(0);
			});
			return returnTL;
		}
//		System.out.println();
	}
	private Timeline increaseAC(boolean isA, int increment){
		Timeline returnTL;
		PlayerPanel player = isA? playerA:playerB;
		int currentHP = player.getHP();
		int currentAC = player.getAC();

		System.out.println("AC");
		if(currentAC + increment > 500){
//			player.setAC(500);
			System.out.println("AC = 500");
			returnTL = animateLbl(isA, "AC = 500");
			returnTL.setOnFinished(e->{
				player.setAC(500);
			});

		} else {
//			player.setAC(currentAC + increment);
			System.out.println("AC +" + increment);
			returnTL = animateLbl(isA, "AC +" + increment);
			returnTL.setOnFinished(e->{
				player.setAC(currentAC + increment);
			});
		}
		System.out.println();
		return returnTL;
	}
	private Timeline magicAttack(boolean isA, int harm){
		Timeline returnTL;
		PlayerPanel player = isA? playerA:playerB;

		int currentHP = player.getHP();

		System.out.println("magicalAttack");
		if(currentHP > harm){

			System.out.println("HP -" + harm);
			returnTL = animateLbl(isA, "HP -" + harm);
			returnTL.setOnFinished(e->player.setHP(currentHP - harm));
		} else {

			System.out.println("magical kill");
			returnTL = animateLbl(isA, "magical kill");
			returnTL.setOnFinished(e->player.setHP(0));
		}
		System.out.println();

		return returnTL;
	}
	private Timeline increaseHP(boolean isA, int increment){
		Timeline returnTL;
		PlayerPanel player = isA? playerA:playerB;

		int currentHP = player.getHP();

		System.out.println("HP");
		if(currentHP + increment > 1000){

			System.out.println("HP =1000");
			returnTL = animateLbl(isA, "HP =1000");
			returnTL.setOnFinished(e->player.setHP(1000));
		} else {

			System.out.println("HP +" + increment);
			returnTL = animateLbl(isA, "HP +" + increment);
			returnTL.setOnFinished(e->player.setHP(currentHP + increment));
		}
		System.out.println();
		return returnTL;
	}

	private Timeline animateLbl(boolean isA, String text){
		SequentialTransition returnST = new SequentialTransition();
		Label lblPlayer = lblPlayer(isA, text);
		GamePanel.this.getChildren().add(lblPlayer);
		Timeline lblTL = new Timeline(
				new KeyFrame(Duration.millis(400), new KeyValue(lblPlayer.scaleXProperty(), 1.5, Interpolator.EASE_BOTH)),
				new KeyFrame(Duration.millis(400), new KeyValue(lblPlayer.scaleYProperty(), 1.5, Interpolator.EASE_BOTH)),
				new KeyFrame(Duration.millis(1000), new KeyValue(lblPlayer.opacityProperty(), 1, Interpolator.EASE_OUT))
		);
		//后面被覆盖掉了。。。
		lblTL.setOnFinished(e -> {
			GamePanel.this.getChildren().remove(lblPlayer);
		});
		return lblTL;
	}

	public Label lblPlayer(boolean isA, String text) {
		Label lblPlayer = new Label(text);
		lblPlayer.getStyleClass().add("game-lblPoints");
		lblPlayer.setOpacity(0);
		lblPlayer.setLayoutX(GridPanel.GRIDPANEL_WIDTH * 0.2);

		if (isA) {
			lblPlayer.setLayoutY(GridPanel.GRIDPANEL_HEIGHT + GamePanel.OFFSET_HEIGHT + 30);
		} else {
			lblPlayer.setLayoutY(GamePanel.OFFSET_HEIGHT - 50);
		}
		return lblPlayer;
	}
}
