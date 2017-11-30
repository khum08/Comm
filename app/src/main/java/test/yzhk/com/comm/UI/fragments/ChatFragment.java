package test.yzhk.com.comm.UI.fragments;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.Map;

import test.yzhk.com.comm.R;

/**
 * Created by 大傻春 on 2017/11/24.
 */

public class ChatFragment extends BaseFragment {

    public View mChatView;

    @Override
    public View initView() {
        mChatView = View.inflate(mContext, R.layout.fragment_chat, null);
        TextView tv_title = (TextView) mChatView.findViewById(R.id.tv_title);
        tv_title.setText(R.string.app_name);
        ImageView iv_add = (ImageView) mChatView.findViewById(R.id.iv_add);
        iv_add.setVisibility(View.VISIBLE);
        return mChatView;
    }

    @Override
    public void initData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
                if(conversations!=null){

                }

            }
        }.start();


        MyAdapter mAdapter = new MyAdapter();
        ListView lv_chat = (ListView) mChatView.findViewById(R.id.lv_chat);

        lv_chat.setAdapter(mAdapter);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return null;
        }
    }

}
