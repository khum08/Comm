package test.yzhk.com.comm.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.view.widget.SettingItemView;
import test.yzhk.com.comm.utils.ToastUtil;

public class FriDetailActivity extends BaseActivity {

    private String mFriName;
    private static final int DELETE = 699;
    private static final int ADD_BLACK = 818;
    private static final int CREATE_CONVERSATION = 899;

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
        add2blacknumber();

    }

    //加入黑名单
    private void add2blacknumber() {
        SettingItemView item_add2black = (SettingItemView) findViewById(R.id.item_add2black);
        item_add2black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EMClient.getInstance().contactManager().addUserToBlackList(mFriName,true);
                    Intent intent = new Intent();
                    intent.putExtra("friName",mFriName);
                    setResult(ADD_BLACK,intent);
                    finish();
                    ToastUtil.showToast(FriDetailActivity.this,"添加黑名单成功");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    ToastUtil.showToast(FriDetailActivity.this,"添加黑名单失败");
                }
            }
        });
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
                //创建回话
                Intent creatConversation = new Intent();
                creatConversation.putExtra("friName",mFriName);
                setResult(CREATE_CONVERSATION,creatConversation);
                finish();
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
                                        ToastUtil.showToast(FriDetailActivity.this,"删除好友成功");
                                        Intent intent = new Intent().putExtra("friName", mFriName);
                                        FriDetailActivity.this.setResult(DELETE,intent);
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
