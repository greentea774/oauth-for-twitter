package ～;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.security.InvalidKeyException;

import static ～.OauthConnect.get_RequestToken;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        //認証開始のボタン
        findViewById(R.id.oauth_start).setOnClickListener(this);
        //ログアウトのボタン
        findViewById(R.id.oauth_logout).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //タップされたボタンのidを取得する
        int button_id = v.getId();
        //idごとに違う処理を行う
        switch (button_id){
            //認証開始が押された場合の処理
            case R.id.oauth_start:
                try {
                    //リクエストトークン取得処理
                    get_RequestToken();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                break;
            //ログアウトが押された場合の処理
            case R.id.oauth_logout:

        }
    }
}
