package myxiaoxiaole;

import javafx.animation.Interpolator;

public class GameInterpolators {
    public static Interpolator yInterpolatro() {
        return new Interpolator() {
            @Override
            protected double curve(double t) {
                return Math.pow(t, 0.6);

            }
        };
    }
    public static Interpolator freeFall = new Interpolator() {
            @Override
            protected double curve(double t) {
                if(t <= 0.8){
                    return (t*t*(1.5625));
                } else if (t <= 0.9){
                    return 1-Math.pow(t-0.8, 0.5)*Math.pow(0.1, 0.5);
                } else {
                    return 0.9 + (t-0.9)*(t-0.9)*10;
                }
//                return t;
            }
        };

}