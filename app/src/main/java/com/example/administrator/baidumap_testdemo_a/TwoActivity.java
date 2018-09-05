package com.example.administrator.baidumap_testdemo_a;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;


import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;


import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.baidumap_testdemo_a.MyUtil.MyToast;
import com.example.administrator.baidumap_testdemo_a.adapter.pager_pager;




public class TwoActivity extends AppCompatActivity {
    private ViewPager vp;
    private Button button;
    private Fragment[] frags;
    private LinearLayout dot1;
    private ImageView[] dot_img;
    private String aString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zong);
        if(ContextCompat.checkSelfPermission(TwoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(TwoActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
        }else {
            MyToast.newToast(TwoActivity.this, "已开启定位权限");

//W
            getpermission();
        }
        SharedPreferences ii = getSharedPreferences("iss", MODE_PRIVATE);
        SharedPreferences.Editor edit = ii.edit();
        aString = ii.getString("str", "");
        if (aString.length() > 0) {
            Intent it = new Intent(TwoActivity.this, MainActivity.class);
            startActivity(it);
            this.finish();
        }


        vp = findViewById(R.id.vp);
        dot1 = findViewById(R.id.dot);
        button=findViewById(R.id.tiyan);

        frags = new Fragment[]{new sanActivity(), new SiActivity(), new WuActivity()};
        dot_img = new ImageView[frags.length];
        pager_pager pager = new pager_pager(getSupportFragmentManager(), frags);
        vp.setAdapter(pager);
        vp.setOffscreenPageLimit(frags.length-1);

        inintdot();
        date();
        vp.setPageTransformer(true, new ViewPager.PageTransformer() {//监听翻页的动画
            @Override
            public void transformPage(@NonNull View page, float position) {
                qping(page, position);
            }
        });
        vp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {//监听滑动viewpager的事件
            @Override
            public void onPageSelected(int position) {
                if (position == frags.length - 1) {

                    dot1.setVisibility(View.GONE);//当滑到第三个页面时隐藏小图标



                } else if (position == frags.length - 2) {
                    dot1.setVisibility(View.VISIBLE);//否则出现小图标


                } else if (position == frags.length - 3) {
                    dot1.setVisibility(View.VISIBLE);//否则出现小图标


                }
                changeDot(position);
            }
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200://刚才的识别码
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户同意权限,执行我们的操作
                } else {//用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
                    MyToast.newToast(TwoActivity.this, "未开启定位权限,请手动设置开启权限");
                    getpermission();
                }
                break;
            default:
                break;

        }
    }
    public void getpermission() {
        LocationManager lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                finish();
            } else {
                // 有权限了，去放肆吧。
//                        Toast.makeText(getActivity(), "有权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            MyToast.newToast(this, "系统检测到未开启GPS定位服务");
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
    }


    private void date() {//判断是否是第一次下载


//        boolean isfer = shared.getBoolean("isfer", true);
//

//        if (isfer) {
//            //第一次进入跳转
//            editor.putBoolean("isfer", false);
//            editor.commit();
//        } else {
//            //第二次进入跳转
//            finish();
//        }

    }

    private void inintdot() {//创建下面的三个小圆点

        for (int i = 0; i < frags.length; i++) {
            dot_img[i] = new ImageView(this);
            //创建四个小图标
            if (i == 0) {
                dot_img[i].setImageResource(R.drawable.b1);
                dot_img[i].setImageResource(R.drawable.select_blue);//每个小图标都弄上一个选择器
            } else if (i == 1) {
                dot_img[i].setImageResource(R.drawable.b2);
                dot_img[i].setImageResource(R.drawable.select_two);
            } else if (i == 2) {
                dot_img[i].setImageResource(R.drawable.b3);
                dot_img[i].setImageResource(R.drawable.select_three);
            }
            dot_img[i].setPadding(20, 20, 20, 20);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
            dot1.addView(dot_img[i], params);


        }
        changeDot(0);
    }

    public void changeDot(int position) {

        for (int i = 0; i < frags.length; i++) {
            dot_img[i].setEnabled(i == position);//是否匹配


        }
    }

    public void qping(View view, float position) {//每次滑动翻页的翻页效果
        if (position >= -1 && position <= 1) {
            view.setPivotX(position > 0 ? 0 : view.getWidth() / 2);
            view.setPivotY(view.getHeight() / 2);
            view.setScaleX((float) ((1 - Math.abs(position) < 0.5) ? 0.5 : (1 - Math.abs(position))));
            view.setScaleY((float) ((1 - Math.abs(position) < 0.5) ? 0.5 : (1 - Math.abs(position))));
        }
    }

}
