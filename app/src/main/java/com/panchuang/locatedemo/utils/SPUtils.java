package com.panchuang.locatedemo.utils;

import android.content.Context;

/**
 * ClassName SPUtils
 * PackageName com.panchuang.lijie.zhbj.utils
 * ToDo
 * Created by LiJie on 2016/1/22.
 */
public class SPUtils {
    public final static String SP_CONFIG = "config";
    public final static String KEY_IS_GUIDE = "isWentGuide";
    public final static String KEY_IS_READ = "newsId";
    public static boolean getBoolean(Context context,String key,boolean defaultValue){
        return context.getSharedPreferences(SP_CONFIG, Context.MODE_PRIVATE).getBoolean(key,defaultValue);
    }
    public static void setBoolean(Context context,String key,boolean value){
        context.getSharedPreferences(SP_CONFIG,Context.MODE_PRIVATE).edit().putBoolean(key,value).commit();
    }
    public static String getString(Context context,String key,String defaultValue){
        return context.getSharedPreferences(SP_CONFIG, Context.MODE_PRIVATE).getString(key, defaultValue);
    }
    public static void setString(Context context,String key,String value){
        context.getSharedPreferences(SP_CONFIG,Context.MODE_PRIVATE).edit().putString(key, value).commit();
    }
}
