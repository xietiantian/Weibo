package bupt.tiantian.weibo.customview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by tiantian on 16-8-24.
 */
public class DefaultOnLinkClickListener implements StatusTextView.OnLinkClickListener {

    public static final String TAG="onLinkClick";
    private Context mContext;

    public DefaultOnLinkClickListener(Context context) {
        mContext = context;
    }

    @Override
    public void onAtClick(String at) {
        Log.d(TAG,at);
        Toast.makeText(mContext, "at: " + at, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTopicClick(String topic) {
        Log.d(TAG,topic);
        Toast.makeText(mContext, "topic: " + topic, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUrlClick(String url) {
        Log.d(TAG,url);
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mContext.startActivity(it);
    }
}
