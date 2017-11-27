package test.yzhk.com.comm.UI.pages;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import test.yzhk.com.comm.R;

/**
 * Created by 大傻春 on 2017/11/24.
 */

public class InitChat extends InitContent {

    private View mChatView;

    public InitChat(Context cxt) {
        super(cxt);
    }

    @Override
    public View initView() {
        mChatView = View.inflate(mContext, R.layout.content_chat, null);
        return mChatView;
    }

    @Override
    public void initData() {
        MyAdapter mAdapter = new MyAdapter();
        ListView lv_chat = (ListView) mChatView.findViewById(R.id.lv_chat);
        final EditText chatContent = (EditText) mChatView.findViewById(R.id.et_chatcontent);
        Button bt_send = (Button) mChatView.findViewById(R.id.bt_send);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = chatContent.getText().toString().trim();
                if (content != null) {

                }else{

                }
            }
        });


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
            ViewHold viewHold;
            if (convertView == null) {
                viewHold = new ViewHold();
                convertView = View.inflate(mContext, R.layout.list_item_chat, null);
                viewHold.ll_me = (LinearLayout) convertView.findViewById(R.id.ll_me);
                viewHold.ll_other = (LinearLayout) convertView.findViewById(R.id.ll_me);
                viewHold.tv_me = (TextView) convertView.findViewById(R.id.tv_me);
                viewHold.tv_other = (TextView) convertView.findViewById(R.id.tv_other);
                viewHold.iv_me = (ImageView) convertView.findViewById(R.id.iv_me);
                viewHold.iv_other = (ImageView) convertView.findViewById(R.id.iv_other);
                convertView.setTag(viewHold);

            } else {
                viewHold = (ViewHold) convertView.getTag();
            }

            return null;
        }
    }

    static class ViewHold {
        public LinearLayout ll_me;
        public LinearLayout ll_other;
        public TextView tv_me;
        public TextView tv_other;
        public ImageView iv_me;
        public ImageView iv_other;


    }
}
