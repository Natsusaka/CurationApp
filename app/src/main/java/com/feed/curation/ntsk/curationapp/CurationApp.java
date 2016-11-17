package com.feed.curation.ntsk.curationapp;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by NTSK on 2016/11/14.
 */

public class CurationApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
