package test.yzhk.com.comm.view.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import test.yzhk.com.comm.global.MyApplication;

/**
 * Created by 大傻春 on 2017/12/7.
 */

public class BaseActivity extends AppCompatActivity {

    public MyApplication mApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (MyApplication)getApplication();
        mApplication.addActivity(this);
    }

}
