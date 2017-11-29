package test.yzhk.com.comm.UI.fragments;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.activities.LoginActivity;
import test.yzhk.com.comm.UI.view.SettingItemView;
import test.yzhk.com.comm.utils.Toastutil;

/**
 * Created by 大傻春 on 2017/11/24.
 */

public class SelfFragment extends BaseFragment {


    private View selfPage;

    @Override
    public View initView() {
        selfPage = View.inflate(mContext, R.layout.fragment_self, null);
        TextView tv_title = (TextView) selfPage.findViewById(R.id.tv_title);
        tv_title.setText("我");
        return selfPage;
    }

    @Override
    public void initData() {
        //设置用户名
        TextView tv_username = (TextView) selfPage.findViewById(R.id.tv_username);
//        MainActivity activity = (MainActivity) getActivity();
//        String userName = activity.getUserName();
//        tv_username.setText(userName);

        String currUsername = EMClient.getInstance().getCurrentUser();
        tv_username.setText(currUsername);

        initSignOut();
        initRegister();
        initSetting();

    }

    private void initSetting() {
        SettingItemView item_setting = (SettingItemView) selfPage.findViewById(R.id.item_setting);
        item_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //退出登录
    private void initSignOut() {

        SettingItemView item_signout = (SettingItemView) selfPage.findViewById(R.id.item_signout);
        item_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EMClient.getInstance().logout(true, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(mContext,LoginActivity.class));
                        mContext.finish();
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }
                    @Override
                    public void onError(int code, String message) {
                        Toastutil.showToast(mContext,"退出失败");
                    }
                });
            }
        });


    }

    private void initRegister() {
        SettingItemView item_register_login = (SettingItemView) selfPage.findViewById(R.id.item_register_login);
        item_register_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,LoginActivity.class));
            }
        });

    }


}
