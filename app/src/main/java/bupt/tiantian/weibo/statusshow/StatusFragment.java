package bupt.tiantian.weibo.statusshow;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.utils.LogUtil;

import bupt.tiantian.weibo.login.AccessTokenKeeper;
import bupt.tiantian.weibo.Constants;
import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.imgshow.PicUrlHolder;
import bupt.tiantian.weibo.statuslistshow.ImgGridAdapter;
import bupt.tiantian.weibo.statuslistshow.NoScrollGridView;
import bupt.tiantian.weibo.statuslistshow.StatusTextView;
import bupt.tiantian.weibo.util.Num2String;
import bupt.tiantian.weibo.util.StatusParcelable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {
    private static final String TAG = "StatusFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATUS = "status";

    // TODO: Rename and change types of parameters
    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;
    /**
     * 用于获取评论等操作的API
     */
    private CommentsAPI mCommentsAPI = null;

    private Status mStatus;
    private View mStatusFragmentView;
    private RecyclerView mRecyclerView;
    private CommentAdapter mAdapter;
    private OnFragmentInteractionListener mInteractListener;
    private LinearLayout mStatusCard;

    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param args
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(Bundle args) {
        StatusFragment fragment = new StatusFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mStatus = ((StatusParcelable) bundle.getParcelable(STATUS)).mStatus;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this.getActivity());
        // 对statusAPI实例化
        mCommentsAPI = new CommentsAPI(this.getActivity(), Constants.APP_KEY, mAccessToken);

        // Inflate the layout for this fragment
        mStatusFragmentView = inflater.inflate(R.layout.fragment_status, container, false);
        mStatusCard = (LinearLayout) mStatusFragmentView.findViewById(R.id.status_card);
        StatusCardViewHolder holder = new StatusCardViewHolder(this.getContext(), mStatusCard);
        holder.createView(mStatus);
        mRecyclerView = (RecyclerView) mStatusFragmentView.findViewById(R.id.rvCommentsList);

        //获得RecyclerViewHeader对象
        RecyclerViewHeader header = (RecyclerViewHeader) mStatusFragmentView.findViewById(R.id.header);
        // set LayoutManager for RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        //Attach RecyclerViewHeader to your RecyclerView:
        header.attachTo(mRecyclerView);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setVisibility(View.INVISIBLE);
        new Thread(new CommentRefreshThread()).start();
        return mStatusFragmentView;
    }

    class CommentRefreshThread implements Runnable {
        @Override
        public void run() {
            if (mCommentsAPI != null) {
                mCommentsAPI.show(Long.parseLong(mStatus.id), 0L, 0L, 10, 1, 0, mRequestListener);//参数3：加载10条评论
            }
        }
    }

    private RequestListener mRequestListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                if (response.startsWith("{\"comments\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    CommentList comments = CommentList.parse(response);
                    if (comments != null && comments.total_number > 0) {
                        mAdapter = new CommentAdapter(getContext(), comments);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(getContext(), info.toString(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mInteractListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInteractListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public OnFragmentInteractionListener getInteractListener() {
        return mInteractListener;
    }

    public interface OnFragmentInteractionListener {
        void onStatusPicClicked(Bundle bundle);

        void onStatusCardClicked(Bundle bundle);
    }

    public static class StatusCardViewHolder {
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
        public Context mContext;


        public StatusCardViewHolder(Context context, View statusCard) {
            ivProfile = (SimpleDraweeView) statusCard.findViewById(R.id.ivProfile);
            tvUserName = (TextView) statusCard.findViewById(R.id.tvUserName);
            tvCreateTime = (TextView) statusCard.findViewById(R.id.tvCreateTime);
            tvStatus = (StatusTextView) statusCard.findViewById(R.id.tvStatus);
            tvRetweetStatus = (StatusTextView) statusCard.findViewById(R.id.tvRetweetStatus);
            divider = statusCard.findViewById(R.id.divider);
            rlStatus = (RelativeLayout) statusCard.findViewById(R.id.rlStatus);
            rlRetweetStatus = (RelativeLayout) statusCard.findViewById(R.id.rlRetweetStatus);
            gridStatusImg = (NoScrollGridView) statusCard.findViewById(R.id.gridStatusImg);
            gridRetweetStatusImg = (NoScrollGridView) statusCard.findViewById(R.id.gridRetweetStatusImg);
            btnComment = (Button) statusCard.findViewById(R.id.btnComment);
            btnLike = (Button) statusCard.findViewById(R.id.btnLike);
            btnRetweet = (Button) statusCard.findViewById(R.id.btnRetweet);
            mContext = context;
        }

        public void createView(Status status) {
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
            tvStatus.setOnLinkClickListener(new OnStatusTextClickListener(mContext));

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
                tvRetweetStatus.setOnLinkClickListener(new OnStatusTextClickListener(mContext));
                if (retweetStatus.pic_urls != null && retweetStatus.pic_urls.size() > 0) {
                    picUrlHolder = new PicUrlHolder(retweetStatus.pic_urls);
                    gridImg = gridRetweetStatusImg;
                } else {
                    gridRetweetStatusImg.setVisibility(View.GONE);
                }
                rlRetweetStatus.setOnClickListener(new OnStatusCardClickListener(mContext, retweetStatus));
            }

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
                ImgGridAdapter imgGridAdapter = new ImgGridAdapter(mContext, picUrlHolder);
                gridImg.setAdapter(imgGridAdapter);
                gridImg.setOnItemClickListener(new OnPicClickListener(mContext, picUrlHolder));
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
