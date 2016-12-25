package bupt.tiantian.weibo;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import java.util.List;

import bupt.tiantian.weibo.models.SimpleUser;
import bupt.tiantian.weibo.sql.SqliteHelper;
import bupt.tiantian.weibo.sql.UserTokenDao;
import bupt.tiantian.weibo.sql.UserTokenServise;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testCreateDB() {
        SqliteHelper helper = new SqliteHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
    }

    public void testAddToken() {
        Oauth2AccessToken token = new Oauth2AccessToken("abc", null);
        token.setUid("123");
        token.setRefreshToken("def");
        token.setExpiresTime(456);
        UserTokenServise servise = new UserTokenDao(getContext());
        servise.addToken(token);
    }

    public void testUpdateUser() {
        Oauth2AccessToken token = new Oauth2AccessToken("abc", null);
        token.setUid("123");
        token.setRefreshToken("def");
        token.setExpiresTime(456);
        SimpleUser user = new SimpleUser(token);
        user.setAvatarLarge("hahahahah");
        user.setScreenName("aaaaaaa");
        UserTokenServise servise = new UserTokenDao(getContext());
        servise.updateUser(user.getUID(),user.getScreenName(),user.getAvatarLarge());
    }

    public void testGetUserList() {
        String TAG = "testGetUserList";
        UserTokenServise servise = new UserTokenDao(getContext());
        List<SimpleUser> list = servise.getTokenList();
        SimpleUser user = null;
        for(int i=0;i<list.size();i++){
            user = list.get(i);
            if(user!=null){
                Log.d(TAG, "UID: " + user.getUID());
                Log.d(TAG, "ScreenName: "+user.getScreenName());
                Log.d(TAG, "AvatarLarge: "+user.getAvatarLarge());
                Log.d(TAG, "AccessToken: "+user.getAccessToken().getToken());
                Log.d(TAG, "RefreshToken: "+user.getAccessToken().getRefreshToken());
                Log.d(TAG, "ExpiresIn: "+user.getAccessToken().getExpiresTime());
            }
        }
    }
}