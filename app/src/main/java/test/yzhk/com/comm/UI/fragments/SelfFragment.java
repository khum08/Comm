package test.yzhk.com.comm.UI.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.UI.activities.LoginActivity;
import test.yzhk.com.comm.UI.activities.NickSigningActivity;
import test.yzhk.com.comm.UI.activities.SettingActivity;
import test.yzhk.com.comm.UI.view.CircleImageView;
import test.yzhk.com.comm.UI.view.SettingItemView;
import test.yzhk.com.comm.utils.PrefUtil;
import test.yzhk.com.comm.utils.ToastUtil;

import static android.app.Activity.RESULT_OK;
import static test.yzhk.com.comm.R.id.item_nickname;

/**
 * Created by 大傻春 on 2017/11/24.
 */

public class SelfFragment extends BaseFragment {


    private View selfPage;
    private CircleImageView mCi_headerview;
    protected static Uri tempUri;
    private static final int UPLOADHEADERIMAGE = 772;
    private static final int CROP_SMALL_PICTURE = 833;
    private static final int NICKNAME_SETTING = 967;
    private static final int SIGNNING_SETTING = 827;
    private SettingItemView mItem_nickname;
    private SettingItemView mItem_signning;

    @Override
    public View initView() {
        selfPage = View.inflate(mContext, R.layout.fragment_self, null);
        TextView tv_title = (TextView) selfPage.findViewById(R.id.tv_title);
        mCi_headerview = (CircleImageView) selfPage.findViewById(R.id.ci_headerview);

        tv_title.setText("我");
        return selfPage;
    }

    @Override
    public void initData() {
        //设置用户名
        TextView tv_username = (TextView) selfPage.findViewById(R.id.tv_username);

        String currUsername = EMClient.getInstance().getCurrentUser();
        tv_username.setText(currUsername);

        initSignOut();
        initSignning();

        initRegister();
        initSetting();
        initHeaderView();
        initNickName();

    }

    private void initSignning() {
        mItem_signning = (SettingItemView) selfPage.findViewById(R.id.item_signning);
        String signning = PrefUtil.getString(mContext, "signning", "签名:天高任鸟飞");
        mItem_signning.setText("签名:"+signning);
        mItem_signning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,NickSigningActivity.class);
                intent.putExtra("requestcode",SIGNNING_SETTING);
                startActivityForResult(intent,SIGNNING_SETTING);
            }
        });
    }

    private void initNickName() {
        mItem_nickname = (SettingItemView) selfPage.findViewById(item_nickname);
        String nickname = PrefUtil.getString(mContext, "nickname", "昵称:小圆");
        mItem_nickname.setText("昵称:"+nickname);
        mItem_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,NickSigningActivity.class);
                intent.putExtra("requestcode",NICKNAME_SETTING);
                startActivityForResult(intent,NICKNAME_SETTING);
            }
        });
    }

    private void initHeaderView() {
        mCi_headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //利用popupMenu显示
//                PopupMenu popupMenu = new PopupMenu(mContext, v);
//                popupMenu.getMenuInflater().inflate(R.menu.headerimage_menu, popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.lookdetail:
//                                showHeaderImageDetail();
//                                break;
//                            case R.id.changeImage:
//                                uploadHeaderImage();
//
//                                break;
//                        }
//                        return true;
//                    }
//                });
//                popupMenu.show();
                //2 利用dialog显示
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final String[] items = {"查看大图", "更换头像"};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            showHeaderImageDetail();
                        } else {
                            uploadHeaderImage();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    //上传头像功能
    private void uploadHeaderImage() {
        Intent openAlbumIntent = new Intent(
                Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, UPLOADHEADERIMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case NICKNAME_SETTING:
                    String nickname = data.getStringExtra("result");
                    mItem_nickname.setText("昵称:"+nickname);
                    break;
                case SIGNNING_SETTING:
                    String signning = data.getStringExtra("result");
                    mItem_signning.setText( "签名:"+signning);
                    break;
                case UPLOADHEADERIMAGE:
                    startPhotoZoom(data.getData());
                    break;
                case CROP_SMALL_PICTURE:
                    setImageToView(data);
                    break;
            }
        }
    }

    //对图片进行剪裁
    private void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    //保存图片至本地缓存文件，未保存到服务器
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            mCi_headerview.setImageBitmap(photo);
        }
    }
    

    //点击头像显示大图
    private void showHeaderImageDetail() {

        //第二中方法
        final Dialog dialog2 = new Dialog(mContext, R.style.dialog);
        View inflate = View.inflate(mContext, R.layout.dialog_photo_entry, null);
        ImageView img = (ImageView) inflate.findViewById(R.id.iv_bigimage);
//        Glide.with(mContext).load(R.drawable.def).into(img);
        img.setImageResource(R.drawable.def);
        dialog2.setContentView(inflate);
        dialog2.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    dialog.dismiss();
                }
                return false;
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
        dialog2.show();
    }

    //初始化设置条目
    private void initSetting() {
        SettingItemView item_setting = (SettingItemView) selfPage.findViewById(R.id.item_setting);
        item_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SettingActivity.class));
            }
        });
    }

    //退出登录
    private void initSignOut() {

        SettingItemView item_signout = (SettingItemView) selfPage.findViewById(R.id.item_signout);
        item_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EMClient.getInstance().logout(true, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(mContext, LoginActivity.class));
                        mContext.finish();
                        ToastUtil.showToast(mContext, "退出成功，请重新登录");
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                        ToastUtil.showToast(mContext, "退出失败");
                    }
                });
            }
        });


    }

    private void initRegister() {
        SettingItemView item_register_login = (SettingItemView) selfPage.findViewById(R.id.item_register_login);
        item_register_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
            }
        });

    }


}
