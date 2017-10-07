package bupt.tiantian.weibo.sql;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import java.util.ArrayList;
import java.util.List;

import bupt.tiantian.weibo.models.SimpleUser;

/**
 * Created by tiantian on 16-12-15.
 */
public class UserTokenDao implements UserTokenServise {
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_UID = "uid";
    public static final String KEY_EXPIRES_IN = "expires_in";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_SCREEN_NAME = "screen_name";//用户昵称
    public static final String KEY_AVATAR_LARGE = "avatar_large";//用户头像
    //获得 helper对象用来操纵数据库  
    private SqliteHelper mHelper = null;

    public UserTokenDao(Context context) {
        mHelper = new SqliteHelper(context);
    }

    @Override
    public boolean addToken(Oauth2AccessToken token) {
        SQLiteDatabase database = null;
        boolean flag = false;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_UID, token.getUid());
            values.put(KEY_ACCESS_TOKEN, token.getToken());
            values.put(KEY_REFRESH_TOKEN, token.getRefreshToken());
            values.put(KEY_EXPIRES_IN, token.getExpiresTime());
            database = mHelper.getWritableDatabase();
            database.insertOrThrow(TB_NAME, null, values);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return flag;
    }

    @Override
    public boolean deleteUser(String uid) {
        SQLiteDatabase database = null;
        boolean flag = false;
        try {
            database = mHelper.getWritableDatabase();
            database.delete(TB_NAME, "uid=?", new String[]{uid});
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return flag;
    }


    @Override
    public boolean updateUser(String uid, String screenName, String avatarLarge) {
        SQLiteDatabase database = null;
        boolean flag = false;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_SCREEN_NAME, screenName);
            values.put(KEY_AVATAR_LARGE, avatarLarge);
            database = mHelper.getWritableDatabase();
            database.update(TB_NAME, values, KEY_UID + " = ?", new String[]{uid});
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return flag;
    }

    @Override
    public List<SimpleUser> getTokenList() {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        List<SimpleUser> userList = new ArrayList<>();
        try {
            database = mHelper.getReadableDatabase();
            cursor = database.query(TB_NAME, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                SimpleUser user = new SimpleUser(
                        cursor.getString(cursor.getColumnIndex(KEY_UID)),
                        cursor.getString(cursor.getColumnIndex(KEY_ACCESS_TOKEN)),
                        cursor.getString(cursor.getColumnIndex(KEY_REFRESH_TOKEN)),
                        cursor.getLong(cursor.getColumnIndex(KEY_EXPIRES_IN)),
                        null, null);
                userList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return userList;
    }

    @Override
    public List<SimpleUser> getUserTokenList() {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        List<SimpleUser> userList = new ArrayList<>();
        try {
            database = mHelper.getReadableDatabase();
            cursor = database.query(TB_NAME, null, null, null, null, null, null);
            String screenName;
            while (cursor.moveToNext()) {
                screenName = cursor.getString(cursor.getColumnIndex(KEY_SCREEN_NAME));
                if (!TextUtils.isEmpty(screenName)) {
                    SimpleUser user = new SimpleUser(
                            cursor.getString(cursor.getColumnIndex(KEY_UID)),
                            cursor.getString(cursor.getColumnIndex(KEY_ACCESS_TOKEN)),
                            cursor.getString(cursor.getColumnIndex(KEY_REFRESH_TOKEN)),
                            cursor.getLong(cursor.getColumnIndex(KEY_EXPIRES_IN)),
                            cursor.getString(cursor.getColumnIndex(KEY_SCREEN_NAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_AVATAR_LARGE)));
                    userList.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return userList;
    }
}
