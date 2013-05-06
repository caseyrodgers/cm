package hotmath.gwt.cm_mobile3.client.ui;

import hotmath.gwt.cm_mobile_shared.client.ui.TouchAnchor;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedEvent;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedHandler;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;

public class AboutButtonIndicator extends TouchAnchor {
    
    public AboutButtonIndicator() {
        getElement().setInnerHTML("<img src='/gwt-resources/images/mobile/icon-info.png'/>");
        addStyleName("about-dialog");
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
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
                    if(!info.isChanged()) {
                        stopRotatingImage();
                    }
                    else {
                        startRotatingImage();
                    }
                }
            }

        });
        
    }
    
    Timer _timer;
    protected void startRotatingImage() {
        stopRotatingImage();
        _timer = new Timer() {
            @Override
            public void run() {
                Element e = getElement();
                if(e.getInnerHTML().indexOf("-changed") > -1) {
                    getElement().setInnerHTML("<img src='/gwt-resources/images/mobile/icon-info.png'/>");
                }
                else {
                    getElement().setInnerHTML("<img src='/gwt-resources/images/mobile/icon-info-changed.png'/>");
                }
            }
        };
        _timer.scheduleRepeating(2000);
    }


    private void stopRotatingImage() {
        if(_timer != null) {
            _timer.cancel();
            _timer = null;
            getElement().setInnerHTML("<img src='/gwt-resources/images/mobile/icon-info.png'/>");
        }
    }
    
}
