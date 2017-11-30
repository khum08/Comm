package test.yzhk.com.comm.dao;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.Map;

/**
 * Created by 大傻春 on 2017/11/30.
 */

public class ConversationsDao {

    public static Map<String, EMConversation> getAllConversations() {
        Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();
        return allConversations;
    }

    public static void getConversation() {


    }
}
