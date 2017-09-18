package org.sumbootFrame.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtil {
	public static String jdateformat = "yyyy-MM-dd HH:mm:ss";
	protected static SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.jdateformat);

    public static final int YEAR = 1;
    public static final int MONTH = 2;
    public static final int DATE = 5;
    public static final int HOUR = 10;
    public static final int MINUTE = 12;
    public static final int SECOND = 13;

	public static Date parse(String date) throws ParseException {
		return sdf.parse(date);
	}

    public static Date parse(String date,String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(date);
    }

	public static String format(Date date) {
		return format(date,jdateformat);
	}
	public static String format(Date date, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
    public static Date dateOffset(Date date,int offset){
        return dateOffset(date,offset,DateUtil.DATE);
    }
    public static Date dateOffset(Date date,int offset,int type){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(type, offset);
        return cal.getTime();
    }

}