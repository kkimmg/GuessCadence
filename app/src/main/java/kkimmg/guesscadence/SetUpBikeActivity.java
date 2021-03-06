package kkimmg.guesscadence;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

public class SetUpBikeActivity extends AppCompatActivity {
    /**
     * 自転車の情報
     */
    public static final String KEY_BIKEINFO = "BIKEINFO";
    /**
     * セットアップ情報
     */
    public static final String KEY_SETUPINFO = "SETUPINFO";
    /**
     * 画面向き（縦）
     */
    public static final int ORIENTATION_PORTRAIT_SP = 0;
    /**
     * 画面向き（横）
     */
    public static final int ORIENTATION_LANDSCAPE_SP = 1;
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
     * 呼び出し画面
     */
    private static final int ACTIVITY_SIDE = 1;
    /**
     * 呼び出し画面
     */
    private static final int ACTIVITY_TOP = 2;
    /**
     * 自転車の情報
     */
    private BikeInfo bikeInfo;
    /**
     * 変更前の角度の単位
     */
    private int previousAngleUnit = SetUpInfo.UNIT_RADIAN;
    /**
     * 変更前の重量の単位
     */
    private int previousWeightUnit = BikeInfo.WEIGHT_KG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_bike);

        try {
            bikeInfo = (BikeInfo) getIntent().getSerializableExtra(SetUpBikeActivity.KEY_BIKEINFO);
        } catch (Exception ex) {
            bikeInfo = new BikeInfo();
        }

        final EditText edBikeName = findViewById(R.id.edBikeName);
        final EditText edBikeWeight = findViewById(R.id.edBikeWeight);
        final Spinner spWeightUnit = findViewById(R.id.spWeightUnit);
        final EditText edPitch = findViewById(R.id.edPitch);
        final Button btGoPitch = findViewById(R.id.btGoPitch);
        final Spinner spPitchUnit = findViewById(R.id.spPitchUnit);
        final Button btResetPitch = findViewById(R.id.btResetPitch);
        final Spinner spOrientation = findViewById(R.id.spOrientation);
        final Spinner spLocation = findViewById(R.id.spLocation);
        final Button btGoOrientation = findViewById(R.id.btGoOrientation);
        final Button btResetOrientation = findViewById(R.id.btResetOrientation);
        final Button btSetUpOK = findViewById(R.id.btSetUpOK);
        final Button btSetUpCancel = findViewById(R.id.btSetUpCancel);

        // 重量の単位を変更
        spWeightUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                double oldValue = Float.valueOf(edBikeWeight.getText().toString());
                double newValue = BikeInfo.transrateWeight(previousAngleUnit, i, oldValue);
                edBikeWeight.setText(String.valueOf(newValue));
                previousWeightUnit = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // 角度の単位を変更
        spPitchUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                float oldValue = Float.valueOf(edPitch.getText().toString());
                float newValue = SensorCache.transrateFromTo(previousAngleUnit, i, oldValue);
                edPitch.setText(String.valueOf(newValue));
                previousAngleUnit = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spPitchUnit.setSelection(SetUpInfo.UNIT_RADIAN);
                previousAngleUnit = SetUpInfo.UNIT_RADIAN;
            }
        });
        // 角度設定画面を表示
        btGoPitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetUpInfo info = new SetUpInfo();
                info.setPitchUnit(spPitchUnit.getSelectedItemPosition());
                info.setPitch(Float.valueOf(edPitch.getText().toString()) - SensorCache.transrateToUnitFromRadian(info.getPitchUnit(), (float) SensorCache.HPI));
                Intent intent = new Intent(getApplicationContext(), kkimmg.guesscadence.SetUpSideActivity.class);
                intent.putExtra(KEY_SETUPINFO, info);
                startActivityForResult(intent, ACTIVITY_SIDE);
            }
        });
        // 角度のリセット
        btResetPitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SensorCache sensorCache = new SensorCache(SetUpBikeActivity.this);
                sensorCache.registerSensorListener();
                ResetAngleThread th = new ResetAngleThread(sensorCache, edPitch, spPitchUnit);
                edPitch.post(th);
            }
        });
        // 縦横のリセット
        btResetOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int orientation = getResources().getConfiguration().orientation;
                switch (orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        spOrientation.setSelection(ORIENTATION_PORTRAIT_SP);
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        spOrientation.setSelection(ORIENTATION_LANDSCAPE_SP);
                        break;
                }
            }
        });
        // 縦横設定画面の表示
        btGoOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetUpInfo info = new SetUpInfo();
                info.setSetUpLocation(spLocation.getSelectedItemPosition());
                switch (spOrientation.getSelectedItemPosition()) {
                    case ORIENTATION_PORTRAIT_SP:
                        info.setOrientation(Configuration.ORIENTATION_PORTRAIT);
                        break;
                    case ORIENTATION_LANDSCAPE_SP:
                        info.setOrientation(Configuration.ORIENTATION_LANDSCAPE);
                        break;
                }
                Intent intent = new Intent(getApplicationContext(), kkimmg.guesscadence.SetUpTopActivity.class);
                intent.putExtra(KEY_SETUPINFO, info);
                startActivityForResult(intent, ACTIVITY_TOP);
            }
        });

        btSetUpOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyToInfo(bikeInfo);
                // 結果を設定
                Intent intent = new Intent();
                intent.putExtra(SetUpBikeActivity.KEY_BIKEINFO, bikeInfo);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btSetUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 結果を設定
                Intent intent = new Intent();
                intent.putExtra(SetUpBikeActivity.KEY_BIKEINFO, bikeInfo);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    /**
     * セットアップ情報から画面に反映する
     * @param bikeInfo セットアップ情報
     */
    public void applyToScreen (BikeInfo bikeInfo) {
        final EditText edBikeName = findViewById(R.id.edBikeName);
        final EditText edBikeWeight = findViewById(R.id.edBikeWeight);
        final Spinner spWeightUnit = findViewById(R.id.spWeightUnit);
        final EditText edPitch = findViewById(R.id.edPitch);
        final Button btGoPitch = findViewById(R.id.btGoPitch);
        final Spinner spPitchUnit = findViewById(R.id.spPitchUnit);
        final Button btResetPitch = findViewById(R.id.btResetPitch);
        final Spinner spOrientation = findViewById(R.id.spOrientation);
        final Spinner spLocation = findViewById(R.id.spLocation);
        final Button btGoOrientation = findViewById(R.id.btGoOrientation);
        final Button btResetOrientation = findViewById(R.id.btResetOrientation);
        final Switch swLock = findViewById(R.id.swLock);

        SetUpInfo setUpInfo = bikeInfo.getSetUpInfo();
        SetUpInfo info = setUpInfo.getInitialValue();
        edBikeName.setText(bikeInfo.getName());
        edBikeWeight.setText(String.valueOf(bikeInfo.getWeight()));
        spWeightUnit.setSelection(bikeInfo.getWeightUnit());
        //
        setUpInfo.setPitch(Float.valueOf(edPitch.getText().toString()));
        setUpInfo.setPitchUnit(spPitchUnit.getSelectedItemPosition());
        setUpInfo.setSetUpLocation(spLocation.getSelectedItemPosition());
        switch (spOrientation.getSelectedItemPosition()) {
            case ORIENTATION_PORTRAIT_SP:
                setUpInfo.setOrientation(Configuration.ORIENTATION_PORTRAIT);
                break;
            case ORIENTATION_LANDSCAPE_SP:
                setUpInfo.setOrientation(Configuration.ORIENTATION_LANDSCAPE);
                break;
        }
        //
        info.setPitch(Float.valueOf(edPitch.getText().toString()));
        info.setPitchUnit(spPitchUnit.getSelectedItemPosition());
        info.setSetUpLocation(spLocation.getSelectedItemPosition());
        switch (spOrientation.getSelectedItemPosition()) {
            case ORIENTATION_PORTRAIT_SP:
                info.setOrientation(Configuration.ORIENTATION_PORTRAIT);
                break;
            case ORIENTATION_LANDSCAPE_SP:
                info.setOrientation(Configuration.ORIENTATION_LANDSCAPE);
                break;
        }
        swLock.setChecked(setUpInfo.isLocked());
    }

    /**
     * 画面からセットアップ情報に反映する
     * @param bikeInfo セットアップ情報
     */
    public void applyToInfo (BikeInfo bikeInfo) {
        final EditText edBikeName = findViewById(R.id.edBikeName);
        final EditText edBikeWeight = findViewById(R.id.edBikeWeight);
        final Spinner spWeightUnit = findViewById(R.id.spWeightUnit);
        final EditText edPitch = findViewById(R.id.edPitch);
        final Button btGoPitch = findViewById(R.id.btGoPitch);
        final Spinner spPitchUnit = findViewById(R.id.spPitchUnit);
        final Button btResetPitch = findViewById(R.id.btResetPitch);
        final Spinner spOrientation = findViewById(R.id.spOrientation);
        final Spinner spLocation = findViewById(R.id.spLocation);
        final Button btGoOrientation = findViewById(R.id.btGoOrientation);
        final Button btResetOrientation = findViewById(R.id.btResetOrientation);
        final Switch swLock = findViewById(R.id.swLock);
        bikeInfo.setName(edBikeName.getText().toString());
        bikeInfo.setWeight(Double.valueOf(edBikeWeight.getText().toString()));
        bikeInfo.setWeightUnit(spWeightUnit.getSelectedItemPosition());
        SetUpInfo setUpInfo = bikeInfo.getSetUpInfo();
        SetUpInfo info = setUpInfo.getInitialValue();
        info.setPitchUnit(spPitchUnit.getSelectedItemPosition());
        info.setPitch(Float.valueOf(edPitch.getText().toString()) - SensorCache.transrateToUnitFromRadian(info.getPitchUnit(), (float) SensorCache.HPI));
        info.setSetUpLocation(spLocation.getSelectedItemPosition());
        switch (spOrientation.getSelectedItemPosition()) {
            case ORIENTATION_PORTRAIT_SP:
                info.setOrientation(Configuration.ORIENTATION_PORTRAIT);
                break;
            case ORIENTATION_LANDSCAPE_SP:
                info.setOrientation(Configuration.ORIENTATION_LANDSCAPE);
                break;
        }
        setUpInfo.setLocked(swLock.isChecked());
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_SIDE:
                if (data != null && data.hasExtra(KEY_SETUPINFO)) {
                    SetUpInfo info = (SetUpInfo) data.getSerializableExtra(KEY_SETUPINFO);
                    final EditText edPitch = findViewById(R.id.edPitch);
                    edPitch.setText(String.valueOf(info.getPitch() + SensorCache.transrateToUnitFromRadian(info.getPitchUnit(), (float) SensorCache.HPI)));
                }
                break;
            case ACTIVITY_TOP:
                if (data != null && data.hasExtra(KEY_SETUPINFO)) {
                    SetUpInfo info = (SetUpInfo) data.getSerializableExtra(KEY_SETUPINFO);
                    final Spinner spOrientation = findViewById(R.id.spOrientation);
                    final Spinner spLocation = findViewById(R.id.spLocation);
                    switch (info.getOrientation()) {
                        case Configuration.ORIENTATION_PORTRAIT:
                            spOrientation.setSelection(ORIENTATION_PORTRAIT_SP);
                            break;
                        case Configuration.ORIENTATION_LANDSCAPE:
                            spOrientation.setSelection(ORIENTATION_LANDSCAPE_SP);
                            break;
                    }
                    spLocation.setSelection(info.getSetUpLocation());
                }
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                // 戻るボタンが押された場合
                new AlertDialog.Builder(this)
                        .setTitle("SetUpInfo")
                        .setMessage("Save SetUp Info")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                applyToInfo(bikeInfo);
                                // 結果を設定
                                Intent intent = new Intent();
                                intent.putExtra(SetUpBikeActivity.KEY_BIKEINFO, bikeInfo);
                                setResult(RESULT_OK, intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 結果を設定
                                Intent intent = new Intent();
                                intent.putExtra(SetUpBikeActivity.KEY_BIKEINFO, bikeInfo);
                                setResult(RESULT_CANCELED, intent);
                                finish();
                            }
                        }).show();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 角度をリセットするスレッド
     */
    private static class ResetAngleThread extends Thread {
        private SensorCache sensorCache;
        private EditText edPitch;
        private Spinner spPitchUnit;

        /**
         * コンストラクタ
         *
         * @param sensorCache センサー情報
         * @param edPitch     角度を示すビュー
         * @param spPitchUnit 角度の単位
         */
        public ResetAngleThread(SensorCache sensorCache, EditText edPitch, Spinner spPitchUnit) {
            super();
            this.sensorCache = sensorCache;
            this.edPitch = edPitch;
            this.spPitchUnit = spPitchUnit;
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
            float pitch = attitude[SensorCache.ATITUDE_PITCH];
            float[] gravity = sensorCache.getGravity();
            if (gravity[1] < 0) {
                pitch *= -1;
            }
            float value = SensorCache.transrateToUnitFromRadian(spPitchUnit.getSelectedItemPosition(), (float) (pitch + SensorCache.HPI));
            edPitch.setText(String.valueOf(value));
            sensorCache.unregisterSensorListener();
        }
    }
}
