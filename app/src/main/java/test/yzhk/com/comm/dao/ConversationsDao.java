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

    public static List<EMMessage> getConversation(Context context, String username) {

        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username, null, true);
        if (conversation != null) {
            conversation.markAllMessagesAsRead();
            List<EMMessage> mMessages = conversation.getAllMessages();
            return mMessages;
        }

        return null;
    }
}
