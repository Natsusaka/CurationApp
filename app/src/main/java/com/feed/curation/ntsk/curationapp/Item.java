package com.feed.curation.ntsk.curationapp;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by NTSK on 2016/11/14.
 */

public class Item extends RealmObject implements Serializable{
    private String mTitle; //記事のタイトル
    private String mDescription; //記事の本文
    private Date mDate; // 記事の投稿日時
    private String mUrl; //記事のURL
    private String mSitename; // サイトの名前
    private String mImageUrl;

    public Item() {
        mTitle = "";
        mDescription = "";
        mDate = null;
        mUrl = "";
        mSitename = "";
        mImageUrl = "";
    }

    @PrimaryKey
    private String id;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getSitename() {
        return mSitename;
    }

    public void setSitename(String sitename) {
        mSitename = sitename;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
