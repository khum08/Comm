package test.yzhk.com.comm.UI.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.activities.BlackNumberActivity;
import test.yzhk.com.comm.UI.activities.GroupMakerActivity;
import test.yzhk.com.comm.UI.pagers.BasePager;
import test.yzhk.com.comm.UI.pagers.ContactsFragmentPager;
import test.yzhk.com.comm.UI.pagers.ContactsPager;
import test.yzhk.com.comm.UI.pagers.GroupFragmentPager;
import test.yzhk.com.comm.UI.pagers.GroupPager;
import test.yzhk.com.comm.utils.ToastUtil;
import test.yzhk.com.comm.utils.UIUtil;


/**
 * Created by 大傻春 on 2017/11/27.
 */

public class ContactsFragment extends BaseFragment {

    private static final String TAG_GROUP = "TAG_GROUP";
    private static final String TAG_CONTACTS = "TAG_CONTACTS";
    private View mContactsView;

    //adapter的数据
    //服务器端好友列表
    private TextView tv_title;

    private ImageView iv_add;

    private static final int ENTER_FRI_ACTIVITY = 916;
    private static final int GO_BLACKLISST = 240;
    private ViewPager mViewpager_contacts;
    private ArrayList<BasePager> mPagerList;
    private MyPagerAdapter mPagerAdapter;
    private TextView tv_another;
    private ContactsFragmentPager mContactsFragmentPager;
    private GroupFragmentPager mGroupFragmentPager;
    private int lastShowFragment;
    private FragmentManager mFm;
    private String[] tags = {TAG_CONTACTS, TAG_GROUP};
    private MyAdapter mAdapter;
    private ArrayList<BaseFragment> mFragmentPagerList;

    @Override
    public View initView() {

        mContactsView = View.inflate(mContext, R.layout.fragment_contact, null);
        //标题栏
        tv_title = (TextView) mContactsView.findViewById(R.id.tv_title);
        tv_title.setText("联系人");
        tv_another = (TextView) mContactsView.findViewById(R.id.tv_another);

        iv_add = (ImageView) mContactsView.findViewById(R.id.iv_add);
        iv_add.setImageResource(R.drawable.ic_more_detail);
        iv_add.setVisibility(View.VISIBLE);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
        mViewpager_contacts = (ViewPager) mContactsView.findViewById(R.id.viewpager_contacts);
        return mContactsView;
    }


    @Override
    public void initData() {
//       initData1();
        initData2();
    }

    private void initData2() {
        mFragmentPagerList = new ArrayList<>();
        mContactsFragmentPager = new ContactsFragmentPager();
        mGroupFragmentPager = new GroupFragmentPager();
        mFragmentPagerList.add(mContactsFragmentPager);
        mFragmentPagerList.add(mGroupFragmentPager);

        mAdapter = new MyAdapter(mContext.getSupportFragmentManager());
        mViewpager_contacts.setAdapter(mAdapter);
        mViewpager_contacts.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    if (positionOffset > 0.5) {
                        tv_another.setTextSize(UIUtil.dip2px(8, mContext));
                        tv_title.setTextSize(UIUtil.dip2px(4, mContext));
                    } else {
                        tv_another.setTextSize(UIUtil.dip2px(4, mContext));
                        tv_title.setTextSize(UIUtil.dip2px(8, mContext));
                    }
                }
                if (position == 1) {
                    if (positionOffset > 0.5) {
                        tv_title.setTextSize(UIUtil.dip2px(8, mContext));
                        tv_another.setTextSize(UIUtil.dip2px(4, mContext));
                    } else {
                        tv_title.setTextSize(UIUtil.dip2px(4, mContext));
                        tv_another.setTextSize(UIUtil.dip2px(8, mContext));
                    }
                }

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void initData1() {
        mPagerList = new ArrayList<>();
        mPagerList.add(new ContactsPager(mContext));
        mPagerList.add(new GroupPager(mContext));

        mPagerAdapter = new MyPagerAdapter();
        mViewpager_contacts.setAdapter(mPagerAdapter);

        mViewpager_contacts.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    if (positionOffset > 0.5) {
                        tv_another.setTextSize(UIUtil.dip2px(8, mContext));
                        tv_title.setTextSize(UIUtil.dip2px(4, mContext));
                    } else {
                        tv_another.setTextSize(UIUtil.dip2px(4, mContext));
                        tv_title.setTextSize(UIUtil.dip2px(8, mContext));
                    }
                }
                if (position == 1) {
                    if (positionOffset > 0.5) {
                        tv_title.setTextSize(UIUtil.dip2px(8, mContext));
                        tv_another.setTextSize(UIUtil.dip2px(4, mContext));
                    } else {
                        tv_title.setTextSize(UIUtil.dip2px(4, mContext));
                        tv_another.setTextSize(UIUtil.dip2px(8, mContext));
                    }
                }

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewpager_contacts.setCurrentItem(0, true);
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
                                ContactsFragmentPager baseFragment = (ContactsFragmentPager)mFragmentPagerList.get(0);
                                baseFragment.showSearchView();
                                break;
                            case R.id.tv_add_fri:
                                showAddDialog();
                                break;
                            case R.id.tv_make_group:
                                startActivity(new Intent(mContext, GroupMakerActivity.class));
                                break;
                            case R.id.tv_blacknum:
                                Intent intent = new Intent(mContext, BlackNumberActivity.class);
                                startActivityForResult(intent, GO_BLACKLISST);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENTER_FRI_ACTIVITY || requestCode ==GO_BLACKLISST) {
            BaseFragment baseFragment = mFragmentPagerList.get(0);
            Log.e("contactsfragments", "================pass here");
            if (baseFragment != null) {
                Log.e("baseFragment", "================is not null");
                baseFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mPagerList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //实例化条目
            BasePager basePager = mPagerList.get(position);
            View view = basePager.mRootView;
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            super.destroyItem(container, position, object);
        }
    }

//    //防止重复加载
//    @Override
//    public void onAttachFragment(Fragment fragment) {
//        if (mContactsFragmentPager == null && fragment instanceof ContactsFragmentPager)
//            mContactsFragmentPager = (BaseFragment) fragment;
//        if (mMapFragment == null && fragment instanceof ContactsFragment)
//            mMapFragment = (BaseFragment) fragment;
//
//    }

    class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.e("fragmentAdapter", "===========pass here===========");
            return mFragmentPagerList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentPagerList.size();
        }
    }

}

























