package test.yzhk.com.comm.UI.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.Glide;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.dao.ConversationsDao;
import test.yzhk.com.comm.utils.ToastUtil;
import test.yzhk.com.comm.utils.UriUtil;


public class SingleRoomActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SingleRoomActivity";
    private static final int GET_PHOTO = 2;
    private static final int OPEN_CAMERA = 3;
    private String mUserName;
    private ListView mLv_chat_content;
    private ChatAdapter mChatAdapter;

    private String mMe;


    public List<EMMessage> conversationlist = new ArrayList<>();
    private ImageView mIv_add;
    private EditText mEt_chatcontent;

    private ImageView mIv_voice;
    private Button mBt_talk;
    private ImageView mIv_keyboard;
    private LinearLayout mView_more_action;
    private Button mBt_send;
    private ImageView mIv_add_choice;
    private static Uri tempUri;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mChatAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_room);
        //权限申请
        initView();

    }

    private void initView() {
        mMe = EMClient.getInstance().getCurrentUser();
        initTitle();
        initListView();
        startChat();
        initMoreAction();
    }


    private void startChat() {

        mEt_chatcontent = (EditText) findViewById(R.id.et_chatcontent);
        mBt_send = (Button) findViewById(R.id.bt_send);
        mIv_add_choice = (ImageView) findViewById(R.id.iv_add_choice);
        mView_more_action = (LinearLayout) findViewById(R.id.view_more_action);

        //弹出更多操作界面
        mIv_add_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlKeyboard();
            }
        });
        mEt_chatcontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mEt_chatcontent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mView_more_action.setVisibility(View.GONE);
                }
            }
        });

        //监听输入框 弹出发送按钮
        mEt_chatcontent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    mBt_send.setVisibility(View.VISIBLE);
                    mIv_add_choice.setVisibility(View.GONE);
                } else {
                    mBt_send.setVisibility(View.GONE);
                    mIv_add_choice.setVisibility(View.VISIBLE);
                }
            }
        });

        //发送消息
        mBt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEt_chatcontent.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {

                    mEt_chatcontent.setText("");

                    //创建一条文本消息，text为消息文字内容，mUserName为对方用户或者群聊的id，异步发送消息
                    EMMessage message = EMMessage.createTxtSendMessage(text, mUserName);
                    EMClient.getInstance().chatManager().sendMessage(message);

                    //集合变化
                    conversationlist.add(message);
                    mChatAdapter.notifyDataSetChanged();

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
                controlKeyboard();
                mView_more_action.setVisibility(View.GONE);

            }
        });
        mIv_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEt_chatcontent.setVisibility(View.VISIBLE);
                mIv_voice.setVisibility(View.VISIBLE);

                mBt_talk.setVisibility(View.GONE);
                mIv_keyboard.setVisibility(View.GONE);
                controlKeyboard();
                mView_more_action.setVisibility(View.GONE);
            }
        });

    }

    //控制键盘
    public void controlKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//开启或者关闭软键盘
        if (isSoftShowing()) {
            mView_more_action.setVisibility(View.VISIBLE);
        } else {
            mView_more_action.setVisibility(View.GONE);
        }
    }

    //判断键盘是否显示
    private boolean isSoftShowing() {
        //获取当前屏幕内容的高度
        int screenHeight = getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom != 0;
    }

    //消息接收监听器
    public EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            Log.e(TAG, "收到了新消息 onMessageReceived");
            conversationlist.addAll(messages);
            mHandler.sendEmptyMessage(0);

        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
            Log.e(TAG, "收到了新消息 onCmdMessageReceived");
            conversationlist.addAll(messages);
            mChatAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
            ToastUtil.showToast(SingleRoomActivity.this, "消息已送达");
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

    //销毁监听器
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }


    public void initListView() {

        mLv_chat_content = (ListView) findViewById(R.id.lv_chat_content);
        initConversation();
        mChatAdapter = new ChatAdapter();
        mLv_chat_content.setAdapter(mChatAdapter);
    }

    private void initConversation() {
        List<EMMessage> newConversationlist = ConversationsDao.getConversation(SingleRoomActivity.this, mUserName,6);
        if (newConversationlist != null) {
            conversationlist = newConversationlist;
            Log.e(TAG, "conversationlist的size为" + conversationlist.size());
        }
        mChatAdapter = new ChatAdapter();
        mLv_chat_content.setAdapter(mChatAdapter);
    }


    //初始化titlebar
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
        mIv_add.setImageResource(R.drawable.ic_person_add);
        mIv_add.setVisibility(View.VISIBLE);
        mIv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.singleroom_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_showall:
                        //显示所有聊天记录
                        List<EMMessage> newConversationlist = ConversationsDao.getConversation(SingleRoomActivity.this, mUserName,-1);
                        newConversationlist = conversationlist;
                        mChatAdapter.notifyDataSetChanged();
                        break;
                    case R.id.item_clear:
                        conversationlist.clear();
                        mChatAdapter.notifyDataSetChanged();
                        //删除聊天记录
                        EMClient.getInstance().chatManager().deleteConversation(mUserName, true);
                        break;
                    case R.id.item_info:
                        //查看好友信息
                        Intent intent = new Intent(SingleRoomActivity.this, FriDetailActivity.class);
                        intent.putExtra("friName", mUserName);
                        startActivityForResult(intent, 0);
                        break;
                    case R.id.item_add2black:
                        //加入黑名单
                        showBlackConfirm(mUserName);
                        break;

                }

                return true;
            }
        });

        popupMenu.show();
    }

    //确认加入黑名单对话框
    private void showBlackConfirm(String userName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("把好友加入黑名单后双方发消息时对方都收不到哦。\n您确认要这么残忍吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            EMClient.getInstance().contactManager().addUserToBlackList(mUserName, true);
                            ToastUtil.showToast(SingleRoomActivity.this, "加入黑名单成功");
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            ToastUtil.showToast(SingleRoomActivity.this, "加入黑名单失败");
                        }
                    }
                });
        TextView textView = new TextView(this);
        textView.setPadding(45,35,0,0);
        textView.setTextSize(20);
        textView.setText("确认加入黑名单吗？");
        textView.setTextColor(getResources().getColor(R.color.name));
        builder.setCustomTitle(textView);
        builder.show();
    }


    public class ChatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return conversationlist.size();
        }

        public EMMessage getItem(int position) {
            return conversationlist.get(position);
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

            EMMessage message = getItem(position);

            if (message.getFrom().equalsIgnoreCase(mMe)) {

                if (message.getType() != null) {
                    if (message.getType().equals(EMMessage.Type.TXT)) {
                        viewHold.tv_me.setVisibility(View.VISIBLE);
                        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                        viewHold.tv_me.setText(body.getMessage());

                    } else if (message.getType().equals(EMMessage.Type.IMAGE)) {
                        viewHold.iv_me.setVisibility(View.VISIBLE);
                        EMImageMessageBody body = (EMImageMessageBody) message.getBody();
                        Glide.with(SingleRoomActivity.this).load(body.getLocalUrl()).into(viewHold.iv_me);

                    } else if (message.getType().equals(EMMessage.Type.VOICE)) {
                        viewHold.iv_me.setVisibility(View.VISIBLE);
                    }
                    viewHold.rl_root_me.setVisibility(View.VISIBLE);
                }
            } else {

                if (message.getType() != null) {
                    if (message.getType().equals(EMMessage.Type.TXT)) {
                        viewHold.tv_other.setVisibility(View.VISIBLE);
                        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                        viewHold.tv_other.setText(body.getMessage());
                    } else if (message.getType().equals(EMMessage.Type.IMAGE)) {
                        viewHold.iv_other.setVisibility(View.VISIBLE);
                        EMImageMessageBody body = (EMImageMessageBody) message.getBody();
                        Glide.with(SingleRoomActivity.this).load(body.getLocalUrl()).into(viewHold.iv_other);

                    } else if (message.getType().equals(EMMessage.Type.VOICE)) {
                        viewHold.iv_other.setVisibility(View.VISIBLE);
                    }
                    viewHold.rl_root_other.setVisibility(View.VISIBLE);
                }

            }
            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            scrollMyListViewToBottom();
        }

    }

    //使listview显示的一直是最后一个item
    private void scrollMyListViewToBottom() {
        mLv_chat_content.post(new Runnable() {
            @Override
            public void run() {
                mLv_chat_content.setSelection(mLv_chat_content.getCount() - 1);
            }
        });
    }


    static class ViewHold {

        public RelativeLayout rl_root_me;
        public TextView tv_me;
        public ImageView iv_me;

        public RelativeLayout rl_root_other;
        public TextView tv_other;
        public ImageView iv_other;
    }

    private TextView tv_photo;
    private TextView tv_camera;
    private TextView tv_video;
    private TextView tv_location;
    private TextView tv_call;
    private TextView tv_facetime;
    private TextView tv_file;
    private TextView tv_money;

    //设置点击侦听
    private void initMoreAction() {
        tv_photo = (TextView) findViewById(R.id.tv_photo);
        tv_camera = (TextView) findViewById(R.id.tv_camera);
        tv_video = (TextView) findViewById(R.id.tv_video);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_call = (TextView) findViewById(R.id.tv_call);
        tv_facetime = (TextView) findViewById(R.id.tv_facetime);
        tv_file = (TextView) findViewById(R.id.tv_file);
        tv_money = (TextView) findViewById(R.id.tv_money);

        tv_photo.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
        tv_video.setOnClickListener(this);
        tv_location.setOnClickListener(this);
        tv_call.setOnClickListener(this);
        tv_facetime.setOnClickListener(this);
        tv_file.setOnClickListener(this);
        tv_money.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_photo:
                Intent getPhoto = new Intent(Intent.ACTION_GET_CONTENT);
                getPhoto.setType("image/*");
                startActivityForResult(getPhoto, GET_PHOTO);
                break;
            case R.id.tv_camera:
                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
                openCamera.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                startActivityForResult(openCamera, OPEN_CAMERA);
                break;
            case R.id.tv_video:

                break;
            case R.id.tv_location:
                enterMapActivity();
                break;
            case R.id.tv_call:

                break;
            case R.id.tv_facetime:

                break;
            case R.id.tv_file:

                break;
            case R.id.tv_money:

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GET_PHOTO:
                    //加载图片
                    EMMessage imageSendMessage = EMMessage.createImageSendMessage(UriUtil.getFileAbsolutePath(this, data.getData()), false, mUserName);
//                    if (chatType == CHATTYPE_GROUP)
//                       message.setChatType(ChatType.GroupChat);
//                    EMClient.getInstance().chatManager().sendMessage(imageSendMessage);
                    conversationlist.add(imageSendMessage);
                    mChatAdapter.notifyDataSetChanged();
                    break;
                case OPEN_CAMERA:
                    EMMessage imageSendMessage2 = EMMessage.createImageSendMessage(UriUtil.getFileAbsolutePath(this, tempUri), false, mUserName);
//                    if (chatType == CHATTYPE_GROUP)
//                       message.setChatType(ChatType.GroupChat);
//                    EMClient.getInstance().chatManager().sendMessage(imageSendMessage);
                    conversationlist.add(imageSendMessage2);
                    mChatAdapter.notifyDataSetChanged();

                    break;

            }

        }
    }

    //进入千度地图页面
    private void enterMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        //初始化地图sdk
        SDKInitializer.initialize(getApplicationContext());
    }

}




















