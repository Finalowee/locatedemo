package com.panchuang.locatedemo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.panchuang.locatedemo.bean.Connphone;
import com.panchuang.locatedemo.bean.Fence;
import com.panchuang.locatedemo.bean.GpsData;
import com.panchuang.locatedemo.bean.UserInfo;

/**
 * ClassName CommonUtils
 * PackageName com.panchuang.locatedemo.utils.CommonUtils
 * ToDo
 * Created by LiJie on 2016/3/3.
 */
public class CommonUtils {
    /**
     * 解析目标位置Json数组
     */
    public static GpsData parseLocationData(String cache) {
        Gson gson = new Gson();
        GpsData gps = null;

        try{
            gps = gson.fromJson(cache, GpsData.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return gps;

    }

    /**
     * 解析电子围栏json数组
     *
     * @param cache
     * @return Fence
     */
    public static Fence parseFenceData(String cache) {
        Gson gson = new Gson();
        Fence fence = null;
        try{
            fence = gson.fromJson(cache, Fence.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        return fence;
    }

    /**
     * 获取网络状态
     * @param context
     * @return boolean
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 解析用户关联号码
     * @param result
     * @return Connphone
     */
    public static Connphone parseConnphoneData(String result) {
        Gson gson = new Gson();
        Connphone connphone =  null;
        try{
            connphone = gson.fromJson(result, Connphone.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        return connphone;
    }

    /**
     * 解析用户详细信息
     * @param cache
     * @return UserInfo
     */
    public static UserInfo parseUserData(String cache) {
        Gson gson = new Gson();
        UserInfo userInfo = null;
        try{
            userInfo = gson.fromJson(cache, UserInfo.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        return userInfo;
    }
}
