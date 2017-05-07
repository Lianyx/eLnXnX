package myxiaoxiaole;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by tiberius on 2017/5/6.
 */
public class Images {
    public static Image[] jewelNormalImages = new Image[5];
    public static Image[] jewel45Images = new Image[5];
    public static Image[] jewelTLImages = new Image[5];

    static {
        for (int i = 0; i < 5; i++) {

            jewelNormalImages[i] = new Image("/./images/" + i +".png");
            jewel45Images[i] = new Image("/./images/" + (i+5) + ".png");
            jewelTLImages[i] = new Image("/./images/" + (i+10) + ".png");
        }
    }
}
