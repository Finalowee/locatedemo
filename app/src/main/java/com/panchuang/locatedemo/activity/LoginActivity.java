package com.panchuang.locatedemo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.panchuang.locatedemo.R;
import com.panchuang.locatedemo.utils.SPUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

    private String username;
    private String password;
    private String strResult;
    private boolean isLoginSuccess;

    private EditText et_username;
    private EditText et_password;
    private Button btn_loginSubmit;
    private Button btn_loginRegister;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isLoginSuccess = SPUtils.getBoolean(this, "isLoginSuccess", false);
        if (isLoginSuccess) {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", SPUtils.getString(this, "username", ""));
            startActivity(intent);


        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        findViewById();
        setListener();
    }

    private void findViewById() {
        et_username = (EditText) findViewById(R.id.loginPhoneNumber);
        et_password = (EditText) findViewById(R.id.loginPassword);
        btn_loginSubmit = (Button) findViewById(R.id.login);
        btn_loginRegister = (Button) findViewById(R.id.register);
    }

    private void setListener() {
        btn_loginSubmit.setOnClickListener(submitListener);
        btn_loginRegister.setOnClickListener(registerListener);
    }

    /**
     * 注册Listener
     */
    private View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            // 转向注册页面
            startActivity(intent);
        }
    };


    //登录Button Listener
    private View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Thread loginThread = new Thread(new LoginHandler());
                loginThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    class LoginHandler implements Runnable {
        @Override
        public void run() {
            Looper.prepare();
            username = et_username.getText().toString();
            password = et_password.getText().toString();
            String validateURL = "http://112.64.126.222:8080/zhangtao/login.php";
            boolean loginState = validateLocalLogin(username, password, validateURL);
            // 登陆成功
            if (loginState) {

                progressDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.login_progressDialog_connectSuccess),
                        getString(R.string.login_progressDialog_loading));
                // 需要传输数据到登陆后的界面,
                Intent intent = new Intent();
                intent.putExtra("username", username);
                intent.setClass(LoginActivity.this, MainActivity.class);
                // 转向登陆后的页面
                startActivity(intent);
                finish();
                progressDialog.dismiss();

            } else {

                progressDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.login_progressDialog_wrong),
                        getString(R.string.login_progressDialog_input), true, true);

            }
            Looper.loop();

        }
    }

    private boolean validateLocalLogin(String username, String password, String validateUrl) {
        // 用于标记登陆状态
        boolean loginState;

        HttpPost httpRequest = new HttpPost(validateUrl);
        //Post运作传送变数必须用NameValuePair[]阵列储存

        //传参数 服务端获取的方法为request.getParameter("name")

        List params = new ArrayList();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        try {

            //发出HTTP request

            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            //取得HTTP response

            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);

            //若状态码为200 ok

            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                //取出回应字串

                strResult = EntityUtils.toString(httpResponse.getEntity());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (strResult.substring(strResult.length() - 1).equals("2")) {
            loginState = true;
        } else {
            loginState = false;
        }

        if (loginState) {
            SPUtils.setString(LoginActivity.this, "username", username);
            SPUtils.setString(LoginActivity.this, "password", password);
            SPUtils.setBoolean(LoginActivity.this, "isLoginSuccess", true);
        } else {
            SPUtils.setBoolean(LoginActivity.this, "isLoginSuccess", false);
        }

        return loginState;
    }

    /**
     * 如果登录成功过,则将登陆用户名和密码记录在SharePreferences
     *
     * */

//        /** 登录后台通知更新UI线程,主要用于登录失败,通知UI线程更新界面 */
//        Handler loginHandler = new Handler() {
//            public void handleMessage(Message msg) {
//                isNetError = msg.getData().getBoolean("isNetError");
//                if (proDialog != null) {
//                    proDialog.dismiss();
//                }
//                if (isNetError) {
//                    Toast.makeText(LoginActivity.this, "登陆失败:\n1.请检查您网络连接.\n2.请联系我们.!",
//                            Toast.LENGTH_SHORT).show();
//                }
//                // 用户名和密码错误
//                else {
//                    Toast.makeText(LoginActivity.this, "登陆失败,请输入正确的用户名和密码!",
//                            Toast.LENGTH_SHORT).show();
//                    // 清除以前的SharePreferences密码
//                    clearSharePassword();
//                }
//            }
//        };

//    /** 清除密码 */
//    private void clearSharePassword() {
//        SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
//        share.edit().putString(SHARE_LOGIN_PASSWORD, "").commit();
//        share = null;
//    }

}
