package test.yzhk.com.comm.UI.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.view.SettingItemView;
import test.yzhk.com.comm.utils.Toastutil;

public class FriDetailActivity extends AppCompatActivity {

    private String mFriName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fri_detail);

        initView();
    }

    private void initView() {

        initTitle();
        deleteFri();
        sendMsg();

    }

    //初始化自定义的titlebar
    private void initTitle() {
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv_title = (TextView)findViewById(R.id.tv_title);
        Intent intent = getIntent();
        mFriName = intent.getStringExtra("friName");
        tv_title.setText(mFriName);

    }

    //发消息
    private void sendMsg() {
        Button bt_msg = (Button) findViewById(R.id.bt_msg);
        bt_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriDetailActivity.this, SingleRoomActivity.class);
                intent.putExtra("userName",mFriName);
                startActivity(intent);
                FriDetailActivity.this.finish();
            }
        });


    }

    //删除好友
    private void deleteFri() {
        SettingItemView item_remove = (SettingItemView) findViewById(R.id.item_remove);
        item_remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(){
                                @Override
                                public void run() {
                                    try {
                                        EMClient.getInstance().contactManager().deleteContact(mFriName);
                                        Toastutil.showToast(FriDetailActivity.this,"删除好友成功");
                                        FriDetailActivity.this.finish();
                                    } catch (HyphenateException e) {
                                        e.printStackTrace();
                                    }
                                }
                }.start();

            }
        });
    }
}
