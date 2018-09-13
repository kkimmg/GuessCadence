package kkimmg.guesscadence;

import java.io.Serializable;

/**
 * 電話の自転車への設置状況
 */
public class SetUpInfo implements Serializable {
    /** ハンドルバーの右側 */
    public static final int SETUP_BAR_RIGHT = 0;
    /** ハンドルバーの左側 */
    public static final int SETUP_BAR_LEFT = 1;
    /** ハンドルバーの中央 */
    public static final int SETUP_BAR_CENTER = 2;
    /** チューブトップ */
    public static final int SETUP_TUBETOP = 3;

    /**
     * 画面向き
     */
    private int Rotation;
    /**
     * 水平
     */
    private double horizontal;
    /**
     * 垂直
     */
    private double vertical;
    /**
     * ロール
     */
    private double roll;

    /**
     * ロール
     */
    public double getRoll() {
        return roll;
    }

    /**
     * ロール
     */
    public void setRoll(double roll) {
        this.roll = roll;
    }

    /**
     * 画面向き
     */
    public int getRotation() {
        return Rotation;
    }

    /**
     * 画面向き
     */
    public void setRotation(int rotation) {
        Rotation = rotation;
    }

    /**
     * 水平
     */
    public double getHorizontal() {
        return horizontal;
    }

    /**
     * 水平
     */
    public void setHorizontal(double horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * 垂直
     */
    public double getVertical() {
        return vertical;
    }

    /**
     * 垂直
     */
    public void setVertical(double vertical) {
        this.vertical = vertical;
    }

}
