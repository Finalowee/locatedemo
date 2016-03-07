package com.panchuang.locatedemo.bean;

import java.util.ArrayList;

/**
 * ClassName GpsData
 * PackageName com.panchuang.locatedemo.bean.GpsData
 * ToDo
 * Created by LiJie on 2016/3/2.
 */
public class GpsData {

    public String status;
    public ArrayList<Location> locations;
    public Location location;

    public class Location{
        public int id;
        public double gpsx;
        public double gpsy;
        public String datetime;
        public int Sos;
    }

}
