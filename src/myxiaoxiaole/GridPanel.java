package myxiaoxiaole;

import javafx.animation.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Lighting;
//  import javafx.scene.control.Tooltip;可以增加属性描述
//Tooltip.install(jewel.getParent(), new Tooltip("circle"));加这样的话就可以
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

public class GridPanel extends Pane {
    private static final int DROP_DURATION = 1400 / 3;
    private static final int CHANGE_DURATION = 800 / 4;
    private static final int CHANGEBACK_DURATION = 400 / 2;
    private static final int VANISH_DURATION = 200;

    // 暂时public，可能以后采用常量接口
    public static final int CELL_SIZE = /* 50 */60;
    public static final int CELL_X = 7;
    public static final int CELL_Y = /* 12 */10;//这个要改的话可能也需要改其他的panel的坐标
    public static final int GRIDPANEL_WIDTH = CELL_SIZE * CELL_X;
    public static final int GRIDPANEL_HEIGHT = CELL_SIZE * CELL_Y;

    private static final double B_POSITION_Y = -50;
    private static final double B_POSITION_X = GRIDPANEL_WIDTH - GRIDPANEL_WIDTH/1.8 - CELL_SIZE - 30;
    private static final double A_POSITION_Y = GRIDPANEL_HEIGHT;
    private static final double A_POSITION_X = GRIDPANEL_WIDTH/1.8 + 30;

    private GamePanel gamePanel;

    private final Jewel[][] grid;
    public Jewel selected = null;
    public volatile boolean inAnimation = false;
    private ArrayList<ArrayList<Jewel>> jewelsToBeDeleted = new ArrayList<>();
    public boolean AsTurn = true;
    public Animation currentAnimation;//这个用来控制动画暂停，只跟踪和逻辑相关的动画，所以drop动画和人物动画就不管了


    public GridPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        // this.setWidth(GRIDPANEL_WIDTH);
        // this.setHeight(GRIDPANEL_HEIGHT);
        this.setLayoutY((GamePanel.HEIGHT - GRIDPANEL_HEIGHT) * 0.5);
        this.setLayoutX((GamePanel.WIDTH - GRIDPANEL_WIDTH) * 0.5);
        this.grid = new Jewel[CELL_X][CELL_Y];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                do {
                    grid[i][j] = createJewel(i, j);
                } while (checkDeleteWhileInitializing(i, j));

