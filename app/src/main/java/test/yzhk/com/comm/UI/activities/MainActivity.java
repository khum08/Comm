package test.yzhk.com.comm.UI.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.fragments.BaseFragment;
import test.yzhk.com.comm.UI.fragments.ChatFragment;
import test.yzhk.com.comm.UI.fragments.MapFragment;
import test.yzhk.com.comm.UI.fragments.SelfFragment;

public class MainActivity extends AppCompatActivity {


    private int lastShowFragment = 0;
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
    private BottomNavigationView mNavigation;
    private Fragment[] mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断sdk是否登录成功过，并没有退出和被踢，否则跳转到登陆界面
//        if (!EMClient.getInstance().isLoggedInBefore()) {
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }


        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        initFragments();

    }

    private void initFragments() {
        BaseFragment chatFragment = new ChatFragment();
        BaseFragment mapFragment = new MapFragment();
        BaseFragment selfFragment = new SelfFragment();

        mFragments = new Fragment[]{chatFragment, mapFragment, selfFragment};

        lastShowFragment = 2;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, selfFragment)
                .show(selfFragment).commit();

    }

    public void switchFragment(int lastIndex, int Index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(mFragments[lastIndex]);
        if (!mFragments[Index].isAdded()) {
            transaction.add(R.id.content, mFragments[Index]);
        }
        transaction.show(mFragments[Index]).commitAllowingStateLoss();
    }



}
