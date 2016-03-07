package com.panchuang.locatedemo.activity;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.panchuang.locatedemo.Global;
import com.panchuang.locatedemo.R;
import com.panchuang.locatedemo.base.BaseMapActivity;
import com.panchuang.locatedemo.bean.Connphone;
import com.panchuang.locatedemo.bean.Fence;
import com.panchuang.locatedemo.bean.GpsData;
import com.panchuang.locatedemo.utils.CommonUtils;
import com.panchuang.locatedemo.utils.LocalCacheUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseMapActivity {

    //电子围栏中心，半径
    private LatLng center;
    private int radius;
    //获取位置信息数目
    private int locationCount = 4;
    //电子围栏内部填充色
    private int fillColor = 0x44ff0000;
    //电子围栏边框颜色
    private int strokeColor = 0xffff0000;
    //电子围栏边框宽度
    private int strokeWidth = 1;
    //用户号码
    private String connphone = "13795584649";
    //加载完成标志位
    private boolean loadLocation;
    private boolean loadFence;
    //目标当前位置
    private LatLng currentLocation;
    private DisplayMode currentMode = DisplayMode.locationPoint;
    //位置信息
    private TextView title;
    //电子围栏外的位置图标
    private int[] markersId = new int[]{
            R.mipmap.icon_marka,
            R.mipmap.icon_markb,
            R.mipmap.icon_markc,
            R.mipmap.icon_markd,
            R.mipmap.icon_marke,
            R.mipmap.icon_markf,
            R.mipmap.icon_markg,
            R.mipmap.icon_markh,
            R.mipmap.icon_marki,
            R.mipmap.icon_markj,

    };
    //电子围栏内的位置图标
    private int[] markersIdGreen = new int[]{
            R.mipmap.icon_marka_green,
            R.mipmap.icon_markb_green,
            R.mipmap.icon_markc_green,
            R.mipmap.icon_markd_green,
            R.mipmap.icon_marke_green,
            R.mipmap.icon_markf_green,
            R.mipmap.icon_markg_green,
            R.mipmap.icon_markh_green,
            R.mipmap.icon_marki_green,
            R.mipmap.icon_markj_green,

    };
    private long div = 60000;
    //加载完成刷新地图
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    loadLocation = true;
                    if (loadFence && loadLocation) {
                        refreshMap();
                    }
                    break;
                case 1:
                    loadFence = true;
                    if (loadFence && loadLocation) {
                        refreshMap();
                    }
                    break;
                case 2:
                    initData();

                    break;
            }
        }
    };
    private String[] addresses;
    private String fenceResult;
    private String locationResult;
    private String connphoneResult;
    private String username;


    /**
     * 刷新地图显示
     */
    private void refreshMap() {
        if (locations == null) {
            return;
        }
        baiduMap.clear();
        // 设置中心点
        MapStatusUpdate mapStatusUpdatePoint = MapStatusUpdateFactory
                .newLatLng(currentLocation);
        baiduMap.setMapStatus(mapStatusUpdatePoint);
        //marker点击事件
        MarkListener markListener = new MarkListener();
        baiduMap.setOnMarkerClickListener(markListener);

        //添加电子围栏 圆心center 半径radius 颜色fillColor 边框strokeColor,strokeWidth
        if(center!=null){
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(center)
                    .radius(radius)
                    .fillColor(fillColor)
                    .stroke(new Stroke(strokeWidth, strokeColor));
            baiduMap.addOverlay(circleOptions);
        }



        //根据显示模式添加位置标记
        int times = 1;
        if (currentMode == DisplayMode.locationRoads) {
            times = locations.size();

        }

        //添加历史位置


        List<LatLng> latlngs = new ArrayList<>();
        if (!CommonUtils.isNetworkConnected(ctx)) {
            String result = LocalCacheUtils.getAddressFromLpcal(ctx);
            if (result != null) {
                addresses = result.split("|");
                for (int i = 0; i < times; i++) {
                    final int index = i;
                    GpsData.Location location = locations.get(i);
                    LatLng lng = new LatLng(location.gpsx, location.gpsy);
                    //将经纬度加入集合，方便显示路径线
                    latlngs.add(lng);
                    final String date = location.datetime;
                    addMarkerFromLocal(lng, date, index);
                }
            }else{

            }
        } else {
            addresses = new String[locations.size()];
            for (int i = 0; i < times; i++) {
                final int index = i;
                GpsData.Location location = locations.get(i);
                LatLng lng = new LatLng(location.gpsx, location.gpsy);

                //将经纬度加入集合，方便显示路径线
                latlngs.add(lng);
                final String date = location.datetime;

                //获取经纬度对应的位置信息
                ReverseGeoCodeOption geoCodeOption = new ReverseGeoCodeOption();
                GeoCoder geocoder = GeoCoder.newInstance();
                geoCodeOption.location(lng);
                geocoder.reverseGeoCode(geoCodeOption);
                geocoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                    }

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                        //添加标记
                        addMarker(reverseGeoCodeResult, date, index);
                        addresses[index] = reverseGeoCodeResult.getAddress();
                    }
                });
            }

            LocalCacheUtils.setAddressToLocalToLocal(ctx,addresses);
        }
