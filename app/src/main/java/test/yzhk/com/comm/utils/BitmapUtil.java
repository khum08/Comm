package test.yzhk.com.comm.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 大傻春 on 2017/12/6.
 */

public class BitmapUtil {

    /**
     * 保存头像应用文件中
     *
     * @param bitmap
     * @param mContext
     */
    public static void saveHeaderImage(Bitmap bitmap, Context mContext) {
        File filesDir = mContext.getFilesDir();
        File headerimage = new File(filesDir, "headerimage");
        if (headerimage.exists()) {
            headerimage.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(headerimage);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取头像文件 如何存在返回，不存在返回null
     */

    public static Bitmap getHeaderImage(Context context) {
        File filesDir = context.getFilesDir();
        File headerimage = new File(filesDir, "headerimage");
        if (!headerimage.exists()) {
            return null;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(headerimage);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
