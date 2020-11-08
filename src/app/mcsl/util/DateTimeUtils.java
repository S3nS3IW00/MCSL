package app.mcsl.util;

import app.mcsl.manager.logging.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    public static String calculateTime(long seconds) {
        String time;
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        if (seconds < 60) {
            time = second + " second";
        } else if (seconds < 3600) {
            time = minute + " minute " + second + " second";
        } else if (seconds < 86400) {
            time = hours + " hour " + minute + " minute " + second + " second";
        } else {
            time = day + " day " + hours + " hour " + minute + " minute " + second + " second";
        }
        return time;
    }

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
