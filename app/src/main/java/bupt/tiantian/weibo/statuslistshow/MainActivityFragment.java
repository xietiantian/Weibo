package bupt.tiantian.weibo.statuslistshow;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

import bupt.tiantian.weibo.login.AccessTokenKeeper;
import bupt.tiantian.weibo.Constants;
import bupt.tiantian.weibo.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String TAG = "MainActivityFragment";
    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;
    /**
     * 用于获取微博信息流等操作的API
     */
    private StatusesAPI mStatusesAPI = null;
    private ProgressBar mProgressBar;
    private StatusAdapter mAdapter;

    /**
     * UI控件
     */
    private RecyclerView mRecyclerView;
    private View mMainFragmentView;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    private OnFragmentInteractionListener mInteractListener;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this.getActivity());
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this.getActivity(), Constants.APP_KEY, mAccessToken);


        mMainFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) mMainFragmentView.findViewById(R.id.rvStatusList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mProgressBar = (ProgressBar) mMainFragmentView.findViewById(R.id.progressBar);


        mSwipeRefreshLayout = (SwipeRefreshLayout) mMainFragmentView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new StatusRefreshThread()).start();
            }
        });
//        mSwipeRefreshLayout.setRefreshing(true);

        //首次刷新
        new Thread(new StatusRefreshThread()).start();
        //显示中央的进度条，不显示微博``
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);

        return mMainFragmentView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        if (context instanceof OnFragmentInteractionListener) {
            mInteractListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private RequestListener mRequestListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        Toast.makeText(getContext(),
                                "获取微博信息流成功, 条数: " + statuses.statusList.size(),
                                Toast.LENGTH_LONG).show();
                        mAdapter = new StatusAdapter(getContext(), statuses);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
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

    class StatusRefreshThread implements Runnable {
        @Override
        public void run() {
            if (mStatusesAPI != null) {
                mStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mRequestListener);//参数3：加载10条微博
            }
        }
    }


    public OnFragmentInteractionListener getInteractListener() {
        return mInteractListener;
    }

    public interface OnFragmentInteractionListener {
        void onStatusPicClicked(Bundle bundle);
        void onStatusCardClicked(Bundle bundle);
        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
    }
}
