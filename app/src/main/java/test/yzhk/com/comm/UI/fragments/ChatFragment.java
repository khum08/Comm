package test.yzhk.com.comm.UI.fragments;

import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.Map;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.activities.SingleRoomActivity;
import test.yzhk.com.comm.dao.ConversationsDao;
import test.yzhk.com.comm.engine.ParseConversations;
import test.yzhk.com.comm.utils.DateUtil;
import test.yzhk.com.comm.utils.ToastUtil;

import static test.yzhk.com.comm.R.id.item_contact;
import static test.yzhk.com.comm.R.id.item_paymoney;
import static test.yzhk.com.comm.R.id.item_scan;

/**
 * Created by 大傻春 on 2017/11/24.
 */

public class ChatFragment extends BaseFragment {

    public View mChatView;
    public Map<String, EMConversation> mMap;
    public ArrayList<String> mKeyList;
    private static final String TAG = "ChatFragment";
    private ConversationAdapter mAdapter;

    private ListView mLv_chat;

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

    private void showSettings(View v) {
        PopupMenu popupMenu = new PopupMenu(mContext, v);
        popupMenu.getMenuInflater().inflate(R.menu.chatfragment_more, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case item_scan:
                        ToastUtil.showToast(mContext,"显示二维码扫描页面");
                        break;
                    case item_paymoney:
                        ToastUtil.showToast(mContext,"显示钱包页面");
                        break;
                    case item_contact:
                        ToastUtil.showToast(mContext,"显示聊天界面");
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
        Map<String, EMConversation> allConversations = ConversationsDao.getAllConversations();
        mMap = ParseConversations.parse(allConversations);
        mKeyList = new ArrayList<>();
        if (mMap != null) {
            for (Map.Entry<String, EMConversation> entry : mMap.entrySet()) {
                mKeyList.add(entry.getKey());
            }
        }
//        for (int i = 0; i < mKeyList.size(); i++) {
//            String s = mKeyList.get(i);
//            Log.e(TAG, s);
//        }

        mAdapter = new ConversationAdapter();

        mLv_chat.setAdapter(mAdapter);
        mLv_chat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, SingleRoomActivity.class);
                String conversationId = mKeyList.get(position);

                intent.putExtra("userName",conversationId);
                startActivity(intent);

                //未读消息数目清零
                EMConversation conversation = mMap.get(conversationId);
                conversation.markAllMessagesAsRead();

            }
        });
        mLv_chat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupMenu(view,position);
                return true;
            }
        });

    }

    private void showPopupMenu(View view,final int position) {

        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.conversation_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_delete:
                        String conversationId = mKeyList.get(position);
                        EMClient.getInstance().chatManager().deleteConversation(conversationId, true);
                        mKeyList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        ToastUtil.showToast(mContext,"删除成功");
                        break;
                    case R.id.item_markread:
                        ToastUtil.showToast(mContext,"聊天标记为已读");
                        break;
                }


                return false;
            }
        });
        popupMenu.show();

    }

    public class ConversationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mKeyList.size();
        }

        @Override
        public String getItem(int position) {
            return mKeyList.get(position);
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

            if (mMap != null) {
                String conversationId = getItem(position);
                holder.tv_conv_name.setText(conversationId);

                EMConversation conversation = mMap.get(conversationId);
                //设置未读
                int unreadMsgCount = conversation.getUnreadMsgCount();
                if(unreadMsgCount>0){
                    if(unreadMsgCount>99){
                        holder.tv_unread.setText("99");
                    }else{
                        holder.tv_unread.setText(unreadMsgCount+"");
                    }
                }else{
                    holder.tv_unread.setVisibility(View.INVISIBLE);
                }
                //设置时间
                EMMessage lastMessage = conversation.getLastMessage();
                long msgTime = lastMessage.getMsgTime();
                long interval = msgTime - System.currentTimeMillis();
                if(interval>86400){
                    holder.tv_time.setText(DateUtil.formate("E",msgTime));
                }else{
                    holder.tv_time.setText(DateUtil.formate("HH:mm",msgTime));
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

}
