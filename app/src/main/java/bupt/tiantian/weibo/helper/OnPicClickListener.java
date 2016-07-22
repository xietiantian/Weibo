package bupt.tiantian.weibo.helper;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.activity.MainActivity;
import bupt.tiantian.weibo.activity.MainActivityFragment;
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
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putBoolean("large",NetChecker.getLargePicFlag());
        bundle.putParcelableArrayList("url", mPicUrlHolder.getPicUrlList());
        MainActivityFragment mainFrag= (MainActivityFragment) ((MainActivity)mContext).getSupportFragmentManager().findFragmentById(R.id.fragMain);
        if(mainFrag!=null){
            mainFrag.getInteractListener().onStatusPicClicked(bundle);
        }
    }
}
