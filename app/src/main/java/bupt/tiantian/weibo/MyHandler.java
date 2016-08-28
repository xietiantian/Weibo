package bupt.tiantian.weibo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by tiantian on 16-7-12.
 */
public class MyHandler extends Handler {
    private Context mContext;

    public MyHandler(Context context){
        super();
        mContext=context;
    }
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case Constants.MSG_SAVE_PIC_SUCCESS:
                Toast.makeText(mContext, R.string.save_pic_success, Toast.LENGTH_SHORT).show();
                break;
            case Constants.MSG_SAVE_PIC_FAIL:
                Toast.makeText(mContext, R.string.save_pic_fail, Toast.LENGTH_SHORT).show();
                break;
            case Constants.MSG_SDCARD_ERROR:
                Toast.makeText(mContext, R.string.sdcard_state_error, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
