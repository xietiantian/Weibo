package bupt.tiantian.weibo.helper;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tiantian on 16-7-5.
 */
public class PicUrlHolder {
    public static final Pattern THUMB_PATTERN = Pattern.compile("thumbnail");
    public static final String MIDDLE_PIC_STRING = "bmiddle";
    public static final String LARGE_PIC_STRING = "large";

    public ArrayList<PicUrl> picUrlList = new ArrayList<>();
    public int mLength;

    public PicUrlHolder(ArrayList<String> urlStringList) {
        if (urlStringList != null && (mLength = urlStringList.size()) > 0) {
            for (int i = 0; i < mLength; i++) {
                picUrlList.add(new PicUrl(urlStringList.get(i)));
            }
        }else{
            mLength=0;
        }
    }

    public class PicUrl {
        private String mThumbnailUrl = null;
        private String mMiddleUrl = null;
        private String mLargeUrl = null;

        public PicUrl(String thumbnailUrl) {
            if (!TextUtils.isEmpty(thumbnailUrl)) {
                this.mThumbnailUrl = thumbnailUrl;
                Matcher matcher = THUMB_PATTERN.matcher(thumbnailUrl);
                this.mMiddleUrl = matcher.replaceFirst(MIDDLE_PIC_STRING);
            }
        }

        public String getThumbnailUrl() {
            return mThumbnailUrl;
        }

        public String getMiddleUrl() {
            if (mMiddleUrl != null) {
                return mMiddleUrl;
            } else if (mThumbnailUrl != null) {
                Matcher matcher = THUMB_PATTERN.matcher(mThumbnailUrl);
                mMiddleUrl = matcher.replaceFirst(MIDDLE_PIC_STRING);
                return mMiddleUrl;
            } else {
                return null;
            }
        }

        public String getLargeUrl() {
            if (mLargeUrl != null) {
                return mLargeUrl;
            } else if (mThumbnailUrl != null) {
                Matcher matcher = THUMB_PATTERN.matcher(mThumbnailUrl);
                mLargeUrl = matcher.replaceFirst(LARGE_PIC_STRING);
                return mLargeUrl;
            } else {
                return null;
            }
        }
    }
}
