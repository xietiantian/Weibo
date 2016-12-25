package bupt.tiantian.weibo.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import bupt.tiantian.weibo.models.SimpleUser;

/**
 * Created by tiantian on 16-12-14.
 */
public class SqliteHelper extends SQLiteOpenHelper {
    //数据库名称
    private static final String DB_NAME = "tiantianweibo.db";
    //数据库版本
    private static final int DB_VERSION = 1;


    public SqliteHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }
    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + UserTokenServise.TB_NAME + "("
                + UserTokenDao.KEY_UID + " integer primary key,  "
                + UserTokenDao.KEY_ACCESS_TOKEN + " text, "
                + UserTokenDao.KEY_REFRESH_TOKEN + " text, "
                + UserTokenDao.KEY_EXPIRES_IN + " integer, "
                + UserTokenDao.KEY_SCREEN_NAME + " text, "
                + UserTokenDao.KEY_AVATAR_LARGE + " text" + ")"
        );
        Log.e("Database", "onCreate");
    }

    //更新表
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserTokenServise.TB_NAME);
        onCreate(db);
        Log.e("Database", "onUpgrade");
    }

    //更新列
    public void updateColumn(SQLiteDatabase db, String oldColumn, String newColumn, String typeColumn) {
        try {
            db.execSQL("ALTER TABLE " + UserTokenServise.TB_NAME
                    + " CHANGE " + oldColumn + " " + newColumn + " " + typeColumn
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
