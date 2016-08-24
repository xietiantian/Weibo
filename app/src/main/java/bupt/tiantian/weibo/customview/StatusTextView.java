package bupt.tiantian.weibo.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bupt.tiantian.weibo.R;

/**
 * Created by tiantian on 16-8-15.
 * reference: https://github.com/luvictor/WeiboTextView
 */
public class StatusTextView extends TextView {

    private static final int DEFAULT_LINK_HIGHLIGHT_COLOR = Color.BLUE;// 默认链接高亮颜色
    // 定义正则表达式
    private static final String AT = "@([\\w\\-]{4,30})(?=\\W|$)";// @人
    private static final String TOPIC = "#(\\w+)#";// ##话题
    private static final String EMOJI = "\\[([a-zA-Z\\u4e00-\\u9fa5]{1,5})\\]";// 表情
    private static final String URL = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";// url
    private static final String REGEX = "(" + AT + ")|(" + TOPIC + ")|(" + EMOJI + ")|(" + URL + ")";
    private static final Pattern mPattern = Pattern.compile(REGEX);
    // matcher中提取相应内容的组号
    public static final int GROUP_NUM_AT = 1;//@用户昵称
    public static final int GROUP_NUM_AT_KEY = 2;//用户昵称
    public static final int GROUP_NUM_TOPIC = 3;//#话题#
    public static final int GROUP_NUM_TOPIC_KEY = 4;//话题
    public static final int GROUP_NUM_EMOJI = 5;//[表情]
    public static final int GROUP_NUM_EMOJI_KEY = 6;//表情
    public static final int GROUP_NUM_URL = 7;//网址
    private static final List<String> DEF_EMOJI_STRING_LIST = Arrays.asList(
            //默认
            "微笑", "嘻嘻", "哈哈", "爱你", "挖鼻", "吃惊", "晕", "泪", "馋嘴", "抓狂",
            "哼", "可爱", "怒", "汗", "害羞", "睡", "钱", "偷笑", "笑cry", "doge",
            "喵喵", "酷", "衰", "闭嘴", "鄙视", "色", "鼓掌", "悲伤", "思考", "生病",
            "亲亲", "怒骂", "太开心", "白眼", "右哼哼", "左哼哼", "嘘", "委屈", "吐", "可怜",
            "哈欠", "挤眼", "失望", "顶", "疑问", "困", "感冒", "拜拜", "黑线", "阴险",
            "打脸", "傻眼", "互粉", "心", "伤心", "猪头", "熊猫", "兔子", "握手", "作揖",
            "赞", "耶", "good", "弱", "NO", "ok", "haha", "来", "拳头", "威武",
            "鲜花", "钟", "浮云", "飞机", "月亮", "太阳", "微风", "下雨", "给力", "神马",
            "围观", "话筒", "奥特曼", "草泥马", "萌", "囧", "织", "礼物", "喜", "围脖",
            "音乐", "绿丝带", "蛋糕", "蜡烛", "干杯", "男孩儿", "女孩儿", "肥皂", "照相机", "浪",
            "沙尘暴"
    );
    private static final List<String> LXH_EMOJI_STRING_LIST = Arrays.asList(
            //浪小花
            "笑哈哈", "好爱哦", "噢耶", "偷乐", "泪流满面", "巨汗", "抠鼻屎", "求关注", "好喜欢", "崩溃",
            "好囧", "震惊", "别烦我", "不好意思", "羞嗒嗒", "得意地笑", "纠结", "给劲", "悲催", "甩甩手",
            "好棒", "瞧瞧", "不想上班", "困死了", "许愿", "丘比特", "有鸭梨", "想一想", "躁狂症", "转发",
            "互相膜拜", "雷锋", "杰克逊", "玫瑰", "hold住", "群体围观", "推荐", "赞啊", "被电", "霹雳"
    );

    private int mLinkHighlightColor;// 链接高亮的颜色，默认蓝色
    private int mTextSize;// 文字大小，用来设置emoji图片大小
    private OnLinkClickListener mOnLinkClickListener;


    public StatusTextView(Context context) {
        super(context);
        initView(context, null);
    }


