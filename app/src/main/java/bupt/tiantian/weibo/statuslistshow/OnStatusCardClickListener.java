package bupt.tiantian.weibo.statuslistshow;

import android.util.Log;
import android.view.View;

/**
 * Created by tiantian on 16-8-24.
 */
public class OnStatusCardClickListener implements View.OnClickListener {
    private static final String TAG = "OnStatusClick";

    public OnStatusCardClickListener() {

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "on item click");
    }
}
