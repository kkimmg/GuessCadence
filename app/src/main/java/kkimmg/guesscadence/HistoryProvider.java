package kkimmg.guesscadence;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Iterator;

public class HistoryProvider extends ContentProvider {
    /**
     * プロバイダ名
     */
    public static final String PROVIDER_NAME = "k_kim_mg.guesscadence.HistoryProvider";
    /**
     * テーブル名（履歴）
     */
    public static final String TABLE_HISTORY = "HISTORY";
    /**
     * IDを表す文字列
     */
    public static final String CONST_ID = "ID";
    /**
     * URIの元情報（履歴）
     */
    public static final String CONTENT_BASE_HISTORY = "content://" + PROVIDER_NAME + "/" + TABLE_HISTORY;
    /**
     * URIの元情報（履歴（ID指定））
     */
    public static final String CONTENT_BASE_HISTORYID = "content://" + PROVIDER_NAME + "/" + TABLE_HISTORY + CONST_ID;
    /**
     * URI（履歴）
     */
    public static final Uri CONTENT_HISTORY = Uri.parse(CONTENT_BASE_HISTORY);
    /**
     * URI（履歴ID）
     */
    public static final Uri CONTENT_HISTORYID = Uri.parse(CONTENT_BASE_HISTORYID);
    /**
     * データベースファイル名
     */
    private static final String DATABASE_NAME = "historys.db";
    /**
     * データベースバージョン（レイアウト変更時この数値を変更）</br>
     * 履歴</br>
     * 1:初期バージョン</br>
     */
    private static final int DB_VERSION = 1;
    /**
     * データアクセス方法（履歴）
     */
    private static final int TYPE_HISTORY = 0;
    /**
     * データアクセス方法（履歴（ID指定））
     */
    private static final int TYPE_HISTORYID = 1;
    /**
     * URI特定
     */
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, TABLE_HISTORY, TYPE_HISTORY);
        uriMatcher.addURI(PROVIDER_NAME, TABLE_HISTORY + CONST_ID, TYPE_HISTORYID);
    }

    /**
     * データベースインスタンス
     */
    private SQLiteDatabase db;
    /**
     * セッションイテレータ用のカーソル
     */
    private static Cursor ite_cursor = null;
    public HistoryProvider() {
    }

    /**
     * 履歴の登録
     *
     * @param context コンテキスト
     * @param history 履歴
     * @return 履歴
     */
    public static RideHistory insertRideHistory(Context context, RideHistory history) {
        ContentValues cv = new ContentValues();
        cv.put("sessionid", history.getSessionId());
        cv.put("historytype", history.getHistoryType());
        cv.put("sensortype", history.getSensorType());
        cv.put("historystamp", history.getTimestamp());
        cv.put("value_0", history.getValue(0));
        cv.put("value_1", history.getValue(1));
        cv.put("value_2", history.getValue(2));
        cv.put("value_3", history.getValue(3));
        cv.put("value_4", history.getValue(4));
        cv.put("value_5", history.getValue(5));

        Uri uri = context.getContentResolver().insert(HistoryProvider.CONTENT_HISTORY, cv);
        long row = ContentUris.parseId(uri);
        history.setId(row);

        return history;
    }

    private static final class HistoryIterator implements Iterator<RideHistory> {
        /**
         * コンテクスト
         */
        private Context context;
        /**
         * カーソル
         */
        private Cursor cursor;

        @NonNull
        public Iterator<RideHistory> iterator() {
            return null;
        }

        /**
         * コンストラクタ
         *
         * @param cursor カーソル
         */
        public HistoryIterator(Context context, Cursor cursor) {
            this.context = context;
            this.cursor = cursor;
        }

        @Override
        public boolean hasNext() {
            return !cursor.isLast() && !cursor.isAfterLast();
        }

        @Override
        public RideHistory next() {
            RideHistory history = null;
            if (cursor.moveToNext()) {
                history.setId(cursor.getLong(cursor.getColumnIndex("_id")));
                cursor2history(context, cursor, history);
            }
            return history;
        }
    }

    public static Iterator<RideHistory> getHistoryIterator (Context context) {
        closeSessionIterator();
        ite_cursor = context.getContentResolver().query(Uri.parse(SessionProvider.CONTENT_BASE_SESSION), null, null, null, null, null);
        HistoryProvider.HistoryIterator ite = new HistoryProvider.HistoryIterator(context, ite_cursor);
        return ite;
    }

    /**
     * カーソルから履歴へ転送する
     * @param context コンテキスト
     * @param cursor カーソル
     * @param history 履歴
     * @return 履歴
     */
    private static RideHistory cursor2history (Context context, Cursor cursor, RideHistory history) {
        int historytype = cursor.getInt(cursor.getColumnIndex("historytype"));
        if (historytype == RideHistory.HISTORY_LOCATION) {
            if (!(history instanceof LocationHistory)) {
                history = new LocationHistory();
            }
            getLocationHistory(context, cursor, (LocationHistory) history);
        } else if (historytype == RideHistory.HISTORY_SENSOR) {
            if (!(history instanceof SensorEventHistory)) {
                history = new SensorEventHistory();
            }
            getSensorEventHistory(context, cursor, (SensorEventHistory) history);
        }
        return history;
    }

    /**
     * 履歴の取得
     * @param context コンテキスト
     * @param history 履歴
     * @return 履歴
     */
    public static RideHistory getHistory (Context context, RideHistory history) {
        Cursor cursor1 = context.getContentResolver().query(Uri.parse(HistoryProvider.CONTENT_BASE_HISTORYID + "#" + history.getId()), null, null, null, null);
        if (cursor1 != null) {
            if (cursor1.moveToNext()) {
                history = cursor2history(context, cursor1, history);
            }
        }
        cursor1.close();
        return history;
    }

    /**
     * 履歴の取得
     * @param context コンテキスト
     * @param cursor カーソル
     * @param history 履歴
     * @return 履歴
     */
    public static SensorEventHistory getSensorEventHistory (Context context, Cursor cursor, SensorEventHistory history) {
        float[] values = new float[6];

        history.setId(cursor.getLong(cursor.getColumnIndex("_id")));
        history.setSensorType(cursor.getInt(cursor.getColumnIndex("sensertype")));
        history.setSessionId(cursor.getLong(cursor.getColumnIndex("sessionid")));
        history.setTimestamp(cursor.getLong(cursor.getColumnIndex("historystamp")));
        values[0] = cursor.getFloat(cursor.getColumnIndex("value_0"));
        values[1] = cursor.getFloat(cursor.getColumnIndex("value_1"));
        values[2] = cursor.getFloat(cursor.getColumnIndex("value_2"));
        values[3] = cursor.getFloat(cursor.getColumnIndex("value_3"));
        values[4] = cursor.getFloat(cursor.getColumnIndex("value_4"));
        values[5] = cursor.getFloat(cursor.getColumnIndex("value_5"));
        history.setValues(values);

        return history;
    }

    /**
     * 履歴の取得
     * @param context コンテキスト
     * @param cursor カーソル
     * @param history 履歴
     * @return 履歴
     */
    public static LocationHistory getLocationHistory (Context context, Cursor cursor, LocationHistory history) {

        history.setId(cursor.getLong(cursor.getColumnIndex("_id")));
        history.setSessionId(cursor.getLong(cursor.getColumnIndex("sessionid")));
        history.setTimestamp(cursor.getLong(cursor.getColumnIndex("historystamp")));
        history.setAltitude(cursor.getFloat(cursor.getColumnIndex("value_0")));
        history.setLatitude(cursor.getFloat(cursor.getColumnIndex("value_1")));
        history.setLongitude(cursor.getFloat(cursor.getColumnIndex("value_2")));
        history.setHasSpeed(cursor.getFloat(cursor.getColumnIndex("value_3")) != 0.0D);
        history.setSpeed(cursor.getFloat(cursor.getColumnIndex("value_4")));

        return history;
    }

    /**
     * 履歴の更新
     *
     * @param context コンテキスト
     * @param history 履歴
     * @return 履歴
     */
    public static RideHistory updateRideHistory(Context context, RideHistory history) {
        ContentValues cv = new ContentValues();
        cv.put("sessionid", history.getSessionId());
        cv.put("historytype", history.getHistoryType());
        cv.put("sensortype", history.getSensorType());
        cv.put("historystamp", history.getTimestamp());
        cv.put("value_0", history.getValue(0));
        cv.put("value_1", history.getValue(1));
        cv.put("value_2", history.getValue(2));
        cv.put("value_3", history.getValue(3));
        cv.put("value_4", history.getValue(4));
        cv.put("value_5", history.getValue(5));

        context.getContentResolver().update(Uri.parse(HistoryProvider.CONTENT_BASE_HISTORYID + "/#" + history.getId()), cv, null, null);

        return history;
    }

    /**
     * セッション単位で履歴を削除
     * @param context コンテキスト
     * @param session セッション
     */
    public static void deleteSessionHistory (Context context, RideSession session) {
        context.getContentResolver().delete(HistoryProvider.CONTENT_HISTORY, "", new String[] {String.valueOf(session.getId())});
    }

    /**
     * 削除
     */
    public static void deleteHistory(Context context, RideHistory history) {
        context.getContentResolver().delete(Uri.parse(HistoryProvider.CONTENT_BASE_HISTORYID + "/#" + history.getId()), null, null);
    }
    /**
     * セッションイテレータに使用したカーソルを閉じる
     */
    public static void closeSessionIterator() {
        if (ite_cursor != null) {
            if (!ite_cursor.isClosed()) {
                ite_cursor.close();
            }
        }
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int ret = 0;
        String id;
        switch (uriMatcher.match(uri)) {
            case TYPE_HISTORYID:
                id = uri.getFragment();//.getPathSegments().get(1);
                selection = "_ID = " + id;
                ret = db.delete(TABLE_HISTORY, selection, selectionArgs);
                break;
            case TYPE_HISTORY:
                if (selection == null || selection.length() == 0) {
                    selection = "sessionid = " + selectionArgs[0];
                }
                ret = db.delete(TABLE_HISTORY, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    @Override
    public String getType(Uri uri) {
        String ret = null;
        switch (uriMatcher.match(uri)) {
            case TYPE_HISTORY:
                ret = "vnd.android.cursor.dir/history";
                break;
            case TYPE_HISTORYID:
                ret = "vnd.android.cursor.item/history";
                break;
        }
        return ret;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;
        Uri content = CONTENT_HISTORY;
        String table = TABLE_HISTORY;

        long row = db.insert(table, null, values);
        if (row > 0) {
            ret = ContentUris.withAppendedId(content, row);
            getContext().getContentResolver().notifyChange(ret, null);
        }
        return ret;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        HistoryProvider.DBHelper innerHelper = new HistoryProvider.DBHelper(context);
        db = innerHelper.getWritableDatabase();
        innerHelper.onCreate(db);
        return (db != null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        String id;
        String table = null;
        switch (uriMatcher.match(uri)) {
            case TYPE_HISTORY:
                table = TABLE_HISTORY;
                builder.setTables(table);
                String appendWhere = "sessionid = " + selectionArgs[0];
                builder.appendWhere(appendWhere);
                break;
            case TYPE_HISTORYID:
                id = uri.getFragment();//uri.getPathSegments().get(1);
                table = TABLE_HISTORY;
                builder.setTables(table);
                builder.appendWhere("_ID = " + id);
                break;
        }

        if (sortOrder == null || sortOrder == "") {
            sortOrder = "_ID";
        }
        Cursor ret = builder.query(db, projection, selection, null, null, null, sortOrder);
        ret.setNotificationUri(getContext().getContentResolver(), uri);
        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int ret = 0;
        String id;
        switch (uriMatcher.match(uri)) {
            case TYPE_HISTORYID:
                id = values.getAsString("_id");
                selection = "_ID = " + id;
                ret = db.update(TABLE_HISTORY, values, selection, selectionArgs);
                break;
            case TYPE_HISTORY:
                ret = db.update(TABLE_HISTORY, values, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    /**
     * DBヘルバー
     */
    private static class DBHelper extends SQLiteOpenHelper {
        /**
         * バイク情報を保持するテーブル
         */
        private static final String SQL_CREATE_TABLE_HISTORY =
                "CREATE TABLE History (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "sessionid INTEGER," +
                        "historytype INTEGER," +
                        "sensortype INTEGER," +
                        "historystamp INTEGER," +
                        "value_0 REAL," +
                        "value_1 REAL," +
                        "value_2 REAL," +
                        "value_3 REAL," +
                        "value_4 REAL," +
                        "value_5 REAL" +
                        ");";

        /**
         * コンストラクタ
         *
         * @param context コンテキスト
         */
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try {
                sqLiteDatabase.execSQL(SQL_CREATE_TABLE_HISTORY);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            // とりあえず何もしない
        }
    }

}
