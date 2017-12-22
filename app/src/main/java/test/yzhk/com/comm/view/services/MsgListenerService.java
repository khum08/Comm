package test.yzhk.com.comm.view.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import test.yzhk.com.comm.eventbus.MessageEvent;

public class MsgListenerService extends Service {
    private Object mNewMessages;

    public MsgListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override
    public void onDestroy() {
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        super.onDestroy();
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            getNewMessages(messages);
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }
        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    public void getNewMessages(List<EMMessage> messages) {
        EventBus.getDefault().post(new MessageEvent(messages));
    }
}
