package bupt.tiantian.weibo.imgshow;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by tiantian on 16-7-5.
 */
public class PicUrlHolder {
    public static final Pattern THUMB_PATTERN = Pattern.compile("thumbnail");
    public static final String MIDDLE_PIC_STRING = "bmiddle";
    public static final String LARGE_PIC_STRING = "large";

    private ArrayList<PicUrl> mPicUrlList = null;
    private int mLength;

    public PicUrlHolder(ArrayList<String> urlStringList) {
        if (urlStringList != null && (mLength = urlStringList.size()) > 0) {
            mPicUrlList = new ArrayList<>(mLength);
            for (int i = 0; i < mLength; i++) {
                mPicUrlList.add(new PicUrl(urlStringList.get(i)));
            }
        } else {
            mLength = 0;
        }
    }

    public int getLength() {
        return mLength;
    }

    public ArrayList<PicUrl> getPicUrlList() {
        return mPicUrlList;
    }
}
