package bupt.tiantian.weibo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import java.text.SimpleDateFormat;

import bupt.tiantian.weibo.statuslistshow.MainActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int TIME = 1000;
    private static final int TIME_SHORT = 500;
    private static final int GO_HOME = 1000;

    /**
     * 显示认证后的信息，如 AccessToken
     */
    private TextView tvToken;

    /**
     * 默认隐藏，如果授权过期则显示，内含登录、注册、忘记密码三个按钮
     */
    private LinearLayout llButtons;

    private AuthInfo mAuthInfo;

    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;

    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvToken = (TextView) findViewById(R.id.tvToken);
        llButtons = (LinearLayout) findViewById(R.id.llButtons);

        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(LoginActivity.this, mAuthInfo);

        //用户注销----调试用
//        findViewById(R.id.btnLogout).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnSignUp).setOnClickListener(this);
        findViewById(R.id.btnForget).setOnClickListener(this);

        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()) {//AccessToken可用，即已经登录
            //显示Token, for dubug
//            updateTokenView(true);
            //发送进入主界面的消息
            mHandler.sendEmptyMessageDelayed(GO_HOME,TIME);
        } else {//AccessToken 不可用，未登录
            // 显示下方的三个按钮并设置点击事件
            llButtons.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                if (mAccessToken.isSessionValid()) {//AccessToken可用，即已经登录
                    //显示Token, for dubug
//                    updateTokenView(true);
                    //发送进入主界面的消息
                    mHandler.sendEmptyMessageDelayed(GO_HOME,TIME);
                }else{

                    mSsoHandler.authorize(new AuthListener());
                }
                break;
            case R.id.btnSignUp:
                Uri uriSignUp= Uri.parse("http://m.weibo.cn/reg/index?vt=4&res=wel&wm=3349&backURL=http%3A%2F%2Fm.weibo.cn%2F%3Fjumpfrom%3Dwapv4%26tip%3D1");
                Intent signUp = new Intent(Intent.ACTION_VIEW, uriSignUp);
                startActivity(signUp);
                break;
            case R.id.btnForget:
                Uri uriForget = Uri.parse("https://passport.weibo.cn/forgot/forgot?entry=wapsso&from=0");
                Intent forget = new Intent(Intent.ACTION_VIEW, uriForget);
                startActivity(forget);
                break;
//            case R.id.btnLogout://for dubug
//                AccessTokenKeeper.clear(getApplicationContext());
//                mAccessToken = new Oauth2AccessToken();
//                updateTokenView(false);
//                llButtons.setVisibility(View.VISIBLE);
//                break;
            default:
                break;
        }

    }

    private android.os.Handler mHandler=new android.os.Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case GO_HOME:
                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                    break;
            }
        }
    };


    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     *
     * @see {@link android.app.Activity#onActivityResult(int requestCode, int resultCode, Intent data)}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 显示当前 Token 信息。
     *
     * @param hasExisted 配置文件中是否已存在 token 信息并且合法
     */
    private void updateTokenView(boolean hasExisted) {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
        String message = String.format(format, mAccessToken.getToken(), date);
        if (hasExisted) {
            message = getString(R.string.weibosdk_demo_token_has_existed) + "\n" + message;
        }
        tvToken.setText(message);
    }


    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息
            String phoneNum = mAccessToken.getPhoneNum();
            if (mAccessToken.isSessionValid()) {

                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);
                Toast.makeText(LoginActivity.this,
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
                // 显示 Token, for dubug
//                updateTokenView(false);
                //设置登录按钮不可见
                llButtons.setVisibility(View.INVISIBLE);
                //发送进入主界面的消息
                mHandler.sendEmptyMessageDelayed(GO_HOME,TIME_SHORT);

            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}