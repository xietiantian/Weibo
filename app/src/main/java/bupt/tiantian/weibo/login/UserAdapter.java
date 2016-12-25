package bupt.tiantian.weibo.login;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;

import java.util.List;

import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.models.SimpleUser;


/**
 * Created by tiantian on 16-12-21.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private static final String TAG = "AvailableUserAdapter";
    private static final String USER_SELECTED = "[启用]";

    private int mSelected;
    private List<SimpleUser> mUsers;
    private Context mContext;
    private LayoutInflater mInflater;

    public UserAdapter(Context context, List<SimpleUser> users, int defaultIndex) {
        this.mUsers = users;
        this.mContext = context;
        this.mSelected = defaultIndex;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.user_card, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position < mUsers.size()) {
            final SimpleUser user = mUsers.get(position);
            holder.createUserCrad(user, mContext);
            if (mSelected == position) {
                holder.tvUserState.setText(USER_SELECTED);
            } else {
                holder.tvUserState.setText("");
            }
        } else {
            holder.tvScreenName.setText("添加账户");
        }
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSelected = holder.getAdapterPosition();
//                if(mSelected == mUsers.size()){
//
//                }
//                notifyDataSetChanged();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvScreenName;
        public SimpleDraweeView ivUserAvatar;
        public TextView tvUserState;
        public View userCard;
        public View rlItem;
        public ImageView ivLogin;
        public ImageView ivLogout;


        public ViewHolder(View itemView) {
            super(itemView);
            rlItem = itemView.findViewById(R.id.rlItem);
            userCard = itemView.findViewById(R.id.user_card);
            tvUserState = (TextView) itemView.findViewById(R.id.tvUserState);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            ivUserAvatar = (SimpleDraweeView) itemView.findViewById(R.id.ivUserAvatar);
            ivLogin = (ImageView) itemView.findViewById(R.id.ivLogin);
            ivLogout = (ImageView) itemView.findViewById(R.id.ivLogout);

        }

        public void createUserCrad(SimpleUser user, Context context) {
            tvScreenName.setText(user.getScreenName());
            //显示头像
            DraweeController profileImgController = Fresco.newDraweeControllerBuilder()
                    .setOldController(ivUserAvatar.getController())
                    .setImageRequest(ImageRequest.fromUri(user.getAvatarLarge()))
                    .build();
            ivUserAvatar.setController(profileImgController);
        }
    }
}