    public StatusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        // 要实现文字的点击效果，这里需要做特殊处理
        setMovementMethod(LinkMovementMethod.getInstance());
        mTextSize = (int) this.getTextSize();
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusTextView);

            // 设置链接高亮颜色
            int mLinkHighlightColor = typedArray.getColor(
                    R.styleable.StatusTextView_linkHighlightColor, DEFAULT_LINK_HIGHLIGHT_COLOR);
            if (mLinkHighlightColor != 0) {
                setLinkHighlightColor(mLinkHighlightColor);
            }

            typedArray.recycle();
        }
    }


    public void setLinkHighlightColor(int mLinkHighlightColor) {
        this.mLinkHighlightColor = mLinkHighlightColor;
    }

    /**
     * 因为父类的setText(CharSequence text)是final的，只能重写该方法
     * <p/>
     * 注：实际上setText(CharSequence text)内部也是调用了该方法
     *
     * @param text
     * @param type
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(getWeiboContent(text), type);
    }

    /**
     * 设置微博内容样式
     *
     * @param source 原始文字内容
     * @return 添加了clickablespan和imagespan的spannable字符串
     */
    public SpannableString getWeiboContent(CharSequence source) {
        SpannableString spannableString = new SpannableString(source);

        // 设置正则
        final Matcher matcher = mPattern.matcher(spannableString);

        if (matcher.find()) {
            // 重置正则位置
            matcher.reset();
        }

        while (matcher.find()) {
            // 根据group的括号索引，可得出具体匹配哪个正则(0代表全部，1代表第一个括号)
            final String at = matcher.group(GROUP_NUM_AT);
            final String topic = matcher.group(GROUP_NUM_TOPIC);
            String emoji = matcher.group(GROUP_NUM_EMOJI);
            final String url = matcher.group(GROUP_NUM_URL);


            // 处理@符号
            if (at != null) {
                final String atKey=matcher.group(GROUP_NUM_AT_KEY);
                // 获取匹配位置
                int start = matcher.start(GROUP_NUM_AT);
                int end = start + at.length();
                NoUnderlineClickableSpan clickableSpan = new NoUnderlineClickableSpan() {

                    @Override
                    public void onClick(View v) {
                        if (mOnLinkClickListener != null) {
                            mOnLinkClickListener.onAtClick(atKey);
                        }
                    }
                };
                spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // 处理话题##符号
            if (topic != null) {
                final String topicKey=matcher.group(GROUP_NUM_TOPIC_KEY);
                int start = matcher.start(GROUP_NUM_TOPIC);
                int end = start + topic.length();
                NoUnderlineClickableSpan clickableSpan = new NoUnderlineClickableSpan() {

                    @Override
                    public void onClick(View v) {
                        if (mOnLinkClickListener != null) {
                            mOnLinkClickListener.onTopicClick(topicKey);
                        }
                    }
                };
                spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (emoji != null) {
                final String emojiKey=matcher.group(GROUP_NUM_EMOJI_KEY);
                int start = matcher.start(GROUP_NUM_EMOJI);
                int end = start + emoji.length();
                String emojiLabel;
                int emojiIndex = DEF_EMOJI_STRING_LIST.indexOf(emojiKey);
                if (emojiIndex != -1) {
                    emojiLabel = "face";
                } else {
                    emojiIndex = LXH_EMOJI_STRING_LIST.indexOf(emojiKey);
                    emojiLabel = "lxh";
                }

                if (emojiIndex != -1) {
                    // 对表情符以img标记进行修饰，改用drawable显示出来
                    int drawableId = getResources().
                            getIdentifier(emojiLabel + emojiIndex, "drawable", "bupt.tiantian.weibo");
                    Drawable drawable = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        drawable = getResources().getDrawable(drawableId, null);
                    } else {
                        //noinspection deprecation
                        drawable = getResources().getDrawable(drawableId);
                    }
                    if (drawable != null) {
                        drawable.setBounds(0, 0, mTextSize, mTextSize);
                        // 需要处理的文本，[smile]是需要被替代的文本
                        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                        spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

            // 处理url地址
            if (url != null) {
                int start = matcher.start(GROUP_NUM_URL);
                int end = start + url.length();
                NoUnderlineClickableSpan clickableSpan = new NoUnderlineClickableSpan() {

                    @Override
                    public void onClick(View v) {
                        if (mOnLinkClickListener != null) {
                            mOnLinkClickListener.onUrlClick(url);
                        }
                    }
                };
                spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannableString;
    }


    public OnLinkClickListener getOnLinkClickListener() {
        return mOnLinkClickListener;
    }

    public void setOnLinkClickListener(OnLinkClickListener mOnLinkClickListener) {
        this.mOnLinkClickListener = mOnLinkClickListener;
    }


    /**
     * 继承ClickableSpan复写updateDrawState方法，自定义所需样式
     *
     * @author Rabbit_Lee
     */
    private class NoUnderlineClickableSpan extends ClickableSpan {

        @Override
        public void onClick(View v) {

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(mLinkHighlightColor);
            ds.setUnderlineText(false);
        }
    }

    /**
     * 链接点击的监听器
     *
     * @author Victor
     * @email 468034043@qq.com
     * @time 2016年5月11日 下午5:15:23
     */
    public interface OnLinkClickListener {
        /**
         * 点击了@的人， 如"@victor"中的"victor"
         *
         * @param at
         */
        void onAtClick(String at);

        /**
         * 点击了话题，如"#中国#"中的"中国"
         *
         * @param topic
         */
        void onTopicClick(String topic);

        /**
         * 点击了url，如"http://www.google.com"
         *
         * @param url
         */
        void onUrlClick(String url);
    }
}
