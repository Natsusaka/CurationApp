package com.feed.curation.ntsk.curationapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by NTSK on 2016/11/11.
 */

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    public TabFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TabFragment.newInstance("icon.jp");
            case 1:
                return TabFragment.newInstance("dtmstation.com");
            case 2:
                return TabFragment.newInstance("minet.jp");
            case 3:
                return TabFragment.newInstance("synthsonic.net");
            case 4:
                return TabFragment.newInstance("dawsoku");
            case 5:
                return TabFragment.newInstance("audioon");
        }
        return null;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public String getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Icon";
            case 1:
                return "DTMステーション";
            case 2:
                return "MIブログ";
            case 3:
                return "SynthSonic";
            case 4:
                return "DAW速報";
            case 5:
                return "音響のまとめ";
        }
        return null;
    }

}
