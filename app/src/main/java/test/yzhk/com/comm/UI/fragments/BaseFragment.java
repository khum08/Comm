package test.yzhk.com.comm.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import test.yzhk.com.comm.UI.activities.MainActivity;

/**
 * Created by 大傻春 on 2017/11/24.
 */

public abstract class BaseFragment extends Fragment {


    public MainActivity mContext;
    public View mRootView;

    public abstract View initView();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = initView();
        initData();
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    protected abstract void initData();
}
