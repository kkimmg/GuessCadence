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
    public static final int SETUP_BAR_LEFT = 2;
    /**
     * ハンドルバーの中央
     */
    public static final int SETUP_BAR_CENTER = 1;
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
     * ID
     */
    private long id;
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        SetUpInfo ret = (SetUpInfo) super.clone();


        copyFromTo(this, ret);


        return ret;
    }

    /**
     * ロックされた設定か？
     */
    private boolean locked = false;
//    /**
//     * 重力加速度
//     */
//    private float[] gravity;
    /**
     * 初期値
     */
    private SetUpInfo initialValue;

        /**
     * 重力加速度から端末の角度を計算する
     * @return 端末の角度
     */
    public static double getPitchFromGravity (int orientation, float[] gravity) {
        double ret = 0.0F;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // cosΘ = b /  c
            ret = Math.acos(gravity[1] / SensorCache.STANDARD_GRAVITY);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ret = Math.acos(gravity[1] / SensorCache.STANDARD_GRAVITY);
        }
        return ret;
    }


    /**
     * 端末の角度から重力加速度を計算する
     *
     * @param radian 端末の角度
     * @param orientation 端末の縦横
     * @return 重力加速度
     */
    public static float[] getGraviry(int orientation, double radian) {
        float[] ret = new float[2];
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            ret[0] = 0.0F;
            ret[1] = (float) (Math.cos(radian) * SensorCache.STANDARD_GRAVITY);// cosΘ = b /  c → b = cosΘ * c
            ret[2] = (float) (Math.sin(radian) * -SensorCache.STANDARD_GRAVITY);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ret[0] = 0.0F;
            ret[1] = (float) (Math.cos(radian) * SensorCache.STANDARD_GRAVITY);// cosΘ = b /  c → b = cosΘ * c
            ret[2] = (float) (Math.sin(radian) * -SensorCache.STANDARD_GRAVITY);
        }
        return ret;
    }

    /**
     * ID
     */
    public long getId() {
        return id;
    }

    /**
     * ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * すでに編集済の設定か？
     */
    public boolean isLocked() {
        return locked;
    }
//    /**
//     * 重力加速度
//     */
//    public float[] getGravity() {
//        if (gravity == null) {
//            gravity = new float[2];
//        }
//        return gravity.clone();
//    }
//    /**
//     * 重力加速度
//     */
//    public void setGravity(float[] gravity) {
//        this.gravity = (gravity != null ? gravity.clone() : null);
//    }
//
//    /**
//     * 重力加速度から端末の角度を計算する
//     * @return 端末の角度
//     */
//    public float getPitchFromGravity () {
//        float ret = 0.0F;
//        if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
//            // cosΘ = b /  c
//            ret = gravity[1] / SensorCache.STANDARD_GRAVITY;
//        } else if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
//            ret = gravity[1] / SensorCache.STANDARD_GRAVITY;
//        }
//        return ret;
//    }
//

    /**
     * すでに編集済の設定か？
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * 端末の角度から重力加速度を計算する
     *
     * @return 重力加速度
     */
    public float[] getGravityFromPitch() {
        return getGraviry(orientation, getPitchByRadian());
    }
//
//    /**
//     * 端末の角度から加速度の成分を再設定する
//     */
//    public void resetGravityFromPitch () {
//        this.setGravity(getGravityFromPitch());
//    }

    /**
     * 初期値
     */
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

    /**
     * 初期値
     */
    public void setInitialValue(SetUpInfo initialValue) {
        this.initialValue = initialValue;
        if (isLocked()) {
            copyFrom(initialValue);
        }
    }

    /**
     * 値をコピーする
     *
     * @param src コピー元
     * @param tag コピー先
     */
    public void copyFromTo(SetUpInfo src, SetUpInfo tag) {
        tag.setRoll(src.getRoll());
        tag.setOrientation(src.getOrientation());
        tag.setPitch(src.getPitch());
        tag.setPitchUnit(src.getPitchUnit());
        tag.setSetUpLocation(src.getSetUpLocation());
    }

    /**
     * 値をコピーする
     *
     * @param src コピー元
     */
    public void copyFrom(SetUpInfo src) {
        copyFromTo(src, this);
    }

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
    public float getPitchByRadian() {
        return SensorCache.transrateToRadianFromUnit(getPitchUnit(), getPitch());
    }

    /**
     * 垂直核をラジアンでセットする
     *
     * @param radian ラジアン値
     */
    public void setPitchByRadian(float radian) {
        float value = SensorCache.transrateToUnitFromRadian(getPitchUnit(), radian);
        setPitch(value);
    }
}
