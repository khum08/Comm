package test.yzhk.com.comm.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.utils.PrefUtil;
import test.yzhk.com.comm.utils.ToastUtil;

public class NickSigningActivity extends BaseActivity {

    private EditText mNickName;
    private Button mButton;
    private static final int NICKNAME_SETTING = 967;
    private static final int SIGNNING_SETTING = 827;
    private int requestcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_signing);
        Intent intent = getIntent();
        requestcode = intent.getIntExtra("requestcode",0);
        initTitle();

        initView();
    }

    private void initTitle() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        if(requestcode==NICKNAME_SETTING){
            tv_title.setText("修改昵称");
        }else if(requestcode == SIGNNING_SETTING){
            tv_title.setText("修改签名");
        }
    }

    private void initView() {
        mNickName = (EditText) findViewById(R.id.et_content_nick);
        //弹出软键盘
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputManager = (InputMethodManager) mNickName.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mNickName, 0);
            }

        }, 200);


        mButton = (Button) findViewById(R.id.tv_confirm_nick);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mNickName.getText().toString().trim();
                if(TextUtils.isEmpty(text)){
                    ToastUtil.showToast(NickSigningActivity.this,"输入框不能为空哦");
                }else{
                    Intent intent = new Intent(NickSigningActivity.this, MainActivity.class);
                    intent.putExtra("result",text);
                    setResult(NickSigningActivity.RESULT_OK,intent);
                    finish();
                    if(requestcode==NICKNAME_SETTING){
                        PrefUtil.putString(NickSigningActivity.this,"nickname","小圆");
                    }else if(requestcode == SIGNNING_SETTING){
                        PrefUtil.putString(NickSigningActivity.this,"signning","她在丛中笑");
                    }
                }
            }
        });
    }
}
