package myxiaoxiaole;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Heroes extends Pane {
	ImageView a = new ImageView();

	public Heroes(Image image,int size) {
		a.setImage(image);
		a.setFitHeight(size);
		a.setPreserveRatio(true);
		a.setSmooth(true);
		this.getChildren().add(a);
	}

	public void setImage(Image image) {
		a.setImage(image);
	}

}
