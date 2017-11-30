package test.yzhk.com.comm.UI.fragments;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.Map;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.dao.ConversationsDao;
import test.yzhk.com.comm.engine.ParseConversations;

/**
 * Created by 大傻春 on 2017/11/24.
 */

public class ChatFragment extends BaseFragment {

    public View mChatView;
    public Map<String, EMMessage> mMap;
    public ArrayList<String> mKeyList;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ConversationAdapter mAdapter = new ConversationAdapter();
                    ListView lv_chat = (ListView) mChatView.findViewById(R.id.lv_chat);
                    lv_chat.setAdapter(mAdapter);
                    break;
            }

        }
    };

    @Override
    public View initView() {
        mChatView = View.inflate(mContext, R.layout.fragment_chat, null);
        TextView tv_title = (TextView) mChatView.findViewById(R.id.tv_title);
        tv_title.setText(R.string.app_name);
        ImageView iv_add = (ImageView) mChatView.findViewById(R.id.iv_add);
        iv_add.setVisibility(View.VISIBLE);
        return mChatView;
    }

    @Override
    public void initData() {
        new Thread() {

            @Override
            public void run() {
                super.run();
                Map<String, EMConversation> allConversations = ConversationsDao.getAllConversations();
                mMap = ParseConversations.parse(allConversations);
                mKeyList = new ArrayList<>();
                if (mMap != null) {
                    for (Map.Entry<String, EMMessage> entry : mMap.entrySet()) {
                        mKeyList.add(entry.getKey());
                    }
                }
                mHandler.sendEmptyMessage(0);

            }
        }.start();


    }

    public class ConversationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mKeyList != null) {
                return mKeyList.size();
            }
            return 0;
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
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(mMap!=null){
                String conversationId = getItem(position);
                holder.tv_conv_name.setText(conversationId);

                EMMessage emMessage = mMap.get(conversationId);
                if (emMessage.getType() == EMMessage.Type.TXT) {
                    EMTextMessageBody body = (EMTextMessageBody) emMessage.getBody();
                    String message = body.getMessage();
                    holder.tv_conv_content.setText(message);
                }else if(emMessage.getType() == EMMessage.Type.VOICE){
                    holder.tv_conv_content.setText("语音");
                }else if(emMessage.getType() == EMMessage.Type.IMAGE){
                    holder.tv_conv_content.setText("图片");
                }else {
                    holder.tv_conv_content.setText("");
                }
            }

            return convertView;
        }
    }

    static class ViewHolder {
        public TextView tv_conv_name;
        public TextView tv_conv_content;
    }

}
