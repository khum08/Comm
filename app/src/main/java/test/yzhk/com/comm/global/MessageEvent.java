package test.yzhk.com.comm.global;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by 大傻春 on 2017/12/19.
 */

public class MessageEvent {
    public String msg;
    public MessageEvent(String msg){
        this.msg = msg;
    }
    public String getMsg(){
        return msg;
    }

    public List<EMMessage> messages;
    public MessageEvent(List<EMMessage> messages){
        this.messages = messages;
    }
    public List<EMMessage> getMessages(){
        return messages;
    }
}
