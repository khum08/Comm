package test.yzhk.com.comm.UI.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import test.yzhk.com.comm.R;

public class SingleRoomActivity extends AppCompatActivity {

    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_room);

        initView();


    }

    private void initView() {
        initTitle();
        initListView();
    }

    private void initListView() {

        Intent intent = getIntent();
        mUserName = intent.getStringExtra("userName");

        ListView lv_chat_content = (ListView) findViewById(R.id.lv_chat_content);
        ChatAdapter chatAdapter = new ChatAdapter();

        new Thread() {
            @Override
            public void run() {

                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mUserName);
                //获取此会话的所有消息
                List<EMMessage> messages = conversation.getAllMessages();
                //SDK初始化加载的聊天记录为20条，到顶时需要去DB里获取更多
                //获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，APP中无需再次把获取到的messages添加到会话中
//                List<EMMessage> messages = conversation.loadMoreMsgFromDB(startMsgId, pagesize);
            }
        }.start();

        lv_chat_content.setAdapter(chatAdapter);


    }

    private void initTitle() {
        Intent data = getIntent();
        String userName = data.getStringExtra("userName");
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(userName);

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(SingleRoomActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ImageView iv_add = (ImageView) findViewById(R.id.iv_add);
        iv_add.setImageResource(R.drawable.ic_person);
        iv_add.setVisibility(View.VISIBLE);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 2017/11/29 进入聊天详情页
            }
        });
    }


    public class ChatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

}




















