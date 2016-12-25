package bupt.tiantian.weibo.models;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;

import bupt.tiantian.weibo.Constants;
import bupt.tiantian.weibo.sql.UserTokenDao;
import bupt.tiantian.weibo.sql.UserTokenServise;
import bupt.tiantian.weibo.statuslistshow.StatusAdapter;

/**
 * Created by tiantian on 16-12-14.
 */
public class SimpleUser {
    public static final String TAG = "SimpleUser";


    /**
     * 用户UID（int64）
     */
    private String mUID;
    /**
     * 用户昵称
     */
    private String mScreenName;
    /**
     * 用户大头像地址
     */
    private String mAvatarLarge;

    private Oauth2AccessToken mAccessToken;
//    private Context mContext;


    public SimpleUser(String uid, String accessToken, String refreshToken,
                      long expiresTime, String screenName, String avatarLarge){
        mUID = uid;
        mAvatarLarge = avatarLarge;
        mScreenName = screenName;
        mAccessToken = new Oauth2AccessToken(accessToken,null);
        mAccessToken.setUid(uid);
        mAccessToken.setRefreshToken(refreshToken);
        mAccessToken.setExpiresTime(expiresTime);
    }

    public SimpleUser(Oauth2AccessToken accessToken) {
        mAccessToken = accessToken;
        mUID = accessToken.getUid();
//        mContext =context;
    }

    public String getUID() {
        return mUID;
    }

    public Oauth2AccessToken getAccessToken() {
        return mAccessToken;
    }

    public String getAvatarLarge() {
        return mAvatarLarge;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public void setScreenName(String screenName) {
        this.mScreenName = screenName;
    }

    public void setAvatarLarge(String avatarLarge) {
        this.mAvatarLarge = avatarLarge;
    }
}
