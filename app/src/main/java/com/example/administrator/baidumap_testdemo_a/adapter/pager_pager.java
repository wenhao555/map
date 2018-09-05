package com.example.administrator.baidumap_testdemo_a.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;





public class  pager_pager extends FragmentPagerAdapter {


    private Fragment[] fream;

    public pager_pager(FragmentManager fm,Fragment[] fream) {
        super(fm);


        this.fream = fream;
    }

    @Override
    public Fragment getItem(int i) {
            return fream[i];
    }

    @Override
    public int getCount() {
        return fream.length;
    }
}
