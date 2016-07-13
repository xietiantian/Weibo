package bupt.tiantian.weibo.customview;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by tiantian on 16-7-11.
 */
public class PicOptionAlertDialog extends AlertDialog{
    protected PicOptionAlertDialog(Context context) {
        super(context);
    }

    protected PicOptionAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    protected PicOptionAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
