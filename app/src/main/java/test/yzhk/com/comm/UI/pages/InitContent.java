package test.yzhk.com.comm.UI.pages;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 大傻春 on 2017/11/24.
 */

public abstract class InitContent extends Fragment {
    
    public Context mContext;
    public InitContent(Context cxt){
        this.mContext = cxt;
    }


    public abstract View initView();


    /**
     * 在MainActivity中手动调用
     */
    public abstract void initData();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = initView();
        return rootView;
    }
}
