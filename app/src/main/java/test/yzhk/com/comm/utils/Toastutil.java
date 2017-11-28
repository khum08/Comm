package test.yzhk.com.comm.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by 大傻春 on 2017/11/28.
 */

public class Toastutil {

    /**
     * 主线程和子线程都可显示toast
     *
     * @param ctx 上下文
     * @param msg 吐司信息
     */
    public static void showToast(final Activity ctx, final String msg) {
        if ("main".equals(Thread.currentThread().getName())) {
            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
        } else {
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
