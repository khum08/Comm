package test.yzhk.com.comm.UI.activities;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.exceptions.HyphenateException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.utils.FileUtil;
import test.yzhk.com.comm.utils.OpenFileUtil;
import test.yzhk.com.comm.utils.ToastUtil;


public class FileShareActivity extends AppCompatActivity {

    public static final String TAG = "FileShareActivity";
    private FloatingActionButton mFab_upload;
    private ListView lv_files;
    private String mGroupId;
    private int pageNum = 1;
    private List<EMMucSharedFile> mEmMucSharedFiles;
    private static final int GET_DATA = 790;
    private static final int UPLOAD_FILE = 725;
    private static final int REFRESH_DATA = 585;
    private ProgressBar pb_file;
    private FileAdapter mFileAdapter;
    private List<EMMucSharedFile> moreFiles;
    private SwipeRefreshLayout swipe_refresh_file;
    private long mCurrentTimeMillis;
    private Toolbar toolbar_share_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_share);
        Intent intent = getIntent();
        mGroupId = intent.getStringExtra("groupId");
        initView();
        initData();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA:
                    Log.e(TAG, "zou le new adapter mhandler");
                    mFileAdapter = new FileAdapter();
                    lv_files.setAdapter(mFileAdapter);
                    break;
                case REFRESH_DATA:
                    mFileAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                try {
                    mEmMucSharedFiles = EMClient.getInstance().groupManager().fetchGroupSharedFileList(mGroupId, pageNum, 15);
                    Log.e(TAG, "mEmMucSharedFiles的长度为" + mEmMucSharedFiles.size());
                    pageNum++;
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(GET_DATA);
            }
        }.start();
    }

    public void getMoreData() {
        new Thread() {
            @Override
            public void run() {
                try {
                    moreFiles = EMClient.getInstance().groupManager().fetchGroupSharedFileList(mGroupId, pageNum, 15);
                    pageNum++;
                    mEmMucSharedFiles.addAll(moreFiles);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(REFRESH_DATA);
            }
        }.start();
    }

    private void initView() {
        toolbar_share_file = (Toolbar) findViewById(R.id.toolbar_share_file);
        setSupportActionBar(toolbar_share_file);
        lv_files = (ListView) findViewById(R.id.lv_files);
        mFab_upload = (FloatingActionButton) findViewById(R.id.fab_upload);
        mFab_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, UPLOAD_FILE);
            }
        });
        pb_file = (ProgressBar) findViewById(R.id.pb_file);
        lv_files.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                        lv_files.getLastVisiblePosition() == mFileAdapter.getCount() - 1) {
                    getMoreData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        initRefresh();
    }

    private void initRefresh() {
        swipe_refresh_file = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_file);
        swipe_refresh_file.setColorSchemeResources(R.color.red, R.color.colorPrimary, R.color.name);
        swipe_refresh_file.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentTimeMillis = System.currentTimeMillis();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            pageNum = 1;
                            mEmMucSharedFiles = EMClient.getInstance().groupManager().fetchGroupSharedFileList(mGroupId, pageNum, 15);
                            pageNum++;
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        //每次刷新至少要1500毫秒
                        long newTime = System.currentTimeMillis();
                        if (newTime - mCurrentTimeMillis > 1500) {
                            FileShareActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    swipe_refresh_file.setRefreshing(false);
                                }
                            });
                        } else {
                            try {
                                Thread.sleep(1500 - (newTime - mCurrentTimeMillis));
                                FileShareActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipe_refresh_file.setRefreshing(false);
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        mHandler.sendEmptyMessage(REFRESH_DATA);
                    }
                }.start();
            }
        });
    }

    public String path;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPLOAD_FILE:
                    Uri uri = data.getData();
                    if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                        path = uri.getPath();
                        return;
                    }
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                        path = getPath(this, uri);
                    } else {//4.4以下下系统调用方法
                        path = getRealPathFromURI(uri);
                    }
                    File file = new File(path);
                    pb_file.setMax((int) file.getTotalSpace());

                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                FileShareActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pb_file.setVisibility(View.VISIBLE);
                                    }
                                });
                                EMClient.getInstance().groupManager().uploadGroupSharedFile(mGroupId, path, new EMCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        FileShareActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pb_file.setVisibility(View.INVISIBLE);
                                            }
                                        });

                                        ToastUtil.showToast(FileShareActivity.this, "上传成功");
                                        //页面重新请求服务器更新数据
                                        try {
                                            pageNum = 1;
                                            mEmMucSharedFiles = EMClient.getInstance().groupManager().fetchGroupSharedFileList(mGroupId, pageNum, 15);
                                        } catch (HyphenateException e) {
                                            e.printStackTrace();
                                        }
                                        mFileAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        FileShareActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pb_file.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                        ToastUtil.showToast(FileShareActivity.this, "上传失败");
                                    }

                                    @Override
                                    public void onProgress(final int i, String s) {
                                        FileShareActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pb_file.setProgress(i);
                                            }
                                        });
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
            }
        }
    }

    class FileAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mEmMucSharedFiles != null) {
                return mEmMucSharedFiles.size();
            }
            return 0;
        }

        @Override
        public EMMucSharedFile getItem(int position) {
            if (mEmMucSharedFiles != null) {
                return mEmMucSharedFiles.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(FileShareActivity.this, R.layout.view_file, null);
                viewHolder.tv_filename = (TextView) convertView.findViewById(R.id.tv_filename);
                viewHolder.tv_filesize = (TextView) convertView.findViewById(R.id.tv_filesize);
                viewHolder.tv_src_size = (TextView) convertView.findViewById(R.id.tv_src_size);
                viewHolder.iv_download = (ImageView) convertView.findViewById(R.id.iv_download);
                viewHolder.tv_read = (TextView) convertView.findViewById(R.id.tv_read);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final EMMucSharedFile item = getItem(position);

            viewHolder.tv_filename.setText(item.getFileName());
            viewHolder.tv_filesize.setText("(" + Formatter.formatFileSize(FileShareActivity.this, item.getFileSize()) + ")");
            String format = new SimpleDateFormat("MM-dd").format(item.getFileUpdateTime());
            viewHolder.tv_src_size.setText("来源：" + item.getFileOwner() + " 时间：" + format);
            boolean fileExit = FileUtil.isFileExit(FileShareActivity.this, item.getFileName());
            if (fileExit) {
                viewHolder.iv_download.setVisibility(View.GONE);
                viewHolder.tv_read.setVisibility(View.VISIBLE);
            } else {
                viewHolder.iv_download.setVisibility(View.VISIBLE);
                viewHolder.tv_read.setVisibility(View.GONE);
            }
            final File savePath = FileUtil.createFileSavePath(FileShareActivity.this, item.getFileName());
            //下载文件的逻辑
            viewHolder.iv_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pb_file.setVisibility(View.INVISIBLE);
                    pb_file.setMax((int) item.getFileSize());
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().downloadGroupSharedFile(mGroupId, item.getFileId(), savePath.getAbsolutePath(), new EMCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        ToastUtil.showToast(FileShareActivity.this, "文件下载成功");
                                        FileShareActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                viewHolder.iv_download.setVisibility(View.GONE);
                                                viewHolder.tv_read.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                        ToastUtil.showToast(FileShareActivity.this, "文件下载失败");
                                    }

                                    @Override
                                    public void onProgress(final int i, String s) {
                                        FileShareActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pb_file.setProgress(i);
                                            }
                                        });

                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                ToastUtil.showToast(FileShareActivity.this, "文件下载失败");
                            }
                        }
                    }.start();
                }
            });
            //查看文件的逻辑
            viewHolder.tv_read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //业务实现上有点问题 // TODO: 2017/12/10  
                    Intent intent = OpenFileUtil.openFile(savePath.getAbsolutePath());
                    FileShareActivity.this.startActivity(intent);
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        public TextView tv_filename;
        public TextView tv_filesize;
        public TextView tv_src_size;
        public TextView tv_read;
        public ImageView iv_download;

    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    //左上角返回键
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
