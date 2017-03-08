package bupt.tiantian.weibo.login;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.openapi.UsersAPI;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import bupt.tiantian.weibo.Constants;
import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.models.SimpleUser;
import bupt.tiantian.weibo.sql.UserTokenDao;
import bupt.tiantian.weibo.sql.UserTokenServise;
import bupt.tiantian.weibo.statuslistshow.MainActivity;
import bupt.tiantian.weibo.util.NetChecker;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "LoginActivity";
    private static final int TIME = 10000;
    private static final int TIME_SHORT = 500;
    private static final int GO_HOME = 1000;

    /**
     * 显示认证后的信息，如 AccessToken
     */
//    private TextView tvToken;

    /**
     * 默认隐藏，如果授权过期则显示，内含登录、注册、忘记密码三个按钮
     */
    private LinearLayout llButtons;
    /**
     * 可用的用户列表
     */
    private RecyclerView rvUsers;
    /**
     * rvUsers的Adapter
     */
    private UserAdapter mUserAdapter;

    private TextView tvTimer;
    private Timer mTimer = new Timer();
    private int mSecontCountDown = 5;
    /**
     * 帮助实现recyclerview的侧滑功能
     */
    private ItemTouchHelper mItemTouchHelper;

    private AuthInfo mAuthInfo;

    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
//    private Oauth2AccessToken accessToken;

    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;

    private UsersAPI mUsersAPI;
    private List<SimpleUser> mUsers;
    private int mSelectedUserIndex = -1;
    private UserTokenServise mUserTokenDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NetChecker.setNetWorkFlag(LoginActivity.this);


//        tvToken = (TextView) findViewById(R.id.tvToken);
        llButtons = (LinearLayout) findViewById(R.id.llButtons);
        rvUsers = (RecyclerView) findViewById(R.id.rvUsers);
        tvTimer = (TextView) findViewById(R.id.tvTimer);

        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(LoginActivity.this, mAuthInfo);
        mUserTokenDao = new UserTokenDao(LoginActivity.this);


        //用户注销----for debug
//        findViewById(R.id.btnLogout).setOnClickListener(this);
        findViewById(R.id.btnAuth).setOnClickListener(this);
        findViewById(R.id.btnSignUp).setOnClickListener(this);
        findViewById(R.id.btnForget).setOnClickListener(this);

        mUsers = mUserTokenDao.getTokenList();
        try {
            SimpleUser user;
            CountDownLatch latch = new CountDownLatch(mUsers.size());
            for (int i = 0; i < mUsers.size(); i++) {
                user = mUsers.get(i);
                mUsersAPI = new UsersAPI(LoginActivity.this, Constants.APP_KEY, user.getAccessToken());
                new Thread(new UserTokenCheckThread(user, latch)).start();//如何保证所有线程都完成= =
            }
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mUsers = mUserTokenDao.getUserTokenList();//这是可用的User list
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(this);
        //如果数据库中有可用的User且从pref中读到了上次登陆的token
        if (mUsers.size() > 0 && NetChecker.getNetworkFlag() != NetChecker.NO_NETWORK) {
            mSelectedUserIndex = 0;
            if (accessToken.isSessionValid()) {
                for (int i = 0; i < mUsers.size(); i++) {
                    if (accessToken.getUid().equals(mUsers.get(i).getUID())) {
                        mSelectedUserIndex = i;
                    }
                }
            }

            //显示Token, for debug
//            updateTokenView(true);

            rvUsers.setLayoutManager(new LinearLayoutManager(LoginActivity.this));
            rvUsers.setItemAnimator(new DefaultItemAnimator());
            mItemTouchHelper = new ItemTouchHelper(new UserItemTouchCallback() {
                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    final int pos = viewHolder.getAdapterPosition();
                    if (direction == ItemTouchHelper.RIGHT || direction == ItemTouchHelper.END) {
                        if (pos < mUsers.size()) {
                            mSelectedUserIndex = pos;
                            login(mUsers.get(pos));
                        } else {
                            mSsoHandler.authorize(new AuthListener());
                        }
                    } else {
                        final SimpleUser item = mUsers.get(pos);
                        mUsers.remove(pos);
                        mUserAdapter.notifyItemRemoved(pos);
                        String text = "注销用户: " + item.getScreenName();
                        Snackbar.make(viewHolder.itemView, text, Snackbar.LENGTH_LONG)
                                .setAction("撤销", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mUsers.add(pos, item);
                                        mUserAdapter.notifyItemInserted(pos);
                                    }
                                })
                                .setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        if (event != DISMISS_EVENT_ACTION) {
                                            mUserTokenDao.deleteUser(item.getUID());
                                        }
                                    }
                                })
                                .show();
                    }
                }
            });
            mItemTouchHelper.attachToRecyclerView(rvUsers);
            rvUsers.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mTimer != null) {
                        mTimer.cancel();
                    }
                    tvTimer.setText(getString(R.string.login_hint));
                    return false;
                }
            });
            mUserAdapter = new UserAdapter(LoginActivity.this, mUsers, mSelectedUserIndex);
            rvUsers.setAdapter(mUserAdapter);
            rvUsers.setVisibility(View.VISIBLE);
            mTimer.schedule(mTimerTask, 0, 1000);
            tvTimer.setVisibility(View.VISIBLE);

            //发送进入主界面的消息
