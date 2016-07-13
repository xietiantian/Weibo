package bupt.tiantian.weibo.helper;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.regex.Matcher;

/**
 * Created by tiantian on 16-7-6.
 */
public class PicUrl implements Parcelable {
    private String mThumbnailUrl = null;
    private String mMiddleUrl = null;
    private String mLargeUrl = null;

    public PicUrl(String thumbnailUrl) {
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            this.mThumbnailUrl = thumbnailUrl;
            Matcher matcher = PicUrlHolder.THUMB_PATTERN.matcher(thumbnailUrl);
            this.mMiddleUrl = matcher.replaceFirst(PicUrlHolder.MIDDLE_PIC_STRING);
        }
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public String getMiddleUrl() {
        if (mMiddleUrl != null) {
            return mMiddleUrl;
        } else if (mThumbnailUrl != null) {
            Matcher matcher = PicUrlHolder.THUMB_PATTERN.matcher(mThumbnailUrl);
            mMiddleUrl = matcher.replaceFirst(PicUrlHolder.MIDDLE_PIC_STRING);
            return mMiddleUrl;
        } else {
            return null;
        }
    }

    public String getLargeUrl() {
        return mLargeUrl;
    }

    public void setLargeUrlFromThumb(){
        if(TextUtils.isEmpty(mLargeUrl)) {
            Matcher matcher = PicUrlHolder.THUMB_PATTERN.matcher(mThumbnailUrl);
            mLargeUrl = matcher.replaceFirst(PicUrlHolder.LARGE_PIC_STRING);
        }
    }

    public boolean isLargeUrlSet(){
        return !TextUtils.isEmpty(mLargeUrl);
    }
    /*以下代码为实现parcelable添加*/
    protected PicUrl(Parcel in) {
        mThumbnailUrl = in.readString();
        mMiddleUrl = in.readString();
        mLargeUrl = in.readString();
    }

    public static final Creator<PicUrl> CREATOR = new Creator<PicUrl>() {
        @Override
        public PicUrl createFromParcel(Parcel in) {
            return new PicUrl(in);
        }

        @Override
        public PicUrl[] newArray(int size) {
            return new PicUrl[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mThumbnailUrl);
        dest.writeString(mMiddleUrl);
        dest.writeString(mLargeUrl);
    }
}
