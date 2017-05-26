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
    public static Image background1 = new Image("/./images/final/background1.jpg");
    public static Image background2 = new Image("/./images/final/background2.jpg");
    public static Image entering = new Image("/./images/final/entering.jpg");
    public static Image hero1 = new Image("/./images/final/hero1.png");
    public static Image hero1attack = new Image("/./images/final/hero1attack.gif");
    public static Image hero1hurted = new Image("/./images/final/hero1hurted.gif");
    static {
        for (int i = 0; i < 5; i++) {
//            jewelNormalImages[i] = new Image("/./images/" + i + ".gif");
            jewel45Images[i] = new Image("/./images/" + (i+5) + ".gif");
            jewelTLImages[i] = new Image("/./images/" + (i+10) + ".png");
        }

        jewelNormalImages[0] = new Image("/./images/final/attack.png");
        jewelNormalImages[1] = new Image("/./images/final/shield.png");
        jewelNormalImages[2] = new Image("/./images/final/mage.png");
        jewelNormalImages[3] = new Image("/./images/final/HP.png");
        jewelNormalImages[4] = new Image("/./images/final/nothing.png");

    }
}