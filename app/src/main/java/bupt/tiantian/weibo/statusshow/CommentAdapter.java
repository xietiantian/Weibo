package bupt.tiantian.weibo.statusshow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;

import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.imgshow.PicUrlHolder;
import bupt.tiantian.weibo.statuslistshow.NoScrollGridView;
import bupt.tiantian.weibo.statuslistshow.OnStatusCardClickListener;
import bupt.tiantian.weibo.statuslistshow.OnStatusTextClickListener;
import bupt.tiantian.weibo.statuslistshow.StatusTextView;
import bupt.tiantian.weibo.util.Num2String;

/**
 * Created by tiantian on 16-9-20.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private static final String TAG = "StatusAdapter";

    private CommentList mComments;
    private Context mContext;
    private LayoutInflater mInflater;

    public CommentAdapter(Context context, CommentList comments) {
        this.mComments = comments;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.comment, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Comment comment = mComments.commentList.get(position);

        holder.tvCommentCreateTime.setText(comment.created_at);
        holder.tvCommentUserName.setText(comment.user.screen_name);
        holder.tvComment.setText(comment.text);
        //显示头像
        DraweeController profileImgController = Fresco.newDraweeControllerBuilder()
                .setOldController(holder.ivCommentProfile.getController())
                .setLowResImageRequest(ImageRequest.fromUri(comment.user.profile_image_url))
                .setImageRequest(ImageRequest.fromUri(comment.user.avatar_large))
                .build();
        holder.ivCommentProfile.setController(profileImgController);
    }

    @Override
    public int getItemCount() {
        return mComments == null ? 0 : mComments.commentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView ivCommentProfile;
        public TextView tvCommentUserName;
        public TextView tvCommentCreateTime;
        public StatusTextView tvComment;



        public ViewHolder(View itemView) {
            super(itemView);

            ivCommentProfile = (SimpleDraweeView) itemView.findViewById(R.id.ivCommentProfile);
            tvCommentUserName = (TextView) itemView.findViewById(R.id.tvCommentUserName);
            tvCommentCreateTime = (TextView) itemView.findViewById(R.id.tvCommentCreateTime);
            tvComment = (StatusTextView) itemView.findViewById(R.id.tvComment);
        }
    }
}
