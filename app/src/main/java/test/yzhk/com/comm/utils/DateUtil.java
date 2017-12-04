package test.yzhk.com.comm.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 大傻春 on 2017/12/4.
 */

public class DateUtil {

    /**
     *
     * @param dateFormat 待返回的时间格式
     * @param time long型的时间
     * @return 时间字符串
     */
    public static String formate(String dateFormat, Long time){
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(new Date(time));
    }
}
