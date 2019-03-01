package com.example.blockcall.adapter;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.blockcall.fragment.BaseFragment;
import com.example.blockcall.fragment.BlacklistTab;
import com.example.blockcall.fragment.BlockcallTab;

public class TabAdapter extends FragmentStatePagerAdapter {


    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0 : {
                return new BlacklistTab();
//                return BaseFragment.newInstance(i);
            }
            case 1 : {
                return new BlockcallTab();
//                return BaseFragment.newInstance(i);
            }
            default : {
                return null;
            }
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0 : {
                title = "BlacklistTab";
                break;
            }
            case 1 : {
                title = "Blocked Calls";
                break;
            }
        }
        return title;
    }
}
