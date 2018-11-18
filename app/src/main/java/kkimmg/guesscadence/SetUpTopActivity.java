package kkimmg.guesscadence;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class SetUpTopActivity extends AppCompatActivity {
    /** セットアップ情報 */
    private SetUpInfo setUpInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_top);

        try {
            setUpInfo = (SetUpInfo) getIntent().getSerializableExtra(SetUpBikeActivity.KEY_SETUPINFO);
        } catch (Exception ex) {
            setUpInfo = new SetUpInfo();
        }

        SetUpTopView setUpTopView = (SetUpTopView)findViewById(R.id.vwSetUpTopView2);
        setUpTopView.setSetUpInfo(setUpInfo);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                // 戻るボタンが押された場合

                // 結果を設定
                Intent intent = new Intent();
                intent.putExtra(SetUpBikeActivity.KEY_SETUPINFO, setUpInfo);
                setResult(RESULT_OK, intent);
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
