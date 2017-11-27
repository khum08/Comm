package test.yzhk.com.comm.UI.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.pages.InitChat;
import test.yzhk.com.comm.UI.pages.InitContent;
import test.yzhk.com.comm.UI.pages.InitMap;
import test.yzhk.com.comm.UI.pages.InitSelf;

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
    private FrameLayout mContent;
    private Fragment[] mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mContent = (FrameLayout) findViewById(R.id.content);
        initFragments();

    }

    private void initFragments() {
        InitContent initChat = new InitChat(MainActivity.this);
        InitMap initMap = new InitMap(MainActivity.this);
        InitSelf initSelf = new InitSelf(MainActivity.this);
        mFragments = new Fragment[]{initChat, initMap, initSelf};

        lastShowFragment = 2;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, initSelf)
                .show(initChat).commit();

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
