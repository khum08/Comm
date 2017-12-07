package test.yzhk.com.comm.UI.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.utils.ToastUtil;

public class GroupMakerActivity extends BaseActivity {

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
    private RadioGroup mRg_group;
    private ListView mLv_group;
    private GroupAdapter mGroupAdapter;
    //adapter的数据
    private List<String> mUsernames = new ArrayList<>();
    //服务器端好友列表
    private List<String> serverUserList;
    //本地数据中黑名单的列表
    private List<String> mBlacklist;
    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            tv_group_isloading.setVisibility(View.GONE);
            mLv_group.setVisibility(View.VISIBLE);

            mGroupAdapter = new GroupAdapter();
            mLv_group.setAdapter(mGroupAdapter);
        }
    };

    private void initView() {
        mTv_confirm_group = (TextView) findViewById(R.id.tv_confirm_group);
        mTv_group_name = (AutoCompleteTextView)findViewById(R.id.tv_group_name);
        mEt_desc_ = (AutoCompleteTextView)findViewById(R.id.et_desc_);
        mRg_group = (RadioGroup) findViewById(R.id.rg_group);
        mLv_group = (ListView) findViewById(R.id.lv_group);
        tv_group_isloading =(TextView)findViewById(R.id.tv_group_isloading);

        mTv_confirm_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //输入错误判断
                String name = mTv_group_name.getText().toString();
                String desc = mEt_desc_.getText().toString();
                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(desc)){
                    ToastUtil.showToast(GroupMakerActivity.this,"输入框不能为空哦");
                }

            }
        });
    }

    public void initData(){
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
                    if(mBlacklist!=null){
                        mUsernames.removeAll(mBlacklist);
                    }
                    mHandler.sendEmptyMessage(0);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

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
        public View getView(int position, View convertView, ViewGroup parent) {

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
            viewHolder.cb_item.setChecked(false);
            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView iv_contact_icon;
        public TextView tv_contact;
        public CheckBox cb_item;
    }

}
