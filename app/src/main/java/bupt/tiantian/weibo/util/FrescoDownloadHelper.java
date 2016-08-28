package bupt.tiantian.weibo.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bupt.tiantian.weibo.Constants;
import bupt.tiantian.weibo.MyHandler;

/**
 * Created by tiantian on 16-7-12.
 */
public class FrescoDownloadHelper implements OnPicDownloadListener {
    public static final String DEFAULT_IMG_PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "weibo" + File.separator + "img";

    private Context mContext;
    private MyHandler mHandler;

    public FrescoDownloadHelper(Context context) {
        mContext = context;
        mHandler = new MyHandler(mContext);
    }

    public void savePicture(final String urlString) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mHandler.sendEmptyMessage(Constants.MSG_SDCARD_ERROR);
            return;
        }

        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(urlString))
                .setProgressiveRenderingEnabled(true)
                .build();
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest);//Fresco可以根据图片url获取到磁盘缓存CacheKey
        File file = getCachedImageOnDisk(cacheKey);//通过CacheKey查找磁盘中有没有缓存文件
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        final String picName = dateFormat.format(new Date()) + (int) (Math.random() * 9000 + 1000);
        if (file != null) {//磁盘中有缓存文件，拷贝并重命名
            File savedPic = FileCopyHelper.copyFile(file, DEFAULT_IMG_PATH, picName);
            if (savedPic != null) {
                onDownloadSuccess(savedPic);
            } else {
                onDownloadFailed();
            }
        } else {//磁盘中没有缓存文件，去获取bitmap cache中的bitmap
            getImageFromCache(imageRequest, picName);
        }
    }

    public static File getCachedImageOnDisk(CacheKey cacheKey) {
        File localFile = null;
        if (cacheKey != null) {
            if (ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            } else if (ImagePipelineFactory.getInstance().getSmallImageFileCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageFileCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            }
        }

        return localFile;
    }

    public void getImageFromCache(ImageRequest imageRequest, final String filename) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(imageRequest, mContext);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(Bitmap bitmap) {
                if (bitmap == null) {
                    onDownloadFailed();
                    return;
                }
                File appDir = new File(DEFAULT_IMG_PATH);
                appDir.mkdirs();
                String fileName = filename + ".jpg";
                File file = new File(appDir, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    onDownloadSuccess(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    onDownloadFailed();
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                onDownloadFailed();
            }
        }, CallerThreadExecutor.getInstance());
    }

    @Override
    public void onDownloadSuccess(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        mContext.sendBroadcast(intent);//这个广播的目的是更新图库
        mHandler.sendEmptyMessage(Constants.MSG_SAVE_PIC_SUCCESS);
    }

    @Override
    public void onDownloadFailed() {
        mHandler.sendEmptyMessage(Constants.MSG_SAVE_PIC_FAIL);

    }
}
