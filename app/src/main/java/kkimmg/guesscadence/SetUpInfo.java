package kkimmg.guesscadence;

import android.content.res.Configuration;

import java.io.Serializable;

/**
 * 電話の自転車への設置状況
 */
public class SetUpInfo implements Serializable {
    /**
     * ハンドルバーの右側
     */
    public static final int SETUP_BAR_RIGHT = 0;
    /**
     * ハンドルバーの左側
     */
    public static final int SETUP_BAR_LEFT = 1;
    /**
     * ハンドルバーの中央
     */
    public static final int SETUP_BAR_CENTER = 2;
    /**
     * チューブトップ
     */
    public static final int SETUP_TUBETOP = 3;


    /**
     * 角度の単位（ラジアン）
     */
    public static final int UNIT_RADIAN = 0;
    /**
     * 角度の単位（パーセント）
     */
    public static final int UNIT_PERCENT = 1;
    /**
     * 角度の単位（パーミル）
     */
    public static final int UNIT_PERMIL = 2;
    /**
     * 角度の単位（度）
     */
    public static final int UNIT_DEGLEE = 3;

    /**
     * 電話のセット位置
     */
    private int setUpLocation = SETUP_BAR_RIGHT;
    /**
     * 画面向き
     */
    private int orientation;
    /**
     * ピッチ
     */
    private float pitch;
    /**
     * 選択された角度単位
     */
    private int pitchUnit = UNIT_RADIAN;
    /**
     * ロール
     */
    private float roll;
    /**
     * すでに編集済の設定か？
     */
    private boolean edited = false;
    /**
     * すでに編集済の設定か？
     */
    public boolean isLocked() {
        return locked;
    }
    /**
     * すでに編集済の設定か？
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * ロックされた設定か？
     */
    private boolean locked = false;
    /** 初期値 */
    public SetUpInfo getInitialValue() {
        if (isLocked()) {
            return this;
        }
        if (initialValue == null) {
            initialValue = new SetUpInfo();
            initialValue.setLocked(true);
        }
        return initialValue;
    }
    /** 初期値 */
    public void setInitialValue(SetUpInfo initialValue) {
        this.initialValue = initialValue;
    }

    /** 初期値 */
    private SetUpInfo initialValue;
    /**
     * 選択された角度単位
     *
     * @return 選択された角度単位
     */
    public int getPitchUnit() {
        return pitchUnit;
    }

    /**
     * 選択された角度単位
     *
     * @param pitchUnit 選択された角度単位
     */
    public void setPitchUnit(int pitchUnit) {
        this.pitchUnit = pitchUnit;
    }

    /**
     * すでに編集済の設定か？
     *
     * @return true 編集済<br>false 未編集
     */
    public boolean isEdited() {
        return edited;
    }

    /**
     * すでに編集済の設定か？
     *
     * @param edited true 編集済<br>false 未編集
     */
    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    /**
     * 電話のセット位置
     */
    public int getSetUpLocation() {
        if (!(setUpLocation == SETUP_BAR_RIGHT || setUpLocation == SETUP_BAR_CENTER || setUpLocation == SETUP_BAR_LEFT || setUpLocation == SETUP_TUBETOP)) {
            setSetUpLocation(SETUP_BAR_RIGHT);
        }
        return setUpLocation;
    }

    /**
     * 電話のセット位置
     */
    public void setSetUpLocation(int setUpLocation) {
        this.setUpLocation = setUpLocation;
    }

    /**
     * ロール
     */
    public float getRoll() {
        return roll;
    }

    /**
     * ロール
     */
    public void setRoll(float roll) {
        this.roll = roll;
    }

    /**
     * 画面向き
     */
    public int getOrientation() {
        if (!(orientation == Configuration.ORIENTATION_LANDSCAPE || orientation == Configuration.ORIENTATION_PORTRAIT)) {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
        }
        return orientation;
    }

    /**
     * 画面向き
     *
     * @param orientation 画面向き
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    /**
     * 画面向き時計回りに変更する
     */
    public void rotateOrientation() {
        int w = getOrientation();
        switch (w) {
            case Configuration.ORIENTATION_PORTRAIT:
                orientation = Configuration.ORIENTATION_LANDSCAPE;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                orientation = Configuration.ORIENTATION_PORTRAIT;
                break;
            default:
                orientation = Configuration.ORIENTATION_PORTRAIT;
                break;
        }
    }

    /**
     * 垂直
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * 垂直
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * 垂直角をラジアンで取得する
     */
    public float getPitchByRadian () {
//        float ret = 0;
//        switch (getPitchUnit()) {
//            case UNIT_RADIAN:
//                ret = getPitch();
//                break;
//            case UNIT_PERCENT:
//                ret = (float)(Math.atan(getPitch() / 100D));
//                break;
//            case UNIT_PERMIL:
//                ret = (float)(Math.atan(getPitch() / 1000D));
//                break;
//            case UNIT_DEGLEE:
//                ret = (float)(getPitchUnit() * 180D / Math.PI);
//                break;s
//        }
//        return ret;
        return SensorCache.transrateToRadianFromUnit(getPitchUnit(), getPitch());
    }

    /**
     * 垂直核をラジアンでセットする
     * @param radian ラジアン値
     */
    public void setPitchByRadian (float radian) {
        float value = SensorCache.transrateToUnitFromRadian(getPitchUnit(), radian);
        setPitch(value);
    }
}
