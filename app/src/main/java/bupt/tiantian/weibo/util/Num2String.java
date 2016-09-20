package bupt.tiantian.weibo.util;

/**
 * Created by tiantian on 16-9-18.
 */
public class Num2String {
    public static String transform(int n){
        if(n > 100000000){
            return Integer.toString(n/100000000)+"亿";
        }else if(n > 10000){
            return Integer.toString(n/10000)+"万";
        }else{
            return Integer.toString(n);
        }
    }
}
