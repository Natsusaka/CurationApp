package com.feed.curation.ntsk.curationapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by NTSK on 2016/11/16.
 */

public class ImgGetTask extends AsyncTask<String,Integer,Bitmap> {

    private MainActivity mActivity;
    private Context mContext;
    private String mUrl;


    public ImgGetTask(MainActivity activity) {
        mActivity = activity;
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        String text = null;
        String url = params[0];
        mUrl = url;
        Log.d("params", mUrl);
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        Bitmap bitmap = null;
        int maxImageSize = 0;

        try {
            // HTMLのドキュメントを取得
            Document document = Jsoup.connect(url).get();
            // ドキュメント内からimgタグのものを抽出
            Elements images = document.getElementsByTag("img");

            for (int i = 0; i < images.size(); i++) {
                // 記事内のimgタグのものを順番にbitmapで取得する
                try {
                    Element image = images.get(i);
                    // imgタグ内のsrcを抽出しURLを生成
                    String imagePath = image.attr("src");
                    URL imageUrl = new URL(imagePath);
                    // 画像を取得
                    connection = (HttpURLConnection)imageUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    inputStream = connection.getInputStream();
                    // データは読み込まず、まずサイズの情報だけ取得する
                    //BitmapFactory.Options options = new BitmapFactory.Options();
                    //options.inJustDecodeBounds = true;
                    //Bitmap tempBitmap = BitmapFactory.decodeStream(inputStream, null, options);
                    Bitmap tempBitmap = BitmapFactory.decodeStream(inputStream);
                    // 画像のサイズを取得し、記事内でサイズが最も大きいものをサムネイルとする
                    if (tempBitmap != null) {
                        int width = tempBitmap.getWidth();
                        int height = tempBitmap.getHeight();
                        //int width = options.outWidth;
                        //int height = options.outHeight;
                        int tempsize = width * height;

                        if (tempsize > maxImageSize) {
                            maxImageSize = tempsize;
                            // options.inJustDecodeBounds = false;
                            //tempBitmap = BitmapFactory.decodeStream(inputStream);
                            bitmap = tempBitmap;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("maxbitmap",String.valueOf(maxImageSize));
        return bitmap;

    }

    @Override
    protected void onPostExecute(Bitmap result) {
        try {
            /* 取得したBitmapをjpgで保存する*/
            File root = Environment.getExternalStorageDirectory();

            // 保存するファイル名を作成
            Date mDate = new Date();
            SimpleDateFormat fileNamefomat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fileName = fileNamefomat.format(mDate) + ".jpg";

            // ファイルを作成
            File saveFile = new File(root,fileName);

            // ファイルの保存
            FileOutputStream fileOutputStream = null;
            fileOutputStream = new FileOutputStream(saveFile);
            //FileOutputStream out = mContext.openFileOutput(fileName, mContext.MODE_PRIVATE);

            result.compress(Bitmap.CompressFormat.JPEG, 100 , fileOutputStream);
            Log.d("result", "resultOK");

            String savePath = saveFile.getPath();
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmQuery<Item> query = realm.where(Item.class);
            query.equalTo("mUrl",mUrl);
            RealmResults<Item> itemRealmResults = query.findAll();
            Item item = itemRealmResults.get(0);
            item.setImageUrl(savePath);
            Log.d("item.get", item.getImageUrl());
            realm.copyToRealmOrUpdate(item);
            Log.d("savePath" , savePath);
            realm.commitTransaction();

            fileOutputStream.close();


            /*File dir = new File(Environment.getExternalStorageDirectory().getPath());
            if (dir.exists()) {
                File file = new File(dir.getAbsoluteFile() + "/" + fileName);
                Log.d("read" , fileName);
                if (file.exists()) {
                    Bitmap bm = BitmapFactory.decodeFile(file.getPath());
                    //mImageView.setImageBitmap(bm);
                    Log.d("read" , "readBM");
                } else {
                    Log.d("NoBM" , "NoBM");
                }
            }else {
                Log.d("NoDir","Nodir");
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
