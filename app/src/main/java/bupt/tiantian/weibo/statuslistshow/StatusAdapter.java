package bupt.tiantian.weibo.statuslistshow;

/**
 * Created by tiantian on 16-6-21.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.imgshow.PicUrlHolder;
import bupt.tiantian.weibo.statusshow.OnPicClickListener;
import bupt.tiantian.weibo.statusshow.OnStatusCardClickListener;
import bupt.tiantian.weibo.statusshow.OnStatusTextClickListener;
import bupt.tiantian.weibo.util.Num2String;

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
        holder.createStatusCrad(status, mContext);
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
        public RelativeLayout rlStatus;
        public RelativeLayout rlRetweetStatus;
        public NoScrollGridView gridStatusImg;
        public NoScrollGridView gridRetweetStatusImg;
        public Button btnLike;
        public Button btnRetweet;
        public Button btnComment;


        public ViewHolder(View itemView) {
            super(itemView);

            ivProfile = (SimpleDraweeView) itemView.findViewById(R.id.ivProfile);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvCreateTime = (TextView) itemView.findViewById(R.id.tvCreateTime);
            tvStatus = (StatusTextView) itemView.findViewById(R.id.tvStatus);
            tvRetweetStatus = (StatusTextView) itemView.findViewById(R.id.tvRetweetStatus);
            divider = itemView.findViewById(R.id.divider);
            rlStatus = (RelativeLayout) itemView.findViewById(R.id.rlStatus);
            rlRetweetStatus = (RelativeLayout) itemView.findViewById(R.id.rlRetweetStatus);
            gridStatusImg = (NoScrollGridView) itemView.findViewById(R.id.gridStatusImg);
            gridRetweetStatusImg = (NoScrollGridView) itemView.findViewById(R.id.gridRetweetStatusImg);
            btnComment = (Button) itemView.findViewById(R.id.btnComment);
            btnLike = (Button) itemView.findViewById(R.id.btnLike);
            btnRetweet = (Button) itemView.findViewById(R.id.btnRetweet);
        }

        public void createStatusCrad(Status status, Context context) {
            Status retweetStatus = null;
            PicUrlHolder picUrlHolder = null;

            tvCreateTime.setText(status.created_at);
            tvUserName.setText(status.user.screen_name);
            tvStatus.setText(status.text);
            if (status.reposts_count > 0) {
                btnRetweet.setText(Num2String.transform(status.reposts_count));
            }
            if (status.attitudes_count > 0) {
                btnLike.setText(Num2String.transform(status.attitudes_count));
            }
            if (status.comments_count > 0) {
                btnComment.setText(Num2String.transform(status.comments_count));
            }
            tvStatus.setOnLinkClickListener(new OnStatusTextClickListener(context));

            NoScrollGridView gridImg = gridStatusImg;
            if (status.retweeted_status == null) {//原创微博
                rlRetweetStatus.setVisibility(View.GONE);
                if (status.pic_urls != null && status.pic_urls.size() > 0) {
                    picUrlHolder = new PicUrlHolder(status.pic_urls);
                } else {
                    gridStatusImg.setVisibility(View.GONE);
                }
            } else {//转发微博
                retweetStatus = status.retweeted_status;
                tvRetweetStatus.setText("@" + retweetStatus.user.screen_name + ": " + retweetStatus.text);
                tvRetweetStatus.setOnLinkClickListener(new OnStatusTextClickListener(context));
                if (retweetStatus.pic_urls != null && retweetStatus.pic_urls.size() > 0) {
                    picUrlHolder = new PicUrlHolder(retweetStatus.pic_urls);
                    gridImg = gridRetweetStatusImg;
                } else {
                    gridRetweetStatusImg.setVisibility(View.GONE);
                }
                rlRetweetStatus.setOnClickListener(new OnStatusCardClickListener(context, retweetStatus));
            }
            rlStatus.setOnClickListener(new OnStatusCardClickListener(context, status));

            //显示头像
            DraweeController profileImgController = Fresco.newDraweeControllerBuilder()
                    .setOldController(ivProfile.getController())
                    .setLowResImageRequest(ImageRequest.fromUri(status.user.profile_image_url))
                    .setImageRequest(ImageRequest.fromUri(status.user.avatar_large))
                    .build();
            ivProfile.setController(profileImgController);


            if (picUrlHolder != null) {//若有图，显示图片
                if (picUrlHolder.getLength() == 1 || picUrlHolder.getLength() == 2 || picUrlHolder.getLength() == 4) {
                    gridImg.setNumColumns(2);
                } else {
                    gridImg.setNumColumns(3);
                }
                ImgGridAdapter imgGridAdapter = new ImgGridAdapter(context, picUrlHolder);
                gridImg.setAdapter(imgGridAdapter);
                gridImg.setOnItemClickListener(new OnPicClickListener(context, picUrlHolder));
                gridImg.setOnTouchInvalidPositionListener(new NoScrollGridView.OnTouchInvalidPositionListener() {
                    @Override
                    public boolean onTouchInvalidPosition(int motionEvent) {
                        return false; //不终止路由事件让父级控件处理事件
                    }
                });
                gridImg.setVisibility(View.VISIBLE);
            }
        }
    }
}

