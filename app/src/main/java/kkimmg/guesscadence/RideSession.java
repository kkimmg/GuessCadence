package kkimmg.guesscadence;

import java.io.Serializable;

/**
 * 自転車セッションs
 */
public class RideSession implements Serializable {
    /**
     * 実際にはセッション外のID
     */
    public static final long OUT_OF_SESSION_ID = 0;
    /**
     * セッションID
     */
    private long id;
    /**
     * 自転車（マスタ）
     */
    private BikeInfo initialBikeInfo;
    /**
     * 自転車
     */
    private BikeInfo bikeInfo;
    /**
     * 開始日時
     */
    private long start = new java.util.Date().getTime();
    /**
     * 終了日時
     */
    private long end;

    /**
     * 自転車（マスタ）
     */
    public BikeInfo getInitialBikeInfo() {
        return initialBikeInfo;
    }

    /**
     * 自転車（マスタ）
     */
    public void setInitialBikeInfo(BikeInfo initialBikeInfo) {
        this.initialBikeInfo = initialBikeInfo;
    }

    /**
     * セッションID
     */
    public long getId() {
        return id;
    }

    /**
     * セッションID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 自転車
     */
    public BikeInfo getBikeInfo() {
        return bikeInfo;
    }

    /**
     * 自転車
     */
    public void setBikeInfo(BikeInfo bikeInfo) {
        this.bikeInfo = bikeInfo;
    }

    /**
     * 開始日時
     */
    public long getStart() {
        return start;
    }

    /**
     * 開始日時
     */
    public void setStart(long start) {
        this.start = start;
    }

    /**
     * 終了日時
     */
    public long getEnd() {
        return end;
    }

    /**
     * 終了日時
     */
    public void setEnd(long end) {
        this.end = end;
    }
}
