package bupt.tiantian.weibo.helper;

import java.io.File;

/**
 * Created by tiantian on 16-7-19.
 */
public interface OnPicDownloadListener {
    public void onDownloadSuccess(File file);
    public void onDownloadFailed();
}
