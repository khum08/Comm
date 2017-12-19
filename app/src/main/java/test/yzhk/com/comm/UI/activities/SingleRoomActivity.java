package test.yzhk.com.comm.UI.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.Glide;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.utils.FileUtil;
import test.yzhk.com.comm.utils.ToastUtil;
import test.yzhk.com.comm.utils.UriUtil;

import static com.hyphenate.chat.EMMessage.createImageSendMessage;
import static test.yzhk.com.comm.global.C.GET_DATA;
import static test.yzhk.com.comm.global.C.GET_GROUP_DATA;
import static test.yzhk.com.comm.global.C.GET_LOCATION;
import static test.yzhk.com.comm.global.C.GET_PHOTO;
import static test.yzhk.com.comm.global.C.OPEN_CAMERA;
import static test.yzhk.com.comm.global.C.OPEN_VIDEO;
import static test.yzhk.com.comm.global.C.REFRESH_DATA;


public class SingleRoomActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SingleRoomActivity";

    private String mUserName;
    private ListView mLv_chat_content;
    private ChatAdapter mChatAdapter;

    private String mMe;

    public List<EMMessage> conversationlist = new ArrayList<>();
    private List<EMMessage> showMessages = new ArrayList<>();
    //将要存进数据库中的newMessages
    private List<EMMessage> newMessages = new ArrayList<>();
    private ImageView mIv_add;
    private EditText mEt_chatcontent;

    private ImageView mIv_voice;
    private Button mBt_talk;
    private ImageView mIv_keyboard;
    private LinearLayout mView_more_action;
    private Button mBt_send;
    private ImageView mIv_add_choice;

    private SwipeRefreshLayout mSingleroon_refresh;
    private Uri photoUri1;
    private File mImgFile;
    private Uri mVideoUri;
    private File mVideoFile;
    private EMConversation mConversation;
    //聊天消息列表
    private List<EMMessage> mAllMessages;
    private long mCurrentTimeMillis;
    private String mGroupId;
    private EMMessage.ChatType mChatType;
    private EMGroup mGroup;
    private TextView mTv_title;
    private String mGroupName;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_DATA:
                    mChatAdapter.notifyDataSetChanged();
                    break;
                case GET_DATA:
                    mChatAdapter = new ChatAdapter();
                    mLv_chat_content.setAdapter(mChatAdapter);
                    break;
                case GET_GROUP_DATA:
                    mTv_title.setText(mGroup.getGroupName());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mUserName = intent.getStringExtra("userName");
        mGroupId = intent.getStringExtra("groupId");
        if(mUserName!=null){
            mChatType = EMMessage.ChatType.Chat;
        }else if(mGroupId!=null){
            mChatType = EMMessage.ChatType.GroupChat;
        }

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

        initRefresh();
    }

    //处理下拉刷新操作
    private void initRefresh() {
        mSingleroon_refresh = (SwipeRefreshLayout) findViewById(R.id.singleroon_refresh);
        mSingleroon_refresh.setColorSchemeResources(R.color.red, R.color.colorPrimary, R.color.name);
        mSingleroon_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentTimeMillis = System.currentTimeMillis();
                new Thread() {
                    @Override
                    public void run() {
                        //分页加载数据,每次加载20条
                        try {
                            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mUserName);
                            if(showMessages.size()<conversation.getAllMsgCount()){
                                showMessages.clear();
                                List<EMMessage> allMessages = conversation.getAllMessages();
                                showMessages.addAll(allMessages);
                            }else{
                                EMMessage lastmessage = showMessages.get(showMessages.size() - 1);
                                List<EMMessage> messages = conversation.loadMoreMsgFromDB(lastmessage.getMsgId(), 20);
                                if(messages!=null){
                                    showMessages.addAll(messages);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //保证刷新不会太快
                        long newTime = System.currentTimeMillis();
                        if(newTime-mCurrentTimeMillis>1500){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSingleroon_refresh.setRefreshing(false);
                                }
                            });
                        }else{
                            try {
                                Thread.sleep(1500-(newTime-mCurrentTimeMillis));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSingleroon_refresh.setRefreshing(false);
                                }
                            });
                        }
                        mHandler.sendEmptyMessage(REFRESH_DATA);
                    }
                }.start();
            }
        });
    }

    //初始化底部聊天输入控件
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
                mView_more_action.setVisibility(View.GONE);
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
                    final EMMessage message = EMMessage.createTxtSendMessage(text, mUserName);
                    if (mChatType == EMMessage.ChatType.GroupChat)
                        message.setChatType(EMMessage.ChatType.GroupChat);
                    newMessages.add(message);
                    new Thread() {
                        @Override
                        public void run() {
                            EMClient.getInstance().chatManager().sendMessage(message);
                        }
                    }.start();

                    //集合变化
                    showMessages.add(message);
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
            showMessages.addAll(messages);
            newMessages.addAll(messages);
            mHandler.sendEmptyMessage(REFRESH_DATA);
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
            ToastUtil.showToast(SingleRoomActivity.this, "对方企图撤回一条消息，哈哈哈哈");
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
        new Thread(){
            @Override
            public void run() {
                EMClient.getInstance().chatManager().removeMessageListener(msgListener);
                EMClient.getInstance().chatManager().importMessages(newMessages);
            }
        }.start();
    }


    public void initListView() {
        mLv_chat_content = (ListView) findViewById(R.id.lv_chat_content);
        new Thread() {
            @Override
            public void run() {
                //初始化聊天记录
                mConversation = EMClient.getInstance().chatManager().getConversation(mUserName);
                if (mConversation != null) {
                    mAllMessages = mConversation.getAllMessages();
                    Log.e(TAG,"mAllMessages的长度为："+mAllMessages.size());
                    if (mAllMessages != null) {
                        if (mAllMessages.size() >= 4) {
                            for (int i = 0; i < 4; i++) {
                                showMessages.add(mAllMessages.get(i));
                            }
                        } else {
                            showMessages.addAll(mAllMessages);
                        }
                    }
                }
                Log.e(TAG,"showMessages的长度为："+showMessages.size());
                mHandler.sendEmptyMessage(GET_DATA);
            }
        }.start();

    }

    //初始化titlebar
    private void initTitle() {
        mTv_title = (TextView) findViewById(R.id.tv_title);
        mIv_add = (ImageView) findViewById(R.id.iv_add);
        if(mChatType== EMMessage.ChatType.GroupChat){
            initGroup();
        }else if(mChatType == EMMessage.ChatType.Chat){
            mTv_title.setText(mUserName);
        }

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

        if(mChatType== EMMessage.ChatType.GroupChat){
            mIv_add.setImageResource(R.drawable.ic_people_white_24dp);
            mIv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SingleRoomActivity.this, GroupInfoActivity.class);
                    intent.putExtra("groupId",mGroupId);
                    intent.putExtra("groupName",mGroupName);
                    startActivity(intent);
                }
            });
        }else if(mChatType == EMMessage.ChatType.Chat){
            mIv_add.setImageResource(R.drawable.ic_person_white_24dp);
            mIv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(v);
                }
            });
        }
        mIv_add.setVisibility(View.VISIBLE);
    }

    //如果是群组，则初始化群组信息
    private void initGroup() {
        mGroup = EMClient.getInstance().groupManager().getGroup(mGroupId);
        new Thread(){
            @Override
            public void run() {
                try {
                    mGroup = EMClient.getInstance().groupManager().getGroupFromServer(mGroupId);
                    mHandler.sendEmptyMessage(GET_GROUP_DATA);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        if(mGroup!=null){
            mGroupName = mGroup.getGroupName();
            mTv_title.setText(mGroupName);
        }
    }

    //右上角更多操作按钮
    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.singleroom_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_clear:
                        conversationlist.clear();
                        mChatAdapter.notifyDataSetChanged();
                        //删除聊天记录
                        new Thread() {
                            @Override
                            public void run() {
                                EMClient.getInstance().chatManager().deleteConversation(mUserName, true);
                            }
                        }.start();
                        break;
                    case R.id.item_info:
                        //查看好友信息
                        Intent intent = new Intent(SingleRoomActivity.this, FriDetailActivity.class);
                        intent.putExtra("friName", mUserName);
                        startActivity(intent);
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
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    EMClient.getInstance().contactManager().addUserToBlackList(mUserName, true);
                                    ToastUtil.showToast(SingleRoomActivity.this, "添加成功，对象已接收不到您的消息");
                                    //// TODO: 2017/12/8 contactfragment 的数据需更新
                                } catch (HyphenateException e) {
                                    e.printStackTrace();
                                    ToastUtil.showToast(SingleRoomActivity.this, "加入黑名单失败");
                                }
                            }
                        }.start();

                    }
                });
        TextView textView = new TextView(this);
        textView.setPadding(45, 35, 0, 0);
        textView.setTextSize(20);
        textView.setText("确认加入黑名单吗？");
        textView.setTextColor(getResources().getColor(R.color.name));
        builder.setCustomTitle(textView);
        builder.show();
    }


    public class ChatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return showMessages.size();
        }

        public EMMessage getItem(int position) {
            return showMessages.get(position);
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
                viewHold.tv_ta_name = (TextView) convertView.findViewById(R.id.tv_ta_name);
                viewHold.iv_other = (ImageView) convertView.findViewById(R.id.iv_ohter);
                convertView.setTag(viewHold);
            } else {
                viewHold = (ViewHold) convertView.getTag();
            }

            EMMessage message = getItem(position);
            String from = message.getFrom();
            if (from.equalsIgnoreCase(mMe)) {

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
                    } else if (message.getType().equals(EMMessage.Type.LOCATION)) {
                        viewHold.tv_me.setVisibility(View.VISIBLE);
                        EMLocationMessageBody body = (EMLocationMessageBody) message.getBody();
                        viewHold.tv_me.setText(body.getAddress() + "\n经纬度：" + body.getLatitude() + "," + body.getLongitude());
                    }
                    viewHold.rl_root_me.setVisibility(View.VISIBLE);
                }
            } else {
                if(mChatType== EMMessage.ChatType.Chat){
                    viewHold.tv_ta_name.setText("ta");
                }else if(mChatType== EMMessage.ChatType.GroupChat){
                    viewHold.tv_ta_name.setText(from);
                }
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
                    } else if (message.getType().equals(EMMessage.Type.LOCATION)) {
                        viewHold.tv_other.setVisibility(View.VISIBLE);
                        EMLocationMessageBody body = (EMLocationMessageBody) message.getBody();
                        viewHold.tv_other.setText(body.getAddress() + "\n经纬度：" + body.getLatitude() + "," + body.getLongitude());
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
        public TextView tv_ta_name;
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
                mImgFile = FileUtil.createImgFile(this);
                photoUri1 = Uri.fromFile(mImgFile);
                openCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri1);
                startActivityForResult(openCamera, OPEN_CAMERA);
                break;
            case R.id.tv_video:
                Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                mVideoFile = FileUtil.createVideoFile(this);
                mVideoUri = Uri.fromFile(mVideoFile);
                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoUri);
                startActivityForResult(videoIntent, OPEN_VIDEO);
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
                    final EMMessage imageSendMessage = createImageSendMessage(UriUtil.getFileAbsolutePath(this, data.getData()), false, mUserName);
                    if (mChatType == EMMessage.ChatType.GroupChat)
                        imageSendMessage.setChatType(EMMessage.ChatType.GroupChat);
                    new Thread(){
                        @Override
                        public void run() {
                            EMClient.getInstance().chatManager().sendMessage(imageSendMessage);
                        }
                    }.start();
                    showMessages.add(imageSendMessage);
                    newMessages.add(imageSendMessage);
                    mChatAdapter.notifyDataSetChanged();
                    break;
                case OPEN_CAMERA:

                    final EMMessage imageMsg
                            = EMMessage.createImageSendMessage(mImgFile.getAbsolutePath(), false, mUserName);
                    EMClient.getInstance().chatManager().sendMessage(imageMsg);
                    if (mChatType == EMMessage.ChatType.GroupChat)
                        imageMsg.setChatType(EMMessage.ChatType.GroupChat);
                    new Thread(){
                        @Override
                        public void run() {
                            EMClient.getInstance().chatManager().sendMessage(imageMsg);
                        }
                    }.start();
                    showMessages.add(imageMsg);
                    newMessages.add(imageMsg);
                    mChatAdapter.notifyDataSetChanged();
                    break;
                case OPEN_VIDEO:
                    final EMMessage videoMsg = EMMessage.createVideoSendMessage(mVideoFile.getAbsolutePath(), mVideoFile.getAbsolutePath(), (int) mVideoFile.length(), mUserName);
                    if (mChatType == EMMessage.ChatType.GroupChat)
                        videoMsg.setChatType(EMMessage.ChatType.GroupChat);
                    new Thread(){
                        @Override
                        public void run() {
                            EMClient.getInstance().chatManager().sendMessage(videoMsg);
                        }
                    }.start();
                    ToastUtil.showToast(this, "视频发送成功");
                    //// TODO: 2017/12/7 显示视频第一帧

                    break;
                case GET_LOCATION:
                    BDLocation location = data.getParcelableExtra("location");
                    final EMMessage locationMessage = EMMessage.createLocationSendMessage(location.getLatitude(), location.getLongitude()
                            , location.getAddrStr(), mUserName);
                    if (mChatType == EMMessage.ChatType.GroupChat)
                        locationMessage.setChatType(EMMessage.ChatType.GroupChat);
                    new Thread(){
                        @Override
                        public void run() {
                            EMClient.getInstance().chatManager().sendMessage(locationMessage);
                        }
                    }.start();
                    showMessages.add(locationMessage);
                    newMessages.add(locationMessage);
                    mChatAdapter.notifyDataSetChanged();
                    break;
            }

        }
    }

    //进入千度地图页面
    private void enterMapActivity() {
        //初始化地图sdk
        SDKInitializer.initialize(getApplicationContext());
        Intent intent = new Intent(this, MapActivity.class);
        startActivityForResult(intent, GET_LOCATION);

    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(REFRESH_DATA);
        super.onBackPressed();
    }
}




















