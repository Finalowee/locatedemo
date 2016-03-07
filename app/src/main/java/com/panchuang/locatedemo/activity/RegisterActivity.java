package com.panchuang.locatedemo.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.panchuang.locatedemo.Global;
import com.panchuang.locatedemo.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegisterActivity extends Activity {
    private String username;
    private String password;
    //关联号
    private String connphone;
    //查询间隔时间
    private String upinterval;
    //紧急号码
    private String sos1;
    private String sos2;
    private String sos3;
    //电子围栏经纬度
    private String fencex;
    private String fencey;

    private String strResult;

    private EditText view_username;
    private EditText view_password;
    private EditText view_connphone;
    private EditText view_upinterval;
    private EditText view_sos1;
    private EditText view_sos2;
    private EditText view_sos3;
    private Button view_submit;
    private Button view_clear;
    private Button view_eleFence;
    private ProgressDialog proDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        findViews();
        setListener();
    }

        //初始化注册view组件
    private void findViews(){
        view_username = (EditText)findViewById(R.id.et_phonenumber);
        view_password = (EditText)findViewById(R.id.et_passwd);
        view_connphone = (EditText)findViewById(R.id.et_connphone);
        view_upinterval = (EditText)findViewById(R.id.et_lag);
        view_sos1 = (EditText)findViewById(R.id.et_sos1);
        view_sos2 = (EditText)findViewById(R.id.et_sos2);
        view_sos3 = (EditText)findViewById(R.id.et_sos3);
        view_eleFence = (Button)findViewById(R.id.btn_fence);
        view_submit = (Button)findViewById(R.id.btn_commit);
        view_clear = (Button)findViewById(R.id.btn_cancle);
    }

    private void setListener(){
        view_submit.setOnClickListener(submitListener);
        view_clear.setOnClickListener(clearListener);
//        view_eleFence.setOnClickListener(eleFenceListener);
    }

    //监听注册确定按钮
    private View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            username = view_username.getText().toString();
            password = view_password.getText().toString();
            connphone = view_connphone.getText().toString();

            boolean validateFormState = false;

            validateFormState = validateForm(username,password,connphone,validateFormState);

            if(validateFormState){

                try {
                    Thread registerThread = new Thread(new RegisterHandler());
                    registerThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Log.i("info", "validateFormState=false");
            }
        }
    };

    private boolean validateForm(String username,String password,String coonphone,
                                 boolean validateFormState){

        if(username.length() < 6){
            Toast.makeText(getApplicationContext(),getString(R.string.register_invalidatePhoneNumber),
                    Toast.LENGTH_SHORT).show();
        }else if (password.length() < 3){
            Toast.makeText(getApplicationContext(),getString(R.string.register_invalidatePassword),
                    Toast.LENGTH_SHORT).show();
        }else if(coonphone.length() < 6){
            Toast.makeText(getApplicationContext(),getString(R.string.register_invalidateBindPhoneNumber)
                    ,Toast.LENGTH_SHORT).show();
        }else{
            validateFormState = true;
        }
        return validateFormState;
    }


    class RegisterHandler implements Runnable{
        @Override
        public void run(){
            Looper.prepare();
            username = view_username.getText().toString();
            password = view_password.getText().toString();
            connphone = view_connphone.getText().toString();
            upinterval = view_upinterval.getText().toString();
            sos1 = view_sos1.getText().toString();
            sos2 = view_sos2.getText().toString();
            sos3 = view_sos3.getText().toString();

            String validateURL = "http://112.64.126.222:8080/zhangtao/settings_activity.php";
            boolean registerState =validateLocalRegister(username,password,connphone
                    ,upinterval,sos1,sos2,sos3,validateURL);

            String message = username+ Global.DIV+connphone+Global.DIV+upinterval+Global.DIV+""+Global.DIV+""+Global.DIV+sos1+Global.DIV+sos2+Global.DIV+sos3;
            // 登陆成功
            if (registerState) {
                proDialog = ProgressDialog.show(RegisterActivity.this,getString(R.string.register_progressDialog_register),
                        getString(R.string.register_progressDialog_connect), true, true);

                // 需要传输数据到登陆后的界面,
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                // 转向登陆后的页面
                startActivity(intent);
                finish();
                proDialog.dismiss();
                //注册成功自动给关联号发送一条短信
                Toast.makeText(getApplicationContext(),connphone,
                        Toast.LENGTH_SHORT).show();
                sendMessage(RegisterActivity.this,message,connphone);



            }else{
                Toast.makeText(getApplicationContext(),getString(R.string.register_Toast_registerDefeat),
                        Toast.LENGTH_SHORT).show();
            }
            Looper.loop();
        }
    }

    private void sendMessage(Context ctx,String message,String connphone){

        message = "settings_activity:"+message;
        SmsManager smsManager = SmsManager.getDefault();
        Intent intent = new Intent("com.panchuang.login.SEND_SUCCESS");
        PendingIntent sendIntent = PendingIntent.getBroadcast(ctx, 666, intent, PendingIntent.FLAG_ONE_SHOT);
        smsManager.sendTextMessage(connphone,null,message,sendIntent,null);

    }


    //获取当前时间，转换为字符串
    private String getDatetime(){
        Calendar c=Calendar.getInstance();
        String datetime=c.get(Calendar.YEAR)+"-"+ //得到年
        formatTime(c.get(Calendar.MONTH)+1)+"-"+//month加一 //月
        formatTime(c.get(Calendar.DAY_OF_MONTH))+" "+ //日
        formatTime(c.get(Calendar.HOUR_OF_DAY))+":"+ //时
        formatTime(c.get(Calendar.MINUTE))+":"+ //分
        formatTime(c.get(Calendar.SECOND)); //秒
        return datetime;
    }
    private String formatTime(int t){
        return t>=10? ""+t:"0"+t;
    }

    private boolean validateLocalRegister(String username,String password,String connphone,
                                          String upinterval,String sos1,String sos2,String sos3,String validateUrl){
        boolean registerState = false;

        String datetime = getDatetime();

        HttpPost httpRequest =new HttpPost(validateUrl);
        //Post运作传送变数必须用NameValuePair[]阵列储存
        //传参数 服务端获取的方法为request.getParameter("name")
        List params = new ArrayList();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("connphone",connphone));
        params.add(new BasicNameValuePair("datetime",datetime));
        params.add(new BasicNameValuePair("upinterval",upinterval));
        params.add(new BasicNameValuePair("sos1",sos1));
        params.add(new BasicNameValuePair("sos2",sos2));
        params.add(new BasicNameValuePair("sos3",sos3));

        try {
            //发出HTTP request
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            //取得HTTP
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            //若状态码为200 ok
            if(httpResponse.getStatusLine().getStatusCode() == 200){
                //取出回应字串
                strResult = EntityUtils.toString(httpResponse.getEntity());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Log.i("info",strResult);
        //如果与服务器返回的字符串相等，则表示注册成功
        if(strResult.substring(strResult.length()-1).equals("1")){
            registerState = true;
        }else {
            if(strResult.substring(strResult.length()-1).equals("3")){
                Toast.makeText(getApplicationContext(),getString(R.string.register_registed),Toast.LENGTH_SHORT).show();
            }
            registerState = false;
        }

        return registerState;
    }


    // 清空监听按钮
    private View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearForm();
        }
    };

    //清空表单
    private void clearForm() {
        view_username.setText("");
        view_password.setText("");
        view_connphone.setText("");
        view_upinterval.setText("1");
        view_sos1.setText("");
        view_sos2.setText("");
        view_sos3.setText("");
    }

//    //电子围栏，显示当前的位置
//    private View.OnClickListener eleFenceListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent intent = new Intent();
//            intent.setClass();
//            startActivity(intent);
//        }
//    };
}
