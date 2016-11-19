package com.feed.curation.ntsk.curationapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by NTSK on 2016/11/14.
 */

public class RssParserTask extends AsyncTask<String,Integer,String> {
    private Activity mActivity;
    private RecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView = null;
    private ProgressDialog mProgressDialog;
    private OnRecyclerListener mListener = new OnRecyclerListener() {
        @Override
        public void onRecyclerClicked(View v, int position) {

        }
    };


    // コンストラクタ
    public RssParserTask(Activity activity) {
        mActivity = activity;
    }

    //　タスク実行直後にコールされる
    @Override
    protected void onPreExecute() {

    }

    // バックグラウンドでの処理を担う。タスク実行時に渡された値を引数とする
    @Override
    protected String doInBackground(String... params) {
        String result = null;
        try {
            // HTTP経由でアクセスし、InputStreamを取得
            ArrayList<String>mUrlList = new ArrayList<>();
            mUrlList.add("http://www.dtmstation.com/index.rdf");
            mUrlList.add("http://icon.jp/feed");
            mUrlList.add("http://www.minet.jp/blog/feed/");
            mUrlList.add("http://synthsonic.net/index.rdf");
            mUrlList.add("http://dawsoku.ldblog.jp/index.rdf");
            mUrlList.add("http://audioon.blog.jp/index.rdf");
            for (String mUrl : mUrlList) {
                URL url = new URL(mUrl);
                Log.d("URL",mUrl);
                InputStream is = url.openConnection().getInputStream();
                parseXml(is);
                result = mUrl;
            }
            // 元の処理(この場合、URLを1つしか指定できない。)
            //URL url = new URL("param[0]");  //paramはtask.executeする際に引数でURLを渡す。
            //InputStream is = url.openConnection().getInputStream();
            //result = parseXml(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // メインスレッド上で実行される
    @Override
    protected void onPostExecute (String result) {
        //mRecyclerView.setAdapter(result);

        //mListView.setAdapter(result);
    }


    // XMLをパースする
    public void parseXml(InputStream is)throws IOException, XmlPullParserException{
        Realm realm = Realm.getDefaultInstance();
        SimpleDateFormat pubDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        SimpleDateFormat dc_DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.ENGLISH);
        String mSitename = null;

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(is,null);
            int eventType = parser.getEventType();
            Item currentItem = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = null;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                        if (currentItem == null) {
                            // 初回読み込みの際にサイト名を取得
                            if (tag.equals("title")) {
                                mSitename = new String(parser.nextText());
                            }
                        }
                        if (tag.equals("item")) {
                            // itemタグがあれば、新規でアイテムを作成
                            currentItem = new Item();
                            currentItem.setSitename(mSitename);
                            // サイト名をセット
                        } else if (currentItem != null) {
                            if (tag.equals("title")) {
                                // 記事のタイトルを取得
                                currentItem.setTitle(parser.nextText());
                                Log.d("title",currentItem.getTitle());
                            } else if (tag.equals("description")) {
                                // 記事の概要を取得
                                currentItem.setDescription(parser.nextText());
                                Log.d("des",currentItem.getDescription());
                            /*

                            } else if (tag.equals("encoded")) {

                                // 画像のURLを取得する
                                //<![CDATA[<img src="">]]>のようになっている場合直接抽出できないため、
                                //一度文字列に変換した後に、正規表現でURLのみ抽出する。
                                String content = parser.nextText();
                                Log.d("content",content);

                                Pattern p = Pattern.compile("<\\s*img.*src\\s*=\\s*([\\\"'])?([^ \\\"']*)[^>]*>");
                                Matcher m = p.matcher(content);
                                if (m.find()) {
                                    String src = m.group(2);
                                    //currentItem.setImageUrl(src);
                                    Log.d("imgsrc" , src);
                                }

                              */

                            } else if (tag.equals("date")) {
                                // RSS1.0の場合の処理[dc:date]
                                // XmlPullParse を使っている場合 dc:date の dc は xmlPullParser.getName() では取得されない
                                Date formatdate = dc_DateFormat.parse(parser.nextText());
                                currentItem.setDate(formatdate);
                                Log.d("date", String.valueOf(formatdate));
                            } else if (tag.equals("pubDate")) {
                                // RSS2.0の場合の処理[pubDate]
                                Date formatdate = pubDateFormat.parse(parser.nextText());
                                currentItem.setDate(formatdate);
                                Log.d("date2", String.valueOf(formatdate));
                            } else if (tag.equals("link")) {
                                // 記事のURLを取得
                                String link = parser.nextText();
                                currentItem.setUrl(link);
                                // URLはユニークなのでプライマリキーとする
                                currentItem.setId(link);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        if (tag.equals("item")) {

                            // 変更操作はトランザクションの中で実行する
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(currentItem);
                            realm.commitTransaction();

                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

}
