package test.yzhk.com.comm.engine;

import android.util.Log;

import com.hyphenate.chat.EMConversation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 大傻春 on 2017/11/30.
 */

public class ParseConversations {


    private static final String TAG = "ParseConversations";
    /**
     *
     * @param allConversations
     * @return Map<String ,List<EMMessage>>或null
     * string表示对方名称
     * EMMessage 为最后一条信息
     */
    public static Map<String ,EMConversation> parse(Map<String, EMConversation> allConversations){

        if(allConversations!=null){
            Log.e(TAG,"有会话存在");
            Map<String ,EMConversation> map = new HashMap<>();
            for (Map.Entry entry:allConversations.entrySet()) {
                String conversationId = (String) entry.getKey();
                EMConversation conversation = (EMConversation) entry.getValue();
                map.put(conversationId,conversation);
            }
            return map;
        }
        return null;

    }
}
