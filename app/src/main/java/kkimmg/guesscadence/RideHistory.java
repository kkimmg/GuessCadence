package kkimmg.guesscadence;

public interface RideHistory {
    /**
     * 履歴療法
     */
    public static final int HISTORY_BOTH = 0;
    /**
     * センサー履歴
     */
    public static final int HISTORY_SENSOR = 1;
    /**
     * 位置履歴
     */
    public static final int HISTORY_LOCATION = 2;

    /**
     * ID
     */
    public void setId (long id);

    /**
     * ID
     */
    public long getId();
    /**
     * セッションIDを取得
     *
     * @return セッションID
     */
    public long getSessionId();

    /**
     * 履歴タイプ
     *
     * @return 履歴タイプ
     */
    public int getHistoryType();

    /**
     * タイムスタンプ
     *
     * @return タイムスタンプ
     */
    public long getTimestamp();

    /**
     * センサータイプ
     * @return センサータイプ
     */
    public int getSensorType();

    /**
     * センサー値
     *
     * @param index 値の配列位置
     * @return センサー値の配列
     */
    public double getValue(int index);
}
