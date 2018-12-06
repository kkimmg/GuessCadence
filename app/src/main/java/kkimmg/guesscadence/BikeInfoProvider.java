package kkimmg.guesscadence;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * セットアップ情報の格納、呼び出し、および削除を行う。
 */
public class BikeInfoProvider extends ContentProvider {
    /**
     * 自転車の情報
     */
    public static final int BIKE_MASTER = 0;
    /**
     * 自転車の履歴情報
     */
    public static final int BIKE_HISTORY = -1;
    /**
     * 値
     */
    public static final int SETUPTYPE_VALUE = 0;
    /**
     * 初期値
     */
    public static final int SETUPTYPE_INITIAL = 1;
    /**
     * プロバイダ名
     */
    public static final String PROVIDER_NAME = "kkimmg.guesscadence.BikeInfoProvider";
    /**
     * テーブル名（自転車）
     */
    public static final String TABLE_BIKEINFO = "BIKEINFO";
    /**
     * テーブル名（セットアップ情報）
     */
    public static final String TABLE_SETUPINFO = "SETUPINFO";
    /**
     * IDを表す文字列
     */
    public static final String CONST_ID = "ID";
    /**
     * URIの元情報（バイク情報）
     */
    public static final String CONTENT_BASE_BIKEINFO = "content://" + PROVIDER_NAME + "/" + TABLE_BIKEINFO;
    /**
     * URIの元情報（セットアップ情報）
     */
    public static final String CONTENT_BASE_SETUPINFO = "content://" + PROVIDER_NAME + "/" + TABLE_SETUPINFO;
    /**
     * URIの元情報（バイク情報（ID指定））
     */
    public static final String CONTENT_BASE_BIKEINFOID = "content://" + PROVIDER_NAME + "/" + TABLE_BIKEINFO + CONST_ID;
    /**
     * URIの元情報（セットアップ情報（ID指定））
     */
    public static final String CONTENT_BASE_SETUPINFOID = "content://" + PROVIDER_NAME + "/" + TABLE_SETUPINFO + CONST_ID;
    /**
     * URI（バイク情報）
     */
    public static final Uri CONTENT_BIKEINFO = Uri.parse(CONTENT_BASE_BIKEINFO);
    /**
     * URI（セットアップ情報）
     */
    public static final Uri CONTENT_SETUPINFO = Uri.parse(CONTENT_BASE_SETUPINFO);
    /**
     * URI（バイク情報（ID指定））
     */
    public static final Uri CONTENT_BIKEINFOID = Uri.parse(CONTENT_BASE_BIKEINFOID);
    /**
     * URI（セットアップ情報（ID指定））
     */
    public static final Uri CONTENT_SETUPINFOID = Uri.parse(CONTENT_BASE_SETUPINFOID);
    /**
     * データベースファイル名
     */
    private static final String DATABASE_NAME = "bikes.db";
    /**
     * データベースバージョン（レイアウト変更時この数値を変更）</br>
     * 履歴</br>
     * 1:初期バージョン</br>
     */
    private static final int DB_VERSION = 1;
    /**
     * データアクセス方法（バイク情報）
     */
    private static final int TYPE_BIKEINFO = 0;
    /**
     * データアクセス方法（バイク情報（ID指定））
     */
    private static final int TYPE_BIKEINFOID = 1;
    /**
     * データアクセス方法（セットアップ情報）
     */
    private static final int TYPE_SETUPINFO = 2;
    /**
     * データアクセス方法（セットアップ情報（ID指定））
     */
    private static final int TYPE_SETUPINFOID = 3;
    /**
     * URI特定
     */
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, TABLE_BIKEINFO, TYPE_BIKEINFO);
        uriMatcher.addURI(PROVIDER_NAME, TABLE_SETUPINFO, TYPE_SETUPINFO);
        uriMatcher.addURI(PROVIDER_NAME, TABLE_BIKEINFO + CONST_ID, TYPE_BIKEINFOID);
        uriMatcher.addURI(PROVIDER_NAME, TABLE_SETUPINFO + CONST_ID, TYPE_SETUPINFOID);
    }

    /**
     * データベースインスタンス
     */
    private SQLiteDatabase db;

   /**
     * バイク情報の追加
     */
    public static BikeInfo insertBikeInfo(Context context, BikeInfo bikeInfo) {
        return BikeInfoProvider.insertBikeInfo(context, bikeInfo, RideSession.OUT_OF_SESSION_ID);
    }
    /**
     * バイク情報の追加
     */
    public static BikeInfo insertBikeInfo(Context context, BikeInfo bikeInfo, long sessionID) {
        // バイク情報
        ContentValues bikeValues = new ContentValues();
        bikeValues.put("name", bikeInfo.getName());
        bikeValues.put("weight", bikeInfo.getWeight());
        bikeValues.put("weightunit", bikeInfo.getWeightUnit());
        if (bikeInfo.isMasterData()) {
            bikeValues.put("masterdata", BikeInfoProvider.BIKE_MASTER);
        } else {
            bikeValues.put("masterdata", BikeInfoProvider.BIKE_HISTORY);
        }
        bikeValues.put("sessionid", sessionID);
        Uri uri = context.getContentResolver().insert(BikeInfoProvider.CONTENT_BIKEINFO, bikeValues);
        long row = ContentUris.parseId(uri);
        bikeInfo.setId(row);
        // セットアップ情報
        SetUpInfo setUpInfo = bikeInfo.getSetUpInfo();
        insertSetUpInfo(context, setUpInfo, row, SETUPTYPE_VALUE);
        // 初期セットアップ情報
        SetUpInfo initialInfo = setUpInfo.getInitialValue();
        ContentValues initialValues = new ContentValues();
        insertSetUpInfo(context, initialInfo, row, SETUPTYPE_INITIAL);

        return bikeInfo;
    }

    /**
     * セットアップ情報の追加
     */
    public static SetUpInfo insertSetUpInfo(Context context, SetUpInfo setUpInfo, long bikeId, int setUpType) {
        ContentValues setUpValues = new ContentValues();
        setUpValues.put("bikeinfo_id", bikeId);
        setUpValues.put("pitch", setUpInfo.getPitch());
        setUpValues.put("pitchunit", setUpInfo.getPitchUnit());
        setUpValues.put("roll", setUpInfo.getRoll());
        setUpValues.put("orientation", setUpInfo.getOrientation());
        setUpValues.put("location", setUpInfo.getSetUpLocation());
        setUpValues.put("locked", (setUpInfo.isLocked() ? 1 : 0));
        setUpValues.put("setuptype", setUpType);
        Uri uri = context.getContentResolver().insert(BikeInfoProvider.CONTENT_SETUPINFO, setUpValues);
        long row = ContentUris.parseId(uri);
        setUpInfo.setId(row);
        return setUpInfo;
    }

    /**
     * バイク情報の修正
     */
    public static BikeInfo updateBikeInfo(Context context, BikeInfo bikeInfo) {
        return BikeInfoProvider.updateBikeInfo(context, bikeInfo, RideSession.OUT_OF_SESSION_ID);
    }
    /**
     * バイク情報の修正
     */
    public static BikeInfo updateBikeInfo(Context context, BikeInfo bikeInfo, long sessionID) {
        if (bikeInfo.getId() <= 0) {
            return insertBikeInfo(context, bikeInfo);
        }
        // バイク情報
        ContentValues bikeValues = new ContentValues();
        bikeValues.put("_id", bikeInfo.getId());
        bikeValues.put("name", bikeInfo.getName());
        bikeValues.put("weight", bikeInfo.getWeight());
        bikeValues.put("weightunit", bikeInfo.getWeightUnit());
        if (bikeInfo.isMasterData()) {
            bikeValues.put("masterdata", BikeInfoProvider.BIKE_MASTER);
        } else {
            bikeValues.put("masterdata", BikeInfoProvider.BIKE_HISTORY);
        }
        bikeValues.put("sessionid", sessionID);
        context.getContentResolver().update(Uri.parse(BikeInfoProvider.CONTENT_BIKEINFOID + "/#" + bikeInfo.getId()), bikeValues, null, null);
        // セットアップ情報
        SetUpInfo setUpInfo = bikeInfo.getSetUpInfo();
        updateSetUpInfo(context, setUpInfo, bikeInfo.getId(), SETUPTYPE_VALUE);
        // 初期セットアップ情報
        SetUpInfo initialInfo = setUpInfo.getInitialValue();
        ContentValues initialValues = new ContentValues();
        updateSetUpInfo(context, initialInfo, bikeInfo.getId(), SETUPTYPE_INITIAL);

        return bikeInfo;
    }

    /**
     * セットアップ情報の修正
     */
    public static SetUpInfo updateSetUpInfo(Context context, SetUpInfo setUpInfo, long bikeId, int setUpType) {
        if (setUpInfo.getId() <= 0) {
            return insertSetUpInfo(context, setUpInfo, bikeId, setUpType);
        }
        ContentValues setUpValues = new ContentValues();
        setUpValues.put("_id", setUpInfo.getId());
        setUpValues.put("bikeinfo_id", bikeId);
        setUpValues.put("pitch", setUpInfo.getPitch());
        setUpValues.put("pitchunit", setUpInfo.getPitchUnit());
        setUpValues.put("roll", setUpInfo.getRoll());
        setUpValues.put("orientation", setUpInfo.getOrientation());
        setUpValues.put("location", setUpInfo.getSetUpLocation());
        setUpValues.put("locked", (setUpInfo.isLocked() ? 1 : 0));
        setUpValues.put("setuptype", setUpType);
        context.getContentResolver().update(Uri.parse(BikeInfoProvider.CONTENT_SETUPINFOID + "/#" + setUpInfo.getId()), setUpValues, null, null);
        return setUpInfo;
    }

    /**
     * バイク情報の削除
     */
    public static void deleteBikeInfo(Context context, BikeInfo bikeInfo) {
        // バイク情報
        context.getContentResolver().delete(Uri.parse(BikeInfoProvider.CONTENT_BIKEINFOID + "/#" + bikeInfo.getId()), null, null);
        // セットアップ情報
        context.getContentResolver().delete(BikeInfoProvider.CONTENT_SETUPINFO, "", new String[]{String.valueOf(bikeInfo.getId())});
    }

    /**
     * デフォルトのバイク情報
     */
    public static BikeInfo getDefaultBikeInfo(Context context) {
        BikeInfo ret = new BikeInfo();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Long id = prefs.getLong(BikeInfo.DEFAULT_BIKEINFO_KEY, BikeInfo.DEFAULT_BIKEINFO_ID);
        ret.setId(id);
        ret = getBikeInfo(context, ret);

        return ret;
    }

    /**
     * バイク情報の取得
     */
    public static BikeInfo getBikeInfo(Context context, BikeInfo bikeInfo) {
        Cursor cursor1 = context.getContentResolver().query(Uri.parse(BikeInfoProvider.CONTENT_BASE_BIKEINFOID + "#" + bikeInfo.getId()), null, null, null, null);
        if (cursor1 != null) {
            if (cursor1.moveToNext()) {
                bikeInfo.setName(cursor1.getString(cursor1.getColumnIndex("name")));
                bikeInfo.setWeightUnit(cursor1.getInt(cursor1.getColumnIndex("weightunit")));
                bikeInfo.setWeight(cursor1.getDouble(cursor1.getColumnIndex("weight")));
            }
            cursor1.close();
            Cursor cursor2 = context.getContentResolver().query(Uri.parse(BikeInfoProvider.CONTENT_BASE_SETUPINFO), null, null, new String[]{String.valueOf(bikeInfo.getId())}, null);
            if (cursor2 != null) {
                while (cursor2.moveToNext()) {
                    int setuptype = cursor2.getInt(cursor2.getColumnIndex("setuptype"));
                    SetUpInfo info = (setuptype == SETUPTYPE_VALUE ? bikeInfo.getSetUpInfo() : bikeInfo.getSetUpInfo().getInitialValue());
                    info.setId(cursor2.getLong(cursor2.getColumnIndex("_id")));
                    info.setPitchUnit(cursor2.getInt(cursor2.getColumnIndex("pitchunit")));
                    info.setPitch(cursor2.getFloat(cursor2.getColumnIndex("pitch")));
                    info.setRoll(cursor2.getFloat(cursor2.getColumnIndex("roll")));
                    info.setSetUpLocation(cursor2.getInt(cursor2.getColumnIndex("location")));
                    info.setOrientation(cursor2.getInt(cursor2.getColumnIndex("orientation")));
                    info.setLocked((cursor2.getInt(cursor2.getColumnIndex("locked")) == 0 ? false : true));
                }
            }
            cursor2.close();
        }

        return bikeInfo;
    }

    /**
     * バイク情報のリスト取得
     */
    public static List<BikeInfo> getBikeInfoList(Context context) {
        List<BikeInfo> ret = new ArrayList<BikeInfo>();
        Cursor cursor1 = context.getContentResolver().query(Uri.parse(BikeInfoProvider.CONTENT_BASE_BIKEINFO), null, null, null, null);
        if (cursor1 != null) {
            if (cursor1.moveToNext()) {
                BikeInfo info = new BikeInfo();
                info.setId(cursor1.getLong(cursor1.getColumnIndex("_id")));
                info = getBikeInfo(context, info);
                ret.add(info);
            }
            cursor1.close();
        }
        return ret;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int ret = 0;
        String id;
        switch (uriMatcher.match(uri)) {
            case TYPE_BIKEINFOID:
                id = uri.getFragment();//.getPathSegments().get(1);
                selection = "_ID = " + id;
                ret = db.delete(TABLE_BIKEINFO, selection, selectionArgs);
                break;
            case TYPE_BIKEINFO:
                ret = db.delete(TABLE_BIKEINFO, selection, selectionArgs);
                break;
            case TYPE_SETUPINFOID:
                id = uri.getFragment();//.getPathSegments().get(1);
                selection = "_ID = " + id;
                ret = db.delete(TABLE_SETUPINFO, selection, null);
                break;
            case TYPE_SETUPINFO:
                selection = "bikeinfo_id = " + selectionArgs[0];
                ret = db.delete(TABLE_SETUPINFO, selection, null);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    @Override
    public String getType(Uri uri) {
        String ret = null;
        switch (uriMatcher.match(uri)) {
            case TYPE_BIKEINFO:
                ret = "vnd.android.cursor.dir/bikeinfo";
                break;
            case TYPE_BIKEINFOID:
                ret = "vnd.android.cursor.item/bikeinfo";
                break;
            case TYPE_SETUPINFO:
                ret = "vnd.android.cursor.dir/setupinfo";
                break;
            case TYPE_SETUPINFOID:
                ret = "vnd.android.cursor.item/setupinfo";
                break;
        }
        return ret;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;
        Uri content = null;
        String table = null;
        switch (uriMatcher.match(uri)) {
            case TYPE_BIKEINFO:
            case TYPE_BIKEINFOID:
                content = CONTENT_BIKEINFO;
                table = TABLE_BIKEINFO;
                break;
            case TYPE_SETUPINFO:
            case TYPE_SETUPINFOID:
                content = CONTENT_SETUPINFO;
                table = TABLE_SETUPINFO;
                break;
        }
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
        DBHelper innerHelper = new DBHelper(context);
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
            case TYPE_BIKEINFO:
                table = TABLE_BIKEINFO;
                builder.setTables(table);
                builder.appendWhere("masterdata = " + BikeInfoProvider.BIKE_MASTER);
                break;
            case TYPE_BIKEINFOID:
                id = uri.getFragment();//uri.getPathSegments().get(1);
                table = TABLE_BIKEINFO;
                builder.setTables(table);
                builder.appendWhere("_ID = " + id);
                break;
            case TYPE_SETUPINFO:
                table = TABLE_SETUPINFO;
                builder.setTables(table);
                builder.appendWhere("bikeinfo_id = " + selectionArgs[0]);
                break;
            case TYPE_SETUPINFOID:
                id = uri.getFragment();//.getPathSegments().get(1);
                table = TABLE_SETUPINFO;
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
            case TYPE_BIKEINFOID:
                id = values.getAsString("_id");
                selection = "_ID = " + id;
                ret = db.update(TABLE_BIKEINFO, values, selection, selectionArgs);
                break;
            case TYPE_BIKEINFO:
                ret = db.update(TABLE_BIKEINFO, values, selection, selectionArgs);
                break;
            case TYPE_SETUPINFOID:
                id = values.getAsString("_id");
                selection = "_ID = " + id;
                ret = db.update(TABLE_SETUPINFO, values, selection, selectionArgs);
                break;
            case TYPE_SETUPINFO:
                selection = "bikeinfo_id = " + selectionArgs[0];
                ret = db.update(TABLE_SETUPINFO, values, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    /**
     * バイク情報を保存する
     *
     * @param context  コンテキスト
     * @param bikeInfo バイク情報
     * @return 保存したバイク情報
     */
    public BikeInfo saveBikeInfo(Context context, BikeInfo bikeInfo) {
        if (bikeInfo.getId() <= 0) {
            insertBikeInfo(context, bikeInfo);
        } else {
            updateBikeInfo(context, bikeInfo);
        }
        return bikeInfo;
    }

    /**
     * DBヘルバー
     */
    private static class DBHelper extends SQLiteOpenHelper {
        /**
         * バイク情報を保持するテーブル
         */
        private static final String SQL_CREATE_TABLE_BIKE =
                "CREATE TABLE BikeInfo (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "weight REAL," +
                        "weightunit INTEGER," +
                        "masterdata INTEGER," +
                        "sessionid INTEGER" +
                        ");";
        /**
         * セットアップ情報を保持するテーブル
         */
        private static final String SQL_CREATE_TABLE_SETUP =
                "CREATE TABLE SetUpInfo (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "bikeinfo_id INTEGER," +
                        "setuptype INTEGER," +
                        "pitch REAL," +
                        "pitchunit INTEGER," +
                        "roll REAL," +
                        "orientation INTEGER," +
                        "location INTEGER," +
                        "locked INTEGER" +
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
                //sqLiteDatabase.execSQL("drop table BikeInfo");
                sqLiteDatabase.execSQL(SQL_CREATE_TABLE_BIKE);
                sqLiteDatabase.execSQL(SQL_CREATE_TABLE_SETUP);
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
