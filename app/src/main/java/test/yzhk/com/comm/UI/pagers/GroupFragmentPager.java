package test.yzhk.com.comm.UI.pagers;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.activities.SingleRoomActivity;
import test.yzhk.com.comm.UI.fragments.BaseFragment;

import static android.view.View.inflate;

/**
 * Created by 大傻春 on 2017/12/10.
 */

public class GroupFragmentPager extends BaseFragment {

    private List<EMGroup> mGrouplist;
    private View mGroupPager;
    private TextView tv_isloading_group;
    private ListView lv_group_contacts;
    private static final int GET_GROUP_DATA = 413;
    private GroupAdapter mGroupAdapter;
    private RelativeLayout rl_searchview;

    private GroupFragmentPager() {
        super();
    }
    private static GroupFragmentPager instance;
    public static GroupFragmentPager getInstance(){
        if(instance == null)
            instance = new GroupFragmentPager();
        return instance;
    }

    @Override
    public View initView() {
        mGroupPager = inflate(mContext, R.layout.pager_contacts,null);
        //初始化群聊的控件
        tv_isloading_group = (TextView) mGroupPager.findViewById(R.id.tv_isloading_group);
        lv_group_contacts = (ListView) mGroupPager.findViewById(R.id.lv_group_contacts);
        rl_searchview = (RelativeLayout) mGroupPager.findViewById(R.id.rl_searchview);
        rl_searchview.setVisibility(View.GONE);
        return mGroupPager;
    }

    @Override
    protected void initData() {
        new Thread(){
            @Override
            public void run() {
                try {
                    mGrouplist = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    mHandler.sendEmptyMessage(GET_GROUP_DATA);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        lv_group_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMGroup emGroup = mGrouplist.get(position);
                String groupId = emGroup.getGroupId();
                Intent intent = new Intent(mContext, SingleRoomActivity.class);
                intent.putExtra("groupId",groupId);
                mContext.startActivity(intent);
            }
        });
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_GROUP_DATA:
                    tv_isloading_group.setVisibility(View.GONE);
                    lv_group_contacts.setVisibility(View.VISIBLE);
                    mGroupAdapter = new GroupAdapter();
                    lv_group_contacts.setAdapter(mGroupAdapter);
                    break;
            }
        }
    };

    class GroupAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if(mGrouplist!=null)
                return mGrouplist.size();
            return 0;
        }

        @Override
        public EMGroup getItem(int position) {
            if(mGrouplist!=null)
                return mGrouplist.get(position);
            return null;
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
                convertView = inflate(mContext, R.layout.list_item_contact, null);

                viewHolder.iv_contact_icon = (ImageView) convertView.findViewById(R.id.iv_contact_icon);
                viewHolder.tv_contact = (TextView) convertView.findViewById(R.id.tv_contact_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.iv_contact_icon.setImageResource(R.drawable.ic_people_name_24dp);
            EMGroup item = getItem(position);
            viewHolder.tv_contact.setText(item.getGroupName()+"("+ item.getMemberCount()+"/"+ item.getMaxUserCount()+"人)");
            return convertView;
        }
    }
    static class ViewHolder {
        public ImageView iv_contact_icon;
        public TextView tv_contact;
    }

}
