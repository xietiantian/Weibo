package bupt.tiantian.weibo.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;

import bupt.tiantian.weibo.R;

/**
 * Created by tiantian on 16-7-5.
 */
public class ImgGridAdapter extends BaseAdapter {
    private PicUrlHolder mPicUrlHolder;
    private Context mContext;
    private LayoutInflater mInflater;

    public ImgGridAdapter(Context context, PicUrlHolder picUrlHolder) {
        mContext=context;
        mInflater=LayoutInflater.from(mContext);
        mPicUrlHolder=picUrlHolder;
    }


    @Override
    public int getCount() {
        return mPicUrlHolder.getLength();
    }

    @Override
    public Object getItem(int position) {
        if(position<getCount()){
            return mPicUrlHolder.getPicUrlList().get(position);
        }else{
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PicUrl picUrl= (PicUrl) getItem(position);
        View view;
        ViewHolder holder;
        if(convertView==null) {
            view=mInflater.inflate(R.layout.image_in_grid,parent,false);
            holder=new ViewHolder(R.id.ivStatusInGrid,view);
            view.setTag(holder);
        }else{
            view = convertView;
            holder= (ViewHolder) view.getTag();
        }
        DraweeController statusImgController = Fresco.newDraweeControllerBuilder()
                .setOldController(holder.ivStatusInGrid.getController())
                .setLowResImageRequest(ImageRequest.fromUri(picUrl.getThumbnailUrl()))
                .setImageRequest(ImageRequest.fromUri(picUrl.getMiddleUrl()))
                .setAutoPlayAnimations(true)
                .build();
        holder.ivStatusInGrid.setController(statusImgController);
        return view;
    }

    class ViewHolder{
        SimpleDraweeView ivStatusInGrid;

        public ViewHolder(int draweeViewId,View view){
            ivStatusInGrid= (SimpleDraweeView) view.findViewById(draweeViewId);
        }
    }
}
