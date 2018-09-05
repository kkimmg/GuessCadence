package kkimmg.guesscadence;

import java.io.Serializable;

/**
 * バイクの情報
 */
public class BikeInfo implements Serializable {
    /**
     * ID
     */
    private int id = 0;
    /**
     * 名前
     */
    private String name;
    /**
     * 重量
     */
    private double weight = 10.0D;
    /**
     * 設置状況
     */
    private SetUpInfo setUpInfo;

    /**
     * ID
     */
    public int getId() {
        return id;
    }

    /**
     * ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 重量
     */
    public double getWeight() {
        return weight;
    }

    /**
     * 重量
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * 名前
     */
    public String getName() {
        if (name == null) {
            setName("Bike");
        }
        return name;
    }

    /**
     * 名前
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 設置状況
     */
    public SetUpInfo getSetUpInfo() {
        if (setUpInfo == null) {
            setSetUpInfo(new SetUpInfo());
        }
        return setUpInfo;
    }

    /**
     * 設置状況
     */
    public void setSetUpInfo(SetUpInfo setUpInfo) {
        this.setUpInfo = setUpInfo;
    }
}
