package hotmath.gwt.cm_tools.client.ui;


import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.shared.client.CmShared;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.Style.ScrollDirection;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.Slider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;


public class AutoTestWindow extends GWindow {
	

	public interface LogProperties extends PropertyAccess<String> {
		ModelKeyProvider<LogModel> id();
		ValueProvider<LogModel, String> message();
	}

    
    private static AutoTestWindow __instance;
    public static AutoTestWindow getInstance() {
        if(__instance == null)
            __instance = new AutoTestWindow();
        return __instance;
    }
    
    LogProperties props = GWT.create(LogProperties.class);
    

    ListView<LogModel, String> _listView;
    
    ToggleButton _logEnable;
    private AutoTestWindow() {
        
    	super(true);
    	setModal(false);
        CmShared.setQueryParameter("test_rpp_only", "true");
        setResizable(true);
        setMaximizable(true);
        setMinimizable(true);

        setPixelSize(500,200);
        setHeadingText("Auto Test Log");
        
        ListStore<LogModel> store  =new ListStore<LogModel>(props.id());
        _listView = new ListView<LogModel, String>(store, props.message());
        
        setWidget(_listView);
        
        setupTools();
        
        setVisible(true);
    }
    
    public void addLogMessage(String msg) {
    	
        if(!_logEnable.getValue()) {
            return;
        }
        
        if(!isVisible()) {
        	setVisible(true);
        }
        
        LogModel lm = new LogModel(msg);
        _listView.getStore().add(lm);
        
        int scrollTop = _listView.getElement().getScrollTop();
        int size = _listView.getElement().getClientHeight();
        
        _listView.getElement().scrollTo(ScrollDirection.TOP, scrollTop + size);
        _listView.getSelectionModel().select(_listView.getStore().size(), false);
    }
    
    
    Slider _waitTimeForSingleResourceSlider = new Slider();
    private void setupTools() {

        TextButton close = new TextButton("Close");
        close.addSelectHandler(new SelectHandler() {
        	@Override
        	public void onSelect(SelectEvent event) {
                UserInfo.getInstance().setAutoTestMode(false);
                CmMainPanel.__lastInstance.remove(AutoTestWindow.this);
                CmMainPanel.__lastInstance.forceLayout();
                __instance = null;
            }
        });
        close.setToolTip("Close the auto test window");
        
        final ToggleButton run = new ToggleButton("Run");
        run.setValue(true);
        run.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
                if(run.getValue()) {
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
        
        final ToggleButton rppOnly = new ToggleButton("RPP/RPA only");
        if(CmShared.getQueryParameter("test_rpp_only") != null) {
            rppOnly.setValue(true);
        }
        rppOnly.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
                if(rppOnly.getValue()) {
                    addLogMessage("Enabling RPP only mode");
                    CmShared.setQueryParameter("test_rpp_only", "true");
                }
                else {
                    addLogMessage("Disabling RPP only mode");
                    CmShared.removeQueryParameter("test_rpp_only");
                }
            }
        });
        rppOnly.setToolTip("Only show RPPs or RPAs when enabled");
        
        
        _logEnable = new ToggleButton("Enable Log");
        _logEnable.setValue(true);

        _waitTimeForSingleResourceSlider.setWidth(100);
        _waitTimeForSingleResourceSlider.setMaxValue(3000*4);
        _waitTimeForSingleResourceSlider.setValue(3000*2);
        _waitTimeForSingleResourceSlider.setToolTip("Time between resource item loads");
        
        getHeader().addTool(_waitTimeForSingleResourceSlider);

        getHeader().addTool(run);
        getHeader().addTool(rppOnly);
        getHeader().addTool(_logEnable);
        TextButton clear = new TextButton("Clear", new SelectHandler() {
        	@Override
        	public void onSelect(SelectEvent event) {
                _listView.getStore().clear();
            }
        });
        clear.setToolTip("Clear the log window");
        getHeader().addTool(clear);
        getHeader().addTool(close);
    }

    
    public void startAutoTest() {
        UserInfo.getInstance().setAutoTestMode(true);
        CmContext context = ContextController.getInstance().getTheContext();
        if(context != null) {
            context.runAutoTest();
        }
            
    }
    
    public int getTimeForSingleResource() {
        return _waitTimeForSingleResourceSlider.getValue();
    }
}




class LogModel  {
 
	static int __idSource;
	int id;
	String message;
    public LogModel(String msg) {
    	id = (__idSource++);
        setMessage(msg);
    }
    
    public int getId() {
    	return id;
    }
    
    public void setMessage(String message) {
        String time = getFormatedTime();
        this.message = time + " " + message;
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
        return this.message;
    }
}