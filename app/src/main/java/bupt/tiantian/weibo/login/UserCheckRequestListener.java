package bupt.tiantian.weibo.login;

import android.text.TextUtils;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;

import bupt.tiantian.weibo.models.SimpleUser;
import bupt.tiantian.weibo.sql.UserTokenServise;

/**
 * Created by tiantian on 16-12-15.
 */
public class UserCheckRequestListener implements RequestListener {
    private static final String TAG = "UserCheckRequest";
    private UserTokenServise mUserTokenDao;
    private SimpleUser mUser;

    public UserCheckRequestListener(UserTokenServise userTokenServise, SimpleUser user) {
        mUserTokenDao = userTokenServise;
        mUser = user;
    }

    @Override
    public void onComplete(String response) {
        if (!TextUtils.isEmpty(response)) {
            LogUtil.i(TAG, response);
            // 调用 StatusList#parse 解析字符串成微博列表对象
            User user = User.parse(response);
            if (user != null) {
                mUserTokenDao.updateUser(user.id, user.screen_name, user.avatar_large);
            }
        }
    }

    @Override
    public void onWeiboException(WeiboException e) {
        ErrorInfo info = ErrorInfo.parse(e.getMessage());
        LogUtil.e(TAG, e.getMessage());
        LogUtil.e(TAG, info.toString());
        mUserTokenDao.deleteUser(mUser.getUID());
    }
}
