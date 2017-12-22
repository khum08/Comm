package test.yzhk.com.comm.view.activities;

import android.os.Bundle;
import android.widget.TextView;

import test.yzhk.com.comm.R;

public class ServiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        initTitle();
    }

    private void initTitle() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("生活服务");
    }
}
