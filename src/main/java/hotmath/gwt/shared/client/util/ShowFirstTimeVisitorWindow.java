package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Frame;

public class ShowFirstTimeVisitorWindow extends CmWindow {

    public ShowFirstTimeVisitorWindow() {
        setStyleName("show-first-time-visitor-window");
        setSize(440,300);
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
       setLayout(new FitLayout());
       setHeading("Welcome to Catchup Math");
       Frame f = new Frame("/gwt-resources/first-time-visitor.html");
       add(f);
    }
}
