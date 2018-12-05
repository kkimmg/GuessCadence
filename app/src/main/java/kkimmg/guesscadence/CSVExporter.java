package kkimmg.guesscadence;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class CSVExporter implements Runnable {
    /**
     * コンテキスト
     */
    private Context context;

    /**
     * セッション
     */
    private RideSession rideSession;

    /**
     * セッション
     */
    public RideSession getRideSession() {
        return rideSession;
    }

    /**
     * セッション
     */
    public void setRideSession(RideSession rideSession) {
        this.rideSession = rideSession;
    }

    @Override
    public void run() {
        if (rideSession == null || context == null) return;

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);//getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (dir == null || !dir.canWrite()) {
            dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        }
        String outname = rideSession.getBikeInfo().getName() + "_"
                + String.valueOf(rideSession.getStart()) + "_"
                + String.valueOf(rideSession.getEnd())
                + ".csv";
        if (outname != null && outname.trim().length() > 0) {
            File outfile = new File(dir, outname);
            try {
                FileWriter fos = new FileWriter(outfile);
                BufferedWriter writer = new BufferedWriter(fos);

                Cursor cursor1 = context.getContentResolver().query(Uri.parse(SessionProvider.CONTENT_BASE_SESSIONID + "#" + rideSession.getId()), null, null, null, null);
                writeCursorData(writer, SessionProvider.TABLE_SESSION, cursor1);

                cursor1 = context.getContentResolver().query(Uri.parse(BikeInfoProvider.CONTENT_BASE_BIKEINFOID + "#" + rideSession.getInitialBikeInfo().getId()), null, null, null, null);
                writeCursorData(writer, BikeInfoProvider.TABLE_BIKEINFO, cursor1);
                cursor1 = context.getContentResolver().query(Uri.parse(BikeInfoProvider.CONTENT_BASE_SETUPINFO), null, null, new String[]{String.valueOf(rideSession.getInitialBikeInfo().getId())}, null);
                writeCursorData(writer, BikeInfoProvider.TABLE_SETUPINFO, cursor1);



            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * カーソルの書き出し
     * @param writer ライター
     * @param tableName テーブル名
     * @param cursor カーソル
     */
    private void writeCursorData(BufferedWriter writer, String tableName, Cursor cursor) {
        if (cursor == null) return;

        StringBuffer buff = new StringBuffer();
        while (cursor.moveToNext()) {
            appendColumn(buff, tableName);
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String val = cursor.getString(i);
                appendColumn(buff, val);
            }
        }
        cursor.close();
    }

    /**
     * バッファに値を追加
     * @param buff バッファ
     * @param value 値
     */
    private void appendColumn (StringBuffer buff, String value) {
        buff.append("\"");
        for (char ch : value.toCharArray()) {
            if (ch == '"') {
                buff.append("\"");
            }
            buff.append(ch);
            buff.append("\",");
        }
    }

    /**  コンテキスト */
    public Context getContext () {
        return context;
    }
    /**  コンテキスト */
    public void setContext (Context context){
        this.context = context;
    }
}

