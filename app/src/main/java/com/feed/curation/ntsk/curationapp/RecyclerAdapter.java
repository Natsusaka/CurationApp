package com.feed.curation.ntsk.curationapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by NTSK on 2016/11/12.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private LayoutInflater mInflater;
    //private ArrayList<String> mData;
    private ArrayList<Item> mData;
    private Context mContext;
    private OnRecyclerListener mListener;
    private MainActivity mainActivity;

    public RecyclerAdapter(Context context, ArrayList<Item> data, OnRecyclerListener listener) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mData = data;
        mListener = listener;
    }


    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // 表示するレイアウトを設定
        return new ViewHolder(mInflater.inflate(R.layout.list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        // データ表示
        if (mData != null && mData.size() > i && mData.get(i) != null) {
            //viewHolder.textView_main.setText(mData.get(i));
            Item item = mData.get(i);
            viewHolder.textView_main.setText(item.getTitle());
            viewHolder.textView_sub.setText(item.getSitename());

            ImgGetTask imgGetTask = new ImgGetTask(mainActivity,viewHolder.imageView);
            imgGetTask.execute(item.getUrl());
        }

        // クリック処理
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRecyclerClicked(v, i);

            }
        });

    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }

    // ViewHolder(固有ならインナークラスでOK)
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView_main;
        TextView textView_sub;
        LinearLayout layout;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView_main = (TextView)itemView.findViewById(R.id.textView_main);
            textView_sub = (TextView)itemView.findViewById(R.id.textView_sub);
            layout = (LinearLayout)itemView.findViewById(R.id.layout);
            imageView = (ImageView)itemView.findViewById(R.id.imageView);
        }
    }

}
