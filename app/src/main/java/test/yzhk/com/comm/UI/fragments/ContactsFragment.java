package test.yzhk.com.comm.UI.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.activities.BlackNumberActivity;
import test.yzhk.com.comm.UI.activities.FriDetailActivity;
import test.yzhk.com.comm.UI.activities.MainActivity;
import test.yzhk.com.comm.UI.activities.SingleRoomActivity;
import test.yzhk.com.comm.utils.ToastUtil;

/**
 * Created by 大傻春 on 2017/11/27.
 */

public class ContactsFragment extends BaseFragment {


    private View mContactsView;
    private ListView mLv_contact;

    private List<String> mUsernames = new ArrayList<>();
    private List<String> serverUserList;
    private TextView tv_title;
    private TextView tv_isloading;
    private ImageView iv_add;
    private contactsAdapter mContactsAdapter;
    private static final int ENTER_FRI_ACTIVITY = 916;
    private static final int DELETE = 699;
    private static final int ADD_BLACK = 818;
    private static final int CREATE_CONVERSATION = 899;

    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            tv_isloading.setVisibility(View.GONE);
            mLv_contact.setVisibility(View.VISIBLE);

            mContactsAdapter = new contactsAdapter();
            mLv_contact.setAdapter(mContactsAdapter);
        }
    };
    private SearchView mSearchview;


    @Override
    public View initView() {

        mContactsView = View.inflate(mContext, R.layout.fragment_contact, null);

        mLv_contact = (ListView) mContactsView.findViewById(R.id.lv_contact);
        tv_title = (TextView) mContactsView.findViewById(R.id.tv_title);
        tv_title.setText("通讯录");


        tv_isloading = (TextView) mContactsView.findViewById(R.id.tv_isloading);
        mSearchview = (SearchView) mContactsView.findViewById(R.id.searchview);

        iv_add = (ImageView) mContactsView.findViewById(R.id.iv_add);
        iv_add.setImageResource(R.drawable.ic_more_detail);
        iv_add.setVisibility(View.VISIBLE);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });

        return mContactsView;
    }

    //显示底部bottomsheet
    private void showBottomSheet() {
        final BottomSheetLayout bottomSheetLayout = mContext.mRootView;

        MenuSheetView menuSheetView =
                new MenuSheetView(mContext, MenuSheetView.MenuType.LIST, "操作...", new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.tv_search:
                                showSearchView();
                                break;
                            case R.id.tv_add_fri:
                                showAddDialog();
                                break;
                            case R.id.tv_blacknum:
                                Intent intent = new Intent(mContext, BlackNumberActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.tv_nothing:
                                ToastUtil.showToast(mContext, "作者真的很帅");
                                break;
                        }
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.bottomsheet_contacts);
        bottomSheetLayout.showWithSheetView(menuSheetView);


    }

    //联系人搜索框
    private void showSearchView() {
        mSearchview.setVisibility(View.VISIBLE);
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        mSearchview.startAnimation(anim);
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
                    mContactsAdapter.notifyDataSetChanged();
                } else {
                    mUsernames.clear();
                    for (int i = 0; i < serverUserList.size(); i++) {
                        String s = serverUserList.get(i);
                        mUsernames.add(s);
                    }
                    mContactsAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

    }

    //添加好友对话框
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View dialogView = View.inflate(mContext, R.layout.view_addfri, null);
        builder.setView(dialogView);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText et_mail = (EditText) dialogView.findViewById(R.id.et_email);
                final String email = et_mail.getText().toString().trim();
                EditText et_desc = (EditText) dialogView.findViewById(R.id.et_desc);
                final String desc = et_desc.getText().toString().trim();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(desc)) {
                    if (email.length() > 2) {
                        if (!email.equals(EMClient.getInstance().getCurrentUser())) {
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        EMClient.getInstance().contactManager().addContact(email, desc);
                                        ToastUtil.showToast(mContext, "好友申请发送成功");

                                    } catch (HyphenateException e) {
                                        e.printStackTrace();
                                        ToastUtil.showToast(mContext, "好友申请发送失败");
                                    }
                                }
                            }.start();
                        } else {
                            ToastUtil.showToast(mContext, "不能添加自己为好友哦");
                        }
                    } else {
                        ToastUtil.showToast(mContext, "用户名必须大于三位哦");
                    }
                } else {
                    ToastUtil.showToast(mContext, "输入框不能为空哦");
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }


    @Override
    public void initData() {

        new Thread() {
            @Override
            public void run() {
                try {
                    serverUserList = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    if (serverUserList != null && serverUserList.size() == 0) {
                        ToastUtil.showToast(mContext, "暂时还没有好友哦");
                    }
                    mUsernames.addAll(serverUserList);
                    mHandler.sendEmptyMessage(0);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        mLv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friName = mContactsAdapter.getItem(position);
                Intent intent = new Intent(mContext, FriDetailActivity.class);
                intent.putExtra("friName", friName);
                startActivityForResult(intent, ENTER_FRI_ACTIVITY);
            }
        });

        //向上滑动时搜索框收起
        mLv_contact.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem>0){
                    mSearchview.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENTER_FRI_ACTIVITY) {
            switch (resultCode){
                case DELETE:
                case ADD_BLACK:
                    if (mContactsAdapter != null) {
                        String friName = data.getStringExtra("friName");
                        mUsernames.remove(friName);
                        mContactsAdapter.notifyDataSetChanged();
                    }
                    break;
                case CREATE_CONVERSATION:
                    //在chatfragment中创建会话
                    String friName = data.getStringExtra("friName");
                    MainActivity activity = (MainActivity)getActivity();
                    ChatFragment conversationFragment = activity.getConversationFragment();
                    conversationFragment.createConversation();
                    Intent intent = new Intent(mContext, SingleRoomActivity.class);
                    intent.putExtra("userName",friName);
                    startActivity(intent);
                    break;
            }

        }


    }

    class contactsAdapter extends BaseAdapter {

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
                convertView = View.inflate(mContext, R.layout.list_item_contact, null);

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

}

























