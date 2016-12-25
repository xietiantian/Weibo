package bupt.tiantian.weibo.sql;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import java.util.List;

import bupt.tiantian.weibo.models.SimpleUser;

/**
 * Created by tiantian on 16-12-15.
 */
public interface UserTokenServise {
    //用来保存UserID、Access Token、Access Secret 的表名
    public static final String TB_NAME = "UserTokenTable";

    public boolean addToken(Oauth2AccessToken token);
    public boolean deleteUser(String uid);
    public boolean updateUser(String uid,String screenName,String avatarLarge);
    public List<SimpleUser> getTokenList();
    public List<SimpleUser> getUserTokenList();

}
