package myxiaoxiaole;

import javafx.animation.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

public class GridPanel extends Pane {
    private static final int DROP_DURATION = 1400 /3;
    private static final int CHANGE_DURATION = 800 /2;
    private static final int CHANGEBACK_DURATION = 400 /2;
    private static final int VANISH_DURATION = 200;
    private static final int B_POSITION= -50;//A_POSITION = GRIDPANEL_HEIGHT

    //这几个基本数据的传递是个问题，现在暂时打算多写几个全局变量
    //暂时public，改完再说
    //另外我个人倾向于后面的循环中都用常数而不是数组的length
    public static final int CELL_SIZE = /*50*/60;
    public static final int CELL_X = 7;
    public static final int CELL_Y = /*12*/10;
    public static final int GRIDPANEL_WIDTH = CELL_SIZE * CELL_X;
    public static final int GRIDPANEL_HEIGHT = CELL_SIZE * CELL_Y;

    private GamePanel gamePanel;

    private final Jewel[][] grid;
    static Jewel selected = null;
    public volatile boolean inAnimation = false;
    private ArrayList<ArrayList<Jewel>> jewelsToBeDeleted = new ArrayList<>();
    public boolean AsTurn = true;
    //唉。。

    public GridPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
//        this.setWidth(GRIDPANEL_WIDTH);
//        this.setHeight(GRIDPANEL_HEIGHT);
        this.setLayoutY((GamePanel.HEIGHT - GRIDPANEL_HEIGHT) * 0.5);
        this.setLayoutX((GamePanel.WIDTH - GRIDPANEL_WIDTH) * 0.5);
        this.grid = new Jewel[CELL_X][CELL_Y];

		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				do {
//					grid[i][j] = new Jewel(i * CELL_SIZE, j * CELL_SIZE);
                    grid[i][j] = createJewel(i, j);
				} while (checkDeleteWhileInitializing(i, j));

				this.getChildren().addAll(/*createCellAndAddToArray(i, j),*/ grid[i][j]);
			}
		}

//        setAllCellsToFront();//这个需要每次inAnimation调整的时候都要做一次

    }

    private Cell createCellAndAddToArray(int i, int j) {
        //这个代码感觉有个地方不是很明白。i和j这样岂不是相当于就一直保留了？
        Cell cell = new Cell(i, j);

        /**当前状态下是不可以误点的，点了两个不能交换的主动权就交给了对方*/
//        cell.setOnMouseClicked(e -> {
//            if((!AsTurn && j < CELL_Y/2) || (AsTurn && j >= CELL_Y/2)) {
//                if (!inAnimation) {
//                    if (selected == null) {
//                        selected = grid[i][j];
//                    } else {
//                        if (isAdjacent(grid[i][j], selected)) {
//                            startAction(grid[i][j], selected);
//                        }
//                        selected = null;
//                    }
//                }
//            }
//        });

        return cell;
    }

    private Jewel createJewel(int x, int y){//i，j是格子里的位置, x, y是起始的位置。好像只要传j就可以了。。
        Jewel jewel = new Jewel(x*CELL_SIZE, y*CELL_SIZE);
        jewel.setOnMouseClicked(e->{
            if((!AsTurn && jewel.getGridY() < CELL_Y/2) || (AsTurn && jewel.getGridY() >= CELL_Y/2)) {//移动了以后grid[][]变了，但是这里设定的能不能改没有变，点的时候肯定是静止的，所以可以用layoutX/Y来获得i,j
                System.out.print(jewel.getLayoutX()/CELL_SIZE + ":");
                System.out.println(jewel.getLayoutY()/CELL_SIZE);
                System.out.println(selected);
                if (!inAnimation) {
                    if (selected == null) {
                        selected = jewel;
                    } else {
                        if (isAdjacent(jewel, selected)) {
                            startAction(jewel, selected);
                        }
                        selected = null;
                    }
                }
            }
        });

        return jewel;
    }

//    private void setAllCellsToFront() {
        //下面一个不行的原因大概是修改了 getChildren 的 list 吧
//        this.getChildren().stream().filter(i -> i instanceof Cell).map(i -> (Cell) i).collect(Collectors.toList()).forEach(c -> c.toFront());
//		this.getChildren().stream().filter(i -> i instanceof Cell).map(i -> (Cell)i).forEach(c->c.toFront());

