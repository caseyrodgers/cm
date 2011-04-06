package hotmath.gwt.cm_tools.client.ui;


import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;

import java.util.Date;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class AutoTestWindow extends ContentPanel {
    
    private static AutoTestWindow __instance;
    public static AutoTestWindow getInstance() {
        if(__instance == null)
            __instance = new AutoTestWindow();
        return __instance;
    }
    

    ListView<LogModel> _listView = new ListView<LogModel>();
    ToggleButton _logEnable;
    private AutoTestWindow() {
        
        setSize(500,200);
        setHeading("Auto Test Log");
        setLayout(new FitLayout());
        
        _listView.setSimpleTemplate("<div>{message}</div>");
        ListStore<LogModel> store =new ListStore<LogModel>();
        _listView.setStore(store);


        add(_listView);
        
        setupTools();

        _listView.getStore().setMonitorChanges(true);
        
        addLogMessage("Auto Test ready");
    }
    
    public void addLogMessage(String msg) {

        if(!(getParent() == CmMainPanel.__lastInstance)) {
            CmMainPanel.__lastInstance.add(this, new BorderLayoutData(LayoutRegion.SOUTH));
            CmMainPanel.__lastInstance.layout();
        }
        
        
        if(!_logEnable.isPressed()) {
            return;
        }
        
        
        LogModel lm = new LogModel(msg);
        _listView.getStore().add(lm);
        int scrollTop = _listView.el().getScrollTop();
        int size = _listView.el().getHeight();
        
        
        _listView.el().scrollTo("top",  scrollTop + size);
        _listView.getSelectionModel().select(_listView.getStore().getCount(), false);
    }
    
    
    Slider _waitTimeForSingleResourceSlider = new Slider();
    private void setupTools() {


        Button close = new Button("Close");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            public void componentSelected(ButtonEvent ce) {
                UserInfo.getInstance().setAutoTestMode(false);
                CmMainPanel.__lastInstance.remove(AutoTestWindow.this);
                CmMainPanel.__lastInstance.layout();
                __instance = null;
            }
        });
        close.setToolTip("Close the auto test window");
        
        final ToggleButton run = new ToggleButton("Run");
        run.toggle(true);
        run.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            public void componentSelected(ButtonEvent ce) {
                
                if(run.isPressed()) {
                    addLogMessage("Starting auto test");
                    startAutoTest();
                }
                else {
                    UserInfo.getInstance().setAutoTestMode(false);
                    addLogMessage("Stopping auto test");
                }
                
            }
        });
        run.setToolTip("Toggle the auto test process");
        
        _logEnable = new ToggleButton("Enable Log");
        _logEnable.toggle(true);

        _waitTimeForSingleResourceSlider.setWidth(100);
        _waitTimeForSingleResourceSlider.setMaxValue(3000*4);
        _waitTimeForSingleResourceSlider.setValue(3000*2);
        _waitTimeForSingleResourceSlider.setToolTip("Time between resource item loads");
        
        getHeader().addTool(_waitTimeForSingleResourceSlider);
        
        getHeader().addTool(run);
        getHeader().addTool(_logEnable);
        Button clear = new Button("Clear", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                _listView.getStore().removeAll();
            }
        });
        clear.setToolTip("Clear the log window");
        getHeader().addTool(clear);
        getHeader().addTool(close);
    }
    
    class TestType extends BaseModelData {
    	public TestType(String text) {
    		set("text", text);
    	}
    }
    
    public void startAutoTest() {
        UserInfo.getInstance().setAutoTestMode(true);
        CmContext context = ContextController.getInstance().getTheContext();
        if(context != null)
            context.runAutoTest();        
    }
    
    public int getTimeForSingleResource() {
        return _waitTimeForSingleResourceSlider.getValue();
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