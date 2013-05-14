package hotmath.gwt.cm_core.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DateUtils4Gwt {

    private static DateTimeFormat formatDayOfWeek = DateTimeFormat.getFormat("EEEE");
    private static DateTimeFormat formatNormal = DateTimeFormat.getFormat("MMM d");
    
    public static String getPrettyDateString(Date dueDate) {
        return getPrettyDateString(dueDate, true);
    }
    public static String getPrettyDateString(Date dueDate, boolean useRelative) {

        long due = dueDate.getTime();
        int oneDay = (60 * 1000) * 60 * 24;
        long now = new Date().getTime();

        String formatedString;
        if(!useRelative || due < now ) {
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

}
