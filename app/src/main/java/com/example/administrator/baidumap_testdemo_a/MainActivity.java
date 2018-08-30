package com.example.administrator.baidumap_testdemo_a;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.administrator.baidumap_testdemo_a.MyUtil.MyToast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient;
    public BDLocationListener myListener = new MyLocationListener();
    private ImageButton bt, button, buttons, ib_Eject;
    private LatLng latLng;
    private String str;//当前位置
    private boolean isFirstLoc = true; // 是否首次定位
    private boolean isAnim = true;//选项卡是否开启
    private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求
    private static final int PRIVATE_CODE = 1315;//开启GPS权限
    private LocationManager lm;//【位置管理】
    private boolean switchMap = true;
    private RoutePlanSearch mSearch = null;//搜索接口
    private boolean isSwitchMap = true; //卫星和普通地图切换
    private boolean openTraffic = false;//是否打开交通地图信息
    //浏览路线节点相关
    int nodeIndex = -1;//节点索引,供浏览节点时使用
    RouteLine route = null;
    Button mBtnPre = null;//上一个节点
    Button mBtnNext = null;//下一个节点
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    private TextView popupText = null;
    private ImageButton nav;//开启导航
    private ImageButton heat;//开启热力图
    private EditText editSt,editEn;
    private Button drive,transit,walk;
    private Boolean ischecknav=false;
    private TextView tv_start,tv_end;
    // TODO: 2018/8/23 已完成———— 实施定位按钮，当按下定位按钮，地图进行重新定位————实施按钮进行卫星和普通地图之间的切换————防止连点造成动画加载异常
    // TODO: 2018/8/23 已完成——动态刷新，每过*秒进行刷新以达到实时定位效果，具体实现方式同按下按钮定位相同


    // TODO: 2018/8/23 待实施——进入软件进行if语句判断是否有传感器，GPS，基站，WLAN等权限，如果没有，进行动态申请跳转
    // TODO: 2018/8/23 待实施——实现按钮选择GPS，基站，WLAN进行定位
    // TODO: 2018/8/23 待实施——传感器判断，实施360°方向箭头指示
    // TODO: 2018/8/23 待实施——当多个应用启动时，内存发生饱和状态，对APP进行保活状态，防止APP被恶意杀死

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //屏幕常亮
        //    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // TODO: 2018/8/23 屏幕常亮，暂未解决~

        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        getpermission();
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
        //设定中心点坐标
        LatLng cenpt = new LatLng(37.52, 121.39);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        //地图点击事件处理
        mBaiduMap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

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

    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast.newToast(MainActivity.this, "抱歉，未找到结果");
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast.newToast(MainActivity.this, "抱歉，未找到结果");
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast.newToast(MainActivity.this, "抱歉，未找到结果");
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    //定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
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
            mBaiduMap.setMyLocationData(locData);
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
                    editSt.setText(str);
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    // 网络定位结果
                    Toast.makeText(MainActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();
                    str = location.getAddrStr();
                    editSt.setText(str);

                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    // 离线定位结果
                    Toast.makeText(MainActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();
                    editSt.setText(str);

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
        mMapView = findViewById(R.id.bmapView);
        bt = findViewById(R.id.bt);
        bt.setOnClickListener(this);
        button = findViewById(R.id.button);
        button.setOnClickListener(this);
        buttons = findViewById(R.id.buttons);
        ib_Eject = findViewById(R.id.ib_Eject);
        nav = findViewById(R.id.nav);
        heat = findViewById(R.id.heat);




        drive=findViewById(R.id.drive);
        transit=findViewById(R.id.transit);
        walk=findViewById(R.id.walk);

        mBtnPre =  findViewById(R.id.pre);
        mBtnNext = findViewById(R.id.next);
        // 处理搜索按钮响应
        editSt = findViewById(R.id.start);
        editEn = findViewById(R.id.end);

        tv_start=findViewById(R.id.tv_start);
        tv_end=findViewById(R.id.tv_end);



        drive.setVisibility(View.GONE);
        transit.setVisibility(View.GONE);
        walk.setVisibility(View.GONE);
        mBtnPre.setVisibility(View.GONE);
        mBtnNext.setVisibility(View.GONE);
        editSt.setVisibility(View.GONE);
        editEn.setVisibility(View.GONE);
        tv_start.setVisibility(View.GONE);
        tv_end.setVisibility(View.GONE);

        nav.setOnClickListener(this);
        buttons.setOnClickListener(this);
        ib_Eject.setOnClickListener(new NoDoubleListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                //选项卡动画
                if (isAnim) {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(ib_Eject, "Rotation", 0, 165, 115, 135);
                    anim.setDuration(1000);
                    anim.start();
                    //交通弹出
                    ObjectAnimator upscale1 = ObjectAnimator.ofFloat(button, "alpha", 0, 1);
                    upscale1.setDuration(222);
                    upscale1.start();
                    ObjectAnimator upXanim1 = ObjectAnimator.ofFloat(button, "TranslationX", 0, px2dp(-135), px2dp(-75), px2dp(-125));
                    upXanim1.setDuration(200);
                    upXanim1.start();
                    ObjectAnimator upYanim1 = ObjectAnimator.ofFloat(button, "TranslationY", 0, px2dp(-80), px2dp(-40), px2dp(-60));
                    upYanim1.setDuration(200);
                    upYanim1.start();
                    //复位弹出
                    ObjectAnimator upscale2 = ObjectAnimator.ofFloat(bt, "alpha", 0, 1);
                    upscale2.setDuration(222);
                    upscale2.start();
                    ObjectAnimator upYanim2 = ObjectAnimator.ofFloat(bt, "TranslationY", 0, px2dp(-180), px2dp(-120), px2dp(-160));
                    upYanim2.setDuration(600);
                    upYanim2.start();
                    //卫星弹出
                    ObjectAnimator upscale3 = ObjectAnimator.ofFloat(buttons, "alpha", 0, 1);
                    upscale3.setDuration(222);
                    upscale3.start();
                    ObjectAnimator upXanim3 = ObjectAnimator.ofFloat(buttons, "TranslationX", 0, px2dp(135), px2dp(75), px2dp(125));
                    upXanim3.setDuration(999);
                    upXanim3.start();
                    ObjectAnimator upYanim3 = ObjectAnimator.ofFloat(buttons, "TranslationY", 0, px2dp(-80), px2dp(-40), px2dp(-60));
                    upYanim3.setDuration(999);
                    upYanim3.start();
                    //热力弹出
                    ObjectAnimator upscale4 = ObjectAnimator.ofFloat(heat, "alpha", 0, 1);
                    upscale4.setDuration(222);
                    upscale4.start();
                    ObjectAnimator upXanim4 = ObjectAnimator.ofFloat(heat, "TranslationX", 0, px2dp(-75), px2dp(-35), px2dp(-75));
                    upXanim4.setDuration(400);
                    upXanim4.start();
                    ObjectAnimator upYanim4 = ObjectAnimator.ofFloat(heat, "TranslationY", 0, px2dp(-140), px2dp(-90), px2dp(-120));
                    upYanim4.setDuration(400);
                    upYanim4.start();
                    //导航弹出
                    ObjectAnimator upscale5 = ObjectAnimator.ofFloat(nav, "alpha", 0, 1);
                    upscale5.setDuration(222);
                    upscale5.start();
                    ObjectAnimator upXanim5 = ObjectAnimator.ofFloat(nav, "TranslationX", 0, px2dp(75), px2dp(45), px2dp(75));
                    upXanim5.setDuration(800);
                    upXanim5.start();
                    ObjectAnimator upYanim5 = ObjectAnimator.ofFloat(nav, "TranslationY", 0, px2dp(-140), px2dp(-90), px2dp(-120));
                    upYanim5.setDuration(800);
                    upYanim5.start();
                    isAnim = false;
                } else {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(ib_Eject, "Rotation", 135, 290, 250, 270);
                    anim.setDuration(300);
                    anim.start();
                    //交通复位
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
                    //卫星复位
                    ObjectAnimator upscale3 = ObjectAnimator.ofFloat(buttons, "alpha", 1, 0);
                    upscale3.setDuration(222);
                    upscale3.start();
                    ObjectAnimator upXanim3 = ObjectAnimator.ofFloat(buttons, "TranslationX", 0);
                    upXanim3.setDuration(300);
                    upXanim3.start();
                    ObjectAnimator upYanim3 = ObjectAnimator.ofFloat(buttons, "TranslationY", 0);
                    upYanim3.setDuration(300);
                    upYanim3.start();
                    //热力复位
                    ObjectAnimator upscale4 = ObjectAnimator.ofFloat(heat, "alpha", 1, 0);
                    upscale4.setDuration(222);
                    upscale4.start();
                    ObjectAnimator upXanim4 = ObjectAnimator.ofFloat(heat, "TranslationX", 0);
                    upXanim4.setDuration(150);
                    upXanim4.start();
                    ObjectAnimator upYanim4 = ObjectAnimator.ofFloat(heat, "TranslationY", 0);
                    upYanim4.setDuration(150);
                    upYanim4.start();
                    //导航复位
                    ObjectAnimator upscale5 = ObjectAnimator.ofFloat(nav, "alpha", 1, 0);
                    upscale5.setDuration(222);
                    upscale5.start();
                    ObjectAnimator upXanim5 = ObjectAnimator.ofFloat(nav, "TranslationX", 0);
                    upXanim5.setDuration(250);
                    upXanim5.start();
                    ObjectAnimator upYanim5 = ObjectAnimator.ofFloat(nav, "TranslationY", 0);
                    upYanim5.setDuration(250);
                    upYanim5.start();
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
                MyToast.newToast(this, str);
                break;
            case R.id.button:
                if (openTraffic) {
                    MyToast.newToast(this, "实时交通已关闭");
                    mBaiduMap.setTrafficEnabled(false);
                    openTraffic = false;
                    button.setImageResource(R.drawable.ic_rglight_close);
                } else {
                    MyToast.newToast(this, "实时交通已打开");
                    mBaiduMap.setTrafficEnabled(true);
                    openTraffic = true;
                    button.setImageResource(R.drawable.ic_rglight_open);
                }
                break;
            case R.id.buttons:
                if (isSwitchMap == true) {
                    //卫星地图
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    isSwitchMap = false;
                    buttons.setImageResource(R.drawable.ic_satellite);
                    MyToast.newToast(this, "已切换卫星地图");
                } else {
                    //普通地图
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    isSwitchMap = true;
                    buttons.setImageResource(R.drawable.ic_ordinary);
                    MyToast.newToast(this, "已切换普通地图");
                }
                break;
            case R.id.nav:
                if(ischecknav){
                    drive.setVisibility(View.GONE);
                    transit.setVisibility(View.GONE);
                    walk.setVisibility(View.GONE);
                    mBtnPre.setVisibility(View.GONE);
                    mBtnNext.setVisibility(View.GONE);
                    editSt.setVisibility(View.GONE);
                    editEn.setVisibility(View.GONE);
                    tv_start.setVisibility(View.GONE);
                    tv_end.setVisibility(View.GONE);
                    nav.setImageResource(R.drawable.ic_nav_close);
                    ischecknav=false;
                }else{
                    drive.setVisibility(View.VISIBLE);
                    transit.setVisibility(View.VISIBLE);
                    walk.setVisibility(View.VISIBLE);
                    mBtnPre.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);
                    editSt.setVisibility(View.VISIBLE);
                    editEn.setVisibility(View.VISIBLE);
                    tv_start.setVisibility(View.VISIBLE);
                    tv_end.setVisibility(View.VISIBLE);
                    nav.setImageResource(R.drawable.ic_nav_open);
                    ischecknav=true;
                }
                break;
        }
    }


    //切换普通地图
    private void switchComMap(ImageButton ib) {
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        ib.setImageResource(R.drawable.ic_ordinary);
        MyToast.newToast(this, "已切换普通地图");
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

    //px转dp
    private int px2dp(float pxValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (pxValue * scale + 0.5f);
    }

    public void getpermission() {
        lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
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
            Toast.makeText(this, "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
    }


    /**
     * 发起路线规划搜索示例
     *
     * @param v
     */
    public void SearchButtonProcess(View v) {
        //重置浏览节点的路线数据
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaiduMap.clear();
        //设置起终点信息，对于tranist search 来说，城市名无意义
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("烟台", editSt.getText().toString());
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("烟台", editEn.getText().toString());

        // 实际使用中请对起点终点城市进行正确的设定
        if (v.getId() == R.id.drive) {
            mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
        } else if (v.getId() == R.id.transit) {
            mSearch.transitSearch((new TransitRoutePlanOption())
                    .from(stNode)
                    .city("烟台")
                    .to(enNode));
        } else if (v.getId() == R.id.walk) {
            mSearch.walkingSearch((new WalkingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
        }
    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        if (route == null ||
                route.getAllStep() == null) {
            return;
        }
        if (nodeIndex == -1 && v.getId() == R.id.pre) {
            return;
        }
        //设置节点索引
        if (v.getId() == R.id.next) {
            if (nodeIndex < route.getAllStep().size() - 1) {
                nodeIndex++;
            } else {
                return;
            }
        } else if (v.getId() == R.id.pre) {
            if (nodeIndex > 0) {
                nodeIndex--;
            } else {
                return;
            }
        }
        //获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        //移动节点至中心
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(MainActivity.this);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);

    }

    /**
     * 切换路线图标，刷新地图使其生效
     * 注意： 起终点图标使用中心对齐.
     */
    public void changeRouteIcon(View v) {
        if (routeOverlay == null) {
            return;
        }
        if (useDefaultIcon) {
            ((Button) v).setText("自定义起终点图标");
            Toast.makeText(this,
                    "将使用系统起终点图标",
                    Toast.LENGTH_SHORT).show();

        } else {
            ((Button) v).setText("系统起终点图标");
            Toast.makeText(this,
                    "将使用自定义起终点图标",
                    Toast.LENGTH_SHORT).show();

        }
        useDefaultIcon = !useDefaultIcon;
        routeOverlay.removeFromMap();
        routeOverlay.addToMap();
    }


}


















