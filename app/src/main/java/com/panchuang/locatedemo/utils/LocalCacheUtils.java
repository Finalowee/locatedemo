package com.panchuang.locatedemo.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

/**
 * ClassName LocalCacheUtils
 * PackageName com.panchuang.lijie.zhbj.utils
 * ToDo
 * Created by LiJie on 2016/1/27.
 */
public class LocalCacheUtils {


    public static final String KEY_LOCATIONS = "locations";
    public static final String KEY_FENCE = "fence";
    public static final String KEY_MMS = "sms";
    public static final String KEY_ADDRESS = "address";
    public static final String CACHE_PATH =
            Environment.getExternalStorageDirectory()+"/LocateCache";
    /*
    读取本地缓存的数据
    1 位置
    2 电子围栏
    3 短信获取的位置
     */
    public static String getLocationFromLocal(Context ctx){

        return SPUtils.getString(ctx,KEY_LOCATIONS,null);
    }
    public static String getFenceFromLocal(Context ctx){

        return SPUtils.getString(ctx,KEY_FENCE,null);
    }
    public static String getLocationFromMms(Context ctx){

        return SPUtils.getString(ctx,KEY_MMS,null);
    }
    public static String getAddressFromLpcal(Context ctx){

        return SPUtils.getString(ctx,KEY_ADDRESS,null);
    }
    /*
    缓存网络上获取的的数据
    1 位置
    2 电子围栏
    3 短信获取的位置
     */
    public static void setLocationToLocal(Context ctx,String json){
        SPUtils.setString(ctx,KEY_LOCATIONS,json);
    }

    public static void setFenceToLocal(Context ctx,String json){
        SPUtils.setString(ctx,KEY_FENCE,json);
    }
    public static void setLocationToLocalByMMs(Context ctx,String json){
        SPUtils.setString(ctx,KEY_MMS,json);
    }
    public static void setAddressToLocalToLocal(Context ctx,String[] json){
        StringBuffer sb = new StringBuffer();
        if(json[1]!=null){
            for (String s:json) {
                sb.append(s+"|");
            }
            Toast.makeText(ctx,sb.toString(), Toast.LENGTH_SHORT).show();
            SPUtils.setString(ctx,KEY_ADDRESS,sb.toString());
        }
    }
}
