package test.yzhk.com.comm.UI.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.view.SettingItemView;
import test.yzhk.com.comm.utils.ToastUtil;

public class SettingActivity extends AppCompatActivity {

    private SettingItemView mItem_contact_setting;
    private SettingItemView mItem_writer_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initToolbar();
        initContact();
        initWriterInfo();
        initPayWriter();
        initTextsize();
        initNightMode();
        initShare();
    }

    //分享功能
    private void initShare() {
        SettingItemView item_share = (SettingItemView) findViewById(R.id.item_share);
        item_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(SettingActivity.this,"这是分享功能");
            }
        });
    }

    //夜间操作
    private void initNightMode() {
        SwitchCompat tb_nightmode = (SwitchCompat) findViewById(R.id.tb_nightmode);
        //// TODO: 2017/12/6
    }


    //改变字体大小
    private void initTextsize() {
        SettingItemView item_textsize = (SettingItemView) findViewById(R.id.item_textsize);
        item_textsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(SettingActivity.this,"改变字体大小");
            }
        });
    }

    //支持作者
    private void initPayWriter() {
        SettingItemView item_pay = (SettingItemView) findViewById(R.id.item_pay);
        item_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(SettingActivity.this,"进入支付宝或微信支付平台");
            }
        });

    }

    //作者信息
    private void initWriterInfo() {
        mItem_writer_info = (SettingItemView) findViewById(R.id.item_writer_info);
        mItem_writer_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("传闻").setMessage("传闻作者很帅\n其他一无所知\n不过据说17612159408能联系上作者");
                builder.show();
            }
        });
    }

    //联系作者操作
    private void initContact() {
        mItem_contact_setting = (SettingItemView) findViewById(R.id.item_contact_setting);
        mItem_contact_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(SettingActivity.this,"显示与作者聊天的界面");
            }
        });
    }

    //初始化toolbar
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    //初始化左上角返回键
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
