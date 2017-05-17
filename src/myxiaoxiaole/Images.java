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
//            jewelNormalImages[i] = new Image("/./images/" + i + ".gif");
            jewel45Images[i] = new Image("/./images/" + (i+5) + ".png");
            jewelTLImages[i] = new Image("/./images/" + (i+10) + ".png");
        }

        jewelNormalImages[0] = new Image("/./images/final/attack.jpg");
        jewelNormalImages[1] = new Image("/./images/final/shleid.jpg");
        jewelNormalImages[2] = new Image("/./images/final/mage.jpg");
        jewelNormalImages[3] = new Image("/./images/final/HP.jpg");
        jewelNormalImages[4] = new Image("/./images/4.png");

    }
}