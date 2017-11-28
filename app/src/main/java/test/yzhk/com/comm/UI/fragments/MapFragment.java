package test.yzhk.com.comm.UI.fragments;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by 大傻春 on 2017/11/27.
 */

public class MapFragment extends BaseFragment {


    @Override
    public View initView() {
        TextView textView = new TextView(mContext);
        textView.setText("地图");
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

    @Override
    public void initData() {

    }
}
