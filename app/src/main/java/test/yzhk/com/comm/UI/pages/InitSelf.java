package test.yzhk.com.comm.UI.pages;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.activities.LoginActivity;

/**
 * Created by 大傻春 on 2017/11/24.
 */

public class InitSelf extends InitContent {

    public InitSelf(Context cxt) {
        super(cxt);
    }

    private View selfPage;
    @Override
    public View initView() {
        selfPage = View.inflate(mContext, R.layout.content_self, null);
        return selfPage;
    }

    @Override
    public void initData() {
        TextView tv_signin = (TextView) selfPage.findViewById(R.id.tv_signin);
        tv_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,LoginActivity.class));
            }
        });
        //// TODO: 2017/11/24  

    }
    





}
