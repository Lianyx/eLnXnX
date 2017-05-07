package myxiaoxiaole;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;


public class Jewel extends ImageView{

	private final int color;
	static int size;
	private static final int TYPE = 5;
	private int status = 0;
	//0表示正常，1表示5或者TL留下来的特殊方块

	private boolean toBeDelete = false;

	public void setLocation(double x, double y) {
		setLayoutX(x);
		setLayoutY(y);
	}

	//这两个现在用不到
	public int getGridX(){
		return (int)(getX()/size);
	}
	public int getGridY(){
		return (int)(getY()/size);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Jewel(double x, double y) {
		size = (int) (GridPanel.CELL_SIZE);
//		color = (int) (Math.random() * TYPE + 1);
		color = (int) (new Random().nextDouble() * (TYPE));

		selectImage(color);
		setLocation(x, y);



//		setOnMouseMoved(e -> {
//			this.setFill(Color.GRAY);
//		});
//		setOnMouseExited(e -> {
//			this.setFill(colors[color]);
//		});

		setOnMouseClicked(e -> {
			System.out.println("Jewel");
		});
	}
	/**为了Optional特意写的，可能不好*/
	public Jewel (){
		color = -5;
		setStatus(-5);
	}

	public boolean isSameColor(Jewel jewel) {
		if (this.color == jewel.color) {
			return true;
		} else {
			return false;
		}
	}

	//这个相对路径不是很好找啊，还可以尝试用class.getResource
	//TODO 希望用background来省去toFront的麻烦，但是没有用
	public void selectImage(int i){
		if(i < 5){
			this.setImage(Images.jewelNormalImages[i]);
		} else if(i < 10){
			this.setImage(Images.jewel45Images[i - 5]);
		} else if(i < 15){
			this.setImage(Images.jewelTLImages[i - 10]);
		}
		this.setFitWidth(size);
		this.setPreserveRatio(true);
		this.setSmooth(true);
//		this.setCache(true);
	}

	public boolean isToBeDelete() {
		return toBeDelete;
	}

	public void setToBeDelete(boolean toBeDelete) {
		this.toBeDelete = toBeDelete;
	}
	public int getColor() {
		return this.color;
	}

	public int actionNum(){//权宜之计(现在status只有0和1)
		return this.getStatus() * 10 + this.getColor();
	}


//	public boolean isAdjacent(Jewel jewel2) {
//		return Math.abs(this.getX() - jewel2.getX()) == GridPanel.gridSize && this.getY() == jewel2.getY()
//				|| Math.abs(this.getY() - jewel2.getY()) == GridPanel.gridSize && this.getX() == jewel2.getX();
//	}

	public static boolean isSameColor(Jewel...jewels){
		Jewel firstJewel = jewels[0];
		for (Jewel jewel:
			 jewels) {
			if( !firstJewel.isSameColor(jewel)){
				return false;
			}
		}
		return true;
	}

	public static boolean isToBeDelete(Jewel...jewels){
		for (Jewel jewel:
			 jewels) {
			if( !jewel.isToBeDelete()){
				return false;
			}
		}
		return true;
	}

	public static void setToBeDelete(Boolean isTobeDelete, Jewel...jewels){
		for (Jewel jewel:
			 jewels) {
			jewel.setToBeDelete(isTobeDelete);
		}
	}
}
