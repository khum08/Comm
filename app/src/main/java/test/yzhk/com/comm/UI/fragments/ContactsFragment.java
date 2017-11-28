package test.yzhk.com.comm.UI.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.utils.Toastutil;

/**
 * Created by 大傻春 on 2017/11/27.
 */

public class ContactsFragment extends BaseFragment {


    private View mContactsView;
    private ListView mLv_contact;

    private List<String> mUsernames;
    private TextView tv_title;
    private ImageView iv_add;
    private contactsAdapter mContactsAdapter;

    public Handler mHandler = new Handler() {



        @Override
        public void handleMessage(Message msg) {
            mContactsAdapter = new contactsAdapter();
            mLv_contact.setAdapter(mContactsAdapter);
        }
    };


    @Override
    public View initView() {

        mContactsView = View.inflate(mContext, R.layout.fragment_contact, null);
        mLv_contact = (ListView) mContactsView.findViewById(R.id.lv_contact);
        tv_title = (TextView) mContactsView.findViewById(R.id.tv_title);
        tv_title.setText("通讯录");
        iv_add = (ImageView) mContactsView.findViewById(R.id.iv_add);
        iv_add.setVisibility(View.VISIBLE);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });


        return mContactsView;
    }

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
                String desc = et_desc.getText().toString().trim();
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(desc)){
                    if(email.contains("@")){
                        EMClient.getInstance().contactManager().aysncAddContact(email, desc, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                Toastutil.showToast((Activity)mContext,"添加成功");
                                mUsernames.add(0,email);
                                mContactsAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onError(int i, String s) {
                                Toastutil.showToast((Activity)mContext,"添加失败,请重试");
                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
                    }else{
                        Toastutil.showToast((Activity)mContext,"请输入正确的邮箱地址");
                    }

                }else{
                    Toastutil.showToast((Activity) mContext,"输入框不能为空哦");
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
                    mUsernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    mHandler.sendEmptyMessage(0);
                    if (mUsernames != null && mUsernames.size() == 0) {
                        Toastutil.showToast((Activity) mContext, "暂时还没有好友哦");
                    }

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();


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

                viewHolder.iv_contact_icon = (ImageView) convertView.findViewById(R.id.iv_contact_icon);
                viewHolder.tv_contact = (TextView) convertView.findViewById(R.id.tv_contact_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.iv_contact_icon.setImageResource(R.mipmap.ic_launcher);
            viewHolder.tv_contact.setText(getItem(position));

            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView iv_contact_icon;
        public TextView tv_contact;
    }

}

























