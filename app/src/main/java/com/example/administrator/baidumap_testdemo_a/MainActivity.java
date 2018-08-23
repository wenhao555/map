package com.example.administrator.baidumap_testdemo_a;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient;
    public BDLocationListener myListener = new MyLocationListener();
    private ImageButton bt, button, buttons, ib_Eject;
    private LatLng latLng;
    private String str;//当前位置
    private boolean isFirstLoc = true; // 是否首次定位
    private boolean isAnim = true;//选项卡是否开启

    // TODO: 2018/8/23 已完成———— 实施定位按钮，当按下定位按钮，地图进行重新定位————实施按钮进行卫星和普通地图之间的切换————防止连点造成动画加载异常


    // TODO: 2018/8/23 待实施——进入软件进行if语句判断是否有传感器，GPS，基站，WLAN等权限，如果没有，进行动态申请跳转
    // TODO: 2018/8/23 待实施——动态刷新，每过*秒进行刷新以达到实时定位效果，具体实现方式同按下按钮定位相同
    // TODO: 2018/8/23 待实施——实现按钮选择GPS，基站，WLAN进行定位
    // TODO: 2018/8/23 待实施——传感器判断，实施360°方向箭头指示
    // TODO: 2018/8/23 待实施——当多个应用启动时，内存发生饱和状态，对APP进行保活状态，防止APP被恶意杀死


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // TODO: 2018/8/23 屏幕常亮，暂未解决~

        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initView();
        initMap();
        // TODO: 2018/8/23 switch 控件设置
        //去掉百度logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
            //去掉地图上比例尺
            mMapView.showScaleControl(false);
            // 隐藏缩放控件
            mMapView.showZoomControls(false);
        }
    }


    private void initMap() {
        //获取地图控件引用
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        //默认显示普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启交通图
//        mBaiduMap.setTrafficEnabled(true);
        //开启热力图
//        mBaiduMap.setBaiduHeatMapEnabled(true);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        //配置定位SDK参数
        initLocation();
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        //开启定位
        mLocationClient.start();
        //图片点击事件，回到定位点
        mLocationClient.requestLocation();
    }

    //配置定位SDK参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        option.setOpenGps(true); // 打开gps

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    //实现BDLocationListener接口,BDLocationListener为结果监听接口，异步获取定位结果
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据
//            mBaiduMap.setMyLocationData(locData);
            // 当不需要定位图层时关闭定位图层
//            mBaiduMap.setMyLocationEnabled(false);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    // GPS定位结果
                    Toast.makeText(MainActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    // 网络定位结果
                    Toast.makeText(MainActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();
                    str = location.getAddrStr();

                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    // 离线定位结果
                    Toast.makeText(MainActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();

                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    Toast.makeText(MainActivity.this, "服务器错误，请检查", Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    Toast.makeText(MainActivity.this, "网络错误，请检查", Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    Toast.makeText(MainActivity.this, "手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        bt = (ImageButton) findViewById(R.id.bt);
        bt.setOnClickListener(this);
        button = (ImageButton) findViewById(R.id.button);
        button.setOnClickListener(this);
        buttons = (ImageButton) findViewById(R.id.buttons);
        ib_Eject = findViewById(R.id.ib_Eject);
        buttons.setOnClickListener(this);
        ib_Eject.setOnClickListener(new NoDoubleListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                //选项卡动画
                if (isAnim) {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(ib_Eject, "Rotation", 0, 165, 115, 135);
                    anim.setDuration(1000);
                    anim.start();
                    //卫星弹出
                    ObjectAnimator upscale1 = ObjectAnimator.ofFloat(button, "alpha", 0, 1);
                    upscale1.setDuration(222);
                    upscale1.start();
                    ObjectAnimator upXanim1 = ObjectAnimator.ofFloat(button, "TranslationX", 0, -130, -70, -100);
                    upXanim1.setDuration(333);
                    upXanim1.start();
                    ObjectAnimator upYanim1 = ObjectAnimator.ofFloat(button, "TranslationY", 0, -100, -60, -80);
                    upYanim1.setDuration(333);
                    upYanim1.start();
                    //复位弹出
                    ObjectAnimator upscale2 = ObjectAnimator.ofFloat(bt, "alpha", 0, 1);
                    upscale2.setDuration(222);
                    upscale2.start();
                    ObjectAnimator upYanim2 = ObjectAnimator.ofFloat(bt, "TranslationY", 0, -180, -120, -150);
                    upYanim2.setDuration(666);
                    upYanim2.start();
                    //普通弹出
                    ObjectAnimator upscale3 = ObjectAnimator.ofFloat(buttons, "alpha", 0, 1);
                    upscale3.setDuration(222);
                    upscale3.start();
                    ObjectAnimator upXanim3 = ObjectAnimator.ofFloat(buttons, "TranslationX", 0, 130, 80, 115, 100);
                    upXanim3.setDuration(888);
                    upXanim3.start();
                    ObjectAnimator upYanim3 = ObjectAnimator.ofFloat(buttons, "TranslationY", 0, -100, -60, -90, -80);
                    upYanim3.setDuration(888);
                    upYanim3.start();
                    isAnim = false;
                } else {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(ib_Eject, "Rotation", 135, 290, 250, 270);
                    anim.setDuration(300);
                    anim.start();
                    //卫星复位
                    ObjectAnimator upscale1 = ObjectAnimator.ofFloat(button, "alpha", 1, 0);
                    upscale1.setDuration(222);
                    upscale1.start();
                    ObjectAnimator upXanim1 = ObjectAnimator.ofFloat(button, "TranslationX", 0);
                    upXanim1.setDuration(100);
                    upXanim1.start();
                    ObjectAnimator upYanim1 = ObjectAnimator.ofFloat(button, "TranslationY", 0);
                    upYanim1.setDuration(100);
                    upYanim1.start();
                    //复位复位
                    ObjectAnimator upscale2 = ObjectAnimator.ofFloat(bt, "alpha", 1, 0);
                    upscale2.setDuration(222);
                    upscale2.start();
                    ObjectAnimator upYanim2 = ObjectAnimator.ofFloat(bt, "TranslationY", 0);
                    upYanim2.setDuration(200);
                    upYanim2.start();
                    //普通复位
                    ObjectAnimator upscale3 = ObjectAnimator.ofFloat(buttons, "alpha", 1, 0);
                    upscale3.setDuration(222);
                    upscale3.start();
                    ObjectAnimator upXanim3 = ObjectAnimator.ofFloat(buttons, "TranslationX", 0);
                    upXanim3.setDuration(300);
                    upXanim3.start();
                    ObjectAnimator upYanim3 = ObjectAnimator.ofFloat(buttons, "TranslationY", 0);
                    upYanim3.setDuration(300);
                    upYanim3.start();
                    isAnim = true;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt:
                //把定位点再次显现出来
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(mapStatusUpdate);
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                break;
            case R.id.button:
                //卫星地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.buttons:
                //普通地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
        }
    }


    //防连点内部类

    public abstract class NoDoubleListener implements View.OnClickListener {
        public static final int MIN_CLICK_DELAY_TIME = 500;
        private static final String TAG = "NoDoubleListener";
        private long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                onNoDoubleClick(v);
            }
        }

        protected abstract void onNoDoubleClick(View v);
    }


}