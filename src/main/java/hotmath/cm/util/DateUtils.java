package hotmath.cm.util;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    static public String getTimeSinceLabel(Date dateSent) {
        
        long timeNow = System.currentTimeMillis();
        long timeThen = dateSent.getTime();
        
        long timeDiff = timeNow - timeThen;
        
        // how many minutes ago?
        
        long daysAgo = timeDiff / (1000 * 60 * 60 * 24);
        if(daysAgo > 0) {
            return daysAgo + " " + (daysAgo==1?"day":"days") + " ago";
        }
        else {
            long hoursAgo = timeDiff / (1000 * 60 * 60);
            if(hoursAgo > 0) {
                return hoursAgo + " " + (hoursAgo==1?"hour":"hours") + " ago";
            }
            else {
                long minAgo = timeDiff / (1000 * 60);
                if(minAgo > 0) {
                    return minAgo + " " + (minAgo==1?"minute":"minutes") + " ago";
                }
                else {
                    long secondsAgo = timeDiff / (1000);
                    return secondsAgo + " " + (secondsAgo==1?"second":"seconds") + " ago";
                }
            }
        }
        // __formatter.format(dateSent);        
    }
    
    private static SimpleDateFormat formatDayOfWeek = new SimpleDateFormat("EEEE");
    private static SimpleDateFormat formatNormal = new SimpleDateFormat("MMM d");
    private static SimpleDateFormat formatYearMonthDay = new SimpleDateFormat("yyyy-MM-dd");
    
    public static String getPrettyDateString(Date dueDate) {
        return getPrettyDateString(dueDate, true);
    }
    public static String getPrettyDateString(Date dueDate, boolean useRelative) {
        
        if(dueDate == null) {
            return "";
        }

        long due = dueDate.getTime();
        int oneDay = (60 * 1000) * 60 * 24;
        long now = new Date().getTime();

        String formatedString;
        if(!useRelative || (due + Assignment.MILLS_IN_DAY) < now ) {
            // past due
            formatedString = formatNormal.format(dueDate);
        }
        else if (((due - now) / oneDay) < 2) {
            formatedString = "Today";
        } else if (due < now + (oneDay * 2)) {
            formatedString = "Tomorrow";
        } else if (due < now + (oneDay * 7)) {
            // day of week
            formatedString = formatDayOfWeek.format(dueDate);
        } else {
            formatedString = formatNormal.format(dueDate);
        }
        return formatedString;
    }

    public static String getYearMonthDayString(Date date) {
        return formatYearMonthDay.format(date);
    }


}
