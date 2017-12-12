package test.yzhk.com.comm.UI.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.utils.ToastUtil;

public class GroupMakerActivity extends BaseActivity {

    private String mGroupid;
    private String mDesc;
    private EMGroupOptions mOption;
    private EMGroup mGroup;
    private Toolbar toolbar_group_maker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_maker);

        initView();
        initData();
    }

    private TextView mTv_confirm_group;
    private TextView tv_group_isloading;
    private AutoCompleteTextView mTv_group_name;
    private AutoCompleteTextView mEt_desc_;
    private Spinner spinner;
    private TextView tv_selected;
    private TextView tv_selectall;
    private ListView mLv_group;
    private GroupAdapter mGroupAdapter;
    //adapter的数据
    private List<String> mUsernames = new ArrayList<>();
    private List<String> selectedList = new ArrayList<String>();
    //服务器端好友列表
    private List<String> serverUserList;
    //本地数据中黑名单的列表
    private List<String> mBlacklist;
    private int type;
    private static final int GET_DATA = 208;
    private static final int COMPLETE = 885;
    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case GET_DATA:
                    tv_group_isloading.setVisibility(View.GONE);
                    mLv_group.setVisibility(View.VISIBLE);
                    tv_selected.setText("(" + selectedList.size() + "/" + mUsernames.size() + ")");
                    mGroupAdapter = new GroupAdapter();
                    mLv_group.setAdapter(mGroupAdapter);
                    break;
                case COMPLETE:
                    Intent intent = new Intent(GroupMakerActivity.this, SingleRoomActivity.class);
                    intent.putExtra("groupId",mGroup.getGroupId());
                    startActivity(intent);
                    finish();
                    break;
            }

        }
    };

    private void initView() {
        mTv_confirm_group = (TextView) findViewById(R.id.tv_confirm_group);
        mTv_group_name = (AutoCompleteTextView) findViewById(R.id.tv_group_name);
        mEt_desc_ = (AutoCompleteTextView) findViewById(R.id.et_desc_);
        spinner = (Spinner) findViewById(R.id.spinner);
        tv_selected = (TextView) findViewById(R.id.tv_selected);
        tv_selected.setText("(" + selectedList.size() + "/" + mUsernames.size() + ")");
        tv_selectall = (TextView) findViewById(R.id.tv_selectall);
        //初始化toolbar
        toolbar_group_maker = (Toolbar)findViewById(R.id.toolbar_group_maker);
        setSupportActionBar(toolbar_group_maker);
        toolbar_group_maker.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_selectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击了全选
                if (selectedList.isEmpty()) {
                    selectedList.addAll(mUsernames);
                } else {
                    selectedList.clear();
                }
                mGroupAdapter.notifyDataSetChanged();
            }
        });
        mLv_group = (ListView) findViewById(R.id.lv_group);
        tv_group_isloading = (TextView) findViewById(R.id.tv_group_isloading);
        //spinner 的选择监听
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mTv_confirm_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //输入错误判断
                mGroupid = mTv_group_name.getText().toString();
                mDesc = mEt_desc_.getText().toString();
                if (TextUtils.isEmpty(mGroupid) || TextUtils.isEmpty(mDesc)) {
                    ToastUtil.showToast(GroupMakerActivity.this, "输入框不能为空哦");
                } else {
                    mOption = new EMGroupOptions();
                    mOption.maxUsers = 200;
                    switch (type) {
                        case 0:
                            mOption.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                            break;
                        case 1:
                            mOption.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                            break;
                        case 2:
                            mOption.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                            break;
                        case 3:
                            mOption.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                            break;
                    }
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                String[] stringlist = selectedList.toArray(new String[selectedList.size()]);
                                mGroup = EMClient.getInstance().groupManager().createGroup(mGroupid, mDesc, stringlist, "欢迎加入到" + mGroupid + "群聊", mOption);
                                ToastUtil.showToast(GroupMakerActivity.this,"群组创建成功");
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                ToastUtil.showToast(GroupMakerActivity.this,"群组创建失败");
                            }

                        }
                    }.start();
                    mHandler.sendEmptyMessage(COMPLETE);
                }

            }
        });
    }

    public void initData() {
        new Thread() {

            @Override
            public void run() {
                try {
                    serverUserList = EMClient.getInstance().contactManager().getAllContactsFromServer();

                    if (serverUserList != null && serverUserList.size() == 0) {
                        ToastUtil.showToast(GroupMakerActivity.this, "暂时没有好友");
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
                convertView = View.inflate(GroupMakerActivity.this, R.layout.list_item_contact, null);

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
                    tv_selected.setText("(" + selectedList.size() + "/" + mUsernames.size() + ")");
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
