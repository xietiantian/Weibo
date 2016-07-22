package bupt.tiantian.weibo.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 * Created by tiantian on 16-7-11.
 * copy from
 */
public class FileCopyHelper {
    private static final String TAG = "FileCopyHelper";

    /**
     * 复制单个文件
     *
     * @param srcFile      File 源文件File对象
     * @param dstDirString String 拷贝路径，不含文件名
     * @param dstFileName  String 复制后文件名，不含扩展名
     * @return {@code true} 拷贝成功
     * {@code false} on failure
     */
    public static File copyFile(File srcFile, String dstDirString, String dstFileName) {
        FileInputStream inStreamForSuffix = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            if (!srcFile.exists()) {
                return null;
            }
            if (!srcFile.isFile()) {
                return null;
            }
            if (!srcFile.canRead()) {
                return null;
            }
            File dstDir = new File(dstDirString);
            if (!dstDir.exists()) {//目录不存在
                if (!dstDir.mkdirs())//此时返回false一定是因为failure
                    return null;
            }
            inStreamForSuffix = new FileInputStream(srcFile); //读入原文件
            inStream = new FileInputStream(srcFile);
            String suffix = getTypeByStream(inStreamForSuffix);
            File dstFile=new File(dstDirString + File.separator + dstFileName + "." + suffix);
            outStream = new FileOutputStream(dstFile);
            inChannel = inStream.getChannel();//得到对应的文件通道
            outChannel = outStream.getChannel();//得到对应的文件通道
            inChannel.transferTo(0, inChannel.size(), outChannel);//连接两个通道，并且从in通道读取，然后写入out通道
            return dstFile;
        } catch (IOException e) {
            Log.e(TAG, "复制文件出错");
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if(inStreamForSuffix!=null){
                    inStreamForSuffix.close();
                }
                if(inChannel!=null){
                    inChannel.close();
                }
                if(outStream!=null){
                    outStream.close();
                }
                if(outChannel!=null){
                    outChannel.close();
                }

            } catch (IOException e) {
                Log.e(TAG, "IO流关闭出错");
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 根据文件流读取图片文件真实类型
     *
     * @param is 用文件生成的输入流
     * @return 若在识别范围内，返回扩展名，否则返回16进制字符串，长度4字符
     */
    public static String getTypeByStream(InputStream is) {
        byte[] b = new byte[4];
        try {
            is.read(b, 0, b.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String type = bytesToHexString(b).toUpperCase();
        if (type.contains("FFD8FF")) {
            return "jpg";
        } else if (type.contains("89504E47")) {
            return "png";
        } else if (type.contains("47494638")) {
            return "gif";
        } else if (type.contains("49492A00")) {
            return "tif";
        } else if (type.contains("424D")) {
            return "bmp";
        }
        return type;
    }

    /**
     * byte数组转换成16进制字符串
     *
     * @param src 原byte数组
     * @return 16进制字符串
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
