package test.yzhk.com.comm.UI.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import test.yzhk.com.comm.R;

public class GroupActivity extends BaseActivity {

    public String groupId;
    private EMGroup mGroup;
    private String mGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_room);
        initGroup();
        initView();
    }

    //// TODO: 2017/12/8 aaaaaaaaaaaaaaaaaaaaa
    private void initGroup() {
        Intent intent = getIntent();
        mGroupId = intent.getStringExtra("groupId");
        mGroup = EMClient.getInstance().groupManager().getGroup(mGroupId);
        new Thread(){
            @Override
            public void run() {
                try {
                    mGroup = EMClient.getInstance().groupManager().getGroupFromServer(mGroupId);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    private void initView() {
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ImageView iv_add = (ImageView) findViewById(R.id.iv_add);
        iv_add.setVisibility(View.VISIBLE);
        iv_add.setImageResource(R.drawable.ic_people_white_24dp);

        Intent intent = getIntent();

        groupId = intent.getStringExtra("groupId");
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(groupId);

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupActivity.this, GroupInfoActivity.class);
                intent.putExtra("groupId",groupId);
                startActivity(intent);
            }
        });
    }
}
