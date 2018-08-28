package com.example.administrator.baidumap_testdemo_a.MyUtil;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class MyToast {
    //防止Toast重复出现，造成界面紊乱，当下一个Toast出现时，替换掉上一个Toast
    private static ArrayList<Toast> toastList = new ArrayList<Toast>();

    public static void newToast(Context context, String content) {
        //先清空
        cAncelAll();
        //在弹出
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        toastList.add(toast);
        toast.show();
    }

    //清空
    public static void cAncelAll() {
        //判断 如果不为空，就关闭
        if (!toastList.isEmpty()) {
            for (Toast t : toastList) {
                t.cancel();
            }
            toastList.clear();
        }
    }
}