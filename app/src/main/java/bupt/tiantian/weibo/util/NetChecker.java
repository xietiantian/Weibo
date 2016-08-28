package bupt.tiantian.weibo.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import bupt.tiantian.weibo.settings.SettingActivity;

/**
 * Created by tiantian on 16-7-17.
 */
public class NetChecker {

    private static final String TAG = "NetworkChecker";
    public static final int MOBILE_NETWORK = 1;
    public static final int WIFI_NETWORK = 2;
    public static final int NO_NETWORK = 0;

    private static int networkFlag = 0;
    private static boolean largePicFlag = false;

    public NetChecker() {

    }

    public class NetCheckReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            setNetWorkFlag(context);
            setLargePicFlag(context);
        }
    }

    public static void setNetWorkFlag(Context context) {
        int resultNum = NO_NETWORK;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = connectivityManager.getAllNetworks();
                NetworkInfo networkInfo;
                for (Network mNetwork : networks) {
                    networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && resultNum != WIFI_NETWORK)
                            resultNum = MOBILE_NETWORK;
                        else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                            resultNum = WIFI_NETWORK;
                        Log.d(TAG, "NETWORKNAME: " + networkInfo.getTypeName());
                    }
                }
            } else {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            if (anInfo.getType() == ConnectivityManager.TYPE_MOBILE && resultNum != WIFI_NETWORK)
                                resultNum = MOBILE_NETWORK;
                            else if (anInfo.getType() == ConnectivityManager.TYPE_WIFI)
                                resultNum = WIFI_NETWORK;
                            Log.d(TAG, "NETWORKNAME: " + anInfo.getTypeName());
                        }
                    }
                }
            }
        }
        networkFlag = resultNum;
    }

    public static int getNetworkFlag() {
        return networkFlag;
    }

    public static void setLargePicFlag(Context context) {
        boolean result = false;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPref.getBoolean(SettingActivity.KEY_PREF_LOAD_PICTURE, true)) {
            String picMode = sharedPref.getString(SettingActivity.KEY_PREF_PIC_SIZE, "0");
            if (picMode.equals(SettingActivity.PIC_SIZE_NETWORK_DEPENDS)) {
                if (NetChecker.WIFI_NETWORK == NetChecker.getNetworkFlag()) {
                    result = true;
                }
            } else if (picMode.equals(SettingActivity.PIC_SIZE_LARGE)) {
                result = true;
            }
        }
        largePicFlag = result;
        Log.d(TAG, "show large picture = " + result);
    }

    public static void setLargePicFlag(Context context, String picMode) {
        boolean result = false;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPref.getBoolean(SettingActivity.KEY_PREF_LOAD_PICTURE, true)) {
            if (picMode.equals(SettingActivity.PIC_SIZE_NETWORK_DEPENDS)) {
                if (NetChecker.WIFI_NETWORK == NetChecker.getNetworkFlag()) {
                    result = true;
                }
            } else if (picMode.equals(SettingActivity.PIC_SIZE_LARGE)) {
                result = true;
            }
        }
        largePicFlag = result;
        Log.d(TAG, "show large picture = " + result);
    }

    public static boolean getLargePicFlag() {
        return largePicFlag;
    }
}
