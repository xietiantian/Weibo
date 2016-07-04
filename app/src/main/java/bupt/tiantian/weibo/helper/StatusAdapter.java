package bupt.tiantian.weibo.helper;

/**
 * Created by tiantian on 16-6-21.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import bupt.tiantian.weibo.R;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    private StatusList mStatuses;
    private Context mContext;
    private LayoutInflater mInflater;

    public StatusAdapter(Context context, StatusList statuses) {
        this.mStatuses = statuses;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.status_card, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Status status = mStatuses.statusList.get(position);
        Status retweetStatus = null;

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

        DraweeController profileImgController = Fresco.newDraweeControllerBuilder()
                .setOldController(holder.ivProfile.getController())
                .setLowResImageRequest(ImageRequest.fromUri(status.user.profile_image_url))
                .setImageRequest(ImageRequest.fromUri(status.user.avatar_large))
                .build();
        holder.ivProfile.setController(profileImgController);

        DraweeController statusImgController = Fresco.newDraweeControllerBuilder()
                .setOldController(holder.ivStatus.getController())
                .setLowResImageRequest(ImageRequest.fromUri(status.thumbnail_pic))
                .setImageRequest(ImageRequest.fromUri(status.bmiddle_pic))
                .build();
        holder.ivStatus.setController(statusImgController);
    }


    @Override
    public int getItemCount() {
        return mStatuses == null ? 0 : mStatuses.statusList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView ivProfile;
        public TextView tvUserName;
        public TextView tvCreateTime;
        public TextView tvStatus;
        public TextView tvRetweetStatus;
        public View divider;
        public LinearLayout llInStatus;
        public SimpleDraweeView ivStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfile = (SimpleDraweeView) itemView.findViewById(R.id.ivProfile);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvCreateTime = (TextView) itemView.findViewById(R.id.tvCreateTime);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvRetweetStatus = (TextView) itemView.findViewById(R.id.tvRetweetStatus);
            divider = itemView.findViewById(R.id.divider);
            llInStatus = (LinearLayout) itemView.findViewById(R.id.llInStatus);
            ivStatus = (SimpleDraweeView) itemView.findViewById(R.id.ivStatus);
        }
    }

}

