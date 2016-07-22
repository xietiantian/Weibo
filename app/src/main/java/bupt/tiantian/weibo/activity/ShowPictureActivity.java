package bupt.tiantian.weibo.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.customview.MultiTouchViewPager;
import bupt.tiantian.weibo.helper.DraweePagerAdapter;
import bupt.tiantian.weibo.helper.PicUrl;
import me.relex.circleindicator.CircleIndicator;


public class ShowPictureActivity extends AppCompatActivity {
    private static final String PIC_URLS = "url";
    private static final String POSITION = "position";
    private ArrayList<PicUrl> mPicUrls;
    private int mPosition;
    private MultiTouchViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);

        Bundle bundle = getIntent().getExtras();
        mPicUrls = bundle.getParcelableArrayList(PIC_URLS);
        mPosition = bundle.getInt(POSITION);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        mViewPager = (MultiTouchViewPager) findViewById(R.id.vpShowPic);
        mViewPager.setAdapter(new DraweePagerAdapter(mPicUrls, ShowPictureActivity.this));
        mViewPager.setCurrentItem(mPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        indicator.setViewPager(mViewPager);

        mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    public int getPosition() {
        return mPosition;
    }
}
