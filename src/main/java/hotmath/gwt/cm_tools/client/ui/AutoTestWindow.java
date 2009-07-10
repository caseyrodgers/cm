package hotmath.gwt.cm_tools.client.ui;


import hotmath.gwt.shared.client.util.UserInfo;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Label;

public class AutoTestWindow extends Window {
    
    private static AutoTestWindow __instance;
    public static AutoTestWindow getInstance() {
        if(__instance == null)
            __instance = new AutoTestWindow();
        return __instance;
    }
    

    ListView<LogModel> _listView = new ListView<LogModel>();

    private AutoTestWindow() {
        setSize(500,200);
        setTitle("Catchup Student Auto Test");
        
        setClosable(false);
        setHeading("Auto Test Log");
        
        setTopComponent(createTopForm());
        setLayout(new FitLayout());
        
        _listView.setSimpleTemplate("<div>{message}</div>");
        ListStore<LogModel> store =new ListStore<LogModel>();
        _listView.setStore(store);
        
        Button btn = new Button("Close");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            public void componentSelected(ButtonEvent ce) {
                UserInfo.getInstance().setAutoTestMode(false);
                __instance.hide();
                __instance = null;
            }
        });
        
        Button btn2 = new Button("Stop");
        btn2.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            public void componentSelected(ButtonEvent ce) {
                addLogMessage("Stopping auto test");
                UserInfo.getInstance().setAutoTestMode(false);
            }
        });
        
        getHeader().addTool(btn);
        getHeader().addTool(btn2);
        add(_listView);
        
        _listView.getStore().setMonitorChanges(true);
    }
    
    public void addLogMessage(String msg) {
        LogModel lm = new LogModel(msg);
        _listView.getStore().add(lm);
        int scrollTop = _listView.el().getScrollTop();
        int size = _listView.el().getHeight();
        
        
        _listView.el().scrollTo("top",  scrollTop + size);
        _listView.getSelectionModel().select(_listView.getStore().getCount(), false);
    }
    
    
    TextField<String> _waitTimeForSingleResource = new TextField<String>();
    TextField<String> _waitTimeForSingleResourceType = new TextField<String>();
    TextField<String> _waitTimeForSingleLesson = new TextField<String>();    
    private LayoutContainer createTopForm() {
        
        _waitTimeForSingleResource.setValue("3000");
        _waitTimeForSingleResourceType.setValue("10000");
        _waitTimeForSingleLesson.setValue("60000");
        
        LayoutContainer lc = new HorizontalPanel();
        
        Label l = new Label("Item: ");
        lc.add(l);
        lc.add(_waitTimeForSingleResource);
        
        l = new Label("Resource: ");
        lc.add(l);
        lc.add(_waitTimeForSingleResourceType);
        
        l = new Label("Lesson: ");
        lc.add(l);
        lc.add(_waitTimeForSingleLesson);
        return lc;
    }
    
    
    public int getTimeForSingleResource() {
        return Integer.parseInt(_waitTimeForSingleResource.getValue());
    }
    
    public int getTimeForSingleResourceType() {
        return Integer.parseInt(_waitTimeForSingleResourceType.getValue());
    }
    
    public int getTimeForSingleLesson() {
        return Integer.parseInt(_waitTimeForSingleLesson.getValue());
    }
}


class LogModel extends BaseModel {
    
    public LogModel(String msg) {
        setMessage(msg);
    }
    
    public void setMessage(String message) {
        String time = getFormatedTime();
        set("message",time + " " + message);
    }
    
    /** Return the formated time (no SimpleDateFormated in GWT)
     * 
     * @return
     */
    private String getFormatedTime() {
        String time = new Date().toString();
        int s = time.indexOf(":");
        time = time.substring(s-2, s+6);
        return time;
    }
    public String getMessage() {
        return get("message");
    }
}