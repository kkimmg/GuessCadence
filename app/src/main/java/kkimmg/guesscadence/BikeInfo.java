package kkimmg.guesscadence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;

/**
 * バイクの情報
 */
public class BikeInfo implements Serializable,Cloneable {
    /** デフォルトの自転車IDを取得するためのキー */
    public static final String DEFAULT_BIKEINFO_KEY = "DEFAULT_BIKEINFO_KEY";
    /** デフォルトの自転車ID */
    public static final long DEFAULT_BIKEINFO_ID = 0;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        BikeInfo clone = new BikeInfo();
        clone.setName(getName());
        clone.setWeight(getWeight());
        clone.setWeightUnit(getWeightUnit());
        clone.setMasterData(isMasterData());
        clone.setSetUpInfo((SetUpInfo) getSetUpInfo().clone());
        return clone;
    }

    /**
     * 重量の単位を変更する
     * @param srcUnit 変換前単位
     * @param tagUnit 変換後単位
     * @param src 変換前重量
     * @return 変換後重量
     */
    public static double transrateWeight(int srcUnit, int tagUnit, double src) {
        double ret = src;
        if (srcUnit == WEIGHT_LB && tagUnit == WEIGHT_KG) {
            ret = src * 5760.0D / 1000.0D;
        } else if (srcUnit == WEIGHT_LB && tagUnit == WEIGHT_KG) {
            ret = src * 1000.0D / 5760.0D;
        }
        return ret;
    }
    /**
     * 重量単位(KG)
     */
    public static final int WEIGHT_KG = 1;
    /**
     * 重量単位(LB)
     */
    public static final int WEIGHT_LB = 2;
    /**
     * ID
     */
    private long id = 0;
    /**
     * 名前
     */
    private String name;
    /**
     * 重量
     */
    private double weight = 10.0D;
    /**
     * 重量単位
     */
    private int weightUnit = WEIGHT_KG;
    /**
     * 設置状況
     */
    private SetUpInfo setUpInfo = new SetUpInfo();

    /**
     * 重量単位
     */
    public int getWeightUnit() {
        return weightUnit;
    }

    /**
     * 重量単位
     */
    public void setWeightUnit(int weightUnit) {
        this.weightUnit = weightUnit;
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
     * これは自転車のマスターデータですか
     * @return マスターか履歴か
     */
    public boolean isMasterData() {
        return masterData;
    }

    /**
     * これは自転車のマスターデータですか
     * @param masterData マスターか履歴か
     */
    public void setMasterData(boolean masterData) {
        this.masterData = masterData;
    }

    /**
     * これは自転車のマスターデータですか
     */
    private boolean masterData = true;

    /**
     * 名前
     */
    public String getName() {
        if (name == null) {
            setName("New Bike");
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