//            mHandler.sendEmptyMessageDelayed(GO_HOME, TIME);
        } else {
            // 显示下方的三个按钮并设置点击事件，引导用户授权
            llButtons.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTimer != null) {
            mTimer.cancel();
        }
        tvTimer.setText(getString(R.string.login_hint));
    }

    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTimer.setText(String.format(getString(R.string.timer_login_hint), mSecontCountDown));
                    mSecontCountDown--;
                    if (mSecontCountDown < 0) {
                        mTimer.cancel();
                        if (mSelectedUserIndex < mUsers.size()) {
                            login(mUsers.get(mSelectedUserIndex));
                        } else {
                            tvTimer.setText(getString(R.string.login_hint));
                        }
                    }
                }
            });
        }
    };


    class UserTokenCheckThread implements Runnable {
        SimpleUser user;
        CountDownLatch latch;

        public UserTokenCheckThread(SimpleUser user, CountDownLatch latch) {
            this.latch = latch;
            this.user = user;
        }

        @Override
        public void run() {
            if (mUsersAPI != null) {
                mUsersAPI.show(Long.parseLong(user.getUID()), new UserCheckRequestListener(mUserTokenDao, user));
            }
            this.latch.countDown();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAuth:
                mSsoHandler.authorize(new AuthListener());
                break;
            case R.id.btnSignUp:
                Uri uriSignUp = Uri.parse("http://m.weibo.cn/reg/index?vt=4&res=wel&wm=3349&backURL=http%3A%2F%2Fm.weibo.cn%2F%3Fjumpfrom%3Dwapv4%26tip%3D1");
                Intent signUp = new Intent(Intent.ACTION_VIEW, uriSignUp);
                startActivity(signUp);
                break;
            case R.id.btnForget:
                Uri uriForget = Uri.parse("https://passport.weibo.cn/forgot/forgot?entry=wapsso&from=0");
                Intent forget = new Intent(Intent.ACTION_VIEW, uriForget);
                startActivity(forget);
                break;
//            case R.id.btnLogout://for debug
//                AccessTokenKeeper.clear(getApplicationContext());
//                mUserTokenDao.deleteUser(mSelectedUser.getUID());
//                mUsers = null;
//                updateTokenView(false);
//                llButtons.setVisibility(View.VISIBLE);
//                tvTimer.setVisibility(View.GONE);
//                rvUsers.setVisibility(View.GONE);
//                break;
            default:
                break;
        }

    }


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

//    /**
//     * 显示当前 Token 信息。
//     * for debug
//     *
//     * @param hasExisted 配置文件中是否已存在 token 信息并且合法
//     */
//    private void updateTokenView(boolean hasExisted) {
//        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
//                new java.util.Date(mSelectedUser.getAccessToken().getExpiresTime()));
//        String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
//        String message = String.format(format, mSelectedUser.getAccessToken().getToken(), date);
//        if (hasExisted) {
//            message = getString(R.string.weibosdk_demo_token_has_existed) + "\n" + message;
//        }
//        tvToken.setText(message);
//    }

    private void login(SimpleUser user) {
        AccessTokenKeeper.writeAccessToken(LoginActivity.this, user.getAccessToken());
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
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
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken.isSessionValid()) {
                mUserTokenDao.addToken(accessToken);
                Toast.makeText(LoginActivity.this,
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
                // 显示 Token, for debug
//                updateTokenView(false);
                //设置登录按钮不可见
                llButtons.setVisibility(View.INVISIBLE);
                login(new SimpleUser(accessToken));
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