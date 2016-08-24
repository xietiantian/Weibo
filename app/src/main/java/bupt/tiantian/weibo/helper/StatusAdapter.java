package bupt.tiantian.weibo.helper;

/**
 * Created by tiantian on 16-6-21.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.customview.DefaultOnLinkClickListener;
import bupt.tiantian.weibo.customview.StatusTextView;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    private static final String TAG = "StatusAdapter";

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
        PicUrlHolder picUrlHolder = null;


        holder.tvCreateTime.setText(status.created_at);
        holder.tvUserName.setText(status.user.screen_name);
        holder.tvStatus.setText(status.text);
        holder.tvStatus.setOnLinkClickListener(new DefaultOnLinkClickListener(mContext));

        if (status.retweeted_status == null) {//原创微博
            holder.divider.setVisibility(View.GONE);
            holder.tvRetweetStatus.setVisibility(View.GONE);
            if (status.pic_urls != null && status.pic_urls.size() > 0) {
                picUrlHolder = new PicUrlHolder(status.pic_urls);
            }
        } else {//转发微博
            retweetStatus = status.retweeted_status;
            holder.tvRetweetStatus.setText("@" + retweetStatus.user.screen_name + ": " + retweetStatus.text);
            holder.tvRetweetStatus.setOnLinkClickListener(new DefaultOnLinkClickListener(mContext));
            if (retweetStatus.pic_urls != null && retweetStatus.pic_urls.size() > 0) {
                picUrlHolder = new PicUrlHolder(retweetStatus.pic_urls);
            }
        }

        //显示头像
        DraweeController profileImgController = Fresco.newDraweeControllerBuilder()
                .setOldController(holder.ivProfile.getController())
                .setLowResImageRequest(ImageRequest.fromUri(status.user.profile_image_url))
                .setImageRequest(ImageRequest.fromUri(status.user.avatar_large))
                .build();
        holder.ivProfile.setController(profileImgController);


        if (picUrlHolder != null) {//若有图，显示图片
            if (picUrlHolder.getLength() == 1 || picUrlHolder.getLength() == 2 || picUrlHolder.getLength() == 4) {
                holder.gridStatusImg.setNumColumns(2);
            } else {
                holder.gridStatusImg.setNumColumns(3);
            }
            ImgGridAdapter imgGridAdapter = new ImgGridAdapter(mContext, picUrlHolder);
            holder.gridStatusImg.setAdapter(imgGridAdapter);
            holder.gridStatusImg.setOnItemClickListener(new OnPicClickListener(mContext, picUrlHolder));
            holder.gridStatusImg.setVisibility(View.VISIBLE);
        } else {
            holder.gridStatusImg.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return mStatuses == null ? 0 : mStatuses.statusList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView ivProfile;
        public TextView tvUserName;
        public TextView tvCreateTime;
        public StatusTextView tvStatus;
        public StatusTextView tvRetweetStatus;
        public View divider;
        public LinearLayout llInStatus;
        public GridView gridStatusImg;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfile = (SimpleDraweeView) itemView.findViewById(R.id.ivProfile);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvCreateTime = (TextView) itemView.findViewById(R.id.tvCreateTime);
            tvStatus = (StatusTextView) itemView.findViewById(R.id.tvStatus);
            tvRetweetStatus = (StatusTextView) itemView.findViewById(R.id.tvRetweetStatus);
            divider = itemView.findViewById(R.id.divider);
            llInStatus = (LinearLayout) itemView.findViewById(R.id.llInStatus);
            gridStatusImg = (GridView) itemView.findViewById(R.id.gridStatusImg);

        }
    }
}

