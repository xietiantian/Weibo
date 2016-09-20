package bupt.tiantian.weibo.statuslistshow;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.sina.weibo.sdk.openapi.models.Status;

import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.statusshow.StatusFragment;
import bupt.tiantian.weibo.util.StatusParcelable;

/**
 * Created by tiantian on 16-8-24.
 */
public class OnStatusCardClickListener implements View.OnClickListener {
    private static final String TAG = "OnStatusClick";
    Context mContext;
    StatusParcelable mStatusParcelable;

    public OnStatusCardClickListener(Context context, Status status) {
        mContext = context;
        mStatusParcelable = new StatusParcelable(status);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.fragMain);
        if (fragment != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("status", mStatusParcelable);
            if (fragment instanceof MainActivityFragment) {
                ((MainActivityFragment) fragment).getInteractListener().onStatusCardClicked(bundle);
            } else if (fragment instanceof StatusFragment) {
                ((StatusFragment) fragment).getInteractListener().onStatusCardClicked(bundle);
            }
        }
    }
}