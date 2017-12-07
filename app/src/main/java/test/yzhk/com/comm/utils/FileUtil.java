package test.yzhk.com.comm.utils;

import android.content.Context;
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
}










