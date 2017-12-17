package test.yzhk.com.comm.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 大傻春 on 2017/12/7.
 */

public class FileUtil {

    //返回一个图片存储的文件夹

    public static File createImgFile(Context context){
        String fileName = "img_"+ new SimpleDateFormat("MMdd_HHmmss").format(new Date())+".jpg";
        File dir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            dir = Environment.getExternalStorageDirectory();
        }else{
            dir  = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        File file = new File(dir, fileName);
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    //返回一个视频存储的文件夹
    public static File createVideoFile(Context context){
        String fileName = "video_"+System.currentTimeMillis()+".mp4";
        File dir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            dir = Environment.getExternalStorageDirectory();
        }else{
            dir = context.getExternalCacheDir();
        }
        File file = new File(dir, fileName);
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    //创建一个文件保存路径
    public static File createFileSavePath(Context context,String filename){
        File dir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            dir = Environment.getExternalStorageDirectory();
        }else{
            dir = context.getExternalCacheDir();
        }
        File file = new File(dir, filename);
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    //判断文件是否存在
    public static boolean isFileExit(Context context,String filename){
        File dir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            dir = Environment.getExternalStorageDirectory();
        }else{
            dir = context.getExternalCacheDir();
        }
        File file = new File(dir, filename);
        return file.exists();
    }

    //查看文件
    public static void readFile(Context context,String path){
            File file = new File(path);
            if(null==file || !file.exists()){
                return;
            }
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "file/*");
            try {
                context.startActivity(intent);
                context.startActivity(Intent.createChooser(intent,"选择浏览工具"));
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
    }
}










