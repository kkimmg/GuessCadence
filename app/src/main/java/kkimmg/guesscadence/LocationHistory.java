package kkimmg.guesscadence;

import android.location.Location;

import java.io.Serializable;

/**
 * ロケーションの履歴データ
 */
public class LocationHistory implements Serializable {
    /**
     * セッションID
     */
    private long sessionId;
    /**
     * タイムスタンプ
     */
    private long time;
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
     *
     * @param sessionId セッションID
     * @param location  ロケーション
     */
    public LocationHistory(long sessionId, Location location) {
        this.sessionId = sessionId;
        this.time = location.getTime();
        this.altitude = location.getAltitude();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.hasSpeed = location.hasSpeed();
        if (this.hasSpeed) {
            this.speed = location.getSpeed();
        }
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
    public long getTime() {
        return time;
    }

    /**
     * タイムスタンプ
     */
    public void setTime(long time) {
        this.time = time;
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
