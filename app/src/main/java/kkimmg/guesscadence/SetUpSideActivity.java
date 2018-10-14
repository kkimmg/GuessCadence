package kkimmg.guesscadence;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;

public class SetUpSideActivity extends AppCompatActivity  implements SensorEventListener {
    /**
     * センサーマネージャ
     */
    private SensorManager sensorManager;

    /** 加速度 */
    private float[] gravity;
    /** 方角 */
    private float[] geomagnetic;
    /**
     * 角度情報
     */
    private transient float[] attitude = new float[3];
    /** セットアップ情報 */
    private SetUpInfo setUpInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_side);

        try {
            setUpInfo = (SetUpInfo) getIntent().getSerializableExtra(SetUpBikeActivity.KEY_SETUPINFO);
        } catch (Exception ex) {
            setUpInfo = new SetUpInfo();
        }

        final SetUpSideView setUpSideView = (SetUpSideView)findViewById(R.id.vwSetUpSideView2);
        setUpSideView.setSetUpInfo(setUpInfo);

        Button btResetPitch2 = (Button)findViewById(R.id.btResetPitch2);
        btResetPitch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SensorCache sensorCache = new SensorCache(SetUpSideActivity.this);
                sensorCache.registerSensorListener();
                ResetAngleThread th = new ResetAngleThread(sensorCache, setUpInfo, setUpSideView);
                setUpSideView.post(th);
            }
        });

        // センサーマネージャ
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                // 戻るボタンが押された場合

                // 結果を設定
                Intent intent = new Intent();
                intent.putExtra(SetUpBikeActivity.KEY_SETUPINFO, setUpInfo);
                setResult(RESULT_OK, intent);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 角度をリセットするスレッド
     */
    private static class ResetAngleThread extends Thread {
        private SensorCache sensorCache;
        private SetUpInfo setUpInfo;
        private SetUpSideView setUpSideView;

        /**
         * コンストラクタ
         *
         * @param sensorCache センサー情報
         * @param  setUpInfo セットアップ情報
         * @param setUpSideView    角度を示すビュー
         */
        public ResetAngleThread(SensorCache sensorCache, SetUpInfo setUpInfo, SetUpSideView setUpSideView) {
            super();
            this.sensorCache = sensorCache;
            this.setUpInfo = setUpInfo;
            this.setUpSideView = setUpSideView;
        }

        @Override
        public void run() {
            int loop = 0;
            while (!sensorCache.isCached() && loop < 5000) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loop += 500;
            }
            float[] attitude = sensorCache.getAttitude();
            float[] gravity = sensorCache.getGravity();
            float pitch = (float)(attitude[SensorCache.ATITUDE_PITCH]);
//            float pitch = (float)(attitude[SensorCache.ATITUDE_PITCH]  + SensorCache.HPI);
//            if (gravity[1] < 0) {
//                pitch = -(float)(attitude[SensorCache.ATITUDE_PITCH]  + SensorCache.HPI);
//            }
            if (gravity[1] < 0) {
                pitch *= -1;
            }
            setUpInfo.setPitchByRadian(pitch);
            sensorCache.unregisterSensorListener();
            setUpSideView.invalidate();
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        registerSensorListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensorListener();
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
            Resources resources = getResources();
            Configuration config = resources.getConfiguration();

            SensorManager.getRotationMatrix(
                    rotationMatrixOld, null,
                    gravity, geomagnetic);

            switch (config.orientation) {
                case Configuration.ORIENTATION_PORTRAIT:        // 縦
                    SensorManager.remapCoordinateSystem(rotationMatrixOld, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, rotationMatrix);
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:       // 横
                    SensorManager.remapCoordinateSystem(rotationMatrixOld, SensorManager.AXIS_Y, SensorManager.AXIS_X, rotationMatrix);
                    break;
            }

            SensorManager.getOrientation(
                    rotationMatrix,
                    attitude);

            final SetUpSideView setUpSideView = (SetUpSideView)findViewById(R.id.vwSetUpSideView2);
            if (gravity[1] >= 0) {
                setUpSideView.setCurrentAngle((float) (attitude[SensorCache.ATITUDE_PITCH]));
            } else {
                setUpSideView.setCurrentAngle(-(float) (attitude[SensorCache.ATITUDE_PITCH]));
            }
            setUpSideView.setGravity(gravity);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
