package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.ui.EndOfProgramWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class EndOfProgramPanel extends CenterLayoutContainer {
    public EndOfProgramPanel() {
        
        ContentPanel lc = new ContentPanel();
        lc.setHeadingText("Program Completed");
        lc.setWidth(400);
        lc.setHeight(200);
        addStyleName(UserInfo.getInstance().getBackgroundStyle());
        
        if(CmCore.isDebug() == true) {
            EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_LOGOUT));
        }
        lc.add(new HTML(EndOfProgramWindow.msg));
        lc.addButton(new TextButton("OK",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                Window.Location.assign("/");            
            }
        }));
        setWidget(lc);
    }
}
