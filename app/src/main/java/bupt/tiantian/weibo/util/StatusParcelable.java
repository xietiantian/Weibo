package bupt.tiantian.weibo.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.sina.weibo.sdk.openapi.models.Status;

/**
 * Created by tiantian on 16-9-19.
 */
public class StatusParcelable implements Parcelable {
    public Status mStatus;

    public StatusParcelable(Status status) {
        mStatus = status;
    }

    protected StatusParcelable(Parcel in) {
        mStatus.created_at = in.readString();
        mStatus.id = in.readString();
        /** 微博MID String mid*/
        /** 字符串型的微博ID String idstr*/
        mStatus.text = in.readString();
        mStatus.source = in.readString();
        mStatus.favorited = (in.readByte() != 0);
        /** 是否被截断，true：是，false：否 boolean truncated*/
        /**（暂未支持）回复ID String in_reply_to_status_id*/
        /**（暂未支持）回复人UID String in_reply_to_user_id*/
        /**（暂未支持）回复人昵称 String in_reply_to_screen_name*/
        /** 缩略图片地址（小图），没有时不返回此字段 String thumbnail_pic*/
        /** 中等尺寸图片地址（中图），没有时不返回此字段 String bmiddle_pic*/
        /** 原始图片地址（原图），没有时不返回此字段 String original_pic*/

        /** 地理信息字段 Geo geo*/

        /** 微博作者的用户信息字段 User user*/
        mStatus.user.screen_name = in.readString();
        mStatus.user.avatar_large = in.readString();
        mStatus.user.profile_image_url = in.readString();
        if (in.readByte() == 1) {
            StatusParcelable retweet = in.readParcelable(Status.class.getClassLoader());
            mStatus.retweeted_status = retweet.mStatus;
        }

        mStatus.reposts_count = in.readInt();
        mStatus.comments_count = in.readInt();
        mStatus.attitudes_count = in.readInt();

        /** 暂未支持 int mlevel*/
        /**
         * 微博的可见性及指定可见分组信息。该 object 中 type 取值，
         * 0：普通微博，1：私密微博，3：指定分组微博，4：密友微博；
         * list_id为分组的组号
         * Visible visible
         */

        if (in.readByte() == 1) {
            mStatus.pic_urls = in.readArrayList(String.class.getClassLoader());
        }
    }

    public static final Creator<StatusParcelable> CREATOR = new Creator<StatusParcelable>() {
        @Override
        public StatusParcelable createFromParcel(Parcel in) {
            return new StatusParcelable(in);
        }

        @Override
        public StatusParcelable[] newArray(int size) {
            return new StatusParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mStatus.created_at);
        dest.writeString(mStatus.id);
        /** 微博MID String mid*/
        /** 字符串型的微博ID String idstr*/
        dest.writeString(mStatus.text);
        dest.writeString(mStatus.source);
        dest.writeByte((byte) (mStatus.favorited ? 1 : 0));
        /** 是否被截断，true：是，false：否 boolean truncated*/
        /**（暂未支持）回复ID String in_reply_to_status_id*/
        /**（暂未支持）回复人UID String in_reply_to_user_id*/
        /**（暂未支持）回复人昵称 String in_reply_to_screen_name*/
        /** 缩略图片地址（小图），没有时不返回此字段 String thumbnail_pic*/
        /** 中等尺寸图片地址（中图），没有时不返回此字段 String bmiddle_pic*/
        /** 原始图片地址（原图），没有时不返回此字段 String original_pic*/

        /** 地理信息字段 Geo geo*/

        /** 微博作者的用户信息字段 User user*/
        dest.writeString(mStatus.user.screen_name);
        dest.writeString(mStatus.user.avatar_large);
        dest.writeString(mStatus.user.profile_image_url);
        if (mStatus.retweeted_status != null) {
            dest.writeByte((byte) 1);
            dest.writeParcelable(new StatusParcelable(mStatus.retweeted_status), flags);
        } else {
            dest.writeByte((byte) 0);
        }

        dest.writeInt(mStatus.reposts_count);
        dest.writeInt(mStatus.comments_count);
        dest.writeInt(mStatus.attitudes_count);

        /** 暂未支持 int mlevel*/
        /**
         * 微博的可见性及指定可见分组信息。该 object 中 type 取值，
         * 0：普通微博，1：私密微博，3：指定分组微博，4：密友微博；
         * list_id为分组的组号
         * Visible visible
         */

        if (mStatus.pic_urls != null && mStatus.pic_urls.size() > 0) {
            dest.writeByte((byte) 1);
            dest.writeList(mStatus.pic_urls);
        } else {
            dest.writeByte((byte) 0);
        }
    }
}
