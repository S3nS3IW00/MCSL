package app.mcsl.util;

import app.mcsl.manager.logging.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

    public static boolean isEquals(Date date) {
        return date.getTime() - new Date().getTime() <= 1000 && date.getTime() - new Date().getTime() >= 0;
    }

    public static boolean isEqualsTime(String time) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime()).equalsIgnoreCase(time);
    }

    private static Integer mathDatePoint(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        Date stringdate = null;
        try {
            stringdate = format.parse(date.split(" ")[0]);
        } catch (ParseException e) {
            Logger.exception(e);
        }
        return Integer.parseInt(format.format(now).replace("-", "")) - Integer.parseInt(format.format(stringdate).replace("-", ""));
    }

    private static Integer mathTimePoint(String time) {
        DateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        Date stringdate = null;
        try {
            stringdate = format.parse(time.split(" ")[1]);
        } catch (ParseException e) {
            Logger.exception(e);
        }
        return Integer.parseInt(format.format(now).replace(":", "")) - Integer.parseInt(format.format(stringdate).replace(":", ""));
    }

    public static boolean lateDate(String date) {
        return mathDatePoint(date) > 0;
    }

    public static boolean lateTime(String date) {
        if (mathDatePoint(date) == 0) {
            return mathTimePoint(date) > 0;
        }
        return false;
    }

}
