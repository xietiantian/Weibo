package bupt.tiantian.weibo.statusshow;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;

import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.imgshow.PicUrlHolder;
import bupt.tiantian.weibo.statuslistshow.MainActivity;
import bupt.tiantian.weibo.statuslistshow.MainActivityFragment;
import bupt.tiantian.weibo.statusshow.StatusFragment;
import bupt.tiantian.weibo.util.NetChecker;

/**
 * Created by tiantian on 16-7-5.
 */
public class OnPicClickListener implements AdapterView.OnItemClickListener {

    private PicUrlHolder mPicUrlHolder = null;
    private Context mContext = null;

    public OnPicClickListener(Context context, PicUrlHolder picUrlHolder) {
        super();
        this.mPicUrlHolder = picUrlHolder;
        this.mContext = context;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.fragMain);
        if (fragment != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            bundle.putBoolean("large", NetChecker.getLargePicFlag());
            bundle.putParcelableArrayList("url", mPicUrlHolder.getPicUrlList());
            if (fragment instanceof MainActivityFragment) {
                ((MainActivityFragment) fragment).getInteractListener().onStatusPicClicked(bundle);
            } else if (fragment instanceof StatusFragment) {
                ((StatusFragment) fragment).getInteractListener().onStatusPicClicked(bundle);
            }
        }
    }
}
