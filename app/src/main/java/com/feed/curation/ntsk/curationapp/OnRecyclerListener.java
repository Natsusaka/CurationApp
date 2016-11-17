package com.feed.curation.ntsk.curationapp;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.View;


/**
 * Created by NTSK on 2016/11/12.
 */

public interface OnRecyclerListener {
    // OnItemClickListenerのようなものが無いので自分で作る

    void onRecyclerClicked(View v, int position);
}
