package hotmath.gwt.shared.client.rpc;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;

public class QueueMessage extends BaseModel {
    public QueueMessage(String msg) {
        set("log_message", msg);
        set("time_stamp", getDateStart_TimeStamp());
    }

    private String getDateStart_TimeStamp() {
        Date date = new Date();
        return td(date.getHours()) + ":" + td(date.getMinutes()) + ":" + td(date.getSeconds());
    }

    private String td(int num) {
        return num < 10 ? "0" + num : Integer.toString(num);
    }

}
