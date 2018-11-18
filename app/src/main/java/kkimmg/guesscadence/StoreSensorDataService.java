package kkimmg.guesscadence;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_ACCELEROMETER_UNCALIBRATED;
import static android.hardware.Sensor.TYPE_GRAVITY;
import static android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;

public class StoreSensorDataService extends Service implements SensorEventListener, LocationListener {
    /**
     * セッション
     */
    private RideSession rideSession;
    /**
     * 自転車の情報
     */
    private BikeInfo bikeInfo;
    /**
     * センサーマネージャ
     */
    private SensorManager sensorManager;
    /**
     * 設定キー：サンプリング間隔
     */
    public static final String SENSOR_DELAY_KEY = "pref_key_sensor_delay";
    /**
     * 設定キー：ロケーション　パワー消費
     */
    public static final String LOCATION_POWER_KEY = "pref_key_location_power";
    /**
     * 設定キー：ロケーション　精度
     */
    public static final String LOCATION_ACCURACY_KEY = "pref_key_location_accuracy";

    /**
     * サンプリング間隔：最も早い
     */
    public static final String DELAY_FASTEST = "FASTEST";
    /**
     * サンプリング間隔：早い
     */
    public static final String DELAY_GAME = "GAME";
    /**
     * サンプリング間隔：遅い
     */
    public static final String DELAY_UI = "UI";
    /**
     * サンプリング間隔：普通
     */
    public static final String DELAY_NORMAL = "MORMAL";

    /**
     * ロケーションマネージャ
     */
    private LocationManager locationManager;

    public StoreSensorDataService() {

    }

    @Override
    public void onDestroy() {
        terminateSensors();
        super.onDestroy();
    }

    /**
     * 設定から正数値を取得する
     * @param key 設定キー
     * @param defaultValue 初期値
     * @return 設定値または初期値
     */
    private int getPrefInt(String key, int defaultValue) {
        int ret = defaultValue;
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String strSensorDelay = pref.getString(key, String.valueOf(defaultValue));
            ret = Integer.valueOf(strSensorDelay);
        } catch (NumberFormatException e) {
            ret = defaultValue;
        }
        return ret;
    }

    /**
     * センサー類の初期化・登録
     */
    private void initializeSensors () {
        int sensorDelay = getPrefInt(SENSOR_DELAY_KEY, SensorManager.SENSOR_DELAY_FASTEST);
        // センサーマネージャ
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        // 加速度計
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(TYPE_ACCELEROMETER),
                sensorDelay);
        // 重力抜き加速度計
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(TYPE_LINEAR_ACCELERATION),
                sensorDelay);
        // 重力加速度計
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(TYPE_GRAVITY),
                sensorDelay);
        // 方向系
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD),
                sensorDelay);
        // 位置情報はパーミションチェック後
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // ロケーションマネージャ
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // プロバイダ
            Criteria criteria = new Criteria();
            criteria.setAccuracy(getPrefInt(LOCATION_ACCURACY_KEY, Criteria.ACCURACY_HIGH));
            criteria.setPowerRequirement(getPrefInt(LOCATION_POWER_KEY, Criteria.NO_REQUIREMENT));
            String provider = locationManager.getBestProvider(criteria, true);
            // ロケーション
            locationManager.requestLocationUpdates(provider, 0L, 0.0F, this);
        }
    }

    /**
     * センサー類の削除
     */
    private void terminateSensors () {
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        SensorEventHistory sensorEventHistory = new SensorEventHistory(rideSession.getId(), sensorEvent);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LocationHistory locationHistory = new LocationHistory(rideSession.getId(), location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
