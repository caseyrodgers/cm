package hotmath.gwt.cm_qa.client;
import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;


public class CmQa implements EntryPoint {
    Viewport _mainPort = new Viewport();
    public static String __category;
    public static String __userName;
    
    public void onModuleLoad() {
        
        __category = CmGwtUtils.getQueryParameter("category");
        if(__category == null) {
            MessageBox.alert("Error", "Please specify the category", new Listener<MessageBoxEvent>() {
                @Override
                public void  handleEvent(MessageBoxEvent be) {
                }
            });
        }
        _mainPort.setLayout(new BorderLayout());
        
        _mainPort.add(createHeader(), new BorderLayoutData(LayoutRegion.NORTH,50));
        _mainPort.add(createBody(), new BorderLayoutData(LayoutRegion.CENTER));
        
        
        CmQa.__userName = CmGwtUtils.getQueryParameter("user");
        
        if(CmQa.__userName == null) {
            MessageBox.prompt("User Name", "What is your name?",new Listener<MessageBoxEvent>() {
                @Override
                public void handleEvent(MessageBoxEvent be) {
                    if(be.getValue() != null) {
                        CmQa.__userName = be.getValue();
                        
                        userNameLabel.setText(CmQa.__userName);
                        RootPanel.get().add(_mainPort);
                    }
                }
            });
        }
    }
    
    
    Label userNameLabel = new Label();
    private Component createHeader() {
        LayoutContainer lc = new LayoutContainer();
        lc.add(new Html("<h1>QA Items for " + __category + "</h1>"));
        lc.add(userNameLabel);
        return lc;
    }
    
    
    private Component createBody() {
        return new CmQaBody();
    } 
    
    

    /**
     * Static routines used throughout app
     * 
     * TODO: move to separate module
     * 
     * @return
     */
    static public CmServiceAsync getCmService() {
        return _cmService;
    }

    static CmServiceAsync _cmService;

    static {
        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))
            point += "/";

        point = "/cm/";
        _cmService = (CmServiceAsync) GWT.create(CmService.class);
        ((ServiceDefTarget) _cmService).setServiceEntryPoint(point + "services/cmService");
    }    
    
    
}