//        LocalCacheUtils.setAddressToLocalToLocal(ctx, sb.toString());
        //添加路径线
        if (currentMode == DisplayMode.locationRoads) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.BLACK)
                    .width(5)
                    .points(latlngs);
            baiduMap.addOverlay(polylineOptions);
        }
    }

    private void addMarkerFromLocal(LatLng lng, String date, int index) {
        String s = addresses[index] + "\n" + date;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(lng);
        //锚点
        markerOptions.anchor(0.5f, 1);
        markerOptions.title(s);
        //计算与电子围栏中点的距离,以选择图标的颜色
        double d = DistanceUtil.getDistance(lng, center);
        BitmapDescriptor bitmapDes;
        if (currentMode == DisplayMode.locationPoint) {
            bitmapDes = BitmapDescriptorFactory
                    .fromResource(d > radius ? R.mipmap.icon_gcoding : R.mipmap.icon_gcoding_green);
        } else {
            bitmapDes = BitmapDescriptorFactory
                    .fromResource(d > radius ? markersId[index] : markersIdGreen[index]);
        }
        markerOptions.icon(bitmapDes);
        baiduMap.addOverlay(markerOptions);
        if (index == 0) {
            //添加marker标记
            title = new TextView(MainActivity.this);
            title.setBackgroundResource(R.mipmap.popupmap);
            title.setTextSize(10);
            title.setText(s);
            ViewGroup.LayoutParams params = new MapViewLayoutParams.Builder()
                    .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)// 按照经纬度设置位置
                    .position(lng)// 不能传null 设置为mapMode时 必须设置position
                    .width(MapViewLayoutParams.WRAP_CONTENT)
                    .height(MapViewLayoutParams.WRAP_CONTENT)
                    .yOffset(-85)
                    .build();
            mapview.addView(title, params);
        }
    }

    //marker点击监听器
    class MarkListener implements BaiduMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            ViewGroup.LayoutParams params = new MapViewLayoutParams.Builder()
                    .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)// 按照经纬度设置位置
                    .position(marker.getPosition())// 不能传null
                    .width(MapViewLayoutParams.WRAP_CONTENT)
                    .height(MapViewLayoutParams.WRAP_CONTENT)
                    .yOffset(-85)// 距离position的像素 向下是正值 向上是负值
                    .build();
            mapview.updateViewLayout(title, params);
            title.setText(marker.getTitle());
            return true;
        }
    }

    //添加位置标记
    private void addMarker(ReverseGeoCodeResult reverseGeoCodeResult, String date, int index) {

        String s = reverseGeoCodeResult.getAddress() + "\n" + date;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(reverseGeoCodeResult.getLocation());
        //锚点
        markerOptions.anchor(0.5f, 1);
        markerOptions.title(s);

        //计算与电子围栏中点的距离,以选择图标的颜色

        double d = DistanceUtil.getDistance(reverseGeoCodeResult.getLocation(), center);
        BitmapDescriptor bitmapDes;
        if (currentMode == DisplayMode.locationPoint) {
            bitmapDes = BitmapDescriptorFactory
                    .fromResource(d > radius ? R.mipmap.icon_gcoding : R.mipmap.icon_gcoding_green);
        } else {
            bitmapDes = BitmapDescriptorFactory
                    .fromResource(d > radius ? markersId[index] : markersIdGreen[index]);
        }

        markerOptions.icon(bitmapDes);
        baiduMap.addOverlay(markerOptions);
        if (index == 0) {
            //添加marker标记
            title = new TextView(MainActivity.this);
            title.setBackgroundResource(R.mipmap.popupmap);
            title.setTextSize(10);
            title.setText(s);
            ViewGroup.LayoutParams params = new MapViewLayoutParams.Builder()
                    .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)// 按照经纬度设置位置
                    .position(reverseGeoCodeResult.getLocation())// 不能传null 设置为mapMode时 必须设置position
                    .width(MapViewLayoutParams.WRAP_CONTENT)
                    .height(MapViewLayoutParams.WRAP_CONTENT)
                    .yOffset(-85)
                    .build();
            mapview.addView(title, params);
        }
    }

    private ArrayList<GpsData.Location> locations;

    @Override
    protected void initData() {
        /**
         * 获取数据
         */
        mHandler.sendEmptyMessageDelayed(2,div);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        getConnPhoneFromService(username);


    }

    private void getConnPhoneFromService(String username) {
        String path = Global.HOST + Global.GET_CONNPHONE + username;
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if(!responseInfo.result.equals(connphoneResult)){
                    connphoneResult = responseInfo.result;
                    initConnPhone(connphoneResult);

                }

            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });

    }

    private void initConnPhone(String result) {
        Connphone data = CommonUtils.parseConnphoneData(result);
        if (data!=null&&data.status.equals("success")) {
//            LocalCacheUtils.setLocationToLocal(MainActivity.this, result);
            connphone = data.connphone;
            getLocationsFromService();
            getFenceFromService();
        }
    }

    @Override
    protected void initMenuSelect(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(ctx, SettingsActivity.class);
                intent.putExtra("phonenumber",username);
                startActivity(intent);
                break;
            case R.id.move2_current_location:
                MapStatusUpdate mapStatusUpdatePoint = MapStatusUpdateFactory
                        .newLatLng(currentLocation);
                baiduMap.setMapStatus(mapStatusUpdatePoint);
                break;
            case R.id.change_mode:
                if (currentMode == DisplayMode.locationPoint) {
                    currentMode = DisplayMode.locationRoads;
                } else {
                    currentMode = DisplayMode.locationPoint;
                }
                refreshMap();
                break;
        }
        super.initMenuSelect(item);
    }

    public enum DisplayMode {
        locationPoint, locationRoads
    }

    /**
     * 获取目标机电子围栏
     * 返回值为json数组
     * 表示了一个圆形范围
     * 信息类为Fence
     */
    private void getFenceFromService() {
        String path = Global.HOST + Global.GET_FENCE + connphone;
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if(!responseInfo.result.equals(fenceResult)){
                    fenceResult = responseInfo.result;
                    initFenceData(fenceResult);
                    mHandler.sendEmptyMessage(1);
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                String result = LocalCacheUtils.getFenceFromLocal(MainActivity.this);
                if (result != null) {
                    initFenceData(result);
                    mHandler.sendEmptyMessage(1);
                } else {

                }
            }
        });

    }

    /**
     * 初始化目标机历史路径和坐标
     */
    private void initLocationData(String result) {
        GpsData data = CommonUtils.parseLocationData(result);
        if (data!=null&&data.status.equals("success")) {
            LocalCacheUtils.setLocationToLocal(MainActivity.this, result);
            locations = data.locations;
            //最新位置
            GpsData.Location pos = locations.get(0);
            currentLocation = new LatLng(pos.gpsx, pos.gpsy);
        }
    }

    /**
     * 初始化目标机电子围栏
     */
    private void initFenceData(String result) {
        Fence data = CommonUtils.parseFenceData(result);
        if (data!=null&&data.status.equals("success")) {
            LocalCacheUtils.setFenceToLocal(MainActivity.this, result);
            center = new LatLng(data.location.get(0).gpsx, data.location.get(0).gpsy);
            radius = data.location.get(0).dir;
        }
    }

    /**
     * 获取目标机的坐标信息
     * 返回为一个json数组
     * 包含一定数量的坐标
     * 信息类为GpsData
     */
    private void getLocationsFromService() {
        //子线程中联网获取

        String path = Global.HOST + Global.GET_LOCATION + connphone + Global.LOCATION_NUM + locationCount;
        System.out.println(path);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if(!responseInfo.result.equals(locationResult)){
                    locationResult = responseInfo.result;
                    initLocationData(locationResult);
                    mHandler.sendEmptyMessage(0);
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                String result = LocalCacheUtils.getLocationFromLocal(MainActivity.this);
                if (result != null) {
                    initLocationData(result);
                    mHandler.sendEmptyMessage(0);
                }

            }
        });
    }

    //接收短信
    public class SmsReceiver extends BroadcastReceiver {

        private static final String TAG = "SmsReceiver";
        private static final String MMS_LOCATION = "location:";

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");

            DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                String sender = smsMessage.getOriginatingAddress();
                Log.i(TAG, sender);
                String body = smsMessage.getMessageBody();

                Toast.makeText(context, body, Toast.LENGTH_SHORT).show();
                if (body.startsWith(MMS_LOCATION)) {
                    body.replace(MMS_LOCATION, "");

                    abortBroadcast();
                }

            }
        }

    }
}
