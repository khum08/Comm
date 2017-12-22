package test.yzhk.com.comm.eventbus;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Eventbus 消息类
 * Created by 大傻春 on 2017/12/19.
 */

public class MessageEvent {
    public int command;
    public MessageEvent(int command){
        this.command = command;
    }
    public int getCommand(){
        return command;
    }

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
