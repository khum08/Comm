package test.yzhk.com.comm.view.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.utils.ToastUtil;


public class GroupInfoActivity extends AppCompatActivity {

    public String groupId;
    private static final int GET_DATA = 575;
    private static final int GET_BLACKNUMBER = 855;
    private EMGroup mGroup;
    private TextView tv_owner;
    private TextView tv_groupid;
    private LinearLayout ll_groupdesc;
    private List<String> mMembers;
    private String groupName;
    private TextView tv_group_name;
    private LinearLayout ll_admin;
    private LinearLayout ll_number;
    private TextView tv_notice_content;
    private LinearLayout ll_share_file;
    private Switch switch_getmsg;
    private Button btn_out;
    private Button btn_release;
    private String mNotice;
    private LinearLayout ll_change_notice;
    private List<String> mAdminList;
    private List<String> mNumberList;
    private LinearLayout ll_blacknumber;
    private List<String> mBlackNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        initView();
        initData();
    }

    private TextView tv_admin;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DATA:
                    tv_owner.setText(mGroup.getOwner());
                    tv_group_name.setText(mGroup.getGroupName());
                    tv_groupid.setText(mGroup.getGroupId());
                    if (mNotice != null) {
                        tv_notice_content.setText(mNotice);
                    } else {
                        tv_notice_content.setText("暂时没有群公告");
                    }
                    break;
                case GET_BLACKNUMBER:
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
                    String[] blacknumbers = mBlackNumbers.toArray(new String[mBlackNumbers.size()]);
                    builder.setItems(blacknumbers,null);
                    builder.show();
                    break;
            }
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                try {
                    mGroup = EMClient.getInstance().groupManager().getGroupFromServer(groupId,true);
                    mNotice = EMClient.getInstance().groupManager().fetchGroupAnnouncement(groupId);
                    if (mGroup != null) {
                        mMembers = mGroup.getMembers();
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(GET_DATA);
            }
        }.start();
    }

    private void initView() {
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        groupName = intent.getStringExtra("groupName");
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(groupName);

        tv_group_name = (TextView) findViewById(R.id.tv_group_name);
        tv_groupid = (TextView) findViewById(R.id.tv_groupid);
        tv_owner = (TextView) findViewById(R.id.tv_owner);

        ll_admin = (LinearLayout) findViewById(R.id.ll_admin);
        ll_number = (LinearLayout) findViewById(R.id.ll_number);
        ll_groupdesc = (LinearLayout) findViewById(R.id.ll_groupdesc);
        ll_share_file = (LinearLayout) findViewById(R.id.ll_share_file);
        tv_notice_content = (TextView) findViewById(R.id.tv_notice_content);
        switch_getmsg = (Switch) findViewById(R.id.switch_getmsg);
        btn_out = (Button) findViewById(R.id.btn_out);
        btn_release = (Button) findViewById(R.id.btn_release);

        //群主功能
        ll_change_notice = (LinearLayout) findViewById(R.id.ll_change_notice);
        ll_blacknumber = (LinearLayout)findViewById(R.id.ll_blacknumber);

        initAdmin();
        initNumber();
        initDesc();
        initShareFile();
        initBlackNumber();


    }

    private void initBlackNumber() {
        ll_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            mBlackNumbers = EMClient.getInstance().groupManager().getBlockedUsers(groupId);
                            mHandler.sendEmptyMessage(GET_BLACKNUMBER);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            ToastUtil.showToast(GroupInfoActivity.this,"获取黑名单失败");
                        }
                    }
                }.start();

            }
        });
    }

    //弹出群描述dailog
    private void initDesc() {
        ll_groupdesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = mGroup.getDescription();
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
                builder.setTitle("群描述").setMessage(description);
                builder.show();
            }
        });
    }

    //弹出群成员列表
    private void initNumber() {
        ll_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumberList = mGroup.getMembers();
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
                builder.setTitle("群成员");
                if(mNumberList!=null && !mNumberList.isEmpty()){
                   builder.setMessage(mNumberList.toString());
                }else {
                    builder.setMessage("群成员为空");
                }
                builder.setPositiveButton("邀请好友", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enterInvite();
                    }
                });
                builder.show();
            }
        });
    }

    //进入邀请好友页面
    private void enterInvite() {
        Intent intent = new Intent(this, InviteFriendActivity.class);
        intent.putExtra("groupId",groupId);
        startActivity(intent);
    }

    //弹出管理员列表
    private void initAdmin() {
        ll_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdminList = mGroup.getAdminList();
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
                String[] admins = mAdminList.toArray(new String[mAdminList.size()]);
                builder.setItems(admins,null);
                builder.show();
            }
        });
    }

    //进入共享文件activity
    private void initShareFile() {
        ll_share_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GroupInfoActivity.this, FileShareActivity.class);
                intent.putExtra("groupId", mGroup.getGroupId());
                startActivity(intent);
            }
        });
    }
}
