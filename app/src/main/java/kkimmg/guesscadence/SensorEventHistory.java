package kkimmg.guesscadence;

import android.hardware.SensorEvent;

import java.io.Serializable;

/**
 * センサーイベントの履歴データ
 */
public class SensorEventHistory implements Serializable {
    /**
     * セッション外
     */
    public static final int NO_SESSION_ID = -1;
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
     *
     * @param sessionId   セッションID
     * @param sensorEvent センサーイベント
     */
    public SensorEventHistory(long sessionId, SensorEvent sensorEvent) {
        this.sessionId = sessionId;
        this.timestamp = sensorEvent.timestamp;
        this.sensorType = sensorEvent.sensor.getType();
        this.values = sensorEvent.values.clone();
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
