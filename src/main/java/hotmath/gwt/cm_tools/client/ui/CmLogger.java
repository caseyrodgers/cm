package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.rpc.RetryActionManager;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class CmLogger extends CmWindow {
    
    static private CmLogger __instance;
    static public CmLogger getInstance() {
        if(__instance == null)
            __instance = new CmLogger();
        return __instance;
    }
    
    TextArea _logArea = new TextArea();
    TextField<String> _filter = new TextField<String>();
    List<String> _messages = new ArrayList<String>();
    public CmLogger() {
        setHeading("Catchup Logger");
        setSize(800,300);
        setPosition(0, 450);
        setLayout(new FitLayout());
        add(_logArea);
        setScrollMode(Scroll.AUTO);
        setStyleAttribute("z-index", "100");
        ToolBar tb = new ToolBar();
        _filter.setWidth(150);
        _filter.setToolTip("Filter text");
        _filter.addKeyListener(new KeyListener() {
            @Override
            public void componentKeyUp(ComponentEvent event) {
                super.componentKeyUp(event);
                filterAllMessages();
            }
        });
        tb.add(_filter);
        getHeader().addTool(new Button("Clear", new SelectionListener<ButtonEvent>() {
           @Override
            public void componentSelected(ButtonEvent ce) {
               _messages.clear();
               _logArea.clear();
            } 
        }));
        getHeader().addTool(new Button("Select All", new SelectionListener<ButtonEvent>() {
            @Override
             public void componentSelected(ButtonEvent ce) {
                _logArea.selectAll();
             } 
         }));

        getHeader().addTool(new Button("Requests", new SelectionListener<ButtonEvent>() {
            @Override
             public void componentSelected(ButtonEvent ce) {
                info(RetryActionManager.getInstance().toString());
             } 
         }));        
        setTopComponent(tb);
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));
    }
    
    public void filterAllMessages() {
        setVisible(true);
        String f = _filter.getValue();
        String log="";
        for(String s: _messages) {
            if(f == null || f.length() == 0 || s.indexOf(f) > -1) {
                log += s + "\n\n";   
            }
        }
        _logArea.setValue(log);
    }
    
    static public void info(String msg) {
        getInstance()._info(msg);
    }
    static public void debug(String msg) {
        getInstance()._debug(msg);
    }
    static public void error(String msg,Throwable th) {
        getInstance()._error(msg, th);
    }
    
    
    private void _info(String msg) {
        _messages.add(msg);
        
        if(!isVisible())
            return;
        
        String fv = _filter.getValue();
        String la = _logArea.getValue();
        if(la == null)
            la = "";
        if((fv == null || fv.length() == 0) ||
           msg.indexOf(fv) > -1) {
            _logArea.setValue(la + msg + "\n\n");
        }
        
    }
    
    private void _error(String msg, Throwable th) {
        _info(msg + "\n" + th.getStackTrace());
    }
    
    private void _debug(String msg) {
        _info(msg);
    }
}
