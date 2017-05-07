package myxiaoxiaole;

import javafx.animation.*;
import javafx.scene.ParallelCamera;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

public class GridPanel extends Pane {
    private static final int DROP_DURATION = 1400 /2;
    private static final int CHANGE_DURATION = 800 /2;
    private static final int CHANGEBACK_DURATION = 400 /2;
    private static final int VANISH_DURATION = 200;

    //这几个基本数据的传递是个问题，现在暂时打算多写几个全局变量
    //暂时public，改完再说
    //另外我个人倾向于后面的循环中都用常数而不是数组的length
    public static final int CELL_SIZE = 50;
    public static final int CELL_X = 6;
    public static final int CELL_Y = 12;
    public static final int GRIDPANEL_WIDTH = CELL_SIZE * CELL_X;
    public static final int GRIDPANEL_HEIGHT = CELL_SIZE * CELL_Y;

    private GamePanel gamePanel;

    private final Jewel[][] grid;
    static Jewel selected = null;
    public volatile boolean inAnimation = false;
	private Set<Integer> actionsToBeMade = new HashSet<>();/**0~4表示正常动作，5~9表示45和TL的直接动作动作，10~14表示TL动作。0~4分别表示物理攻击，增加护盾，魔法攻击，增加血量，封印*/
    private Vector<Vector<Jewel>> jewelsToBeDeleted = new Vector<>();
	private boolean AsTurn = true;

    public GridPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.setWidth(GRIDPANEL_WIDTH);
        this.setHeight(GRIDPANEL_HEIGHT);
//        this.setLayoutX(5);
        this.setLayoutY((GamePanel.HEIGHT - GRIDPANEL_HEIGHT) / 2);
        this.grid = new Jewel[CELL_X][CELL_Y];

		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				do {
					grid[i][j] = new Jewel(i * CELL_SIZE, j * CELL_SIZE);
				} while (checkDeleteWhileInitializing(i, j));

				this.getChildren().addAll(createCellAndAddToArray(i, j), grid[i][j]);
			}
		}

        setAllCellsToFront();//这个需不需要每次inAnimation调整的时候都要做一次
