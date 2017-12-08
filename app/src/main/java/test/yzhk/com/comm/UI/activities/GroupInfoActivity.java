package test.yzhk.com.comm.UI.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import test.yzhk.com.comm.R;

public class GroupInfoActivity extends AppCompatActivity {

    public String groupId;
    private static final int GET_DATA = 575;
    private EMGroup mGroup;
    private TextView tv_owner;
    private GridView gv_number;
    private TextView tv_groupid;
    private TextView tv_groupdesc;
    private List<String> mMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        initView();
        initData();
    }

    private TextView tv_admin;

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_DATA:
                    tv_owner.setText("群主："+mGroup.getOwner()+"群成员："+mMembers.toString());
                    tv_groupid.setText("群组名称："+mGroup.getGroupId());
                    tv_groupdesc.setText("群组描述"+mGroup.getDescription());
                    break;
            }
        }
    };
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                try {
                    mGroup = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
                    if(mGroup!=null){
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

        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(groupId);

        tv_owner = (TextView)findViewById(R.id.tv_owner);
        gv_number = (GridView)findViewById(R.id.gv_number);
        tv_admin = (TextView)findViewById(R.id.tv_admin);
        tv_groupid = (TextView)findViewById(R.id.tv_groupid);
        tv_groupdesc = (TextView)findViewById(R.id.tv_groupdesc);

    }
}
