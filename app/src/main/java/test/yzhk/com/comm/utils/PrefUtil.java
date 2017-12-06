package test.yzhk.com.comm.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePrefrences工具类
 * @author yzhk
 *
 */
public class PrefUtil {
	private static String CONFIGNAME = "CONFIG";

	public static void putInt(Context context, String key, int defValue) {
		SharedPreferences mPref = context.getSharedPreferences(CONFIGNAME,
				Context.MODE_PRIVATE);
		mPref.edit().putInt(key, defValue).commit();

	}

	public static int getInt(Context context, String key, int defValue) {
		SharedPreferences mPref = context.getSharedPreferences(CONFIGNAME,
				Context.MODE_PRIVATE);
		return mPref.getInt(key, defValue);

	}

	public static void putBoolean(Context context, String key, boolean defValue) {
		SharedPreferences mPref = context.getSharedPreferences(CONFIGNAME,
				Context.MODE_PRIVATE);
		mPref.edit().putBoolean(key, defValue).commit();

	}

	public static boolean getBoolean(Context context, String key,
			boolean defValue) {
		SharedPreferences mPref = context.getSharedPreferences(CONFIGNAME,
				Context.MODE_PRIVATE);
		return mPref.getBoolean(key, defValue);

	}

	public static void putString(Context context, String key, String defValue) {
		SharedPreferences mPref = context.getSharedPreferences(CONFIGNAME,
				Context.MODE_PRIVATE);
		mPref.edit().putString(key, defValue).commit();
	}

	public static String getString(Context context, String key, String defValue) {
		SharedPreferences mPref = context.getSharedPreferences(CONFIGNAME,
				Context.MODE_PRIVATE);
		return mPref.getString(key, defValue);

	}
}
