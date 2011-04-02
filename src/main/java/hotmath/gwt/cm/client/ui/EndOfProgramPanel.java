package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.ui.EndOfProgramWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.google.gwt.user.client.Window;

public class EndOfProgramPanel extends LayoutContainer {
    public EndOfProgramPanel() {
        
        ContentPanel lc = new ContentPanel();
        lc.setHeading("Program Completed");
        lc.setWidth(400);
        lc.setHeight(200);
        addStyleName(UserInfo.getInstance().getBackgroundStyle());
        
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_LOGOUT));
        lc.add(new Html(EndOfProgramWindow.msg));
        lc.addButton(new Button("OK",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                Window.Location.assign("/");
            }
        }));
        
        setLayout(new CenterLayout());
        add(lc);
    }
}
