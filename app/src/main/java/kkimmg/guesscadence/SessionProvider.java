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
import android.util.Log;

import java.util.Iterator;

public class SessionProvider extends ContentProvider {
    /**
     * プロバイダ名
     */
    public static final String PROVIDER_NAME = "kkimmg.guesscadence.SessionProvider";
    /**
     * テーブル名（セッション）
     */
    public static final String TABLE_SESSION = "SESSION";
    /**
     * IDを表す文字列
     */
    public static final String CONST_ID = "ID";
    /**
     * URIの元情報（セッション）
     */
    public static final String CONTENT_BASE_SESSION = "content://" + PROVIDER_NAME + "/" + TABLE_SESSION;
    /**
     * URIの元情報（セッション（ID指定））
     */
    public static final String CONTENT_BASE_SESSIONID = "content://" + PROVIDER_NAME + "/" + TABLE_SESSION + CONST_ID;
    /**
     * URI（セッション）
     */
    public static final Uri CONTENT_SESSION = Uri.parse(CONTENT_BASE_SESSION);
    /**
     * URI（セッションID）
     */
    public static final Uri CONTENT_SESSIONID = Uri.parse(CONTENT_BASE_SESSIONID);
    /**
     * データベースファイル名
     */
    private static final String DATABASE_NAME = "sessions.db";
    /**
     * データベースバージョン（レイアウト変更時この数値を変更）</br>
     * 履歴</br>
     * 1:初期バージョン</br>
     */
    private static final int DB_VERSION = 1;
    /**
     * データアクセス方法（セッション）
     */
    private static final int TYPE_SESSION = 0;
    /**
     * データアクセス方法（セッション（ID指定））
     */
    private static final int TYPE_SESSIONID = 1;
    /**
     * URI特定
     */
    private static final UriMatcher uriMatcher;
    /**
     * セッションイテレータ用のカーソル
     */
    private static Cursor ite_cursor = null;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, TABLE_SESSION, TYPE_SESSION);
        uriMatcher.addURI(PROVIDER_NAME, TABLE_SESSION + CONST_ID, TYPE_SESSIONID);
    }

    /**
     * データベースインスタンス
     */
    private SQLiteDatabase db;

    /**
     * バイク情報の削除
     */
    public static void deleteSession(Context context, RideSession rideSession) {
        context.getContentResolver().delete(Uri.parse(SessionProvider.CONTENT_BASE_SESSIONID + "/#" + rideSession.getId()), null, null);
    }

    /**
     * セッションを挿入する
     *
     * @param context     コンテキスト
     * @param rideSession セッション
     * @return 挿入したセッション
     */
    public static RideSession insertSession(Context context, RideSession rideSession) {
        BikeInfo initialBikeInfo = rideSession.getBikeInfo();
        BikeInfo bikeInfo = BikeInfoProvider.insertBikeInfo(context, initialBikeInfo);

        ContentValues cv = new ContentValues();
        cv.put("startstamp", rideSession.getStart());
        cv.put("endstamp", rideSession.getEnd());
        cv.put("bikemasterid", initialBikeInfo.getId());
        cv.put("bikehistoryid", bikeInfo.getId());

        Uri uri = context.getContentResolver().insert(SessionProvider.CONTENT_SESSION, cv);
        long row = ContentUris.parseId(uri);
        rideSession.setId(row);

        // セッションIDを更新
        BikeInfoProvider.updateBikeInfo(context, bikeInfo, rideSession.getId());

        return rideSession;
    }

    /**
     * セッションを更新する
     *
     * @param context     コンテキスト
     * @param rideSession セッション
     * @return 挿入したセッション
     */
    public static RideSession updateSession(Context context, RideSession rideSession) {
        BikeInfo initialBikeInfo = rideSession.getBikeInfo();
        BikeInfo bikeInfo = BikeInfoProvider.insertBikeInfo(context, initialBikeInfo);

        ContentValues cv = new ContentValues();
        cv.put("startstamp", rideSession.getStart());
        cv.put("endstamp", rideSession.getEnd());
        cv.put("bikemasterid", initialBikeInfo.getId());
        cv.put("bikehistoryid", bikeInfo.getId());

        context.getContentResolver().update(Uri.parse(SessionProvider.CONTENT_BASE_SESSIONID + "/#" + bikeInfo.getId()), cv, null, null);

        return rideSession;
    }

    /**
     * セッションイテレータ
     */
    public static Iterator<RideSession> getSessionIterator(Context context) {
        closeSessionIterator();
        ite_cursor = context.getContentResolver().query(Uri.parse(SessionProvider.CONTENT_BASE_SESSION), null, null, null, null, null);
        SessionIterator ite = new SessionIterator(context, ite_cursor);
        return ite;
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

    /**
     * セッションの取得
     *
     * @param context コンテキスト
     * @param session セッション
     * @return セッション
     */
    public static RideSession getSession(Context context, RideSession session) {
        Cursor cursor1 = context.getContentResolver().query(Uri.parse(SessionProvider.CONTENT_BASE_SESSIONID + "#" + session.getId()), null, null, null, null);
        if (cursor1 != null) {
            if (cursor1.moveToNext()) {
                cursor2session(context, cursor1, session);
            }
            cursor1.close();
        }
        return session;
    }

    /**
     * カーソルからセッションへ転記する
     *
     * @param cursor1 カーソル
     * @param session セッション
     */
    private static void cursor2session(Context context, Cursor cursor1, RideSession session) {
        session.setStart(cursor1.getLong(cursor1.getColumnIndex("startstamp")));
        session.setEnd(cursor1.getLong(cursor1.getColumnIndex("endstamp")));
        long masterId = cursor1.getLong(cursor1.getColumnIndex("bikemasterid"));
        long historyId = cursor1.getLong(cursor1.getColumnIndex("bikehistoryid"));
        // 自転車
        BikeInfo bike = new BikeInfo();
        bike.setId(masterId);
        bike = BikeInfoProvider.getBikeInfo(context, bike);
        session.setBikeInfo(bike);
        // 自転車（履歴）
        BikeInfo history = new BikeInfo();
        history.setId(historyId);
        history = BikeInfoProvider.getBikeInfo(context, history);
        session.setInitialBikeInfo(history);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int ret = 0;
        String id;
        switch (uriMatcher.match(uri)) {
            case TYPE_SESSIONID:
                id = uri.getFragment();//.getPathSegments().get(1);
                selection = "_ID = " + id;
                ret = db.delete(TABLE_SESSION, selection, selectionArgs);
                break;
            case TYPE_SESSION:
                ret = db.delete(TABLE_SESSION, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    @Override
    public String getType(Uri uri) {
        String ret = null;
        switch (uriMatcher.match(uri)) {
            case TYPE_SESSION:
                ret = "vnd.android.cursor.dir/session";
                break;
            case TYPE_SESSIONID:
                ret = "vnd.android.cursor.item/session";
                break;
        }
        return ret;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;
        Uri content = CONTENT_SESSION;
        String table = TABLE_SESSION;

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
        SessionProvider.DBHelper innerHelper = new SessionProvider.DBHelper(context);
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
            case TYPE_SESSION:
                table = TABLE_SESSION;
                builder.setTables(table);
                break;
            case TYPE_SESSIONID:
                id = uri.getFragment();//uri.getPathSegments().get(1);
                table = TABLE_SESSION;
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
            case TYPE_SESSIONID:
                id = values.getAsString("_id");
                selection = "_ID = " + id;
                ret = db.update(TABLE_SESSION, values, selection, selectionArgs);
                break;
            case TYPE_SESSION:
                ret = db.update(TABLE_SESSION, values, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    /**
     * セッションイテレータ
     */
    public static class SessionIterator implements Iterator<RideSession> {
        /**
         * コンテクスト
         */
        private Context context;
        /**
         * カーソル
         */
        private Cursor cursor;

        /**
         * コンストラクタ
         *
         * @param cursor カーソル
         */
        public SessionIterator(Context context, Cursor cursor) {
            this.context = context;
            this.cursor = cursor;
        }

        @Override
        public boolean hasNext() {
            return !cursor.isLast() && !cursor.isAfterLast();
        }

        @Override
        public RideSession next() {
            RideSession session = null;
            if (cursor.moveToNext()) {
                session.setId(cursor.getLong(cursor.getColumnIndex("_id")));
                cursor2session(context, cursor, session);
            }
            return session;
        }
    }

    /**
     * DBヘルバー
     */
    private static class DBHelper extends SQLiteOpenHelper {
        /**
         * バイク情報を保持するテーブル
         */
        private static final String SQL_CREATE_TABLE_SESSION =
                "CREATE TABLE Session (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "startstamp INTEGER," +
                        "endstamp INTEGER," +
                        "bikemasterid INTEGER," +
                        "bikehistoryid INTEGER" +
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
                sqLiteDatabase.execSQL(SQL_CREATE_TABLE_SESSION);
            } catch (Exception ex) {
                Log.e("CREATE DB(SESSION)", ex.getLocalizedMessage(), ex);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            // とりあえず何もしない
        }
    }
}
