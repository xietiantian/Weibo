package bupt.tiantian.weibo.helper;

/**
 * Created by tiantian on 16-6-21.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import java.io.InputStream;
import java.net.URL;

import bupt.tiantian.weibo.R;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    private StatusList mStatuses;
    private Context mContext;
    private LayoutInflater mInflater;
    //    private static DownloadHelper.DownloadImgTask downloadImgTask = null;

    public StatusAdapter(Context context,StatusList statuses) {
        this.mStatuses = statuses;
        this.mContext=context;
        mInflater=LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.status_card, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Status status = mStatuses.statusList.get(position);
        Status retweetStatus=null;

        holder.tvCreateTime.setText(status.created_at);
        holder.tvUserName.setText(status.user.screen_name);
        holder.tvStatus.setText(status.text);

        if (status.retweeted_status == null) {//原创微博
            holder.divider.setVisibility(View.GONE);
            holder.tvRetweetStatus.setVisibility(View.GONE);
        } else {//转发微博
            retweetStatus = status.retweeted_status;
            holder.tvRetweetStatus.setText("@" + retweetStatus.user.screen_name + ": " + retweetStatus.text);
        }

        DownloadImgTask downloadImgTask1 = new DownloadImgTask(holder.ivProfile);
        downloadImgTask1.execute(status.user.avatar_large);
        DownloadImgTask downloadImgTask2 = new DownloadImgTask(holder.ivStatus);
        downloadImgTask2.execute(status.thumbnail_pic);

    }


    @Override
    public int getItemCount() {
        return mStatuses == null ? 0 : mStatuses.statusList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfile;
        public TextView tvUserName;
        public TextView tvCreateTime;
        public TextView tvStatus;
        public TextView tvRetweetStatus;
        public View divider;
        public LinearLayout llInStatus;
        public ImageView ivStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfile = (ImageView) itemView.findViewById(R.id.ivProfile);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvCreateTime = (TextView) itemView.findViewById(R.id.tvCreateTime);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvRetweetStatus = (TextView) itemView.findViewById(R.id.tvRetweetStatus);
            divider = itemView.findViewById(R.id.divider);
            llInStatus = (LinearLayout) itemView.findViewById(R.id.llInStatus);
            ivStatus = (ImageView) itemView.findViewById(R.id.ivStatus);
        }
    }

    public class DownloadImgTask extends AsyncTask<String, Integer, Bitmap> {

        public boolean isRunning = false;
        private ImageView imageView;

        private DownloadImgTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            isRunning = true;
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
//            if (params == null || params.length == 0) {
//                int size = statuses.statusList.size();
//                for (int i = 0; i < size; i++) {
//                    updateRequired = downloadThumb(statuses.statusList.get(i));
//                    publishProgress(i);
//                    if (updateRequired) {
//                        break;
//                    }
//                }
//            } else
            if (params.length == 1) {
                try {
                    InputStream is = new URL(params[0]).openStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                publishProgress(0);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            isRunning = false;
            imageView.setImageBitmap(bitmap);
            super.onPostExecute(bitmap);
        }
    }
}

