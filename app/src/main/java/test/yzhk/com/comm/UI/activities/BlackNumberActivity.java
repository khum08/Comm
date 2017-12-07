package test.yzhk.com.comm.UI.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.utils.ToastUtil;

public class BlackNumberActivity extends BaseActivity {

    private ListView mLv_blacklist;
    private List<String> mBlackListUsernames;
    private BlackNumberAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);

        initListView();
    }

    private void initListView() {
        mLv_blacklist = (ListView)findViewById(R.id.lv_blacklist);
        TextView tv_isloading_black = (TextView) findViewById(R.id.tv_isloading_black);
        mBlackListUsernames = EMClient.getInstance().contactManager().getBlackListUsernames();
        if(mBlackListUsernames.size()<1){
            tv_isloading_black.setText("黑名单为空...");
        }else{
            tv_isloading_black.setVisibility(View.GONE);
            mLv_blacklist.setVisibility(View.VISIBLE);
        }
        mAdapter = new BlackNumberAdapter();
        mLv_blacklist.setAdapter(mAdapter);
        mLv_blacklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                notBlack(position);
            }
        });

    }

    public void notBlack(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("从黑名单中移除")
                .setMessage("移除后能互相收发消息\n请确认")
                .setNegativeButton("取消",null)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            EMClient.getInstance().contactManager().removeUserFromBlackList(mBlackListUsernames.get(position));
                            ToastUtil.showToast(BlackNumberActivity.this,"从黑名单中移除成功");
                            mBlackListUsernames.remove(position);
                            mAdapter.notifyDataSetChanged();

                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            ToastUtil.showToast(BlackNumberActivity.this,"从黑名单中移除失败");
                        }
                    }
                });
          builder.show();
    }

    public class BlackNumberAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(mBlackListUsernames==null){
              return 0;
            }
            return mBlackListUsernames.size();
        }

        @Override
        public String getItem(int position) {
            if(mBlackListUsernames==null)
                return null;
            return mBlackListUsernames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder viewHolder;
            if(convertView==null){
                viewHolder = new MyViewHolder();
                convertView = View.inflate(BlackNumberActivity.this, R.layout.list_item_contact, null);
//                viewHolder.iv_contact_icon = (ImageView) convertView.findViewById(R.id.iv_contact_icon);
                viewHolder.tv_contact = (TextView) convertView.findViewById(R.id.tv_contact_name);
            }else{
                viewHolder = (MyViewHolder) convertView.getTag();
            }

            viewHolder.tv_contact.setText(getItem(position));

            return convertView;
        }
    }

    static class MyViewHolder {
        public ImageView iv_contact_icon;
        public TextView tv_contact;
    }
}





















