package test.yzhk.com.comm.UI.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.domain.ConversationBean;
import test.yzhk.com.comm.engine.ParseMessages;


public class SingleRoomActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SingleRoomActivity";
    private String mUserName;
    private ListView mLv_chat_content;
    private ChatAdapter mChatAdapter;
    private List<EMMessage> mMessages;
    private String mMe;


    public ArrayList<ConversationBean> mConvationList = new ArrayList<ConversationBean>();
    private ImageView mIv_add;
    private EditText mEt_chatcontent;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    mChatAdapter = new ChatAdapter();
                    mLv_chat_content.setAdapter(mChatAdapter);
            }
        }
    };
    private ImageView mIv_voice;
    private Button mBt_talk;
    private ImageView mIv_keyboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_room);
        Log.e(LOG_TAG, "进入了聊天室");
        //权限申请
        initView();

    }

    private void initView() {
        mMe = EMClient.getInstance().getCurrentUser();
        initTitle();
        initListView();
        startChat();
    }

    private void startChat() {
        //监听输入框 弹出发送按钮
        mEt_chatcontent = (EditText) findViewById(R.id.et_chatcontent);
        final Button bt_send = (Button) findViewById(R.id.bt_send);
        final ImageView iv_add_choice = (ImageView) findViewById(R.id.iv_add_choice);

        mEt_chatcontent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>0){
                    bt_send.setVisibility(View.VISIBLE);
                    iv_add_choice.setVisibility(View.GONE);
                }else{
                    bt_send.setVisibility(View.GONE);
                    iv_add_choice.setVisibility(View.VISIBLE);
                }
            }
        });

        //发送消息
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEt_chatcontent.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {

                    mEt_chatcontent.setText("");

                    //创建一条文本消息，text为消息文字内容，mUserName为对方用户或者群聊的id，
                    EMMessage message = EMMessage.createTxtSendMessage(text, mUserName);
                    //异步发送消息
                    EMClient.getInstance().chatManager().sendMessage(message);

                    //集合变化
                    if (mConvationList != null) {
                        ConversationBean bean = new ConversationBean();
                        bean.texBody = text;
                        bean.sender = mMe;
                        bean.type = EMMessage.Type.TXT;
                        mConvationList.add(bean);
                        mChatAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(LOG_TAG, "mConvationList位null");
                    }

                }
            }
        });

        //接收消息
        EMClient.getInstance().chatManager().addMessageListener(msgListener);

        //语音框和键盘框的转换
        mIv_voice = (ImageView) findViewById(R.id.iv_voice);
        mBt_talk = (Button) findViewById(R.id.bt_talk);

        mIv_keyboard = (ImageView) findViewById(R.id.iv_keyboard);

        mIv_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEt_chatcontent.setVisibility(View.GONE);
                mIv_voice.setVisibility(View.GONE);

                mBt_talk.setVisibility(View.VISIBLE);
                mIv_keyboard.setVisibility(View.VISIBLE);
                showKeyboard();

            }
        });
        mIv_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEt_chatcontent.setVisibility(View.VISIBLE);
                mIv_voice.setVisibility(View.VISIBLE);

                mBt_talk.setVisibility(View.GONE);
                mIv_keyboard.setVisibility(View.GONE);
                showKeyboard();
            }
        });

    }

    //键盘的
    public void showKeyboard(){
        mEt_chatcontent.requestFocus();//输入框获取焦点
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//开启或者关闭软键盘
        imm.showSoftInput(mEt_chatcontent,InputMethodManager.SHOW_FORCED);//弹出软键盘时，焦点定位在输入框中
    }

    public EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            ArrayList<ConversationBean> moreList = ParseMessages.parse(messages);
            mConvationList.addAll(moreList);
            mChatAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };


    public EMConversation mConversation;

    public void initListView() {

        mLv_chat_content = (ListView) findViewById(R.id.lv_chat_content);
        initConversation();

        mChatAdapter = new ChatAdapter();
        mLv_chat_content.setAdapter(mChatAdapter);
    }

    private void initConversation() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mConversation = EMClient.getInstance().chatManager().getConversation(mUserName, null, true);
                mConversation.markAllMessagesAsRead();

                if (mConversation != null) {
                    mMessages = mConversation.getAllMessages();
                    //SDK初始化加载的聊天记录为20条，到顶时需要去DB里获取更多
                    //获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，APP中无需再次把获取到的messages添加到会话中
                    //List<EMMessage> messages = conversation.loadMoreMsgFromDB(startMsgId, pagesize);
                    mConvationList = ParseMessages.parse(mMessages);

                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initTitle() {
        Intent data = getIntent();
        mUserName = data.getStringExtra("userName");
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(mUserName);

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

        mIv_add = (ImageView) findViewById(R.id.iv_add);
        mIv_add.setImageResource(R.drawable.ic_more_detail);
        mIv_add.setVisibility(View.VISIBLE);
        mIv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 2017/11/29 进入聊天详情页
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    public class ChatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mConvationList.size();
        }

        public ConversationBean getItem(int position) {
            return mConvationList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold viewHold;
            if (convertView == null) {
                viewHold = new ViewHold();
                convertView = View.inflate(SingleRoomActivity.this, R.layout.list_item_chat, null);

                viewHold.rl_root_me = (RelativeLayout) convertView.findViewById(R.id.rl_root_me);
                viewHold.tv_me = (TextView) convertView.findViewById(R.id.tv_me);
                viewHold.iv_me = (ImageView) convertView.findViewById(R.id.iv_me);

                viewHold.rl_root_other = (RelativeLayout) convertView.findViewById(R.id.rl_root_other);
                viewHold.tv_other = (TextView) convertView.findViewById(R.id.tv_other);
                viewHold.iv_other = (ImageView) convertView.findViewById(R.id.iv_ohter);
                convertView.setTag(viewHold);

            } else {
                viewHold = (ViewHold) convertView.getTag();
            }

            ConversationBean message = getItem(position);

            if (message.sender.equalsIgnoreCase(mMe)) {

                if(message.type!=null) {
                    if (message.type.equals(EMMessage.Type.TXT)) {
                        viewHold.tv_me.setVisibility(View.VISIBLE);
                        viewHold.tv_me.setText(message.texBody);

                    } else if (message.type.equals(EMMessage.Type.IMAGE)) {
                        viewHold.iv_me.setVisibility(View.VISIBLE);
                        Glide.with(SingleRoomActivity.this).load(message.imageUrl).into(viewHold.iv_me);

                    } else if (message.type.equals(EMMessage.Type.VOICE)) {
                        viewHold.iv_me.setVisibility(View.VISIBLE);
                    }
                    viewHold.rl_root_me.setVisibility(View.VISIBLE);
                }

            } else {

                if(message.type!=null){
                    if (message.type.equals(EMMessage.Type.TXT)) {
                        viewHold.tv_other.setVisibility(View.VISIBLE);
                        viewHold.tv_other.setText(message.texBody);
                    } else if (message.type.equals(EMMessage.Type.IMAGE)) {
                        viewHold.iv_other.setVisibility(View.VISIBLE);
                        Glide.with(SingleRoomActivity.this).load(message.imageUrl).into(viewHold.iv_other);

                    } else if (message.type.equals(EMMessage.Type.VOICE)) {
                        viewHold.iv_other.setVisibility(View.VISIBLE);
                    }
                    viewHold.rl_root_other.setVisibility(View.VISIBLE);
                }

            }

            return convertView;
        }


    }

    static class ViewHold {

        public RelativeLayout rl_root_me;
        public TextView tv_me;
        public ImageView iv_me;

        public RelativeLayout rl_root_other;
        public TextView tv_other;
        public ImageView iv_other;

    }


}




















