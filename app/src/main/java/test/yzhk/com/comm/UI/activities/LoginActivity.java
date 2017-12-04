package test.yzhk.com.comm.UI.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import test.yzhk.com.comm.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String mEmail;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button login = (Button) findViewById(R.id.bt_login);
        final Button register = (Button) findViewById(R.id.bt_register);
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tryRegister();
            }
        });
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(R.string.app_name);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    //登录
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString().trim();
        mPassword = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(mPassword) && !isPasswordValid(mPassword)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(mEmail)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            EMClient.getInstance().login(mEmail, mPassword, new EMCallBack() {
                /**
                 * 登陆成功的回调
                 */
                @Override
                public void onSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 加载所有会话到内存
                            EMClient.getInstance().chatManager().loadAllConversations();
                            // 加载所有群组到内存，如果使用了群组的话
                            EMClient.getInstance().groupManager().loadAllGroups();

                            // 登录成功跳转界面
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                /**
                 * 登陆错误的回调
                 * @param i
                 * @param s
                 */
                @Override
                public void onError(final int i, final String s) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("lzan13", "登录失败 Error code:" + i + ", message:" + s);
                            /**
                             * 关于错误码可以参考官方api详细说明
                             * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                             */
                            switch (i) {
                                // 网络异常 2
                                case EMError.NETWORK_ERROR:
                                    Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                    break;
                                // 无效的用户名 101
                                case EMError.INVALID_USER_NAME:
                                    Toast.makeText(LoginActivity.this, "无效的用户名", Toast.LENGTH_SHORT).show();
                                    break;
                                // 无效的密码 102
                                case EMError.INVALID_PASSWORD:
                                    Toast.makeText(LoginActivity.this, "无效的密码", Toast.LENGTH_SHORT).show();
                                    break;
                                // 用户认证失败，用户名或密码错误 202
                                case EMError.USER_AUTHENTICATION_FAILED:
                                    Toast.makeText(LoginActivity.this, "用户认证失败，用户名或密码错误", Toast.LENGTH_SHORT).show();
                                    break;
                                // 用户不存在 204
                                case EMError.USER_NOT_FOUND:
                                    Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                                    break;
                                // 无法访问到服务器 300
                                case EMError.SERVER_NOT_REACHABLE:
                                    Toast.makeText(LoginActivity.this, "无法访问到服务器", Toast.LENGTH_SHORT).show();
                                    break;
                                // 等待服务器响应超时 301
                                case EMError.SERVER_TIMEOUT:
                                    Toast.makeText(LoginActivity.this, "等待服务器响应超时", Toast.LENGTH_SHORT).show();
                                    break;
                                // 服务器繁忙 302
                                case EMError.SERVER_BUSY:
                                    Toast.makeText(LoginActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
                                    break;
                                // 未知 Server 异常 303 一般断网会出现这个错误
                                case EMError.SERVER_UNKNOWN_ERROR:
                                    Toast.makeText(LoginActivity.this, "未知的服务器异常", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                                    break;
                            }
                            showProgress(false);
                        }
                    });
                }

                @Override
                public void onProgress(int i, String s) {
                }
            });
        }


    }

    private boolean isEmailValid(String email) {
        return email.length()>2;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private ProgressDialog mDialog;
    //注册方法
    public void tryRegister(){

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("注册中，请稍后...");
        mDialog.show();
        mEmail = mEmailView.getText().toString().trim();
        mPassword = mPasswordView.getText().toString().trim();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(mEmail, mPassword);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!LoginActivity.this.isFinishing()) {
                                mDialog.dismiss();
                            }
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!LoginActivity.this.isFinishing()) {
                                mDialog.dismiss();
                            }
                            /**
                             * 关于错误码可以参考官方api详细说明
                             * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                             */
                            int errorCode = e.getErrorCode();
                            String message = e.getMessage();
                            Log.d("lzan13", String.format("sign up - errorCode:%d, errorMsg:%s", errorCode, e.getMessage()));
                            switch (errorCode) {
                                // 网络错误
                                case EMError.NETWORK_ERROR:
                                    Toast.makeText(LoginActivity.this, "网络错误 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                // 用户已存在
                                case EMError.USER_ALREADY_EXIST:
                                    Toast.makeText(LoginActivity.this, "用户已存在 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                // 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
                                case EMError.USER_ILLEGAL_ARGUMENT:
                                    Toast.makeText(LoginActivity.this, "参数不合法，一般情况是username使用了uuid导致，不能使用uuid注册 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                // 服务器未知错误
                                case EMError.SERVER_UNKNOWN_ERROR:
                                    Toast.makeText(LoginActivity.this, "服务器未知错误 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                case EMError.USER_REG_FAILED:
                                    Toast.makeText(LoginActivity.this, "账户注册失败 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this, "ml_sign_up_failed code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}

