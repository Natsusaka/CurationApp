package com.feed.curation.ntsk.curationapp;

import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TabFragment.RecyclerFragmentListener , TabFragment.OnPageChangeListener {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setViews();
    }



    private void setViews() {
        // パースタスクを起動する
        //RssParserTask task = new RssParserTask(this);
        //task.execute();

        FragmentManager manager = getSupportFragmentManager();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(manager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onRecyclerEvent(){
        // FlagmentとのAttachの確認に使用する
    }

    @Override
    public void onChange(int index){
        /*Fragment更新の際に、Fragmentから呼ばれで起動する。
        * Viewを再度生成してFragmentを更新する*/
        setViews();
    }

}