                this.getChildren().addAll(grid[i][j]);
            }
        }


    }

    static Timer timer = new Timer();

    private Jewel createJewel(int x, int y) {
        Jewel jewel = new Jewel(x * CELL_SIZE, y * CELL_SIZE);
        jewel.toFront();
        jewel.setOnMouseClicked(e -> {
            if ((!AsTurn && jewel.getGridY() < CELL_Y / 2) || (AsTurn && jewel.getGridY() >= CELL_Y / 2)) {// 移动了以后grid[][]变了，但是这里设定的能不能改没有变，点的时候肯定是静止的，所以可以用layoutX/Y来获得i,j
                System.out.print(jewel.getLayoutX() / CELL_SIZE + ":");
                System.out.println(jewel.getLayoutY() / CELL_SIZE);
                System.out.println(selected);
                if (!inAnimation) {
                    jewel.setScaleX(0.915);
                    jewel.setScaleY(0.915);
                    jewel.setEffect(new Lighting());
                    if (selected == null) { // 未选中第一个
//                        jewel.setScaleX(0.915);
//                        jewel.setScaleY(0.915);
                        jewel.setEffect(new Lighting());
                        selected = jewel;
                    } else { // 已选中第一个
                        if (isAdjacent(jewel, selected)) {
                            System.out.println("A second one!");
//                            jewel.setScaleX(0.915);
//                            jewel.setScaleY(0.915);
                            startAction(jewel, selected);
                            timer.schedule(new TimerTask() {
                                public void run() {
                                    jewel.setScaleX(1);
                                    jewel.setScaleY(1);
                                    jewel.setEffect(new DropShadow(0, null));
                                    this.cancel();
                                }
                            }, 180);
                            selected.setScaleX(1);
                            selected.setScaleY(1);
                            selected.setEffect(new DropShadow(0, null));
                        } else {
                            System.out.println("Not neighbor");
                            jewel.setScaleX(1);
                            jewel.setScaleY(1);
                            jewel.setEffect(new DropShadow(0, null));
                            selected.setScaleX(1);
                            selected.setScaleY(1);
                            selected.setEffect(new DropShadow(0, null));
                        }
                        selected = null;
                    }
                }
            }
        });

        return jewel;
    }

    public void endOneLoop() {
        inAnimation = false;
        currentAnimation = null;

        selected = null;

        AsTurn = !AsTurn;
        if (AsTurn) {
            gamePanel.setBackground(Images.background1);
        } else {
            gamePanel.setBackground(Images.background2);
        }
        gamePanel.clock.set(GamePanel.ROUND_TIME);
        gamePanel.continueTimer();
        if ((!AsTurn) && gamePanel.getLevel() == 2) {
            AIAction();
        }
    }

    public boolean isAdjacent(Jewel jewel1, Jewel jewel2) {
        return Math.abs(jewel1.getLayoutX() - jewel2.getLayoutX()) == CELL_SIZE
                && jewel1.getLayoutY() == jewel2.getLayoutY()
                || Math.abs(jewel1.getLayoutY() - jewel2.getLayoutY()) == CELL_SIZE
                && jewel1.getLayoutX() == jewel2.getLayoutX();
    }

    public void startAction(Jewel jewel1, Jewel jewel2) {
        //是否要volatile？synchronized?
        inAnimation = true;
        gamePanel.pauseTimer();

        ParallelTransition pt1 = interchange(jewel1, jewel2, CHANGE_DURATION);
        pt1.setOnFinished(e1 -> {//将interchange的动画重新赋给另一个，并且setOnAction。避免nonActionSwap的尴尬
            if (!checkDeleteAndMark()) {
                ParallelTransition p = interchange(jewel1, jewel2, CHANGEBACK_DURATION);
                p.setOnFinished(e -> {
                    endOneLoop();
                });
                currentAnimation = p;
                p.play();
                AsTurn = !AsTurn;
            } else {
                delete(DROP_DURATION);
            }
        });

        currentAnimation = pt1;
        pt1.play();
    }

    private ParallelTransition interchange(Jewel jewel1, Jewel jewel2, int delay) {

        ParallelTransition pt = new ParallelTransition();
        pt.setCycleCount(1);

        // 保存原位置
        int x1 = (int) (jewel1.getLayoutX() / CELL_SIZE);
        int y1 = (int) (jewel1.getLayoutY() / CELL_SIZE);
        int x2 = (int) (jewel2.getLayoutX() / CELL_SIZE);
        int y2 = (int) (jewel2.getLayoutY() / CELL_SIZE);

        // 逻辑换
        grid[x1][y1] = grid[x2][y2];
        grid[x2][y2] = jewel1;

        pt.getChildren().add(move(x1, y1, delay));
        pt.getChildren().add(move(x2, y2, delay));
        return pt;
    }

    /**
     * 将grid控制的x，y从原来的位置移动到现在的位置（现在的位置是(x，y)）
     *
     * @return
     */
    private Timeline move(int x, int y, int duration) {// 最核心部分，将实际位置与网格分隔开来

        KeyValue kvX = new KeyValue(grid[x][y].layoutXProperty(), x * CELL_SIZE, Interpolator.EASE_OUT);
        KeyValue kvY = new KeyValue(grid[x][y].layoutYProperty(), y * CELL_SIZE, GameInterpolators.freeFall);
        KeyFrame kf = new KeyFrame(Duration.millis(duration), kvX, kvY);
        Timeline tl = new Timeline(kf);
        return tl;
        // }
    }

    private boolean checkDeleteWhileInitializing(int x, int y) {
        if (x < 2 && y < 2) {
            return false;
        } else if (x < 2) {
            return grid[x][y].isSameColor(grid[x][y - 1]) && grid[x][y].isSameColor(grid[x][y - 2]);
        } else if (y < 2) {
            return grid[x][y].isSameColor(grid[x - 1][y]) && grid[x][y].isSameColor(grid[x - 2][y]);
        } else if (x >= 2 && y >= 2) {
            return (grid[x][y].isSameColor(grid[x][y - 1]) && grid[x][y].isSameColor(grid[x][y - 2]))
                    || (grid[x][y].isSameColor(grid[x - 1][y]) && grid[x][y].isSameColor(grid[x - 2][y]));
        } else {
            return false;
        }
    }

    private boolean checkDeleteAndMark() {

        // 下面两个准备放在一起，写代码的时候假设这两种情况不会重叠
        boolean haveFound = false;
        Jewel j1, j2, j3, j4;

        for (int i = 0; i < CELL_X; i++) {
            for (int j = 0; j < CELL_Y; j++) {
                // 检查横向
                if (Jewel.isSameColor(grid[i][j], j1 = getGrid(i - 1, j), j2 = getGrid(i - 2, j),
                        j3 = getGrid(i - 3, j), j4 = getGrid(i - 4, j))) {
                    /* Jewel.setToBeDelete(true, j1, j2, j3, j4); */

                    grid[i][j].setStatus(1);
                    grid[i][j].selectImage(5 * grid[i][j].getStatus() + grid[i][j].getColor());
                    grid[i][j].setToBeDelete(false);
                    System.out.println();/*理由同下*/

                    haveFound = true;
                } else if (Jewel.isSameColor(grid[i][j], j1 = getGrid(i - 1, j), j2 = getGrid(i - 2, j),
                        j3 = getGrid(i - 3, j))) {
                    Jewel.setToBeDelete(true, grid[i][j], j1, j2, j3);
                    jewelsToBeDeleted.add(new ArrayList<>(Arrays.asList(grid[i][j], j1, j2, j3)));

                    jewelsToBeDeleted.remove(new ArrayList<>(Arrays.asList(j1, j2, j3)));

                    System.out.println();/*只是嫌重复烦*/

                    haveFound = true;
                } else if (Jewel.isSameColor(grid[i][j], j1 = getGrid(i - 1, j), j2 = getGrid(i - 2, j))) {
                    Jewel.setToBeDelete(true, grid[i][j], j1, j2);
                    jewelsToBeDeleted.add(new ArrayList<>(Arrays.asList(grid[i][j], j1, j2)));

                    haveFound = true;

                    System.out.println();/*唉*/
                }
                // 检查竖向
                if (Jewel.isSameColor(grid[i][j], j1 = getGrid(i, j - 1), j2 = getGrid(i, j - 2),
                        j3 = getGrid(i, j - 3), j4 = getGrid(i, j - 4))) {
                    /* Jewel.setToBeDelete(true, j1, j2, j3, j4); */
                    // jewelsToBeDeleted.add(new ArrayList<>(Arrays.asList(j1,
                    // j2, j3, j4)));
                    // 如果是5个的话那么之前的四个一定已经添加过了，所以正好不用再添加

                    grid[i][j].setStatus(1);
                    grid[i][j].selectImage(5 * grid[i][j].getStatus() + grid[i][j].getColor());
                    grid[i][j].setToBeDelete(false);// 这句其实也不要了

                    haveFound = true;
                } else if (Jewel.isSameColor(grid[i][j], j1 = getGrid(i, j - 1), j2 = getGrid(i, j - 2),
                        j3 = getGrid(i, j - 3))) {
                    Jewel.setToBeDelete(true, grid[i][j], j1, j2, j3);
                    jewelsToBeDeleted.add(new ArrayList<>(Arrays.asList(grid[i][j], j1, j2, j3)));

                    jewelsToBeDeleted.remove(new ArrayList<>(Arrays.asList(j1, j2, j3)));//需要把之前三个添加的那个删掉

                    haveFound = true;
                } else if (Jewel.isSameColor(grid[i][j], j1 = getGrid(i, j - 1), j2 = getGrid(i,
                        j - 2))) {/* 有可能两个都有就是以九宫格右下角为顶点的这种情况，所以上面的if块才是这幅样子 */
                    Jewel.setToBeDelete(true, grid[i][j], j1, j2);
                    jewelsToBeDeleted.add(new ArrayList<>(Arrays.asList(grid[i][j], j1, j2)));

                    haveFound = true;
                }
            }
        }
        showJewelsToBeDeleted();

        // TL型，只是去找到节点
        // 有了jewelsToBeDeleted，其实可以不用再做遍历了。一会儿再改。
        for (int i = 0; i < CELL_X; i++) {
            for (int j = 0; j < CELL_Y; j++) {
                if ((Jewel.isSameColor(grid[i][j], getGrid(i + 1, j), getGrid(i, j - 1))
                        && Jewel.isToBeDelete(grid[i][j], getGrid(i + 1, j), getGrid(i, j - 1)))

                        || (Jewel.isSameColor(grid[i][j], getGrid(i, j - 1), getGrid(i - 1, j))
                        && Jewel.isToBeDelete(grid[i][j], getGrid(i, j - 1), getGrid(i - 1, j)))

                        || (Jewel.isSameColor(grid[i][j], getGrid(i - 1, j), getGrid(i, j + 1))
                        && Jewel.isToBeDelete(grid[i][j], getGrid(i - 1, j), getGrid(i, j + 1)))

                        || (Jewel.isSameColor(grid[i][j], getGrid(i, j + 1), getGrid(i + 1, j))
                        && Jewel.isToBeDelete(grid[i][j], getGrid(i, j + 1), getGrid(i + 1, j)))) {

                    // 把包含这个节点的所有（只能是2吧）List整合成一个一个List，并且删去节点这个元素。离散里头这一种集合运算好像有个专门的名字？
                    ArrayList<Jewel> toBeMergedList1 = null;
                    Jewel tempJewel = grid[i][j];
                    for (ArrayList<Jewel> temp : jewelsToBeDeleted.stream().collect(Collectors.toList())) {
                        /*
						 * IDE终于错报一个了，这个stream不能少，
						 * 否则concurrentModificationException，但是这里似乎用literator比较好
						 */
                        if (temp.contains(tempJewel) && toBeMergedList1 == null) {
                            toBeMergedList1 = temp;
//                            jewelsToBeDeleted.remove(temp);//移到下面
                        } else if (temp.contains(tempJewel)) {
                            jewelsToBeDeleted.remove(temp);
                            jewelsToBeDeleted.remove(toBeMergedList1);
                            temp.addAll(toBeMergedList1);
                            temp.remove(tempJewel);
                            temp.remove(tempJewel);// 注意这是List，要remove两次，好像这个也是用set比较好呀
                            jewelsToBeDeleted.add(temp);
                        }
                    }

                    grid[i][j].setStatus(1);
                    grid[i][j].selectImage(5 * grid[i][j].getStatus() + grid[i][j].getColor());
                    grid[i][j].setToBeDelete(false);

                    System.out.println("find TL type, location: " + i + "," + j);
                }
            }
        }

        showJewelsToBeDeleted();

        return haveFound;
    }

    // 利用Optional的思路
    private Jewel getGrid(int i, int j) {
        if (i < 0 || i >= CELL_X || j < 0 || j >= CELL_Y) {
            return new Jewel();
        } else {
            return grid[i][j];
        }
    }

    private void delete(int delay) {
        ParallelTransition dropPt = new ParallelTransition();

        // 这里还要用递归是因为如果要每个都往下（上）移，那么有i和j会比较方便。跟删除无关。
        // 所以这次遍历只负责制作向下（上）移动的动画
        if (AsTurn) {
            for (int i = 0; i < grid.length; i++) {
                int count = -1;
                for (int j = 0; j < grid[0].length; j++) {
                    if (grid[i][j].isToBeDelete()) {

                        for (int temp = 0; temp < j; temp++) {
                            grid[i][j - temp] = grid[i][j - temp - 1];
                        }
                        grid[i][0] = createJewel(i, count);
                        this.getChildren().add(grid[i][0]);

                        count--;
                    }
                }
            }
        } else {
            // 这是从下往上的动画
            for (int i = 0; i < grid.length; i++) {
                int count = 0;// 因为是左上角
                for (int j = grid[0].length - 1; j >= 0; j--) {
                    if (grid[i][j].isToBeDelete()) {

                        for (int temp = 0; temp < grid[0].length - 1 - j; temp++) {
                            grid[i][j + temp] = grid[i][j + temp + 1];
                        }
                        grid[i][grid[0].length - 1] = createJewel(i, CELL_Y + count);
                        this.getChildren().add(grid[i][grid[0].length - 1]);
                        count++;
                    }
                }
            }
        }

        // 这个时候grid已经控制不了要消去的那些Jewel了
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                //TODO 这个可以考虑修改，现在是不加判断全部做动画？
                // try {
                dropPt.getChildren().add(move(i, j, delay));
                // } catch (java.lang.IllegalArgumentException e) {
                // }
            }
        }

        // 这里负责做消失（攻击）的动画
        // 可以直接通过一个list的个数来决定是不是要发动两次攻击
        SequentialTransition attackSt = new SequentialTransition();
        ParallelTransition preparePt = new ParallelTransition();
        // attackSt.getChildren().add(preparePt);
        for (ArrayList<Jewel> list : jewelsToBeDeleted) {
            preparePt.getChildren().add(prepareAttack(list));
            attackSt.getChildren().add(attack(list));
        }

        attackSt.setOnFinished(e -> {
            gamePanel.getChildren().removeAll(gamePanel.getChildren().stream().filter(l -> l instanceof attackLabel)
                    .collect(Collectors.toList()));/*和Attack方法里的代码呼应，都很蠢*/
            if (checkDeleteAndMark()) {
                delete(delay);// 这算调递归吧
            } else {
                endOneLoop();
            }
        });

        // 这种方法和用sequenceTransition一样吧。。
        preparePt.setOnFinished(e -> {
            currentAnimation = attackSt;
            attackSt.play();
        });

        currentAnimation = preparePt;
        preparePt.play();

        dropPt.play();

        jewelsToBeDeleted.clear();
    }

    private Animation prepareAttack(ArrayList<Jewel> jewels) {
        ParallelTransition pt = new ParallelTransition();
        Jewel randomJewel = jewels.get(0);
        randomJewel.toFront();

        if (randomJewel.getColor() == 4) {
            for (Jewel jewel : jewels) {
                pt.getChildren().add(vanish(jewel));
            }
        } else {
            double originX = randomJewel.getLayoutX();// 先这样保险着写，能不能放进去原理还有点不清楚
            double originY = randomJewel.getLayoutY();
            // 可能地址有问题
            for (Jewel jewel : jewels) {
                Timeline tl = new Timeline(
                        new KeyFrame(Duration.millis(100),
                                new KeyValue(jewel.scaleXProperty(), 1.4, Interpolator.EASE_OUT)),
                        new KeyFrame(Duration.millis(100),
                                new KeyValue(jewel.scaleYProperty(), 1.4, Interpolator.EASE_OUT)),
                        new KeyFrame(Duration.millis(200),
                                new KeyValue(jewel.layoutXProperty(), originX + 30, Interpolator.EASE_IN)),
                        new KeyFrame(Duration.millis(200),
                                new KeyValue(jewel.layoutYProperty(), originY + 20, Interpolator.EASE_IN)));

                if (jewel != randomJewel) {
                    tl.setOnFinished(e -> {
                        GridPanel.this.getChildren().remove(jewel);
                    });
                }

                pt.getChildren().add(tl);
            }
        }

        return pt;
    }

    private Animation attack(ArrayList<Jewel> jewels) {
        Jewel randomJewel = jewels.get(0);
        jewels.stream().filter(j -> j.getStatus() == 1).findAny().ifPresent(j -> randomJewel.setStatus(1));
        int size = jewels.size();

        if (randomJewel.getColor() == 5)
            return new SequentialTransition();// 对应上面的vanish()

        Timeline lblTL;
        if (randomJewel.getStatus() == 1) {
            lblTL = gamePanel.processActions(AsTurn, (randomJewel.getColor() + 10));
        } else if (size > 3) {
            lblTL = gamePanel.processActions(AsTurn, (randomJewel.getColor() + 5));
        } else {
            lblTL = gamePanel.processActions(AsTurn, (randomJewel.getColor()));
        }

        Animation moveAnimation = new Timeline();
        if (randomJewel.getColor() == 0) {
            moveAnimation = physicalAttack(randomJewel);
        } else if (randomJewel.getColor() == 2) {
            moveAnimation = magicAttack(randomJewel);
        } else if ((randomJewel.getColor() == 1) || (randomJewel.getColor() == 3)) {
            moveAnimation = supplyAnimation(randomJewel);
        } else {
            moveAnimation = new Timeline(// 这个应该如果是运行的时候才去得到property的话，这么写就大概没什么问题
                    new KeyFrame(Duration.millis(400),
                            new KeyValue(randomJewel.layoutXProperty(), GRIDPANEL_WIDTH / 2)),
                    new KeyFrame(Duration.millis(400),
                            new KeyValue(randomJewel.layoutYProperty(),
                                    (AsTurn && randomJewel.isAttackJewel() || (!AsTurn && !randomJewel.isAttackJewel())
                                            ? B_POSITION_Y : GRIDPANEL_HEIGHT),
                                    GameInterpolators.yInterpolatro())));
        }

        SequentialTransition st = new SequentialTransition();
        st.getChildren().addAll(moveAnimation, lblTL);
        st.setOnFinished(e -> {
            GridPanel.this.getChildren().remove(randomJewel);
            gamePanel.getChildren().stream().filter(l -> l instanceof attackLabel)
                    .forEach(l -> l.setOpacity(0));/*代码这样写有点不雅，但是我拿不到lblPlayer的引用啊，最后做完一轮一起删 */
        });

        return st;
    }

    private Animation physicalAttack(Jewel jewel) {
        ParallelTransition pa1 = new ParallelTransition();
        // System.out.println("fitHeight: "+jewel.getFitHeight());

        // 这条line非常诡异。。似乎坐标是相对于jewel本身来计算的
        PathTransition pt = new PathTransition(Duration.millis(400),
                new Line(CELL_SIZE / 2,
						CELL_SIZE / 2,
                        (AsTurn ? B_POSITION_X : A_POSITION_X) - jewel.getLayoutX(),
                        (AsTurn ? B_POSITION_Y : A_POSITION_Y) - jewel.getLayoutY()),
                jewel);
        pt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pt.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                return t * t * t * t;
            }
        });
        Timeline recTl = new Timeline(new KeyFrame(Duration.millis(400), new KeyValue(jewel.scaleYProperty(), 0)));

        pa1.getChildren().add(pt);
        pa1.getChildren().add(recTl);

        return pa1;
    }

    private Animation magicAttack(Jewel jewel) {
//		jewel.toFront();
        // 同上
        QuadCurve curve = new QuadCurve(CELL_SIZE / 2, CELL_SIZE / 2,
                (AsTurn ? GRIDPANEL_WIDTH : 0 * GRIDPANEL_WIDTH) - jewel.getLayoutX(),
                (AsTurn ? 0.3 * GRIDPANEL_HEIGHT : 0.7 * GRIDPANEL_HEIGHT) - jewel.getLayoutY(),
                (AsTurn ? B_POSITION_X : A_POSITION_X) - jewel.getLayoutX(),
                (AsTurn ? B_POSITION_Y : A_POSITION_Y) - jewel.getLayoutY());
        PathTransition pt = new PathTransition(Duration.millis(600), curve, jewel);

        pt.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                return Math.pow(t, 10);
            }
        });

        return pt;
    }

    private Animation supplyAnimation(Jewel jewel) {
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(jewel.scaleXProperty(), 2)),
                new KeyFrame(Duration.millis(300), new KeyValue(jewel.scaleYProperty(), 2)),
                new KeyFrame(Duration.millis(300), new KeyValue(jewel.opacityProperty(), 0)));

        return tl;
    }

    public void AIAction() {
        ArrayList<List<Jewel>> jewelsCanSwap = new ArrayList<>();

        //TODO AI 有时会动不能消去的
        for (int i = 0; i < CELL_X; i++) {
            for (int j = 0; j < CELL_Y / 2 - 1; j++) {// 这里先不考虑边界上的，-1
                // 下面的一些代码可以合并
                // 第一种情况
                if (Jewel.isSameColor(grid[i][j], getGrid(i - 1, j - 1), getGrid(i - 2, j - 1))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i][j - 1]));
                }
                if (Jewel.isSameColor(grid[i][j], getGrid(i + 1, j - 1), getGrid(i + 2, j - 1))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i][j - 1]));
                }
                if (Jewel.isSameColor(grid[i][j], getGrid(i - 1, j + 1), getGrid(i - 2, j + 1))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i][j + 1]));
                }
                if (Jewel.isSameColor(grid[i][j], getGrid(i + 1, j + 1), getGrid(i + 2, j + 1))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i][j + 1]));
                }

                if (Jewel.isSameColor(grid[i][j], getGrid(i + 1, j - 1), getGrid(i + 1, j - 2))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i + 1][j]));
                }
                if (Jewel.isSameColor(grid[i][j], getGrid(i - 1, j - 1), getGrid(i - 1, j - 2))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i - 1][j]));
                }
                if (Jewel.isSameColor(grid[i][j], getGrid(i - 1, j + 1), getGrid(i - 1, j + 2))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i - 1][j]));
                }
                if (Jewel.isSameColor(grid[i][j], getGrid(i + 1, j + 1), getGrid(i + 1, j + 2))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i + 1][j]));
                }

                // 第二种情况
                if (Jewel.isSameColor(grid[i][j], getGrid(i - 2, j), getGrid(i - 3, j))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i - 1][j]));
                }
                if (Jewel.isSameColor(grid[i][j], getGrid(i + 2, j), getGrid(i + 3, j))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i + 1][j]));
                }

                if (Jewel.isSameColor(grid[i][j], getGrid(i, j - 2), getGrid(i, j - 3))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i][j - 1]));
                }
                if (Jewel.isSameColor(grid[i][j], getGrid(i, j + 2), getGrid(i, j + 3))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i][j + 1]));
                }

                // 第三种情况
                if (Jewel.isSameColor(grid[i][j], getGrid(i - 1, j - 1), getGrid(i + 1, j - 1))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i][j - 1]));
                }
                if (Jewel.isSameColor(grid[i][j], getGrid(i + 1, j - 1), getGrid(i + 1, j + 1))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i][j + 1]));
                }

                if (Jewel.isSameColor(grid[i][j], getGrid(i + 1, j - 1), getGrid(i + 1, j + 1))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i + 1][j]));
                }
                if (Jewel.isSameColor(grid[i][j], getGrid(i - 1, j - 1), getGrid(i - 1, j + 1))) {
                    jewelsCanSwap.add(Arrays.asList(grid[i][j], grid[i - 1][j]));
                }

            }
        }

        if (jewelsCanSwap.isEmpty()) {
            int i = new Random().nextInt(CELL_X - 2);
            int j = new Random().nextInt(CELL_Y / 2 - 2);
            int i1 = 0, j1 = 0;
            int random = new Random().nextInt(4);
            switch (random) {
                case 0:
                    i1 = i + 1;
                    j1 = j;
                    break;
                case 1:
                    i1 = i;
                    j1 = j - 1;
                    break;
                case 2:
                    i1 = i - 1;
                    j1 = j;
                    break;
                case 3:
                    i1 = i;
                    j1 = j + 1;
                    break;
            }

            startAction(grid[i][j], grid[i1][j1]);
        } else {
            Collections.shuffle(jewelsCanSwap);
            List<Jewel> jewelsToBeSwap = jewelsCanSwap.get(0);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("我也不知道怎么办啊😰");
            }

            startAction(jewelsToBeSwap.get(0), jewelsToBeSwap.get(1));
        }

    }

    // 纯粹测试用的
    public void showJewelsToBeDeleted() {
        for (ArrayList<Jewel> list : jewelsToBeDeleted) {
            for (Jewel jewel : list) {
                System.out.print(
                        (int) jewel.getLayoutX() / CELL_SIZE + "," + (int) jewel.getLayoutY() / CELL_SIZE + "   ");
            }
            System.out.println();
        }
    }

    public Animation vanish(Jewel jewel) {
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(jewel.opacityProperty(), 0)));
        tl.setOnFinished(e -> GridPanel.this.getChildren().remove(jewel));
        return tl;
    }
}
