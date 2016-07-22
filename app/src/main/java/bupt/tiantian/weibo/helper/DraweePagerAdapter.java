package bupt.tiantian.weibo.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;

import java.util.ArrayList;

import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.activity.MainActivity;
import bupt.tiantian.weibo.util.NetChecker;
import me.relex.photodraweeview.OnViewTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by tiantian on 16-7-8.
 */
public class DraweePagerAdapter extends PagerAdapter {

    private static final String TAG = "DraweePagerAdapter";
    private ArrayList<PicUrl> mPicUrls;
    private Context mContext;
    private LayoutInflater mInflater;
    private int mViewHeight, mViewWidth;
    AlertDialog picOptAlertDialog;

    public DraweePagerAdapter(ArrayList<PicUrl> picUrls, Context context) {
        mPicUrls = picUrls;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        Rect frame = new Rect();
        ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        mViewHeight = frame.bottom - frame.top;
        mViewWidth = frame.right;
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
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final PhotoDraweeView photoDraweeView = new PhotoDraweeView(container.getContext());
        photoDraweeView.setTag(position);
        final PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        //大图已经加载过或者当前设置&网络环境允许加载大图
        final boolean largeUrlSet = mPicUrls.get(position).isLargeUrlSet() || NetChecker.getLargePicFlag();
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
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
            }

            @Override
            public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);

                int imgHeight = imageInfo.getHeight();
                int imgWidth = imageInfo.getWidth();
                if (mViewWidth != 0 && mViewHeight != 0) {
                    if (imgHeight / (float) imgWidth > mViewHeight / (float) mViewWidth) {
                        float scale = imgHeight * mViewWidth / (float) (imgWidth * mViewHeight);
                        photoDraweeView.setMaximumScale(scale * 3);
                        photoDraweeView.setMediumScale(scale);
                        photoDraweeView.setMinimumScale(1);
                        photoDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_START);
                    }
                }
                Log.d(TAG,"intermediate img height:"+imgHeight+" width:"+imgWidth);
                photoDraweeView.update(imgWidth, imgHeight);
            }

            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);

                if (imageInfo == null) {
                    return;
                }
                int imgHeight = imageInfo.getHeight();
                int imgWidth = imageInfo.getWidth();
                if (mViewWidth != 0 && mViewHeight != 0) {
                    if (imgHeight / (float) imgWidth > mViewHeight / (float) mViewWidth) {
                        float scale = imgHeight * mViewWidth / (float) (imgWidth * mViewHeight);
                        photoDraweeView.setMaximumScale(scale * 3);
                        photoDraweeView.setMediumScale(scale);
                        photoDraweeView.setMinimumScale(1);
                        photoDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_START);
                    }
                }
                Log.d(TAG,"img height:"+imgHeight+" width:"+imgWidth);
                photoDraweeView.update(imgWidth, imgHeight);
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
            public boolean onLongClick(android.view.View v) {
                showAlertDialog(largeUrlSet, position);//弹出alertdialog
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
