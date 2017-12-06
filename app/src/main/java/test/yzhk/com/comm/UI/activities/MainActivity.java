package test.yzhk.com.comm.UI.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.fragments.BaseFragment;
import test.yzhk.com.comm.UI.fragments.ChatFragment;
import test.yzhk.com.comm.UI.fragments.ContactsFragment;
import test.yzhk.com.comm.UI.fragments.SelfFragment;


public class MainActivity extends AppCompatActivity {


    private int lastShowFragment = 0;
    private BottomNavigationView mNavigation;
    private FragmentManager mFm;
    private Fragment[] mFragments;
    private BaseFragment mChatFragment;
    private BaseFragment mMapFragment;
    private BaseFragment mSelfFragment;
    public BottomSheetLayout mRootView;
    private static final String TAG_CONVERSATION = "TAG_CONVERSATION";
    private static final String TAG_CONTACTS = "TAG_CONTACTS";
    private static final String TAG_SETTING = "TAG_SETTING";
    private String[] tags= {TAG_CONVERSATION,TAG_CONTACTS,TAG_SETTING};

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_chat:
                    if (lastShowFragment != 0) {
                        switchFragment(lastShowFragment, 0);
                        lastShowFragment = 0;
                    }
                    return true;
                case R.id.navigation_map:
                    if (lastShowFragment != 1) {
                        switchFragment(lastShowFragment, 1);
                        lastShowFragment = 1;
                    }
                    return true;
                case R.id.navigation_self:
                    if (lastShowFragment != 2) {
                        switchFragment(lastShowFragment, 2);
                        lastShowFragment = 2;
                    }
                    return true;
            }
            return false;
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//         判断sdk是否登录成功过，并没有退出和被踢，否则跳转到登陆界面
        if (!EMClient.getInstance().isLoggedInBefore()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        mRootView = (BottomSheetLayout) findViewById(R.id.container);

        initView();
        checkConnect();
//        addFri();
        initFragments();

    }

    //设置了自动同意好友申请就不会回调该方法
    private void addFri() {
        EMClient.getInstance().contactManager().setContactListener(new ContactListiener());

    }

    //好友申请监听器
    public class ContactListiener implements EMContactListener {

        @Override
        public void onContactAdded(String s) {
        }
        @Override
        public void onContactDeleted(String s) {
        }
        @Override
        public void onContactInvited(String s, String s1) {
            showContactInvited(s, s1);
        }
        @Override
        public void onFriendRequestAccepted(String s) {
        }
        @Override
        public void onFriendRequestDeclined(String s) {
        }
    }

    private void showContactInvited(final String s, String s1) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("好友申请")
                .setMessage("对方号码：" + s + "\n附  言：" + s1)
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EMClient.getInstance().contactManager().asyncDeclineInvitation(s, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError(int i, String s) {
                            }
                            @Override
                            public void onProgress(int i, String s) {
                            }

                        });
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EMClient.getInstance().contactManager().asyncAcceptInvitation(s, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(int i, String s) {
                            }
                            @Override
                            public void onProgress(int i, String s) {
                            }

                        });
                        dialog.dismiss();
                    }
                });
        builder.show();

    }

    //监听连接情况
    private void checkConnect() {
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
    }

    //监听器
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                    } else {
                        if (NetUtils.hasNetwork(MainActivity.this)) {
                            //连接不到聊天服务器
                        } else {
                        }
                        //当前网络不可用，请检查网络设置
                    }
                }
            });
        }
    }


    //初始化底部选择框
    private void initView() {
        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    //初始化fragment
    private void initFragments() {
        mChatFragment = new ChatFragment();
        mMapFragment = new ContactsFragment();
        mSelfFragment = new SelfFragment();

        mFragments = new Fragment[]{mChatFragment, mMapFragment, mSelfFragment};

        lastShowFragment = 0;
        switchFragment(lastShowFragment,0);
    }

    //fragment间的切换
    public void switchFragment(int lastIndex, int Index) {
        mFm = getSupportFragmentManager();
        FragmentTransaction transaction = mFm.beginTransaction();
        transaction.hide(mFragments[lastIndex]);

        if (!mFragments[Index].isAdded()) {
            transaction.add(R.id.content, mFragments[Index],tags[Index]);
        }
        transaction.show(mFragments[Index]).commitAllowingStateLoss();
    }

    //防止重复加载
    @Override
    public void onAttachFragment(Fragment fragment) {
        if (mChatFragment == null && fragment instanceof ChatFragment)
            mChatFragment = (BaseFragment) fragment;
        if (mMapFragment == null && fragment instanceof ContactsFragment)
            mMapFragment = (BaseFragment) fragment;
        if (mSelfFragment == null && fragment instanceof SelfFragment)
            mSelfFragment = (BaseFragment) fragment;
    }
//    @Override
//    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//    }

    public ChatFragment getConversationFragment(){
        ChatFragment conversationFragment = (ChatFragment) mFm.findFragmentByTag(TAG_CONVERSATION);
        return conversationFragment;

    }
}
