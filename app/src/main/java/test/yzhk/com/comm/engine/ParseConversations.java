package test.yzhk.com.comm.engine;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 大傻春 on 2017/11/30.
 */

public class ParseConversations {


    /**
     *
     * @param allConversations
     * @return Map<String ,EMMessage>或null
     * string表示对方名称
     * EMMessage 为最后一条信息
     */
    public static Map<String ,EMMessage> parse(Map<String, EMConversation> allConversations){

        if(allConversations!=null){
            Map<String ,EMMessage> map = new HashMap<>();
            for (Map.Entry entry:allConversations.entrySet()) {
                String conversationId = (String) entry.getKey();
                EMConversation conversation = (EMConversation) entry.getValue();
                EMMessage lastMessage = conversation.getLastMessage();
                map.put(conversationId,lastMessage);
            }
            return map;
        }
        return null;

    }
}
