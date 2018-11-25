package kkimmg.guesscadence;

import android.location.Location;

import java.io.Serializable;

/**
 * ロケーションの履歴データ
 */
public class LocationHistory implements Serializable, RideHistory {
    /**
     * 高度
     */
    public static final int VALUE_ALTITUDE = 0;
    /**
     * 緯度
     */
    public static final int VALUE_LATITUDE = 1;
    /**
     * 経度
     */
    public static final int VALUE_LONGITUDE = 2;
    /**
     * スピード検出可能？
     */
    public static final int VALUE_HASSPEED = 3;
    /**
     * スピード
     */
    public static final int VALUE_SPEED = 4;
    /**
     * ID
     */
    private long id = 0;
    /**
     * セッションID
     */
    private long sessionId;
    /**
     * タイムスタンプ
     */
    private long timestamp;
    /**
     * 高度
     */
    private double altitude;
    /**
     * 緯度
     */
    private double latitude;
    /**
     * 経度
     */
    private double longitude;
    /**
     * スピード検出可能？
     */
    private boolean hasSpeed;
    /**
     * スピード
     */
    private float speed;

    /**
     * コンストラクタ
     */
    public LocationHistory() {
        super();
        sessionId = SensorEventHistory.NO_SESSION_ID;
    }
    /**
     * コンストラクタ
     *
     * @param sessionId セッションID
     * @param location  ロケーション
     */
    public LocationHistory(long sessionId, Location location) {
        this.sessionId = sessionId;
        readFromLoaction(location);
    }

    /**
     * ロケーションから情報を取得する
     * @param location ロケーション
     */
    public void readFromLoaction(Location location) {
        this.timestamp = location.getTime();
        this.altitude = location.getAltitude();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.hasSpeed = location.hasSpeed();
        if (this.hasSpeed) {
            this.speed = location.getSpeed();
        }
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
     * 履歴タイプ
     *
     * @return 履歴タイプ
     */
    @Override
    public int getHistoryType() {
        return RideHistory.HISTORY_LOCATION;
    }

    /**
     * センサータイプ
     * センサータイプはないので任意の値、とりあえず-1
     *
     * @return センサータイプ
     */
    @Override
    public int getSensorType() {
        return -1;
    }

    /**
     * センサー値
     *
     * @param index 値の配列位置
     * @return センサー値の配列
     */
    @Override
    public double getValue(int index) {
        double ret = 0.0F;
        switch (index) {
            case VALUE_ALTITUDE:
                ret = getAltitude();
                break;
            case VALUE_LATITUDE:
                ret = getLatitude();
                break;
            case VALUE_LONGITUDE:
                ret = getLongitude();
                break;
            case VALUE_HASSPEED:
                ret = (hasSpeed() ? 1 : 0);
                break;
            case VALUE_SPEED:
                ret = getSpeed();
                break;
        }
        return ret;
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
     * 高度
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * 高度
     */
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    /**
     * 緯度
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * 緯度
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * 経度
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * 経度
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * スピード検出可能？
     */
    public boolean hasSpeed() {
        return hasSpeed;
    }

    /**
     * スピード検出可能？
     */
    public void setHasSpeed(boolean hasSpeed) {
        this.hasSpeed = hasSpeed;
    }

    /**
     * スピード
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * スピード
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
