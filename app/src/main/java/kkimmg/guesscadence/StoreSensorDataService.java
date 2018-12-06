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
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_ACCELEROMETER_UNCALIBRATED;
import static android.hardware.Sensor.TYPE_ALL;
import static android.hardware.Sensor.TYPE_GRAVITY;
import static android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;

/**
 * センサーデータの受け取り、および保存
 */
public class StoreSensorDataService extends Service implements SensorEventListener, LocationListener {
    /** クライアントに引き渡すバインダ */
    private final IBinder mBinder = new LocalBinder();
    /** 履歴の取得状態 */
    private boolean logging = false;
    /** キュー */
    private Queue<RideHistory> innerList = new ConcurrentLinkedDeque<RideHistory>();

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
    /** クライアントに引き渡すバインダ */
    public class LocalBinder extends Binder {
        StoreSensorDataService getService() {
            return StoreSensorDataService.this;
        }
    }

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

    /** サービスに引き渡す自転車情報のキー */
    public static final String BIKEINFO_KEY = "BIKEINFO";

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

    @Override
    public void onRebind(Intent intent) {
        if (!logging) {
            // 履歴作成していない状態では再度センサーを初期化する
            initializeSensors();
        }
        super.onRebind(intent);
    }

    /**
     * センサー類の初期化・登録
     */
    private void initializeSensors () {
        // センサー設定
        int sensorDelay = getPrefInt(SENSOR_DELAY_KEY, SensorManager/*.SENSOR_DELAY_GAME*/.SENSOR_DELAY_FASTEST);
        // センサーマネージャ
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        // すべて
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(TYPE_ALL),
                sensorDelay);
//        // 加速度計
//        sensorManager.registerListener(
//                this,
//                sensorManager.getDefaultSensor(TYPE_ACCELEROMETER),
//                sensorDelay);
//        // 重力抜き加速度計
//        sensorManager.registerListener(
//                this,
//                sensorManager.getDefaultSensor(TYPE_LINEAR_ACCELERATION),
//                sensorDelay);
//        // 重力加速度計
//        sensorManager.registerListener(
//                this,
//                sensorManager.getDefaultSensor(TYPE_GRAVITY),
//                sensorDelay);
//        // 方向系
//        sensorManager.registerListener(
//                this,
//                sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD),
//                sensorDelay);
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
        // センサー解除
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    long cnt = 1;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long id = (rideSession != null ? rideSession.getId() : RideSession.OUT_OF_SESSION_ID);
        SensorEventHistory sensorEventHistory = new SensorEventHistory(id, sensorEvent);
        if (logging) {
            HistoryProvider.insertRideHistory(this, sensorEventHistory);
            cnt ++;
            if (cnt > 10000) {
                endSession();
                cnt = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        long id = (rideSession != null ? rideSession.getId() : RideSession.OUT_OF_SESSION_ID);
        LocationHistory locationHistory = new LocationHistory(id, location);
        if (logging) {
            HistoryProvider.insertRideHistory(this, locationHistory);
        }
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
        initializeSensors();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (!logging) {
            // 履歴作成していない状態ではセンサー解除する
            terminateSensors();
        }
        return super.onUnbind(intent);
    }

    /**
     * セッションの開始
     * @param session セッション
     */
    public RideSession startSession (RideSession session) {
        rideSession = session;
        logging = true;
        rideSession.setStart(System.currentTimeMillis());

        rideSession = SessionProvider.insertSession(this, rideSession);

        return rideSession;
    }

    /**
     * セッションの終了
     * @return
     */
    public RideSession endSession () {
        logging = false;

        if (rideSession == null) {
            return null;
        }

        rideSession.setEnd(System.currentTimeMillis());
        RideSession ret = SessionProvider.updateSession(this, rideSession);

        //
        try {
            CSVExporter exp = new CSVExporter(this, ret);
            Thread th = new Thread(exp);
            th.start();
        } catch (Exception ex) {
            Log.e("OUTPUT", ex.getMessage(), ex);
        }
        //


        rideSession = null;
        return ret;
    }
}
