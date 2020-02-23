package com.srtc.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Title:ToastUtil
 * <p>
 * Description:Toast提示工具类
 * </p>
 * Author Han.C
 * Date 2019/8/9 9:07
 */
public class ToastUtil {
    private static Toast mToast;

    public static void showShort(Context context, String msg) {
        try {
            if (context != null && !TextUtils.isEmpty(msg)) {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
                mToast.setText(msg);
                mToast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
