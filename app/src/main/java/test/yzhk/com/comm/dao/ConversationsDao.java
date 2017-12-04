package test.yzhk.com.comm.dao;

import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by 大傻春 on 2017/11/30.
 */

public class ConversationsDao {


    public static Map<String, EMConversation> getAllConversations() {
        Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();
        return allConversations;
    }

    public static List<EMMessage> getConversation(Context context, String username,int size) {

        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        if (conversation != null) {
            conversation.markAllMessagesAsRead();

            List<EMMessage> mMessages = conversation.getAllMessages();
            if(size==-1){
                mMessages = conversation.loadMoreMsgFromDB(mMessages.get(mMessages.size()-1).getMsgId(),mMessages.size());
            }
            mMessages = conversation.loadMoreMsgFromDB(mMessages.get(mMessages.size()-1).getMsgId(),size);
            return mMessages;
        }
        return null;
    }

    public static List<EMMessage> getAllMessages(Context context, String username){
        List<EMMessage> conversation = getConversation(context, username, -1);
        return conversation;
    }
}
