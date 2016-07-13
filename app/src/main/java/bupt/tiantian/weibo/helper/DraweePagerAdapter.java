package bupt.tiantian.weibo.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Animatable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;

import java.util.ArrayList;

import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.activity.MainActivity;
import me.relex.photodraweeview.OnViewTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by tiantian on 16-7-8.
 */
public class DraweePagerAdapter extends PagerAdapter {


    private ArrayList<PicUrl> mPicUrls;
    private Context mContext;
    private LayoutInflater mInflater;
    AlertDialog picOptAlertDialog;
    MyHandler mHandler;

    public DraweePagerAdapter(ArrayList<PicUrl> picUrls, Context context) {
        mPicUrls = picUrls;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (mPicUrls != null)
            return mPicUrls.size();
        else
            return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {//设置只刷新当前页
        View view = (View) object;
        int currentPage = ((MainActivity) mContext).getPicFragPagerIdx();
        if (currentPage == (int) view.getTag()) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final PhotoDraweeView photoDraweeView = new PhotoDraweeView(container.getContext());
        photoDraweeView.setTag(position);
        final PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        final boolean largeUrlSet = mPicUrls.get(position).isLargeUrlSet();
        controller.setOldController(photoDraweeView.getController());
        if (largeUrlSet) {
            controller.setLowResImageRequest(ImageRequest.fromUri(mPicUrls.get(position).getMiddleUrl()))
                    .setImageRequest(ImageRequest.fromUri(mPicUrls.get(position).getLargeUrl()));
        } else {
            controller.setLowResImageRequest(ImageRequest.fromUri(mPicUrls.get(position).getThumbnailUrl()))
                    .setImageRequest(ImageRequest.fromUri(mPicUrls.get(position).getMiddleUrl()));
        }
        controller.setAutoPlayAnimations(true);
        controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null) {
                    return;
                }
                photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        });
        photoDraweeView.setController(controller.build());
        //点击返回
        photoDraweeView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                ((MainActivity) mContext).getSupportFragmentManager().popBackStack();
            }
        });
        //长按弹出菜单
        photoDraweeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {//弹出alertdialog
                showAlertDialog(largeUrlSet, position);
                return true;
            }
        });

        try {
            container.addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoDraweeView;
    }

    public void showAlertDialog(final boolean largeUrlSet, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.title_pic_dialog);
        final FrescoDownloadHelper fdHelper = new FrescoDownloadHelper(mContext);
        if (largeUrlSet) {
            builder.setItems(R.array.OptionStringSet1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    fdHelper.savePicture(mPicUrls.get(position).getLargeUrl());
                                }
                            }).start();
                            break;
                        default:
                            break;

                    }
                }
            });
        } else {
            builder.setItems(R.array.OptionStringSet0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            mPicUrls.get(position).setLargeUrlFromThumb();
                            notifyDataSetChanged();
                            break;
                        case 1:
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    fdHelper.savePicture(mPicUrls.get(position).getMiddleUrl());
                                }
                            }).start();
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        picOptAlertDialog = builder.create();
        picOptAlertDialog.show();
    }
}
