package test.yzhk.com.comm.UI.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.activities.ChatRoomMakerActivity;
import test.yzhk.com.comm.UI.activities.GroupMakerActivity;
import test.yzhk.com.comm.UI.activities.SingleRoomActivity;
import test.yzhk.com.comm.UI.activities.WalletActivity;
import test.yzhk.com.comm.utils.DateUtil;
import test.yzhk.com.comm.utils.ToastUtil;

import static android.app.Activity.RESULT_OK;
import static test.yzhk.com.comm.R.id.item_chatroom;
import static test.yzhk.com.comm.R.id.item_group;
import static test.yzhk.com.comm.R.id.item_paymoney;
import static test.yzhk.com.comm.R.id.item_scan;

/**
 * Created by 大傻春 on 2017/11/24.
 */

public class ChatFragment extends BaseFragment {

    private static final int GET_DATA = 644;
    private static final int REFRESH_DATA = 61;
    private static final int REQUEST_CODE_SCAN = 407;
    public View mChatView;
    private static final String TAG = "ChatFragment";
    private ConversationAdapter mAdapter;

    private ListView mLv_chat;
    private SwipeRefreshLayout mSwipeRefreshView;
    private List<String> mConversationList;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DATA:
                    mAdapter = new ConversationAdapter();
                    mLv_chat.setAdapter(mAdapter);
                    break;
                case REFRESH_DATA:
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private Map<String, EMConversation> mAllConversations;
    private long mCurrentTimeMillis;
    private ChatFragment() {
        super();
    }

    private static ChatFragment mChatFragment;
    public static ChatFragment getInstance() {
        if(mChatFragment==null){
            mChatFragment = new ChatFragment();
        }
        return mChatFragment;
    }