//		System.out.println("done");
    }

    private Cell createCellAndAddToArray(int i, int j) {
        //这个代码感觉有个地方不是很明白。i和j这样岂不是相当于就一直保留了？
        Cell cell = new Cell(i, j);

        /**当前状态下是不可以误点的，点了两个不能交换的主动权就交给了对方*/
        cell.setOnMouseClicked(e -> {
            if((!AsTurn && j < CELL_Y/2) || (AsTurn && j >= CELL_Y/2)) {
                if (!inAnimation) {
                    if (selected == null) {
                        selected = grid[i][j];
                    } else {
                        if (isAdjacent(grid[i][j], selected)) {
                            startAction(grid[i][j], selected);
                        }
                        selected = null;
                    }
                }
            }
        });

        return cell;
    }

    private void setAllCellsToFront() {
        //TODO 为什么不能不先toList?
        this.getChildren().stream().filter(i -> i instanceof Cell).map(i -> (Cell) i).collect(Collectors.toList()).forEach(c -> c.toFront());
//		this.getChildren().stream().filter(i -> i instanceof Cell).map(i -> (Cell)i).forEach(c->c.toFront());

    }

    private void endOneLoop(){
        inAnimation = false;
        setAllCellsToFront();
        AsTurn = !AsTurn;
    }

    public boolean isAdjacent(Jewel jewel1, Jewel jewel2) {
        return Math.abs(jewel1.getLayoutX() - jewel2.getLayoutX()) == CELL_SIZE && jewel1.getLayoutY() == jewel2.getLayoutY()
                || Math.abs(jewel1.getLayoutY() - jewel2.getLayoutY()) == CELL_SIZE && jewel1.getLayoutX() == jewel2.getLayoutX();
    }

    public void startAction(Jewel jewel1, Jewel jewel2) {//将interchange的动画重新赋给另一个，并且setOnAction。避免nonActionSwap的尴尬
        //TODO 是否要volatile？synchronized?
        inAnimation = true;

        ParallelTransition pt1 = interchange(jewel1, jewel2, CHANGE_DURATION);
        pt1.play();
        pt1.setOnFinished(e1 -> {
            if (!checkDeleteAndMark()) {
                ParallelTransition p = interchange(jewel1, jewel2, CHANGEBACK_DURATION);
                p.setOnFinished(e -> {
                    endOneLoop();
                });
                p.play();
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
//        double originX = grid[x][y].getX();
//        double originY = grid[x][y].getY();

        //上面那个setLocation没了，下面这个if判断也不能要了

//		grid[x][y].setLocation(x * gridSize, y * gridSize);
//		if (originX == grid[x][y].getX() && originY == grid[x][y].getY()) {
//			return /*Optional.empty()*/ null;
//		} else {
//			PathTransition p = new PathTransition();
//			p.setNode(grid[x][y]);
//			p.setDuration(Duration.millis(duration));
//			p.setPath(new Line(originX + Jewel.size / 2, originY + Jewel.size / 2, grid[x][y].getX() + Jewel.size / 2,
//					grid[x][y].getY() + Jewel.size / 2));
//			return p;

        KeyValue kvX = new KeyValue(grid[x][y].layoutXProperty(), x * CELL_SIZE, Interpolator.EASE_OUT);
        KeyValue kvY = new KeyValue(grid[x][y].layoutYProperty(), y * CELL_SIZE, Interpolator.EASE_OUT);
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
//        int temp;
//        boolean haveFound = false;
//        //第一次发现特殊消去情况的时候(45 or TL)，在那个位置setStatus，并且不设置isDelete，之后照常。
//
//        for (int i = 0; i < grid.length; i++) {//检查竖排三连
//            temp = 1;
//            for (int j = 1; j < grid[0].length; j++) {
//                if (grid[i][j].isSameColor(grid[i][j - 1])) {
//                    temp++;
//                } else if (temp >= 3) {//这时[i][j]之前连续相等
////                    grid[i][j - 1].setStatus((temp > 3) ? 1 : 0);
////                    grid[i][j - 1].setToBeDelete(temp == 3);
//
//                    while (temp > 0) {
//                        grid[i][j - temp].setToBeDelete(true);
//                        haveFound = true;
//                        temp--;
//                    }
//					temp = 1;
//                } else {
//                    temp = 1;
//                }
//            }
//            if (temp >= 3) {
////                grid[i][grid[0].length - 1].setStatus((temp > 3)? 1 : 0);
////                grid[i][grid[0].length - 1].setToBeDelete(temp == 3);
//
//                while (temp > 0) {
//                    grid[i][grid[0].length - temp].setToBeDelete(true);
//                    haveFound = true;
//                    temp--;
//                }
//            }
//
//        }
//
//        for (int j = 0; j < grid[0].length; j++) {//检查横排三连
//            temp = 1;
//            for (int i = 1; i < grid.length; i++) {
//                if (grid[i][j].isSameColor(grid[i - 1][j])) {
//                    temp++;
//                } else if (temp >= 3) {
////                    grid[i - 1][j].setStatus((temp > 3)? 1 : 0);
////                    grid[i - 1][j].setToBeDelete(temp == 3);
//
//                    while (temp > 0) {
//                        grid[i - temp][j].setToBeDelete(true);
//                        haveFound = true;
//                        temp--;
//                    }
//					temp = 1;
//                } else {
//                    temp = 1;
//                }
//            }
//            if (temp >= 3) {
////                grid[grid.length - 1][j].setStatus((temp > 3/* && grid[CELL_X - 1][j].getStatus() == 0*/) ? 1 : 0);
////                grid[grid.length - 1][j].setToBeDelete(temp == 3);
//
//                while (temp > 0) {
//                    grid[grid.length - temp][j].setToBeDelete(true);
//                    haveFound = true;
//                    temp--;
//                }
//            }
//        }
//
//
//        return haveFound;

        //下面两个准备放在一起，写代码的时候假设这两种情况不会重叠
        boolean haveFound = false;
        ArrayList<Jewel> tempList = new ArrayList<>();
        Jewel j1,j2,j3,j4;

        for (int i = 0; i < CELL_X; i++) {
            for (int j = 0; j < CELL_Y; j++) {
                //检查横向
                if(Jewel.isSameColor(grid[i][j], j1 = getGrid(i-1, j), j2 = getGrid(i-2, j), j3 = getGrid(i-3, j), j4 = getGrid(i-4, j))){
                    Jewel.setToBeDelete(true, j1, j2, j3, j4);

                    actionsToBeMade.add(5 + grid[i][j].getColor());

                    grid[i][j].setStatus(1);
                    grid[i][j].setToBeDelete(false);
                    System.out.println();/**理由同下*/

                    haveFound = true;
                } else if(Jewel.isSameColor(grid[i][j], j1 = getGrid(i-1, j), j2 =getGrid(i-2, j), j3 = getGrid(i-3, j))){
                    Jewel.setToBeDelete(true, grid[i][j], j1, j2, j3);
                    jewelsToBeDeleted.add(new Vector<>(Arrays.asList(grid[i][j], j1, j2, j3)));

                    jewelsToBeDeleted.remove(new Vector<>(Arrays.asList(j1, j2, j3)));

                    actionsToBeMade.add(5 + grid[i][j].getColor());
                    System.out.println();/**只是嫌重复烦*/

                    haveFound = true;
                }  else if(Jewel.isSameColor(grid[i][j], j1 = getGrid(i-1, j), j2 = getGrid(i-2, j))){
                    Jewel.setToBeDelete(true, grid[i][j], j1, j2);
                    jewelsToBeDeleted.add(new Vector<>(Arrays.asList(grid[i][j], j1, j2)));

                    actionsToBeMade.add(grid[i][j].getColor());

                    haveFound = true;

                    System.out.println();/**唉*/
                }
                //检查竖向
                if(Jewel.isSameColor(grid[i][j], j1 = getGrid(i, j-1), j2 = getGrid(i, j-2), j3 = getGrid(i, j-3), j4 = getGrid(i, j-4))){
                    Jewel.setToBeDelete(true, j1, j2, j3, j4);
//                    jewelsToBeDeleted.add(new ArrayList<>(Arrays.asList(j1, j2, j3, j4)));
                    //如果是5个的话那么之前的四个一定已经添加过了，所以正好不用再添加


                    actionsToBeMade.add(5 + grid[i][j].getColor());

                    grid[i][j].setStatus(1);
                    grid[i][j].setToBeDelete(false);//这句其实也不要了

                    haveFound = true;
                } else if(Jewel.isSameColor(grid[i][j], j1 = getGrid(i, j-1), j2 = getGrid(i, j-2), j3 = getGrid(i, j-3))){
                    Jewel.setToBeDelete(true, grid[i][j], j1, j2, j3);
                    jewelsToBeDeleted.add(new Vector<>(Arrays.asList(grid[i][j], j1, j2, j3)));
                    //希望找快速创建的方法。这个也可以类型推测？这个方法还是有点重复的感觉啊，asList得到是List不能赋给ArrayList所以只能new一个。

                    jewelsToBeDeleted.remove(new Vector<>(Arrays.asList(j1, j2, j3)));
                    //注意还需要把之前三个添加的那个删掉

                    actionsToBeMade.add(5 + grid[i][j].getColor());

                    haveFound = true;
                } else if(Jewel.isSameColor(grid[i][j], j1 = getGrid(i, j-1), j2 = getGrid(i, j-2))){/*有可能两个都有就是以九宫格右下角为顶点的这种情况，所以上面的if块才是这幅样子*/
                    Jewel.setToBeDelete(true, grid[i][j], j1, j2);
                    jewelsToBeDeleted.add(new Vector<>(Arrays.asList(grid[i][j], j1, j2)));

                    actionsToBeMade.add(grid[i][j].getColor());

                    haveFound = true;
                }
            }
        }
        showJewelsToBeDeleted();

        // TL型，只是去找到节点
        //TODO 有了jewelsToBeDeleted，其实可以不用再做遍历了。一会儿再改
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
                    Vector<Jewel> toBeMergedList1 = null;//如果用lambda又要用这个蛋疼的解决方法了。。
                    Jewel tempJewel = grid[i][j];
                    for(Vector<Jewel> temp: jewelsToBeDeleted.stream().collect(Collectors.toList())) {/**IDE终于错报一个了，这个stream不能少，否则concurrentModificationException*/
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

                    actionsToBeMade.remove(grid[i][j].getColor());//因为肯定加过这个了
                    actionsToBeMade.add(5 + grid[i][j].getColor());//可能之前也加了这个，那么就不管他了。（所以"长TL"型没有特殊效果）
                    grid[i][j].setStatus(1);
                    grid[i][j].setToBeDelete(false);

                    System.out.println("find TL type, location: " + i + "," + j);
                }
            }
        }

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
//                        vanish(grid[i][j]);

                        //0~4和5~9对应的只能有一个，但是可以和10~14的重复
                        if(grid[i][j].getStatus() == 1) {//这里的if可以不要的//利用set不重复的属性
                            actionsToBeMade.add(grid[i][j].actionNum());
                        }

                        for (int temp = 0; temp < j; temp++) {
                            grid[i][j - temp] = grid[i][j - temp - 1];
                        }
                        grid[i][0] = new Jewel(i * CELL_SIZE, count * CELL_SIZE);
                        this.getChildren().add(grid[i][0]);

                        count--;
                    } else if (grid[i][j].getStatus() == 1) {
                        //TODO 变换动画，但是直接加动画可能会出现重复做的情况吧，而且getStatus完全也可以先存起来不用放到遍历里面
                        //TODO 只有两种方块了，下面这个不用写式子了
                        grid[i][j].selectImage(5 * grid[i][j].getStatus() + grid[i][j].getColor());
                    }
                }
            }
        } else {
            //这是从下往上的动画
            for (int i = 0; i < grid.length; i++) {
                int count = 0;//因为是左上角
                for (int j = grid[0].length - 1; j >= 0; j--) {
                    if (grid[i][j].isToBeDelete()) {
//                        vanish(grid[i][j]);

                        actionsToBeMade.add(grid[i][j].actionNum());

                        for (int temp = 0; temp < grid[0].length - 1 - j; temp++) {
                            grid[i][j + temp] = grid[i][j + temp + 1];
                        }
                        grid[i][grid[0].length - 1] = new Jewel(i * CELL_SIZE, CELL_Y * CELL_SIZE + count * CELL_SIZE);
                        this.getChildren().add(grid[i][grid[0].length - 1]);
//
                        count++;
                    } else if (grid[i][j].getStatus() == 1) {
                        grid[i][j].selectImage(5 * grid[i][j].getStatus() + grid[i][j].getColor());
                    }
                }
            }
        }


        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                //TODO 这个可以考虑修改，现在是不加判断全部做动画？
