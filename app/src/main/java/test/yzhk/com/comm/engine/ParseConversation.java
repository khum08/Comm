package test.yzhk.com.comm.engine;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by 大傻春 on 2017/12/2.
 */

public class ParseConversation {

    public static List<EMMessage> parse(EMConversation conversation){
        List<EMMessage> allMessages = conversation.getAllMessages();
        return allMessages;
    }

    public static int getCount(EMConversation conversation){
        int count = conversation.getAllMsgCount();
        return count;
    }

    public static EMMessage getLastMsg(EMConversation conversation){
        EMMessage lastMessage = conversation.getLastMessage();
        return lastMessage;
    }

}
