package jp.tomiyama.noir.sakurainfomation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.util.Arrays;
import java.util.List;

import static jp.tomiyama.noir.sakurainfomation.Util.APP_KEY;
import static jp.tomiyama.noir.sakurainfomation.Util.CLIENT_KEY;

public class MainActivity extends AppCompatActivity {

    private SakuraAdapter adapter;
    ListView listView;

    final static String ORIGINAL_TEXT = "fKICAFHghZvmQftFoQZy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 乱数生成
//        String originalText = RandomStringUtils.randomAlphabetic(20);
//        Log.d("originalText",originalText);


        NCMB.initialize(this,APP_KEY, CLIENT_KEY);
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("SampleClass");

        listView = findViewById(R.id.listView);

        // dataフィールドが,sakura.ORIGINAL_TEXTとなっているデータを検索する条件を設定
        // アップロードは未実装なので，ORIGINAL_TEXTは使っても使わなくてもよい
        query.whereContainedInArray("data", Arrays.asList("sakura",ORIGINAL_TEXT));

        //データストアからデータを検索
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> results, NCMBException e) {
                if (e != null) {
                    //検索失敗時の処理
                    Log.d("status","NG");
                } else {
                    //検索成功時の処理
                    Log.d("status","OK");
                    Log.d("results.size()", String.valueOf(results.size()));

                    adapter = new SakuraAdapter(getApplicationContext(),R.layout.custom_list_layout,results);

                    listView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();

                }
            }
        });


    }

}
