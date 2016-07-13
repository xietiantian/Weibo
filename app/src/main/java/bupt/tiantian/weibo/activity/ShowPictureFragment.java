package bupt.tiantian.weibo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.customview.MultiTouchViewPager;
import bupt.tiantian.weibo.helper.DraweePagerAdapter;
import bupt.tiantian.weibo.helper.PicUrl;
import me.relex.circleindicator.CircleIndicator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowPictureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowPictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowPictureFragment extends Fragment {
    private static final String PIC_URLS = "url";
    private static final String POSITION = "position";
    private ArrayList<PicUrl> mPicUrls;
    private int mPosition;
    private MultiTouchViewPager mViewPager;
    private OnFragmentInteractionListener mListener;

    /**
     * UI控件
     */
    private View mShowPicFragmentView;


    public ShowPictureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Fragment可显示的所有图片的地址list
     * @param param2 打开时显示list中param2位置的图片
     * @return A new instance of fragment ShowPicture.
     */
    public static ShowPictureFragment newInstance(ArrayList<PicUrl> param1, int param2) {
        ShowPictureFragment fragment = new ShowPictureFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PIC_URLS, param1);
        args.putInt(POSITION, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPicUrls = getArguments().getParcelableArrayList(PIC_URLS);
            mPosition = getArguments().getInt(POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mShowPicFragmentView = inflater.inflate(R.layout.fragment_show_picture, container, false);
        CircleIndicator indicator = (CircleIndicator) mShowPicFragmentView.findViewById(R.id.indicator);
        mViewPager = (MultiTouchViewPager) mShowPicFragmentView.findViewById(R.id.vpShowPic);
        mViewPager.setAdapter(new DraweePagerAdapter(mPicUrls, this.getContext()));
        mViewPager.setCurrentItem(mPosition);
        mListener.onPageSelectedChange(mPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mListener.onPageSelectedChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        indicator.setViewPager(mViewPager);
        return mShowPicFragmentView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        //        void onShowPicFragCreate();
        void onPageSelectedChange(int position);

    }
}
