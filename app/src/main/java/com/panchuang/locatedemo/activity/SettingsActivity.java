package com.panchuang.locatedemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.panchuang.locatedemo.Global;
import com.panchuang.locatedemo.R;
import com.panchuang.locatedemo.bean.UserInfo;
import com.panchuang.locatedemo.utils.CommonUtils;

import java.util.List;

/**
 * ClassName SettingsActivity
 * PackageName com.panchuang.locatedemo.activity.SettingsActivity
 * ToDo
 * Created by LiJie on 2016/3/5.
 */
public class SettingsActivity extends Activity implements View.OnClickListener {
    private String phoneNumber;
    private String userResult;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    refreshUserInfo();
                    break;
            }
        }
    };
    private List<UserInfo.Info> userinfos;
    private EditText username;
    private EditText passwd;
    private EditText connphone;
    private EditText sos1;
    private EditText sos2;
    private EditText sos3;
    private EditText lagtime;
    private EditText fence;
    private EditText radius;

    private void refreshUserInfo() {
        if(userinfos!=null){
            UserInfo.Info userinfo = userinfos.get(0);
            if(userinfo!=null){
                username.setText(userinfo.phonenumber);
                passwd.setText(userinfo.password);
                connphone.setText(userinfo.connphone);

                sos1.setText(userinfo.sos1!=null?userinfo.sos1:"");
                sos2.setText(userinfo.sos2!=null?userinfo.sos2:"");
                sos3.setText(userinfo.sos3!=null?userinfo.sos3:"");

                lagtime.setText(userinfo.upinterval!=0?userinfo.upinterval+"":"");
                fence.setText(userinfo.fencex+Global.DIV+userinfo.fencey);
                radius.setText(userinfo.fencedir/1000+"");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        initWidget();
        getUserInfoFromService();
    }

    private void initWidget() {
        username = (EditText) findViewById(R.id.et_phonenumber);
        passwd = (EditText) findViewById(R.id.et_passwd);
        connphone = (EditText) findViewById(R.id.et_connphone);

        sos1 = (EditText) findViewById(R.id.et_sos1);
        sos2 = (EditText) findViewById(R.id.et_sos2);
        sos3 = (EditText) findViewById(R.id.et_sos3);

        lagtime = (EditText) findViewById(R.id.et_lag);
        fence = (EditText) findViewById(R.id.et_fence);
        radius = (EditText) findViewById(R.id.et_radius);

        findViewById(R.id.btn_fence).setOnClickListener(this);
        findViewById(R.id.btn_cancle).setOnClickListener(this);
        findViewById(R.id.btn_commit).setOnClickListener(this);

    }

    private void getUserInfoFromService() {
        //子线程中联网获取
        phoneNumber = getIntent().getStringExtra("phonenumber");
        if (phoneNumber != null) {
            String path = Global.HOST + Global.GET_USERINFO + phoneNumber;
            System.out.println(path);
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.send(HttpRequest.HttpMethod.GET, path, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    userResult = responseInfo.result;
                    initUserData(userResult);
                    System.out.println(userResult);

                }

                @Override
                public void onFailure(HttpException e, String s) {
                }
            });
        }

    }

    private void initUserData(String userResult) {
        UserInfo data = CommonUtils.parseUserData(userResult);
        if (data != null && data.status.equals("success")) {
            userinfos = data.userinfo;
            mHandler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fence:
                jump2FenceSetting();
                break;
            case R.id.btn_cancle:
                finish();
                break;
            case R.id.btn_commit:
                sendUserInfo2Service();
                break;
        }
    }

    private void jump2FenceSetting() {

    }

    private void sendUserInfo2Service() {

    }
}
