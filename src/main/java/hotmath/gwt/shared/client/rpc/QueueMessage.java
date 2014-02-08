package hotmath.gwt.shared.client.rpc;

import java.util.Date;

public class QueueMessage  {
    
    static int __id;
    private String message;
    private String timeStamp;
    private int id;

    public QueueMessage(String msg) {
        this.message = msg;
        this.timeStamp = getDateStart_TimeStamp();
        this.id = __id++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @SuppressWarnings("deprecation")
    private String getDateStart_TimeStamp() {
        Date date = new Date();
        return td(date.getHours()) + ":" + td(date.getMinutes()) + ":" + td(date.getSeconds());
    }

    private String td(int num) {
        return num < 10 ? "0" + num : Integer.toString(num);
    }

    public String toString() {
        return timeStamp + " " + message;
    }
}
