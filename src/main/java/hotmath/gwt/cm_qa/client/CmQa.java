package hotmath.gwt.cm_qa.client;
import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_rpc.client.model.CategoryModel;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc.client.rpc.GetQaCategoriesAction;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;


public class CmQa implements EntryPoint {
    Viewport _mainPort = new Viewport();
    public static String __userName;
    public static String __category;
    public void onModuleLoad() {
        
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
                        setUserName();
                    }
                    else {
                        _mainPort.removeAll();
                        _mainPort.add(new Label("User must be specified"));
                        _mainPort.layout();
                    }
                }
            });
        }
        setUserName();
        RootPanel.get("main-view-port").add(_mainPort);
        readCategoriesFromServer();
    }
    
    private void setUserName() {
        userNameLabel.setText("User: " + CmQa.__userName);        
    }
    
    
    Label userNameLabel = new Label();
    ComboBox<CategoryModelGxt> _categoryCombo;
    private Component createHeader() {
        HorizontalPanel lc = new HorizontalPanel();
        lc.setSpacing(10);
        
        _categoryCombo = new ComboBox<CategoryModelGxt>();
        
        lc.add(new Html("<div style='font-weight: bold;font-size: 120%'>Catchup Math QA Items</div>"));
        lc.add(userNameLabel);        
        _categoryCombo.setStore(new ListStore<CategoryModelGxt>());
        _categoryCombo.setEditable(false);
        _categoryCombo.setEmptyText("-- Select Category --");
        _categoryCombo.setTriggerAction(TriggerAction.ALL);
        _categoryCombo.setDisplayField("category");
        _categoryCombo.addSelectionChangedListener(new SelectionChangedListener<CategoryModelGxt>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<CategoryModelGxt> se) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.CATEGORY_CHANGED, se.getSelectedItem().getCategory()));
            }
        });

        lc.add(_categoryCombo);
        
        
        return lc;
    }
    
    private void readCategoriesFromServer() {
        new RetryAction<CmList<CategoryModel>>() {
            
            @Override
            public void attempt() {
                GetQaCategoriesAction action = new GetQaCategoriesAction();
                setAction(action);
                CmQa.getCmService().execute(action, this);
            }
            
            @Override
            public void oncapture(CmList<CategoryModel> items) {
                //_categoryCombo.getStore().add(new CategoryModelGxt("all"));
                _categoryCombo.getStore().add(CategoryModelGxt.convert(items));
            }
        }.register();            
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




class CategoryModelGxt extends BaseModel {
    
    public CategoryModelGxt(String category) {
        set("category", category);
    }
    
    public String getCategory() {
        return get("category");
    }
    public static List<CategoryModelGxt> convert(CmList<CategoryModel> items) {
        
        List<CategoryModelGxt> list = new ArrayList<CategoryModelGxt>();
        for(int i=0,t=items.size();i<t;i++) {
            list.add(new CategoryModelGxt(items.get(i).getCategory()));
        }
        return list;
    }
}

