package hotmath.gwt.cm_mobile3.client.ui;

import hotmath.gwt.cm_mobile_shared.client.ui.TouchAnchor;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedEvent;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedHandler;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class AboutButtonIndicator extends TouchAnchor {
    
    public AboutButtonIndicator() {
        getElement().setInnerHTML("<img src='/gwt-resources/images/mobile/icon-info.png'/>");
        addStyleName("about-button");
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // CmRpcCore.EVENT_BUS.fireEvent(new ShowOptionsPanelEvent());
                new AboutDialog().showCentered();
            }
        });
        
        CmRpcCore.EVENT_BUS.addHandler(AssignmentsUpdatedEvent.TYPE, new AssignmentsUpdatedHandler() {
            @Override
            public void assignmentsUpdated(AssignmentUserInfo info) {
                if(info == null) {
                    stopRotatingImage();
                }
                else {
                    if(info.isChanged() || info.getUnreadMessageCount() > 0) {
                        startRotatingImage();
                    }
                    else {
                        stopRotatingImage();
                    }
                }
            }
        });
        
    }
    
    protected void startRotatingImage() {
        getElement().setInnerHTML("<img src='/gwt-resources/images/mobile/icon-info-changed.gif'/>");
    }


    private void stopRotatingImage() {
        getElement().setInnerHTML("<img src='/gwt-resources/images/mobile/icon-info.png'/>");
    }
}
