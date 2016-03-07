package com.panchuang.locatedemo.base;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.panchuang.locatedemo.R;

public class BaseMapActivity extends Activity {
    protected BaiduMap baiduMap;
    protected MapView mapview;
    //地图缩放级别
    protected float defaultDisplayLevel = 17;
    protected ActionBar actionBar;
    protected Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        initManager();
        initData();
        getLocation();
        setContentView(R.layout.activity_main);
        init();
    }

    protected void getLocation() {

    }

    protected void initData() {
        initNetReceiver();

    }

    protected void initManager() {SDKInitializer.initialize(getApplicationContext());}
    protected void init() {
        // 设置地图级别（V2.X 3-19 V1.X 3-18）
        // ① 修改了文件的格式 优化了空间的使用（北京 110M 15M）
        // ② 增加了级别 3D效果（18 19）
        mapview = (MapView) findViewById(R.id.mapview);
        baiduMap = mapview.getMap();

        // 描述地图状态将要发生的变化 使用工厂类MapStatusUpdateFactory创建
        MapStatusUpdate mapstatusUpdate = MapStatusUpdateFactory.zoomTo(defaultDisplayLevel);// 默认的级别12
        // 设置缩放级别
        baiduMap.setMapStatus(mapstatusUpdate);
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_grey)));
//        actionBar.setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        int resId = setMenuResId();
        inflater.inflate(resId, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected int setMenuResId() {
        return R.menu.main;
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        initMenuSelect(item) ;
        return super.onMenuItemSelected(featureId, item);
    }

    protected void initMenuSelect(MenuItem item) {

    }
    protected  void initNetReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, intentFilter);
    }

    BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {

            }else {

            }
        }
    };

    @Override
    protected void onDestroy() {
        mapview.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mapview.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapview.onPause();
        super.onPause();
    }

}
