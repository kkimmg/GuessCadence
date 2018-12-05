package kkimmg.guesscadence;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class BikeListActivity extends AppCompatActivity {
    /** 選択モード */
    public static final int MODE_CHOOSE = 0;
    /** 編集モード */
    public static final int MODE_EDIT = 1;
    /** 両方モード */
    public static final int MODE_BOTH = 2;
    /** インテントキー */
    public static final String KEY_BIKEINFO = "BIKEINFO";
    /** 呼び出しアクティビティ（追加） */
    public static final int ACTIVITY_SETUPBIKE_NEW = 100;
    /** 呼び出しアクティビティ（編集） */
    public static final int ACTIVITY_SETUPBIKE_EDIT = 101;

    private void applyBikeList () {
        final ListView lstBikeInfo = findViewById(R.id.lstBikeInfo);
        final List<BikeInfo> bikeList = BikeInfoProvider.getBikeInfoList(this);
        Button btnBikeListAdd = findViewById(R.id.btnBikeListAdd);
        Button btnBikeListEdit = findViewById(R.id.btnBikeListEdit);
        Button btnBikeListDel = findViewById(R.id.btnBikeListDel);
        Button btnBikeListChoose = findViewById(R.id.btnBikeListChoose);
        Button btnBikeListCancel = findViewById(R.id.btnBikeListCancel);
        List<String> nameList = new ArrayList<>();
        for (BikeInfo info : bikeList) {
            nameList.add(info.getName());
        }
        ArrayAdapter<String> bikeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nameList);
        lstBikeInfo.setAdapter(bikeAdapter);

        btnBikeListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BikeInfo newInfo = new BikeInfo();
                Intent intent = new Intent(getApplicationContext(), kkimmg.guesscadence.SetUpBikeActivity.class);
                intent.putExtra(KEY_BIKEINFO, newInfo);
                startActivityForResult(intent, ACTIVITY_SETUPBIKE_NEW);
            }
        });
        btnBikeListEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = lstBikeInfo.getSelectedItemPosition();
                BikeInfo newInfo = bikeList.get(i);
                Intent intent = new Intent(getApplicationContext(), kkimmg.guesscadence.SetUpBikeActivity.class);
                intent.putExtra(KEY_BIKEINFO, newInfo);
                startActivityForResult(intent, ACTIVITY_SETUPBIKE_NEW);
            }
        });
        btnBikeListDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = lstBikeInfo.getSelectedItemPosition();
                final BikeInfo newInfo = bikeList.get(i);
                new AlertDialog.Builder(BikeListActivity.this)
                        .setTitle("SetUpInfo")
                        .setMessage("Save SetUp Info")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                BikeInfoProvider.deleteBikeInfo(BikeListActivity.this, newInfo);
                            }
                        }).show();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_list);

        applyBikeList();
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BikeInfo bikeInfo;
        if (requestCode == RESULT_OK) {
            switch (requestCode) {
                case ACTIVITY_SETUPBIKE_NEW:
                    bikeInfo = (BikeInfo) data.getSerializableExtra(KEY_BIKEINFO);
                    BikeInfoProvider.insertBikeInfo(this, bikeInfo);
                    applyBikeList();
                    break;
                case ACTIVITY_SETUPBIKE_EDIT:
                    bikeInfo = (BikeInfo) data.getSerializableExtra(KEY_BIKEINFO);
                    BikeInfoProvider.updateBikeInfo(this, bikeInfo);
                    applyBikeList();
                    break;
            }
        }
    }
}