//    }

    private void endOneLoop(){
        inAnimation = false;
//        setAllCellsToFront();
        AsTurn = !AsTurn;
        gamePanel.clock.set(GamePanel.ROUND_TIME);
        gamePanel.continueTimer();
    }

    public boolean isAdjacent(Jewel jewel1, Jewel jewel2) {
        return Math.abs(jewel1.getLayoutX() - jewel2.getLayoutX()) == CELL_SIZE && jewel1.getLayoutY() == jewel2.getLayoutY()
                || Math.abs(jewel1.getLayoutY() - jewel2.getLayoutY()) == CELL_SIZE && jewel1.getLayoutX() == jewel2.getLayoutX();
    }

    public void startAction(Jewel jewel1, Jewel jewel2) {//将interchange的动画重新赋给另一个，并且setOnAction。避免nonActionSwap的尴尬
        //TODO 是否要volatile？synchronized?
        inAnimation = true;
        gamePanel.pauseTimer();

        ParallelTransition pt1 = interchange(jewel1, jewel2, CHANGE_DURATION);
        pt1.play();
        pt1.setOnFinished(e1 -> {
            if (!checkDeleteAndMark()) {
//                ParallelTransition p = interchange(jewel1, jewel2, CHANGEBACK_DURATION);
//                p.setOnFinished(e -> {
                    endOneLoop();
//                });
//                p.play();
            } else {
                delete(DROP_DURATION);
            }

        });
    }

    private ParallelTransition interchange(Jewel jewel1, Jewel jewel2, int delay) {

        ParallelTransition pt = new ParallelTransition();
        pt.setCycleCount(1);

        //保存原位置
        int x1 = (int) (jewel1.getLayoutX() / CELL_SIZE);
        int y1 = (int) (jewel1.getLayoutY() / CELL_SIZE);
        int x2 = (int) (jewel2.getLayoutX() / CELL_SIZE);
        int y2 = (int) (jewel2.getLayoutY() / CELL_SIZE);

        //逻辑换
        Jewel tempJewel = jewel1;
        grid[x1][y1] = grid[x2][y2];
        grid[x2][y2] = tempJewel;

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
//		}
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

        //下面两个准备放在一起，写代码的时候假设这两种情况不会重叠
        boolean haveFound = false;
        ArrayList<Jewel> tempList = new ArrayList<>();
        Jewel j1,j2,j3,j4;

        for (int i = 0; i < CELL_X; i++) {
            for (int j = 0; j < CELL_Y; j++) {
                //检查横向
                if(Jewel.isSameColor(grid[i][j], j1 = getGrid(i-1, j), j2 = getGrid(i-2, j), j3 = getGrid(i-3, j), j4 = getGrid(i-4, j))){
                    /*Jewel.setToBeDelete(true, j1, j2, j3, j4);*/

                    grid[i][j].setStatus(1);
                    grid[i][j].selectImage(5 * grid[i][j].getStatus() + grid[i][j].getColor());
                    grid[i][j].setToBeDelete(false);
                    System.out.println();/**理由同下*/

                    haveFound = true;
                } else if(Jewel.isSameColor(grid[i][j], j1 = getGrid(i-1, j), j2 =getGrid(i-2, j), j3 = getGrid(i-3, j))){
                    Jewel.setToBeDelete(true, grid[i][j], j1, j2, j3);
                    jewelsToBeDeleted.add(new ArrayList<>(Arrays.asList(grid[i][j], j1, j2, j3)));

                    jewelsToBeDeleted.remove(new ArrayList<>(Arrays.asList(j1, j2, j3)));

                    System.out.println();/**只是嫌重复烦*/

                    haveFound = true;
                }  else if(Jewel.isSameColor(grid[i][j], j1 = getGrid(i-1, j), j2 = getGrid(i-2, j))){
                    Jewel.setToBeDelete(true, grid[i][j], j1, j2);
                    jewelsToBeDeleted.add(new ArrayList<>(Arrays.asList(grid[i][j], j1, j2)));


                    haveFound = true;

                    System.out.println();/**唉*/
                }
                //检查竖向
                if(Jewel.isSameColor(grid[i][j], j1 = getGrid(i, j-1), j2 = getGrid(i, j-2), j3 = getGrid(i, j-3), j4 = getGrid(i, j-4))){
                    /*Jewel.setToBeDelete(true, j1, j2, j3, j4);*/
//                    jewelsToBeDeleted.add(new ArrayList<>(Arrays.asList(j1, j2, j3, j4)));
                    //如果是5个的话那么之前的四个一定已经添加过了，所以正好不用再添加


                    grid[i][j].setStatus(1);
                    grid[i][j].selectImage(5 * grid[i][j].getStatus() + grid[i][j].getColor());
                    grid[i][j].setToBeDelete(false);//这句其实也不要了

                    haveFound = true;
                } else if(Jewel.isSameColor(grid[i][j], j1 = getGrid(i, j-1), j2 = getGrid(i, j-2), j3 = getGrid(i, j-3))){
                    Jewel.setToBeDelete(true, grid[i][j], j1, j2, j3);
                    jewelsToBeDeleted.add(new ArrayList<>(Arrays.asList(grid[i][j], j1, j2, j3)));
                    //希望找快速创建的方法。这个也可以类型推测？这个方法还是有点重复的感觉啊，asList得到是List不能赋给ArrayList所以只能new一个。

                    jewelsToBeDeleted.remove(new ArrayList<>(Arrays.asList(j1, j2, j3)));
                    //注意还需要把之前三个添加的那个删掉


                    haveFound = true;
                } else if(Jewel.isSameColor(grid[i][j], j1 = getGrid(i, j-1), j2 = getGrid(i, j-2))){/*有可能两个都有就是以九宫格右下角为顶点的这种情况，所以上面的if块才是这幅样子*/
                    Jewel.setToBeDelete(true, grid[i][j], j1, j2);
                    jewelsToBeDeleted.add(new ArrayList<>(Arrays.asList(grid[i][j], j1, j2)));


                    haveFound = true;
                }
            }
        }
        showJewelsToBeDeleted();

        // TL型，只是去找到节点
        //TODO 有了jewelsToBeDeleted，其实可以不用再做遍历了。一会儿再改。
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

                    //把包含这个节点的所有（只能是2吧）List整合成一个一个List，并且删去节点这个元素。
                    //离散里头这一种集合运算好像有个专门的名字？
                    ArrayList<Jewel> toBeMergedList1 = null;
                    Jewel tempJewel = grid[i][j];
                    for(ArrayList<Jewel> temp: jewelsToBeDeleted.stream().collect(Collectors.toList())) {
                        /*IDE终于错报一个了，这个stream不能少，否则concurrentModificationException，但是这里似乎用literator比较好*/
                        if (temp.contains(tempJewel) && toBeMergedList1 == null) {
                            toBeMergedList1 = temp;
                            jewelsToBeDeleted.remove(temp);
                        } else if (temp.contains(tempJewel)) {
                            jewelsToBeDeleted.remove(temp);
                            temp.addAll(toBeMergedList1);
                            temp.remove(tempJewel);
                            temp.remove(tempJewel);//注意这是List，要remove两次，好像这个也是用set比较好呀
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
        //TODO 图片也直接和setStatus一起换？

        showJewelsToBeDeleted();

        return haveFound;
    }

    //利用Optional的思路
    private Jewel getGrid(int i, int j){
        if(i < 0 || i >= CELL_X || j < 0 || j >= CELL_Y){
            return new Jewel();
        } else {
            return grid[i][j];
        }
    }

    private void delete(int delay) {
        ParallelTransition dropPt = new ParallelTransition();

        //这里还要用递归是因为如果要每个都往下（上）移，那么有i和j会比较方便。跟删除无关。
        //所以这次遍历只负责制作向下（上）移动的动画
        if(AsTurn) {
            for (int i = 0; i < grid.length; i++) {
                int count = -1;
                for (int j = 0; j < grid[0].length; j++) {
                    if (grid[i][j].isToBeDelete()) {

                        for (int temp = 0; temp < j; temp++) {
                            grid[i][j - temp] = grid[i][j - temp - 1];
                        }
//                        grid[i][0] = new Jewel(i * CELL_SIZE, count * CELL_SIZE);
                        grid[i][0] = createJewel(i, count);
                        this.getChildren().add(grid[i][0]);

                        count--;
                    }
                }
            }
        } else {
            //这是从下往上的动画
            for (int i = 0; i < grid.length; i++) {
                int count = 0;//因为是左上角
                for (int j = grid[0].length - 1; j >= 0; j--) {
                    if (grid[i][j].isToBeDelete()) {

                        for (int temp = 0; temp < grid[0].length - 1 - j; temp++) {
                            grid[i][j + temp] = grid[i][j + temp + 1];
                        }
//                        grid[i][grid[0].length - 1] = new Jewel(i * CELL_SIZE, CELL_Y * CELL_SIZE + count * CELL_SIZE);
                        grid[i][grid[0].length - 1] = createJewel(i, CELL_Y + count);
                        this.getChildren().add(grid[i][grid[0].length - 1]);
                        count++;
                    }
                }
            }
        }

        //这个时候grid已经控制不了要消去的那些Jewel了
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                //TODO 这个可以考虑修改，现在是不加判断全部做动画？
//				try {
                dropPt.getChildren().add(move(i, j, delay));
//				} catch (java.lang.IllegalArgumentException e) {
//				}
            }
        }



        //这里负责做消失（攻击）的动画
        //可以直接通过一个list的个数来决定是不是要发动两次攻击
        SequentialTransition attackSt = new SequentialTransition();
        ParallelTransition preparePt = new ParallelTransition();
//        attackSt.getChildren().add(preparePt);
        for (ArrayList<Jewel> list:
                jewelsToBeDeleted) {
            preparePt.getChildren().add(prepareAttack(list));
            attackSt.getChildren().add(attack(list));
        }

        attackSt.setOnFinished(e -> {
            gamePanel.getChildren().removeAll(gamePanel.getChildren().stream().filter(l -> l instanceof attackLabel).collect(Collectors.toList()));/**和Attack方法里的代码呼应，都很蠢。。*/
            if (checkDeleteAndMark()) {
                delete(delay);// 这算调递归吧
            } else {
                endOneLoop();
            }
        });

        //这种方法和用sequence一样吗？但是我改成sequence好像不行。。应该可以啊。。
        preparePt.setOnFinished(e->{
            attackSt.play();
        });

        preparePt.play();

        dropPt.play();

        jewelsToBeDeleted.clear();
    }



    private Animation prepareAttack(ArrayList<Jewel> jewels){
        ParallelTransition pt = new ParallelTransition();
        Jewel randomJewel = jewels.get(0);
        randomJewel.toFront();

        if(randomJewel.getColor() == 4){
            for (Jewel jewel : jewels) {
                pt.getChildren().add(vanish(jewel));
            }
        } else {
            double originX = randomJewel.getLayoutX();//先这样保险着写，能不能放进去原理还有点不清楚
            double originY = randomJewel.getLayoutY();
            //可能地址有问题
            for (Jewel jewel:
                    jewels) {
                Timeline tl = new Timeline(
                        new KeyFrame(Duration.millis(100), new KeyValue(jewel.scaleXProperty(), 1.4, Interpolator.EASE_OUT)),
                        new KeyFrame(Duration.millis(100), new KeyValue(jewel.scaleYProperty(), 1.4, Interpolator.EASE_OUT)),
                        new KeyFrame(Duration.millis(200), new KeyValue(jewel.layoutXProperty(), originX + 30, Interpolator.EASE_IN)),
                        new KeyFrame(Duration.millis(200), new KeyValue(jewel.layoutYProperty(), originY + 20, Interpolator.EASE_IN))
                );

                if(jewel != randomJewel){
                    tl.setOnFinished(e->{
                        GridPanel.this.getChildren().remove(jewel);
                    });
                }

                pt.getChildren().add(tl);
            }
        }

        return pt;
    }

    private Animation attack(ArrayList<Jewel> jewels){
        Jewel randomJewel = jewels.get(0);
        jewels.stream().filter(j -> j.getStatus() == 1).findAny().ifPresent(j->randomJewel.setStatus(1));
        int size = jewels.size();

        if(randomJewel.getColor() == 5) return new SequentialTransition();//对应上面的vanish()

        Timeline lblTL;
        if(randomJewel.getStatus() == 1){
            lblTL = gamePanel.processActions(AsTurn, (randomJewel.getColor() + 10));
        } else if(size > 3){
            lblTL = gamePanel.processActions(AsTurn, (randomJewel.getColor() + 5));
        } else {
            lblTL = gamePanel.processActions(AsTurn, (randomJewel.getColor()));
        }

        Animation moveAnimation = new Timeline();
        if(randomJewel.getColor() == 0){
            moveAnimation = physicalAttack(randomJewel);
        } else if (randomJewel.getColor() == 2) {
            moveAnimation = magicAttack(randomJewel);
        } else if ((randomJewel.getColor() == 1) ||(randomJewel.getColor() == 3)) {
            moveAnimation = supplyAnimation(randomJewel);
        } else {
            moveAnimation = new Timeline(//这个应该如果是运行的时候才去得到property的话，这么写就大概没什么问题
                    new KeyFrame(Duration.millis(400), new KeyValue(randomJewel.layoutXProperty(), GRIDPANEL_WIDTH / 2)),
                    new KeyFrame(Duration.millis(400), new KeyValue(randomJewel.layoutYProperty(), (AsTurn && randomJewel.isAttackJewel() || (!AsTurn && !randomJewel.isAttackJewel()) ? B_POSITION : GRIDPANEL_HEIGHT), GameInterpolators.yInterpolatro()))
            );
        }

        SequentialTransition st = new SequentialTransition();
        st.getChildren().addAll(moveAnimation, lblTL);
        st.setOnFinished(e->{
            GridPanel.this.getChildren().remove(randomJewel);
            gamePanel.getChildren().stream().filter(l -> l instanceof attackLabel).forEach(l->l.setOpacity(0));/**代码这样写有点不雅，但是我拿不到lblPlayer的引用啊，最后做完一轮一起删*/
        });

        return st;
    }

    private Animation physicalAttack(Jewel jewel){
        ParallelTransition pa1 = new ParallelTransition();
//        System.out.println("fitHeight: "+jewel.getFitHeight());

        //这条line非常诡异。。似乎坐标是相对于jewel本身来计算的
        PathTransition pt = new PathTransition(Duration.millis(400), new Line(/*jewel.getLayoutX() + CELL_SIZE/2,*/CELL_SIZE/2, /*jewel.getLayoutY() + CELL_SIZE/2,*/CELL_SIZE/2, GRIDPANEL_WIDTH/2 - jewel.getLayoutX(), (AsTurn ? B_POSITION : GRIDPANEL_HEIGHT) - jewel.getLayoutY()), jewel);
        pt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pt.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                return t*t*t*t;
            }
        });
        Timeline recTl = new Timeline(
                new KeyFrame(Duration.millis(400), new KeyValue(jewel.scaleYProperty(), 0))
        );

        pa1.getChildren().add(pt);
        pa1.getChildren().add(recTl);


        return pa1;
    }

    private Animation magicAttack(Jewel jewel){
        //同上
        QuadCurve curve = new QuadCurve(CELL_SIZE/2, CELL_SIZE/2, (AsTurn ? GRIDPANEL_WIDTH : 0 * GRIDPANEL_WIDTH) - jewel.getLayoutX(), (AsTurn ? 0.3 * GRIDPANEL_HEIGHT: 0.7 * GRIDPANEL_HEIGHT) - jewel.getLayoutY(), GRIDPANEL_WIDTH/2 - jewel.getLayoutX(), (AsTurn ? B_POSITION : GRIDPANEL_HEIGHT) - jewel.getLayoutY());
        PathTransition pt = new PathTransition(Duration.millis(600), curve, jewel);

        pt.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                return Math.pow(t,1.65);
            }
        });

        return pt;
    }

    private Animation supplyAnimation(Jewel jewel){
        Timeline tl = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(jewel.scaleXProperty(), 2)),
             new KeyFrame(Duration.millis(300), new KeyValue(jewel.scaleYProperty(), 2)),
             new KeyFrame(Duration.millis(300), new KeyValue(jewel.opacityProperty(), 0))
        );

        return tl;
    }


    //纯粹测试用的
    public void showJewelsToBeDeleted(){
        for (ArrayList<Jewel> list:
                jewelsToBeDeleted) {
            for (Jewel jewel:
                    list) {
                System.out.print((int)jewel.getLayoutX()/CELL_SIZE + "," + (int)jewel.getLayoutY()/CELL_SIZE + "   ");
            }
            System.out.println();
        }
    }

    public Animation vanish(Jewel jewel){
        Timeline tl = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(jewel.opacityProperty(), 0))
        );
        tl.setOnFinished(e->GridPanel.this.getChildren().remove(jewel));
        return tl;
    }
}

