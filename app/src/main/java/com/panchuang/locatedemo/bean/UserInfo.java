package com.panchuang.locatedemo.bean;

import java.util.List;

/**
 * ClassName UserInfo
 * PackageName com.panchuang.locatedemo.bean.UserInfo
 * ToDo
 * Created by LiJie on 2016/3/5.
 */
public class UserInfo {
    public String status;
    public List<Info> userinfo;

    //{"status":"success","userinfo":
    // [{"id":"1075","phonenumber":"14736580","username":"","password":"123456",
    // "datetime":"2016-03-04 17:32:30","connphone":"18516541352","upinterval":"1",
    // "fencex":null,"fencey":null,"fencedir":null,"sos1":"123546","sos2":"","sos3":"12548"}]}
    public class Info {
        public int id;
        public double fencex;
        public double fencey;
        public String phonenumber;
        public String password;
        public String sos1;
        public String connphone;
        public String sos2;
        public String sos3;
        public int upinterval;
        public double fencedir;
    }
}
