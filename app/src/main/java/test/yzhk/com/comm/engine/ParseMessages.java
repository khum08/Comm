package test.yzhk.com.comm.engine;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;

import java.util.ArrayList;
import java.util.List;

import test.yzhk.com.comm.domain.ConversationBean;

/**
 * Created by 大傻春 on 2017/11/30.
 */

public class ParseMessages {

    /**
     *
     * @param messages
     * @return 返回ArrayList<ConversationBean>或mull
     */
    public static ArrayList<ConversationBean> parse(List<EMMessage> messages){
        ArrayList<ConversationBean> list = null;
        if (messages != null) {
            list = new ArrayList<ConversationBean>();
            for (EMMessage message : messages) {
                ConversationBean bean = new ConversationBean();

                bean.sender = message.getFrom();

                EMMessage.Type type = message.getType();

                if (type.equals(EMMessage.Type.TXT)) {
                    EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                    bean.texBody = body.getMessage();
                } else if (type.equals(EMMessage.Type.IMAGE)) {
                    EMImageMessageBody body = (EMImageMessageBody) message.getBody();
                    bean.imageUrl = body.getRemoteUrl();
                } else if (type.equals(EMMessage.Type.VOICE)) {
                    EMVoiceMessageBody body = (EMVoiceMessageBody) message.getBody();
                    bean.voiceUrl = body.getRemoteUrl();
                }
                list.add(bean);
            }

        }
        return list;
    }
}