//				try {
                dropPt.getChildren().add(move(i, j, delay));
//				} catch (java.lang.IllegalArgumentException e) {
//				}
            }
        }


//        gamePanel.processActions(actionsToBeMade, AsTurn? gamePanel.playerA : gamePanel.playerB);

        dropPt.setOnFinished(e -> {
            if (checkDeleteAndMark()) {
                delete(delay);// 这算调递归了吧
            } else {
                endOneLoop();
            }
        });

        //这里负责做消失（攻击）的动画
        //可以直接通过一个list的个数来决定是不是要发动两次攻击
        SequentialTransition vanishSt = new SequentialTransition();
        ParallelTransition vanishPt = new ParallelTransition();
        for (Vector<Jewel> list:
                jewelsToBeDeleted) {
            for (Jewel jewel:
                    list) {
                vanishPt.getChildren().add(vanish(jewel));
                vanish(jewel).play();
            }
            vanishSt.getChildren().add(vanishPt);
            vanishPt = new ParallelTransition();
        }

        vanishSt.setOnFinished(e->{
            dropPt.play();
        });

        vanishSt.play();

//        dropPt.play();

        jewelsToBeDeleted.clear();
        actionsToBeMade.clear();
    }

    //感觉这样的话攻击的逻辑也要写在这里了，至少从这里传，actionToBeMade什么的也可以省了
    //这样等着很蠢，可以考虑先把所有可以消除的按颜色分别融合在一起，比如一个在棋盘上方的球
    private SequentialTransition vanish(Jewel jewel){
        SequentialTransition st = new SequentialTransition();
        Timeline t1 = new Timeline(
                new KeyFrame(Duration.millis(100), new KeyValue(jewel.scaleXProperty(), 1.4, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(100), new KeyValue(jewel.scaleYProperty(), 1.4, Interpolator.EASE_OUT))
        );
        Timeline t2 = new Timeline(
                new KeyFrame(Duration.millis(200), new KeyValue(jewel.layoutXProperty(), GRIDPANEL_WIDTH/2, Interpolator.EASE_IN)),
                new KeyFrame(Duration.millis(200), new KeyValue(jewel.layoutYProperty(), AsTurn? -50 : GRIDPANEL_HEIGHT, Interpolator.EASE_IN))
        );
        st.getChildren().addAll(t1, t2);
        st.setOnFinished(e->{
            this.getChildren().remove(jewel);
        });
        st.play();
        return st;
    }

    public void showJewelsToBeDeleted(){
        for (Vector<Jewel> list:
                jewelsToBeDeleted) {
            for (Jewel jewel:
                    list) {
                System.out.print((int)jewel.getLayoutX()/CELL_SIZE + "," + (int)jewel.getLayoutY()/CELL_SIZE + "   ");
            }
            System.out.println();
        }
    }
}

