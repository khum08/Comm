package test.yzhk.com.comm.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.utils.ToastUtil;

public class InviteFriendActivity extends BaseActivity {

    private TextView tv_count;
    private List<String> serverUserList;
    private List<String> mBlacklist;
    private List<String> mUsernames = new ArrayList<>();
    private List<String> selectedList = new ArrayList<>();
    private static final int GET_DATA = 996;
    private GroupAdapter mGroupAdapter;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case GET_DATA:
                    tv_count.setText("(" + selectedList.size() + "/" + mUsernames.size() + ")");
                    mGroupAdapter = new GroupAdapter();
                    mLv_invite.setAdapter(mGroupAdapter);
                    break;

            }
        }
    };
    private ListView mLv_invite;
    private Button mBtn_selectall;
    private Button btn_selectother;
    private String mGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        Intent intent = getIntent();
        mGroupId = intent.getStringExtra("groupId");

        initToolbar();
        initListView();
        initCount();
    }

    private void initCount() {
        tv_count = (TextView) findViewById(R.id.tv_count);
        mBtn_selectall = (Button) findViewById(R.id.btn_selectall);
        btn_selectother = (Button) findViewById(R.id.btn_selectother);
        mBtn_selectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击了全选
                selectedList.clear();
                selectedList.addAll(mUsernames);
                mGroupAdapter.notifyDataSetChanged();
            }
        });
        btn_selectother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> temp = new LinkedList<String>();
                temp.addAll(mUsernames);
                Iterator<String> iterator = temp.iterator();
                while(iterator.hasNext()){
                    String next = iterator.next();
                    if(selectedList.contains(next)){
                        iterator.remove();
                    }
                }
                selectedList.clear();
                selectedList.addAll(temp);
                mGroupAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initListView() {
        mLv_invite = (ListView) findViewById(R.id.lv_invite);
        new Thread() {

            @Override
            public void run() {
                try {
                    serverUserList = EMClient.getInstance().contactManager().getAllContactsFromServer();

                    if (serverUserList != null && serverUserList.size() == 0) {
                        ToastUtil.showToast(InviteFriendActivity.this, "暂时没有好友");
                    }
                    mUsernames.addAll(serverUserList);
                    mBlacklist = EMClient.getInstance().contactManager().getBlackListUsernames();
                    if (mBlacklist != null) {
                        mUsernames.removeAll(mBlacklist);
                    }
                    mHandler.sendEmptyMessage(GET_DATA);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void initToolbar() {
        Toolbar toolbar_invite = (Toolbar) findViewById(R.id.toolbar_invite);
        setSupportActionBar(toolbar_invite);
        toolbar_invite.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() ==R.id.menu_item1){
                    //点击了确定，发送好友邀请
                    final String[] strings = selectedList.toArray(new String[selectedList.size()]);
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().addUsersToGroup(mGroupId, strings);
                                ToastUtil.showToast(InviteFriendActivity.this,"好友邀请发送成功");
                                finish();
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                ToastUtil.showToast(InviteFriendActivity.this,"好友邀请发送失败");
                            }
                        }
                    }.start();

                }
                return true;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.invite_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //联系人listview的适配器
    class GroupAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mUsernames.size();
        }

        @Override
        public String getItem(int position) {
            return mUsernames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(InviteFriendActivity.this, R.layout.list_item_contact, null);

//                viewHolder.iv_contact_icon = (ImageView) convertView.findViewById(R.id.iv_contact_icon);
                viewHolder.tv_contact = (TextView) convertView.findViewById(R.id.tv_contact_name);
                viewHolder.cb_item = (CheckBox) convertView.findViewById(R.id.cb_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
//            viewHolder.iv_contact_icon.setImageResource(R.mipmap.ic_launcher);
            viewHolder.tv_contact.setText(getItem(position));
            viewHolder.cb_item.setVisibility(View.VISIBLE);
            //监听条目的选择
            viewHolder.cb_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (!selectedList.contains(getItem(position))) {
                            selectedList.add(getItem(position));
                        }
                    } else {
                        selectedList.remove(getItem(position));
                    }
                    tv_count.setText("(" + selectedList.size() + "/" + mUsernames.size() + ")");
                }
            });
            //判断是否被选
            if (selectedList.contains(getItem(position))) {
                viewHolder.cb_item.setChecked(true);
            } else {
                viewHolder.cb_item.setChecked(false);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView iv_contact_icon;
        public TextView tv_contact;
        public CheckBox cb_item;
    }
}
