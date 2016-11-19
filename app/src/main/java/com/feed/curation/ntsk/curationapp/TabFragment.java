package com.feed.curation.ntsk.curationapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by NTSK on 2016/11/11.
 */

public class TabFragment extends Fragment implements OnRecyclerListener{
    private MainActivity mainActivity;
    private Realm mRealm;
    private RealmResults<Item> mItemRealmResults;
    RealmQuery<Item> query;
    private RealmChangeListener mRealmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            // 通知関連の処理
            mItemRealmResults.addChangeListener(listener);//仮
        }
    };
    private RealmChangeListener listener = new RealmChangeListener<RealmResults<Item>>() {
        @Override
        public void onChange(RealmResults<Item> results) {
            //reloadRecycleView();
            for (int i = 0; i < results.size(); i++) {
                Item item = results.get(i);
                String mUrl = item.getUrl();
                ImgGetTask imgGetTask = new ImgGetTask(mainActivity);
                imgGetTask.execute(mUrl);
                refresh();
            }

        }
    };
    private Activity mActivity;
    private  View mView;
    private RecyclerFragmentListener mFragmentListener;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private ArrayList<Item> mItemArrayList;

    //　内部インターフェース
    public interface RecyclerFragmentListener {
        // 紐尽くActivityの確認に使用
        void onRecyclerEvent();
    }

    public interface OnPageChangeListener{
        public void onChange(int index);
    }


    public static TabFragment newInstance(String queryurl) {
        Log.d("instance",queryurl);
        TabFragment flag = new TabFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url" ,queryurl);
        Log.d("bundle",queryurl);
        flag.setArguments(bundle);
        return flag;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof RecyclerFragmentListener)) {
            throw new UnsupportedOperationException(
                    "Listener is not Impletation.");
        } else {
            mFragmentListener = (RecyclerFragmentListener) activity;
        }
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初回起動時に新規Rss情報を取得
        // 以降は基本的にRealm内に格納されたデータを参照する
        //reloadRss();
    }


   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_fragment, container, false);

        // RecyclerViewの参照を取得
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        // レイアウトマネージャを設定
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //Realmの設定
        mRealm = Realm.getDefaultInstance();
        query = mRealm.where(Item.class);
        query.contains("mUrl", getArguments().getString("url"));
        mItemRealmResults = query.findAll();
        //mItemRealmResults = mRealm.where(Item.class).findAll();
        mItemRealmResults = mItemRealmResults.sort("mDate", Sort.DESCENDING);
        mRealm.addChangeListener(mRealmListener);


        // Swipe操作時に更新処理を行なうリスナーをセット
        final SwipeRefreshLayout mSwipeRefresh = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 引っ張って離した時に呼ばれる
                reloadRss();
                if (mSwipeRefresh.isRefreshing()) {
                    mSwipeRefresh.setRefreshing(false);
                }
            }
        });

        reloadRecycleView();

    }

    // RecycleViewクリック時に、ItemDetailActivityへ遷移
    @Override
    public void onRecyclerClicked(View v, int position) {
        // クリック処理
        Item clickitem = mItemArrayList.get(position);
        String itemtitle = clickitem.getTitle();
        String itemurl = clickitem.getUrl();

        Intent intent = new Intent(mActivity, ItemDetailActivity.class);
        intent.putExtra("title", itemtitle);
        intent.putExtra("url" , itemurl);
        Log.d("Click", String.valueOf(itemurl));
        startActivity(intent);
    }


    // RealmResultの中身をRecycleViewのアダプターへセット
    private void reloadRecycleView() {

        ArrayList<Item> itemArrayList = new ArrayList<>();

        for (int i =0; i < mItemRealmResults.size(); i++) {
            if (!mItemRealmResults.get(i).isValid()) continue;

            Item item = new Item();
            item.setId(mItemRealmResults.get(i).getId());
            item.setTitle(mItemRealmResults.get(i).getTitle());
            item.setDescription(mItemRealmResults.get(i).getDescription());
            item.setDate(mItemRealmResults.get(i).getDate());
            item.setSitename(mItemRealmResults.get(i).getSitename());
            item.setUrl(mItemRealmResults.get(i).getUrl());

            itemArrayList.add(item);

        }
        Log.d("itemArrayList", String.valueOf(itemArrayList));
        // クリック処理で利用するため、取得したアイテムリストをメンバ変数へ格納。
        mItemArrayList = itemArrayList;

        // ListViewと同じようにセットする
        mAdapter = new RecyclerAdapter(mActivity, itemArrayList, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    // RSSのリロード処理
    private void reloadRss() {
        // RssParserTaskを起動してRSSを取得
        RssParserTask rsstask = new RssParserTask(mActivity);
        rsstask.execute();
        /*すでにFlagmentが生成されている状態でRssリロードした場合には、
        * 以下のrefreshメソッドを実行しなければ、Viewへ反映されない*/
    }


    /*ViewPager内のFragmentを更新する場合、ViewPagerも更新しなければ反映されない。
    * このため、Activityへ一度通知を行ない、Activity側でViewを再度生成する。*/
    public void refresh() {

        Bundle bundle = getArguments();
        int index = bundle.getInt("INDEX");

        Activity activity = getActivity();
        if(activity instanceof OnPageChangeListener == false){
            System.out.println("activity unimplement OnPageChangeListener");
            return;
        }
        ((OnPageChangeListener)activity).onChange(index);
    }


}
