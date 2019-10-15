package app.mcsl.utils;

import app.mcsl.manager.logging.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class DateTimeUtils {

    public static String calculateTime(long seconds) {
        String time = null;
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        if (seconds < 60) {
            time = second + " second";
        } else if (seconds >= 60 && seconds < 3600) {
            time = minute + " minute " + second + " second";
        } else if (seconds >= 3600 && seconds < 86400) {
            time = hours + " hour " + minute + " minute " + second + " second";
        } else if (seconds >= 86400) {
            time = day + " day " + hours + " hour " + minute + " minute " + second + " second";
        }
        return time;
    }

    public static String calculateElapsedDateTime(Date startDate, Date endDate) {
        String time;
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if (elapsedDays > 3) {
            time = new SimpleDateFormat("YYYY-hh-dd HH:mm").format(endDate);
        } else {
            if (elapsedDays > 0) {
                time = elapsedDays + " napja";
            } else {
                if (elapsedHours > 0) {
                    time = elapsedHours + " órája";
                } else {
                    if (elapsedMinutes > 0) {
                        time = elapsedMinutes + " perce";
                    } else {
                        if (elapsedSeconds > 10) {
                            time = "pár másodperce";
                        } else {
                            time = "éppen most";
                        }
                    }
                }
            }
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

    public static boolean validDate(String date, boolean daily) {
        Pattern p = Pattern.compile((daily ? "\\d\\d:\\d\\d:\\d\\d|\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d" : "\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d"));
        return p.matcher(date).matches();
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
