package test.yzhk.com.comm.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import test.yzhk.com.comm.R;

/**
 * Created by 大傻春 on 2017/11/28.
 */

public class ToastUtil {

    /**
     * 主线程和子线程都可显示toast
     *
     * @param ctx 上下文
     * @param msg 吐司信息
     */
    public static void showToast(final Activity ctx, final String msg) {
        if ("main".equals(Thread.currentThread().getName())) {
            show(ctx, msg);
        } else {
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    show(ctx, msg);
                }
            });
        }
    }

    /**
     * 自定义toast 显示在底部
     * @param cxt
     * @param msg
     */
    private static void show(Activity cxt, String msg) {
        View toastView = View.inflate(cxt, R.layout.view_toast, null);
        TextView tv_toast = (TextView) toastView.findViewById(R.id.tv_toast);
        tv_toast.setText(msg);

        //获取屏幕宽度
        WindowManager wm = (WindowManager) cxt.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        int width = point.x;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                width,
                ViewGroup.LayoutParams.MATCH_PARENT);
        tv_toast.setLayoutParams(params);

        Toast toast = new Toast(cxt);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }



}
