package hotmath.gwt.cm_core.client.util;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DateUtils4Gwt {

    private static DateTimeFormat formatDayOfWeek = DateTimeFormat.getFormat("EEEE");
    private static DateTimeFormat formatNormal = DateTimeFormat.getFormat("MMM d");
    private static DateTimeFormat formatYearMonthDay = DateTimeFormat.getFormat("yyyy-MM-dd");
    
    public static String getPrettyDateString(Date dueDate) {
        return getPrettyDateString(dueDate, true);
    }
    public static String getPrettyDateString(Date dueDate, boolean useRelative) {
        
        if(dueDate == null) {
            return "";
        }

        long due = dueDate.getTime();
        int oneDay = (60 * 1000) * 60 * 24;
        GregorianCalendar g = new GregorianCalendar();
        g.set(Calendar.HOUR,0);
        g.set(Calendar.MINUTE,0);
        int dy = g.get(Calendar.DAY_OF_YEAR)-1;
        g.set(Calendar.DAY_OF_YEAR, dy);
        Date d = g.getTime();
        long now = d.getTime();

        String formatedString;
        if(!useRelative || (due + Assignment.MILLS_IN_DAY) < now ) {
            // past due
            formatedString = formatNormal.format(dueDate);
        }
        else if (((due - now) / oneDay) < 1) {
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
