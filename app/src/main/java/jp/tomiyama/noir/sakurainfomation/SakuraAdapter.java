package jp.tomiyama.noir.sakurainfomation;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.FetchFileCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBFile;
import com.nifty.cloud.mb.core.NCMBObject;

import java.util.List;

import static jp.tomiyama.noir.sakurainfomation.Util.APP_KEY;
import static jp.tomiyama.noir.sakurainfomation.Util.CLIENT_KEY;

public class SakuraAdapter extends ArrayAdapter<NCMBObject> {

    List<NCMBObject> mObjects;

    static class ViewHolder{
        ImageView image;
        TextView name;
        ImageView like;
        TextView likeData;

        ViewHolder(View view){
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            like = view.findViewById(R.id.like);
            likeData = view.findViewById(R.id.likeData);
        }
    }

    SakuraAdapter(Context context, int resource, List<NCMBObject> objects){
        super(context,resource,objects);

        mObjects = objects;
    }

    @Override
    public NCMBObject getItem(int position) {
        return mObjects.get(position);
    }


    //画像の取得
    private void getImage(String IMAGE_FILENAME, final ViewHolder viewHolder){

//        ******* NCMB file download *******
        final NCMBFile[] file = {null};
        final Bitmap[] bMap = new Bitmap[1];

        try {
            file[0] = new NCMBFile(IMAGE_FILENAME);
            file[0].fetchInBackground(new FetchFileCallback() {
                @Override
                public void done(byte[] dataFetch, NCMBException er) {
                    if (er != null) {
                        //失敗処理
                        new AlertDialog.Builder(getContext())
                                .setTitle("Notification from NifCloud")
                                .setMessage("Error:" + er.getMessage())
                                .setPositiveButton("OK", null)
                                .show();
                    } else {
                        //成功処理
                        bMap[0] = BitmapFactory.decodeByteArray(dataFetch, 0, dataFetch.length);
                        // imageViewに表示
                        viewHolder.image.setImageBitmap(bMap[0]);
                    }
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }

//        return bMap[0];

    }

    //LIKE数の送信
    void addLike(String objectID, int likeNumber){

        //初期化
        NCMB.initialize(getContext(),APP_KEY,CLIENT_KEY);
        NCMBObject obj = new NCMBObject("SampleClass");

        //ObjectIDの設定
        obj.setObjectId(objectID);
        //Like数の登録
        obj.put("likeData", likeNumber);


        // データストアへの登録
        obj.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if(e != null){
                    //保存に失敗した場合の処理
                    Log.d("更新","NG");

                }else {
                    //保存に成功した場合の処理
                    Log.d("更新","OK");
                }
            }
        });

        //like数の送信
        try{
            obj.save();
        } catch(NCMBException error){
            NCMBError(error);
        }
    }

    //エラー処理
    private void NCMBError(NCMBException error) {

        StringBuilder sb = new StringBuilder("【Failure】 \n");
        if(error.getCode() != null){
            sb.append("StatusCode : ").append(error.getCode()).append("\n");
        }
        if (error.getMessage() != null){
            sb.append("Message : ").append(error.getMessage()).append("\n");
        }
        Log.e("error",sb.toString());
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        final ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_layout,parent,false);
            viewHolder = new ViewHolder(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final NCMBObject item = getItem(position);

        Resources r = convertView.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(r, R.drawable.heart);

        // null確認
//        Log.d("imageName", String.valueOf(getImage(item.getString("imageName"))));

        getImage(item.getString("imageName"),viewHolder); // getImageメソッドに表示まで移動
//        viewHolder.image.setImageBitmap(getImage(item.getString("imageName")));
        viewHolder.like.setImageBitmap(bitmap);
        viewHolder.name.setText(item.getString("name"));
        viewHolder.likeData.setText(item.getString("likeData"));

        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Likeを押した時のLike数の変更
                int likeNumber = Integer.parseInt(viewHolder.likeData.getText().toString()) + 1;
                viewHolder.likeData.setText(String.valueOf(likeNumber));

                // Like数の変化
                addLike(getItem(position).getObjectId(),likeNumber);

                // Likeを押した時のアニメーション
                AlphaAnimation alpha = new AlphaAnimation(1,0); // 透明度を1から0に変化させる
                alpha.setDuration(500);
                viewHolder.like.startAnimation(alpha);
            }
        });

        return convertView;
    }
}
