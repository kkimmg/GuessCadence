package kkimmg.guesscadence;

import java.io.Serializable;

/**
 * 自転車セッションs
 */
public class RideSession implements Serializable {
    /** セッションID */
    private long id;
    /** 自転車 */
    private BikeInfo bikeInfo;
    /** 開始日時 */
    private long start;
    /** 終了日時 */
    private long end;
    /** セッションID */
    public long getId() {
        return id;
    }
    /** セッションID */
    public void setId(long id) {
        this.id = id;
    }
    /** 自転車 */
    public BikeInfo getBikeInfo() {
        return bikeInfo;
    }
    /** 自転車 */
    public void setBikeInfo(BikeInfo bikeInfo) {
        this.bikeInfo = bikeInfo;
    }
}
