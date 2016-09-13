package bupt.tiantian.weibo.statuslistshow;

import android.content.Context;
import android.util.Log;
import android.view.View;

import bupt.tiantian.weibo.R;

/**
 * Created by tiantian on 16-8-24.
 */
public class OnStatusCardClickListener implements View.OnClickListener {
    private static final String TAG = "OnStatusClick";
    Context mContext;
    String mStatusId;

    public OnStatusCardClickListener(Context context,String statusId) {
        mContext=context;
        mStatusId=statusId;
    }

    @Override
    public void onClick(View v) {
        MainActivityFragment mainFrag= (MainActivityFragment) ((MainActivity)mContext).getSupportFragmentManager().findFragmentById(R.id.fragMain);
        if(mainFrag!=null){
            mainFrag.getInteractListener().onStatusCardClicked(mStatusId);
        }
    }
}
