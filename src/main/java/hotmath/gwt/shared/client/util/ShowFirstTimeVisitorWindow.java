package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.Frame;

public class ShowFirstTimeVisitorWindow extends CmWindow {

    UserInfo user;
    public ShowFirstTimeVisitorWindow(UserInfo user) {
        
        this.user = user;
        setStyleName("show-first-time-visitor-window");
        setSize(440,350);
        setModal(true);
       
        
        Button close = new Button("Close");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        addButton(close);
        getButtonBar().setAlignment(HorizontalAlignment.RIGHT);
        
        drawGui();
        
        
        EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_MODAL_WINDOW_OPEN));
        
        setVisible(true);
    }
    
    
    protected void drawGui() {
       setLayout(new BorderLayout());
       setHeading("Welcome to Catchup Math");
       Frame f = new Frame("/gwt-resources/first-time-visitor.html");
       
       add(f, new BorderLayoutData(LayoutRegion.CENTER));
       add(new ReportCardInfoPanel(user),new BorderLayoutData(LayoutRegion.SOUTH,50));       
    }
}
