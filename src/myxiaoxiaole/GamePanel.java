package myxiaoxiaole;

import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class GamePanel extends Pane {
	public static final int WIDTH = 900 / 2;
	public static final int HEIGHT = 1600 / 2;
	public static final int OFFSET_HEIGHT = (GamePanel.HEIGHT - GridPanel.GRIDPANEL_HEIGHT) / 2;
	public static final String ROUND_TIME = "15";

	private final BooleanProperty layerOnProperty = new SimpleBooleanProperty(false);
	private final BooleanProperty EndGameProperty = new SimpleBooleanProperty(false);
	public final StringProperty clock = new SimpleStringProperty(ROUND_TIME);

	private Timeline timer;

	private final GridPanel gridPanel;
	public PlayerPanel playerA, playerB;

	private Button btQuit, btContinue;
	private Label lblTime;

	private int level;// 0表示网络，1表示双人，2表示单机

	public void setBackground(Image image) {
		BackgroundImage myBI = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		this.setBackground(new Background(myBI));
	};

	public GamePanel(int level) {
		this.level = level;
		this.setPrefWidth(WIDTH);
		this.setPrefHeight(HEIGHT);
		BackgroundImage myBI = new BackgroundImage(Images.background1, BackgroundRepeat.REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		this.setBackground(new Background(myBI));
		gridPanel = new GridPanel(this);
		gridPanel.toFront();
		;
		playerA = new PlayerPanel(1, this);
		playerA.setLayoutX(100);
		playerA.setLayoutY(HEIGHT - OFFSET_HEIGHT);
		// playerA.setScaleY(-1);// 翻转，playerA是下面的那个
		// playerA.setScaleX(-1);

		// 怎么翻转再说。。
		playerB = new PlayerPanel(0, this);
		playerB.setLayoutX(0);
		playerB.setLayoutY(0);
		// playerB.lblHPPoint.setScaleY(-1);
		// playerB.lblACPoint.setScaleY(-1);
		// playerB.lblHPPoint.setScaleX(-1);
		// playerB.lblACPoint.setScaleX(-1);
		// 为了方便看先去掉，下面的标签同理。如果用rotate?

		// Line divideLine = new Line();
		// divideLine.setStartX(0);
		// divideLine.endXProperty().bind(this.widthProperty());
		// divideLine.setStartY(HEIGHT / 2);
		// divideLine.setEndY(HEIGHT / 2);
		// divideLine.setStroke(Color.ORANGE);
		// divideLine.setStrokeWidth(6);

		Button bMenu = new Button("menu");
		//TODO 这里改了 ！！！！！！！！！！！！！！！！！！！！！！！！！！！
		bMenu.setLayoutX(10);
		bMenu.setLayoutY(10);
		bMenu.getStyleClass().add("start-bt5");
		bMenu.setPrefHeight(20);
		bMenu.setPrefWidth(80);
		//TODO 我是分割线 ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
		bMenu.setOnAction(e -> {
			layerOnProperty.set(true);
		});

		this.getChildren().addAll(playerA, playerB, bMenu, gridPanel); // ,divideLine);

		Pane pausePanel = new Pane();
		//TODO这里改了 ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
		pausePanel.setPrefWidth(WIDTH);
		pausePanel.setPrefHeight(HEIGHT);
		pausePanel.setBackground(new Background(new BackgroundImage(Images.pauseBackground, BackgroundRepeat.REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
		btContinue = new Button("Continue");
		btQuit = new Button("Quit");
		btQuit.getStyleClass().add("start-bt3");
//		btQuit.setLayoutY(GamePanel.HEIGHT / 2);
		btQuit.setLayoutY((HEIGHT)/2+50-btQuit.getPrefHeight());
		btQuit.setLayoutX(180);
//		btContinue.setLayoutY(GamePanel.HEIGHT / 2);
		btContinue.getStyleClass().add("start-bt3");
		btContinue.setLayoutY((HEIGHT)/2-50-btContinue.getPrefHeight());
		btContinue.setLayoutX(150);
		//TODO 我是分割线 ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
		pausePanel.getChildren().addAll(btContinue, btQuit);

		btContinue.setOnAction(e -> {
			layerOnProperty.set(false);
		});
		btQuit.setOnAction(e -> {
			StackPane root = (StackPane) this.getParent();
			root.getChildren().remove(this);
		});

		layerOnProperty.addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				this.getChildren().remove(pausePanel);
				gridPanel.inAnimation = false;
				gridPanel.currentAnimation.play();
				timer.play();
			} else {
				//TODO 这里改了 ！！！！！！！！！！！！！！！！！！！！！！！！！！！！
				this.getChildren().remove(bMenu);
				this.getChildren().add(pausePanel);
				this.getChildren().add(bMenu);
				//TODO 我是分割线 ！！！！！！！！！！！！！！！！！！！！！！！！！！！
				gridPanel.inAnimation = true;
				gridPanel.currentAnimation.stop();
				timer.pause();
			}
		});
		
		EndGameProperty.addListener((observable, oldValue, newValue) -> {
			if(newValue){
				System.out.println("kill");
				if (gridPanel.AsTurn) {
					EndScene endScene = new EndScene(Images.downWinner);
					StackPane root = (StackPane) this.getParent();
					root.getChildren().remove(this);
					root.getChildren().add(endScene);
				} else {
					EndScene endScene = new EndScene(Images.upWinner);
					StackPane root = (StackPane) this.getParent();
					root.getChildren().remove(this);
					root.getChildren().add(endScene);
				}
			}
		});

		lblTime = new Label();
		// lblTime.getStyleClass();
		lblTime.textProperty().bind(clock);
		lblTime.setLayoutX(400);
		lblTime.setLayoutY(20);
		this.getChildren().add(lblTime);
		timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
			if (clock.get().equals("0")) {
				clock.setValue(ROUND_TIME);
				gridPanel.AsTurn = !gridPanel.AsTurn;
				gridPanel.selected = null;// 不然的话我可以先点一个不靠边界的然后对方就点不了了

				if (!gridPanel.AsTurn) {
					gridPanel.AIAction();
				}

			} else {
				clock.set(String.valueOf(Integer.parseInt(clock.getValue()) - 1));
			}
		}));
		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();
	}

	public void pauseTimer() {
		timer.pause();
	}

	public void continueTimer() {
		timer.playFromStart();
	}

	public void setLayerOn() {
		layerOnProperty.set(true);
	}

	public void setEndGameOn(){
		EndGameProperty.set(true);
	}
	
	public int getLevel() {
		return level;
	}

	public Timeline processActions(boolean isA, int actionNum) {
		PlayerPanel player = isA ? playerA : playerB;
		switch (actionNum) {
		case 0:
			return physicalAttack(!isA, 100);
		case 1:
			return increaseAC(isA, 50);
		case 2:
			return magicAttack(!isA, 80);
		case 3:
			return increaseHP(isA, 50);
		case 4:
			return new Timeline();
		case 5:
			return physicalAttack(!isA, 200);// 因为目前这种写法之前做过100了
		case 6:
			return increaseAC(isA, 70);
		case 7:
			return magicAttack(!isA, 160);
		case 8:
			return increaseHP(isA, 70);
		case 9:
			return new Timeline();
		case 10:
		 	return physicalAttack(!isA, 300);
		case 11:
			return increaseAC(isA, 100);
		case 12:
			return magicAttack(!isA, 220);
		 case 13:
			 return increaseHP(isA, 100);
		 case 14:break;

		}
		//改成这样试一试吧。。
		return new Timeline();
	}

	private PlayerPanel oppositePlayer(PlayerPanel player) {
		System.out.println(player == playerA);
		return (player == playerA) ? playerB : playerA;
	}

	private Timeline physicalAttack(boolean isA, int harm) {
		Timeline returnTL;
		PlayerPanel player = isA ? playerA : playerB;
		int currentHP = player.getHP();
		int currentAC = player.getAC();

		System.out.println("physicalAttack");
		if (currentAC > harm) {

			System.out.println("AC -" + harm);
			returnTL = animateLbl(isA, "AC -" + harm);

			final int harmDuplicate = harm;
			returnTL.setOnFinished(e -> {
				player.setAC(currentAC - harmDuplicate);
			});
			return returnTL;
		}

		if (currentAC > 0) {
			// player.setAC(0);
			harm = harm - currentAC;
		}
		if (currentHP > harm) {
			// player.setHP(currentHP - harm);
			System.out.println("AC = 0" + (harm > 0 ? "  HP -" + harm : ""));
			returnTL = animateLbl(isA, "AC = 0" + (harm > 0 ? "  HP -" + harm : ""));

			final int harmDuplicate = harm;
			returnTL.setOnFinished(e -> {
				player.setAC(0);// 反正到这一步了AC肯定要变成,或者本来就是0
				player.setHP(currentHP - harmDuplicate);
			});
			return returnTL;
		} else {
			// player.setHP(0);
			System.out.println("kill");
			returnTL = animateLbl(isA, "kill");
			returnTL.setOnFinished(e -> {
				player.setHP(0);
			});
			return returnTL;
		}
		// System.out.println();
	}

	private Timeline increaseAC(boolean isA, int increment) {
		Timeline returnTL;
		PlayerPanel player = isA ? playerA : playerB;
		int currentHP = player.getHP();
		int currentAC = player.getAC();

		System.out.println("AC");
		if (currentAC + increment > 500) {
			// player.setAC(500);
			System.out.println("AC = 500");
			returnTL = animateLbl(isA, "AC = 500");
			returnTL.setOnFinished(e -> {
				player.setAC(500);
			});

		} else {
			// player.setAC(currentAC + increment);
			System.out.println("AC +" + increment);
			returnTL = animateLbl(isA, "AC +" + increment);
			returnTL.setOnFinished(e -> {
				player.setAC(currentAC + increment);
			});
		}
		System.out.println();
		return returnTL;
	}

	private Timeline magicAttack(boolean isA, int harm) {
		Timeline returnTL;
		PlayerPanel player = isA ? playerA : playerB;

		int currentHP = player.getHP();

		System.out.println("magicalAttack");
		if (currentHP > harm) {

			System.out.println("HP -" + harm);
			returnTL = animateLbl(isA, "HP -" + harm);
			returnTL.setOnFinished(e -> player.setHP(currentHP - harm));
		} else {
			
			System.out.println("magical kill");
			returnTL = animateLbl(isA, "magical kill");
			returnTL.setOnFinished(e -> player.setHP(0));
//			Timer timer = new Timer();
//			timer.schedule(new TimerTask() {
//				public void run() {
//					if (isA) {
//						EndScene endScene = new EndScene(Images.downWinner);
//						GamePanel.this.getChildren().add(endScene);
//					} else {
//						EndScene endScene = new EndScene(Images.upWinner);
//						GamePanel.this.getChildren().add(endScene);
//					}
//				}
//			}, 3500);
		}
		System.out.println();

		return returnTL;
	}

	private Timeline increaseHP(boolean isA, int increment) {
		Timeline returnTL;
		PlayerPanel player = isA ? playerA : playerB;

		int currentHP = player.getHP();

		System.out.println("HP");
		if (currentHP + increment > 1000) {

			System.out.println("HP =1000");
			returnTL = animateLbl(isA, "HP =1000");
			returnTL.setOnFinished(e -> player.setHP(1000));
		} else {

			System.out.println("HP +" + increment);
			returnTL = animateLbl(isA, "HP +" + increment);
			returnTL.setOnFinished(e -> player.setHP(currentHP + increment));
		}
		System.out.println();
		return returnTL;
	}

	// Pane pane_for_lblPlayer = new Pane();
	private Timeline animateLbl(boolean isA, String text) {
		SequentialTransition returnST = new SequentialTransition();
		attackLabel lblPlayer = lblPlayer(isA, text);
		GamePanel.this.getChildren().add(lblPlayer);
		// lblPlayer.getChildren().add(Pane pane_for_lblPlayer);
		// TODO 伤害的标签的动画
		Timeline lblTL = new Timeline(
				new KeyFrame(Duration.millis(200),
						new KeyValue(lblPlayer.scaleXProperty(), 1.5, Interpolator.EASE_BOTH)),
				new KeyFrame(Duration.millis(200),
						new KeyValue(lblPlayer.scaleYProperty(), 1.5, Interpolator.EASE_BOTH)),
				new KeyFrame(Duration.millis(350),
						new KeyValue(lblPlayer.opacityProperty(), 1, Interpolator.EASE_OUT)));
		return lblTL;
	}

	// TODO 标签在这里
	public attackLabel lblPlayer(boolean isA, String text) {
		attackLabel lblPlayer = new attackLabel(text);
		// lblPlayer.getStyleClass().add("game-lblPoints");
		lblPlayer.setFont(Font.font("Cambria", 16));
		lblPlayer.setStyle("-fx-font-weight: black");
		lblPlayer.setStyle("-fx-text-fill: #e10000");
		// lblPlayer.setEffect(new DropShadow(2,Color.WHITE));
		lblPlayer.setOpacity(0);
		// TODO 这里改坐标
		if (isA) {
			lblPlayer.setLayoutX(WIDTH - GridPanel.GRIDPANEL_WIDTH * 0.2 - 3);
			lblPlayer.setLayoutY(GridPanel.GRIDPANEL_HEIGHT + GamePanel.OFFSET_HEIGHT + 30);
		} else {
			lblPlayer.setScaleX(-1);
			lblPlayer.setScaleY(-1);
			lblPlayer.setLayoutX(GridPanel.GRIDPANEL_WIDTH * 0.05 + 5);
			lblPlayer.setLayoutY(GamePanel.OFFSET_HEIGHT - 50);
		}
		return lblPlayer;
	}
}

// 只是为了区分Label，这样就可以使用instanceof把PlayerLabel全部移除
class attackLabel extends Label {
	attackLabel(String text) {
		super(text);
	}
}
