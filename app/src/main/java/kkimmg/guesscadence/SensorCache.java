package kkimmg.guesscadence;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;

/**
 * センサーデータのキャッシュ
 */
public class SensorCache implements SensorEventListener, Runnable {
    /**
     * ラジアンの180度
     */
    public static final double HPI = Math.PI / 2;
    /**
     * アジマス
     */
    public static final int ATITUDE_AZIMUTH = 0;
    /**
     * ピッチ
     */
    public static final int ATITUDE_PITCH = 1;

    /**
     * ロール
     */
    public static final int ATITUDE_ROLL = 2;
    /**
     * 角度情報
     */
    private transient float[] attitude = new float[3];
    /**
     * すでにキャッシュされている
     */
    private transient boolean cached = false;
    /**
     * コンテキスト
     */
    private Context context;
    /**
     * センサーマネージャ
     */
    private SensorManager sensorManager;
    /**
     * 加速度
     */
    private float[] gravity;
    /**
     * 加速度
     */
    public float[] getGravity() {
        return gravity;
    }
    /**
     * 加速度
     */
    public void setGravity(float[] gravity) {
        this.gravity = gravity;
    }

    /**

     * 方角
     */
    private float[] geomagnetic;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public SensorCache(Context context) {
        this.context = context;
        // キャッシュされていない
        cached = false;

        // センサーマネージャ
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
    }

    /**
     * すでにキャッシュされている？
     */
    public boolean isCached() {
        return cached;
    }

    /**
     * コンテキスト
     */
    public Context getContext() {
        return context;
    }

    /**
     * コンテキスト
     */
    public void setContext(Context context) {
        this.context = context;
        // キャッシュされていない
        cached = false;
    }

    /**
     * 角度情報
     */
    public float[] getAttitude() {
        return attitude;
    }

    /**
     * センサーリスナの登録
     */
    public void registerSensorListener() {
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * センサーリスナの解放
     */
    public void unregisterSensorListener() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] rotationMatrix = new float[9];
        float[] rotationMatrixOld = new float[9];

        switch (sensorEvent.sensor.getType()) {
            case TYPE_MAGNETIC_FIELD:
                geomagnetic = sensorEvent.values.clone();
                break;
            case TYPE_ACCELEROMETER:
                gravity = sensorEvent.values.clone();
                break;
        }

        if (geomagnetic != null && gravity != null) {
            Resources resources = getContext().getResources();
            Configuration config = resources.getConfiguration();

            SensorManager.getRotationMatrix(
                    rotationMatrixOld, null,
                    gravity, geomagnetic);

            switch (config.orientation) {
                case Configuration.ORIENTATION_PORTRAIT:        // 縦
                    SensorManager.remapCoordinateSystem(rotationMatrixOld, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, rotationMatrix);
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:       // 横
                    SensorManager.remapCoordinateSystem(rotationMatrixOld, SensorManager.AXIS_X, SensorManager.AXIS_Y, rotationMatrix);
                    break;
            }

            SensorManager.getOrientation(
                    rotationMatrix,
                    attitude);

            unregisterSensorListener();

            cached = true;
        }

    }

    /**
     * キャッシュする
     */
    public void cache() {
        cache(true);
    }

    /**
     * キャッシュする
     */
    public boolean cache(boolean wait) {
        registerSensorListener();
        if (wait) {
            Thread th = new Thread(this);
            th.start();
            try {
                th.join(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return cached;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void run() {
        while (!cached) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        unregisterSensorListener();
    }

    /**
     * 指定した単位の角度からラジアンに変換する
     * @param fromUnit 変換前の単位
     * @param value 変換前の数量
     * @return 変換後の数量
     */
    public static float transrateToRadianFromUnit(int fromUnit, float value) {
        float ret = 0.0F;
        switch (fromUnit) {
            case SetUpInfo.UNIT_PERCENT:
                ret = (float)(Math.atan(value / 100D));
                break;
            case SetUpInfo.UNIT_PERMIL:
                ret = (float)(Math.atan(value / 1000D));
                break;
            case SetUpInfo.UNIT_DEGLEE:
                ret = (float)(value * Math.PI / 180D);
                break;
            case SetUpInfo.UNIT_RADIAN:
            default:
                ret = value;
                break;
        }
        return ret;
    }

    /**
     * ラジアンから指定した単位の角度に変換する
     * @param toUnit 変換後の単位
     * @param value 変換前の数量
     * @return 変換後のラジアン値
     */
    public static float transrateToUnitFromRadian(int toUnit, float value) {
        float ret = 0.0F;
        switch (toUnit) {
            case SetUpInfo.UNIT_PERCENT:
                ret = (float)(Math.tan(value) * 100D);
                break;
            case SetUpInfo.UNIT_PERMIL:
                ret = (float)(Math.tan(value) * 1000D);
                break;
            case SetUpInfo.UNIT_DEGLEE:
                ret = (float)(value * 180D / Math.PI);
                break;
            case SetUpInfo.UNIT_RADIAN:
            default:
                ret = value;
                break;
        }
        return ret;
    }

    /**
     * ラジアンから指定した単位の角度に変換する
     * @param fromUnit 変換前の単位
     * @param toUnit 変換後の単位
     * @param value 変換前の数量
     * @return 返還後のラジアン値
     */
    public static float transrateFromTo (int fromUnit, int toUnit, float value) {
        float radian = transrateToRadianFromUnit(fromUnit, value);
        return transrateToUnitFromRadian(toUnit, radian);
    }
}
