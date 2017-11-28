package test.yzhk.com.comm.UI.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import test.yzhk.com.comm.R;


/**
 * Created by 大傻春 on 2017/11/28.
 */

public class SettingItemView extends RelativeLayout {

    private static final String NAME = "http://schemas.test.yzhk.com";
    private int mIv_left;
    private String mTv_setting;
    private String mIntent;

    public SettingItemView(Context context) {
        super(context);
        initView();
    }


    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mIv_left = attrs.getAttributeResourceValue(NAME, "iv_left", R.drawable.ic_right);
        mTv_setting = attrs.getAttributeValue(NAME, "settingName");
        mIntent = attrs.getAttributeValue(NAME, "intent");

        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.view_setting_item,this);
        ImageView setting_item = (ImageView) findViewById(R.id.iv_setting_item);
        TextView tv_setting = (TextView) findViewById(R.id.tv_setting);
        ImageView iv_right = (ImageView) findViewById(R.id.iv_right);

        setting_item.setImageResource(mIv_left);
        tv_setting.setText(mTv_setting);
        iv_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIntent!=null){
                    getContext().startActivity(new Intent(getContext(),mIntent.getClass()));
                }
            }
        });

    }


}
