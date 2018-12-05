package kkimmg.guesscadence;

import android.hardware.SensorEvent;

import java.io.Serializable;

/**
 * センサーイベントの履歴データ
 */
public class SensorEventHistory implements Serializable, RideHistory {
    /**
     * セッション外
     */
    public static final int NO_SESSION_ID = -1;
    /**
     * ID
     */
    private long id = 0;
    /**
     * センサータイプ
     */
    private int sensorType;
    /**
     * セッションID
     */
    private long sessionId;
    /**
     * タイムスタンプ
     */
    private long timestamp;
    /**
     * センサー値
     */
    private float[] values;
    /**
     * コンストラクタ
     */
    public SensorEventHistory() {
        super();
        this.sessionId = NO_SESSION_ID;
    }
    /**
     * コンストラクタ
     *
     * @param sessionId   セッションID
     * @param sensorEvent センサーイベント
     */
    public SensorEventHistory(long sessionId, SensorEvent sensorEvent) {
        this();
        this.sessionId = sessionId;
        readFromEvent(sensorEvent);

    }

    /**
     * イベントから情報を取得する
     * @param sensorEvent イベント
     */
    public void readFromEvent (SensorEvent sensorEvent) {
        this.timestamp = sensorEvent.timestamp;
        this.sensorType = sensorEvent.sensor.getType();
        this.values = sensorEvent.values.clone();
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
     * センサー値
     *
     * @param index 値の配列位置
     * @return センサー値の配列
     */
    @Override
    public double getValue(int index) {
        if (values == null) {
            return 0;
        } else if (index < 0) {
            return 0;
        } else if (values.length <= index) {
            return 0;
        }
        return values[index];
    }

    /**
     * 履歴タイプ
     *
     * @return 履歴タイプ
     */
    @Override
    public int getHistoryType() {
        return RideHistory.HISTORY_SENSOR;
    }

    /**
     * センサータイプ
     */
    public int getSensorType() {
        return sensorType;
    }

    /**
     * センサータイプ
     */
    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    /**
     * セッションID
     */
    public long getSessionId() {
        return sessionId;
    }

    /**
     * セッションID
     */
    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * タイムスタンプ
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * タイムスタンプ
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * センサー値
     */
    public float[] getValues() {
        return values;
    }

    /**
     * センサー値
     */
    public void setValues(float[] values) {
        this.values = values;
    }
}
