package com.panchuang.locatedemo.bean;

import java.util.List;

/**
 * ClassName Fence
 * PackageName com.panchuang.locatedemo.bean.Fence
 * ToDo
 * Created by LiJie on 2016/3/2.
 */
public class Fence {

    public String status;
    public List<Location> location;

    public class Location{
        public double gpsx;
        public double gpsy;
        public int dir;
        public int id;
        public String connphone;
    }
}