    @Override
    public View initView() {

        mChatView = View.inflate(mContext, R.layout.fragment_chat, null);
        TextView tv_title = (TextView) mChatView.findViewById(R.id.tv_title);
        tv_title.setText(R.string.app_name);
        ImageView iv_add = (ImageView) mChatView.findViewById(R.id.iv_add);
        iv_add.setVisibility(View.VISIBLE);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettings(v);
            }
        });

        return mChatView;
    }

    //右上角设置条目
    private void showSettings(View v) {
        PopupMenu popupMenu = new PopupMenu(mContext, v);
        popupMenu.getMenuInflater().inflate(R.menu.chatfragment_more, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case item_scan:
                        Intent intent = new Intent(mContext,  CaptureActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_SCAN);
                        break;
                    case item_paymoney:
                        startActivity(new Intent(mContext, WalletActivity.class));
                        break;
                    case item_group:
                        Intent groupMaker = new Intent(mContext, GroupMakerActivity.class);
                        startActivity(groupMaker);
                        break;
                    case item_chatroom:
                        Intent chatRoomMaker = new Intent(mContext, ChatRoomMakerActivity.class);
                        startActivity(chatRoomMaker);
                        break;
                }

                return true;
            }
        });
        popupMenu.show();

    }

    @Override
    public void initData() {
        mLv_chat = (ListView) mChatView.findViewById(R.id.lv_chat);
        new Thread() {
            @Override
            public void run() {

                mAllConversations = EMClient.getInstance().chatManager().getAllConversations();
                if (mAllConversations != null) {
                    mConversationList = new ArrayList<String>();
                    for (Map.Entry entry : mAllConversations.entrySet()) {
                        String conversationId = (String) entry.getKey();
                        mConversationList.add(conversationId);
                    }
                    mHandler.sendEmptyMessage(GET_DATA);
                }
            }
        }.start();

        //设置listview的点击侦听
        mLv_chat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //未读消息数目清零
                String conversationId = mConversationList.get(position);
                EMConversation conversation = mAllConversations.get(conversationId);
                conversation.markAllMessagesAsRead();

                //跳转聊天室
                Intent intent = new Intent(mContext, SingleRoomActivity.class);
                intent.putExtra("userName", conversationId);
                startActivity(intent);
            }
        });
        mLv_chat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupMenu(view, position);
                return true;
            }
        });

        initSwipeRefresh();
        //监听消息
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    //初始化下拉刷新控件
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void initSwipeRefresh() {
        mSwipeRefreshView = (SwipeRefreshLayout) mChatView.findViewById(R.id.swipe_refresh);
        mSwipeRefreshView.setColorSchemeResources(R.color.red, R.color.colorPrimary, R.color.name);
        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        mSwipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentTimeMillis = System.currentTimeMillis();
                new Thread() {
                    @Override
                    public void run() {
                        mAllConversations = EMClient.getInstance().chatManager().getAllConversations();
                        if (mAllConversations != null) {
                            for (Map.Entry<String, EMConversation> entry : mAllConversations.entrySet()) {
                                if(!mConversationList.contains(entry.getKey())){
                                    mConversationList.add(entry.getKey());
                                }
                            }
                        }
                        //每次刷新至少要1500毫秒
                        long newTime = System.currentTimeMillis();
                        if (newTime - mCurrentTimeMillis > 1500) {
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSwipeRefreshView.setRefreshing(false);
                                }
                            });
                        } else {
                            try {
                                Thread.sleep(1500 - (newTime - mCurrentTimeMillis));
                                mContext.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSwipeRefreshView.setRefreshing(false);
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        mHandler.sendEmptyMessage(REFRESH_DATA);
                    }
                }.start();
            }
        });

    }

    //长按条目显示更多操作
    private void showPopupMenu(View view, final int position) {
        final String conversationId = mConversationList.get(position);
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.conversation_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_delete:

                        new Thread() {
                            @Override
                            public void run() {
                                EMClient.getInstance().chatManager().deleteConversation(conversationId, true);
                                mConversationList.remove(position);
                                mHandler.sendEmptyMessage(REFRESH_DATA);
                                ToastUtil.showToast(mContext, "删除成功");
                            }
                        }.start();

                        break;
                    case R.id.item_markread:
                        //// TODO: 2017/12/6
                        new Thread() {
                            @Override
                            public void run() {
                                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(conversationId);
                                //指定会话消息未读数清零
                                EMConversation remove = mAllConversations.remove(conversationId);
                                remove.markAllMessagesAsRead();
                                mAllConversations.put(conversationId,remove);
                                mHandler.sendEmptyMessage(REFRESH_DATA);
                            }
                        }.start();
                        break;
                }

                return false;
            }
        });
        popupMenu.show();
    }

    //listview 适配器
    public class ConversationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mConversationList != null) {
                return mConversationList.size();
            }
            return 0;
        }

        @Override
        public String getItem(int position) {
            if (mConversationList != null) {
                return mConversationList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.list_item_conversation, null);
                holder.tv_conv_name = (TextView) convertView.findViewById(R.id.tv_conv_name);
                holder.tv_conv_content = (TextView) convertView.findViewById(R.id.tv_conv_content);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_unread = (TextView) convertView.findViewById(R.id.tv_unread);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (mAllConversations != null) {
                String conversationId = getItem(position);
                holder.tv_conv_name.setText(conversationId);

                EMConversation conversation = mAllConversations.get(conversationId);
                //设置未读
                int unreadMsgCount = conversation.getUnreadMsgCount();
                if (unreadMsgCount > 0) {
                    if (unreadMsgCount > 99) {
                        holder.tv_unread.setText("99");
                    } else {
                        holder.tv_unread.setText(unreadMsgCount + "");
                    }
                } else {
                    holder.tv_unread.setVisibility(View.INVISIBLE);
                }
                //设置时间
                EMMessage lastMessage = conversation.getLastMessage();
                long msgTime = lastMessage.getMsgTime();
                long interval = msgTime - System.currentTimeMillis();
                if (interval > 86400) {
                    holder.tv_time.setText(DateUtil.formate("E", msgTime));
                } else {
                    holder.tv_time.setText(DateUtil.formate("HH:mm", msgTime));
                }

                if (lastMessage.getType() == EMMessage.Type.TXT) {
                    EMTextMessageBody body = (EMTextMessageBody) lastMessage.getBody();
                    String message = body.getMessage();
                    holder.tv_conv_content.setText(message);
                } else if (lastMessage.getType() == EMMessage.Type.VOICE) {
                    holder.tv_conv_content.setText("语音");
                } else if (lastMessage.getType() == EMMessage.Type.IMAGE) {
                    holder.tv_conv_content.setText("图片");
                } else {
                    holder.tv_conv_content.setText("");
                }
            }

            return convertView;
        }
    }

    static class ViewHolder {
        public TextView tv_conv_name;
        public TextView tv_conv_content;
        public TextView tv_time;
        public TextView tv_unread;
    }

    //回调接口 创建新的会话
    public void createConversation() {
        new Thread() {
            @Override
            public void run() {
                mAllConversations = EMClient.getInstance().chatManager().getAllConversations();
                if (mAllConversations != null) {
                    for (Map.Entry<String, EMConversation> entry : mAllConversations.entrySet()) {
                        if(!mConversationList.contains(entry.getKey())){
                            mConversationList.add(entry.getKey());
                        }
                    }
                }
                mHandler.sendEmptyMessage(REFRESH_DATA);
            }
        }.start();
    }

    //监听消息的接收
    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            getNewMessages();
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

    //接收到新消息的逻辑
    private void getNewMessages() {
        new Thread(){
            @Override
            public void run() {
                mAllConversations = EMClient.getInstance().chatManager().getAllConversations();
                if (mAllConversations != null) {
                    if (mConversationList == null)
                        mConversationList = new ArrayList<>();
                    for (Map.Entry<String, EMConversation> entry : mAllConversations.entrySet()) {
                        mConversationList.clear();
                        mConversationList.add(entry.getKey());
                    }
                }
                mHandler.sendEmptyMessage(REFRESH_DATA);
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁监听器
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    //给conversation排序
    private void sort(Map<String,EMConversation> map){
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                ToastUtil.showToast(mContext,content);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
