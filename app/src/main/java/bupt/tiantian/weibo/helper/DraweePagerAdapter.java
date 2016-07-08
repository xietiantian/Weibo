package bupt.tiantian.weibo.helper;

import android.graphics.drawable.Animatable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;

import java.util.ArrayList;

import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by tiantian on 16-7-8.
 */
public class DraweePagerAdapter extends PagerAdapter {

    private ArrayList<PicUrl> mPicUrls;

    public DraweePagerAdapter(ArrayList<PicUrl> picUrls) {
        mPicUrls = picUrls;
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
    public Object instantiateItem(ViewGroup container, int position) {
        final PhotoDraweeView photoDraweeView = new PhotoDraweeView(container.getContext());
        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        controller.setOldController(photoDraweeView.getController())
                .setLowResImageRequest(ImageRequest.fromUri(mPicUrls.get(position).getThumbnailUrl()))
                .setImageRequest(ImageRequest.fromUri(mPicUrls.get(position).getMiddleUrl()))
                .setAutoPlayAnimations(true);
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

        try {
            container.addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoDraweeView;
    }
}
