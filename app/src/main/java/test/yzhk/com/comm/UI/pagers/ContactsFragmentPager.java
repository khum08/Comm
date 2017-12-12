package test.yzhk.com.comm.UI.pagers;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.activities.FriDetailActivity;
import test.yzhk.com.comm.UI.activities.MainActivity;
import test.yzhk.com.comm.UI.activities.SingleRoomActivity;
import test.yzhk.com.comm.UI.fragments.BaseFragment;
import test.yzhk.com.comm.UI.fragments.ChatFragment;
import test.yzhk.com.comm.utils.ToastUtil;

import static android.view.View.inflate;

/**
 * Created by 大傻春 on 2017/12/10.
 */

public class ContactsFragmentPager extends BaseFragment {

    private List<String> mUsernames = new ArrayList<>();
    //服务器端好友列表
    private List<String> serverUserList;
    //本地数据中黑名单的列表
    private List<String> mBlacklist;
    private static final int GET_DATA_CONTACTS = 825;
    private static final int DATA_CHANGE = 894;

    private static final int ENTER_FRI_ACTIVITY = 916;
    private static final int DELETE = 699;
    private static final int ADD_BLACK = 818;
    private static final int CREATE_CONVERSATION = 899;
    private static final int GO_BLACKLISST = 240;
    public ContactsAdapter mContactsAdapter;
    private SearchView mSearchview;
    private ListView lv_group_contacts;
    private TextView tv_isloading_group;
    private TextView tv_fri_count;

    @Override
    public View initView() {
        View contactsPager = inflate(mContext, R.layout.pager_contacts, null);

        lv_group_contacts = (ListView) contactsPager.findViewById(R.id.lv_group_contacts);
        tv_isloading_group = (TextView) contactsPager.findViewById(R.id.tv_isloading_group);
        mSearchview = (SearchView) contactsPager.findViewById(R.id.sv_contacts);
        tv_fri_count = (TextView) contactsPager.findViewById(R.id.tv_fri_count);

        return contactsPager;
    }

    private boolean isAnim;
    private boolean hasSearchView;
    @Override
    protected void initData() {
        initSearchView();

        lv_group_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friName = mContactsAdapter.getItem(position);
                Intent intent = new Intent(mContext, FriDetailActivity.class);
                intent.putExtra("friName", friName);
                FragmentManager fm = mContext.getSupportFragmentManager();
                List<Fragment> fragments = fm.getFragments();
                fragments.get(1).startActivityForResult(intent, ENTER_FRI_ACTIVITY);
            }
        });

        //向上滑动时搜索框收起
        lv_group_contacts.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState ==SCROLL_STATE_FLING && hasSearchView && !isAnim){
                    isAnim =true;
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mSearchview,"translationY",0f,-150f);
                    objectAnimator.setDuration(300);
                    objectAnimator.start();
                    isAnim = false;
                    hasSearchView = false;
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        new Thread() {

            @Override
            public void run() {
                try {
                    serverUserList = EMClient.getInstance().contactManager().getAllContactsFromServer();

                    if (serverUserList != null && serverUserList.size() == 0) {
                        ToastUtil.showToast(mContext, "暂时没有好友哦");
                    }
                    mUsernames.addAll(serverUserList);
                    mBlacklist = EMClient.getInstance().contactManager().getBlackListUsernames();
                    if (mBlacklist != null) {
                        mUsernames.removeAll(mBlacklist);
                    }
                    mHandler.sendEmptyMessage(GET_DATA_CONTACTS);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA_CONTACTS:
                    tv_isloading_group.setVisibility(View.GONE);
                    lv_group_contacts.setVisibility(View.VISIBLE);
                    tv_fri_count.setText("联系人("+mUsernames.size()+"人)");
                    mContactsAdapter = new ContactsAdapter();
                    lv_group_contacts.setAdapter(mContactsAdapter);
                    break;
                case DATA_CHANGE:
                    tv_fri_count.setText("联系人("+mUsernames.size()+"人)");
                    mContactsAdapter.notifyDataSetChanged();
                    break;
            }


        }
    };

    public void initSearchView() {
        mSearchview.setVisibility(View.GONE);
        mSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!TextUtils.isEmpty(newText)) {
                    mUsernames.clear();
                    for (int i = 0; i < serverUserList.size(); i++) {
                        String s = serverUserList.get(i);
                        if (s.contains(newText + "")) {
                            mUsernames.add(s);
                        }
                    }
                    mHandler.sendEmptyMessage(DATA_CHANGE);

                } else {
                    mUsernames.clear();
                    for (int i = 0; i < serverUserList.size(); i++) {
                        String s = serverUserList.get(i);
                        mUsernames.add(s);
                    }
                    mHandler.sendEmptyMessage(DATA_CHANGE);
                }
                return true;
            }
        });

    }
    class ContactsAdapter extends BaseAdapter {

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
                convertView = inflate(mContext, R.layout.list_item_contact, null);

//                viewHolder.iv_contact_icon = (ImageView) convertView.findViewById(R.id.iv_contact_icon);
                viewHolder.tv_contact = (TextView) convertView.findViewById(R.id.tv_contact_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
//            viewHolder.iv_contact_icon.setImageResource(R.mipmap.ic_launcher);
            viewHolder.tv_contact.setText(getItem(position));

            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView iv_contact_icon;
        public TextView tv_contact;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENTER_FRI_ACTIVITY) {
            switch (resultCode) {
                case DELETE:
                case ADD_BLACK:
                    Log.e("hhhhhhhhh", "走了ContactsFragmentPager");
                    String friName = data.getStringExtra("friName");
                    mUsernames.remove(friName);
                    mContactsAdapter.notifyDataSetChanged();
                    break;
                case CREATE_CONVERSATION:
                    //在chatfragment中创建会话
                    Log.e("hhhhhhhhh", "走了ContactsFragmentPager");
                    String friName1 = data.getStringExtra("friName");
                    MainActivity activity = (MainActivity) getActivity();
                    ChatFragment conversationFragment = activity.getConversationFragment();
                    conversationFragment.createConversation();
                    Intent intent = new Intent(mContext, SingleRoomActivity.class);
                    intent.putExtra("userName", friName1);
                    startActivity(intent);
                    break;
            }
        }else if(requestCode == GO_BLACKLISST){
            new Thread() {

                @Override
                public void run() {
                    try {
                        serverUserList = EMClient.getInstance().contactManager().getAllContactsFromServer();

                        if (serverUserList != null && serverUserList.size() == 0) {
                            ToastUtil.showToast(mContext, "暂时没有好友哦");
                        }
                        mUsernames.clear();
                        mUsernames.addAll(serverUserList);
                        mBlacklist = EMClient.getInstance().contactManager().getBlackListUsernames();
                        if (mBlacklist != null) {
                            mUsernames.removeAll(mBlacklist);
                        }
                        mHandler.sendEmptyMessage(DATA_CHANGE);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
    public void showSearchView(){
        if(!hasSearchView){
            mSearchview.setVisibility(View.VISIBLE);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(mSearchview, "translationY", -150f, 0f);
            translationY.setDuration(300);
            translationY.start();
            hasSearchView = true;
        }

    }



}
