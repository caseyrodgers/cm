package hotmath.gwt.cm_activity.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ResultsPanel extends DockPanel {
    
    

    interface MyUiBinder extends UiBinder<Widget, ResultsPanel> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    
    
    @UiField FlowPanel wrapperPanel;
    public ResultsPanel() {
        add(uiBinder.createAndBindUi(this), DockPanel.CENTER);
        
        FlowPanel buttonBar = new FlowPanel();
        buttonBar.add(new Button("Try Again",new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_INITIALIZE));
            }
        }));
        add(buttonBar,DockPanel.SOUTH);
    }
    
    public void setScore(UserScore score) {
        wrapperPanel.clear();
        wrapperPanel.add(new HTML("Your Score"));
    }
}
