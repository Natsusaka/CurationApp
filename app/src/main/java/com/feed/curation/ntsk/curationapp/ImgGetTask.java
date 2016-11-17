package com.feed.curation.ntsk.curationapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by NTSK on 2016/11/16.
 */

public class ImgGetTask extends AsyncTask<String,Integer,Bitmap> {

    private MainActivity mActivity;
    private ImageView mImageView;


    public ImgGetTask(MainActivity activity, ImageView imageView) {
        mActivity = activity;
        mImageView = imageView;
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        String text = null;
        String url = params[0];
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
                    Log.d("imagepath" , imagePath);
                    // 画像を取得
                    connection = (HttpURLConnection)imageUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    inputStream = connection.getInputStream();
                    Bitmap tempBitmap = BitmapFactory.decodeStream(inputStream);
                    // 画像のサイズを取得し、記事内でサイズが最も大きいものをサムネイルとする
                    if (tempBitmap != null) {
                        int width = tempBitmap.getWidth();
                        int height = tempBitmap.getHeight();
                        int tempsize = width * height;

                        if (tempsize > maxImageSize) {
                            maxImageSize = tempsize;
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

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {


    }
}
