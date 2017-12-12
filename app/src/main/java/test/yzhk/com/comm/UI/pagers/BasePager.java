package test.yzhk.com.comm.UI.pagers;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import test.yzhk.com.comm.UI.activities.MainActivity;

/**
 * Created by 大傻春 on 2017/12/10.
 */

public abstract class BasePager {
    public View mRootView;
    public Activity mContext;

    public BasePager(Context context) {
        this.mContext = (MainActivity) context;
        this.mRootView = initView();
        initData();
    }

    public abstract View initView();

    public abstract void initData();

}
