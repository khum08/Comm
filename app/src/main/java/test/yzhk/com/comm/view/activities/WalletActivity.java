package test.yzhk.com.comm.view.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import test.yzhk.com.comm.R;

public class WalletActivity extends AppCompatActivity {

    private Toolbar mToolbar_wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        initToolbar();
    }

    private void initToolbar() {
        mToolbar_wallet = (Toolbar) findViewById(R.id.toolbar_wallet);
        setSupportActionBar(mToolbar_wallet);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
