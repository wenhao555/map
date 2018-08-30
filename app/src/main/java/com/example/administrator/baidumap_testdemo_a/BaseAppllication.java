package com.example.administrator.baidumap_testdemo_a;

import android.app.Application;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

public class BaseAppllication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {

            ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            appInfo.metaData.putString("com.baidu.lbsapi.API_KEY","");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
