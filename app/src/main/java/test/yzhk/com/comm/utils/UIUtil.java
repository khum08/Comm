package test.yzhk.com.comm.utils;

import android.content.Context;

/**
 * Created by 大傻春 on 2017/12/10.
 */

public class UIUtil {

    public static int dip2px(float dip,Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }

    public static float px2dip(int px,Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return px / density;
    }
}
