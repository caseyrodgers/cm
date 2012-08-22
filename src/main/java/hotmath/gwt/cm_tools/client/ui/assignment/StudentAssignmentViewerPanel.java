package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.info.Info;

public class StudentAssignmentViewerPanel extends ContentPanel {
    public StudentAssignmentViewerPanel(final CallbackOnComplete onComplete) {

        Button btnReturn = new Button("Return to your program");
        btnReturn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                onComplete.isComplete();
            }
        });
        addTool(btnReturn);

        setWidget(createUi());
    }

    BorderLayoutContainer _mainContainer;
    private Widget createUi() {
        
        _mainContainer = new BorderLayoutContainer();
        if(true) {
            _mainContainer.getElement().setAttribute("style", "background: black");
            _mainContainer.setCenterWidget(new Label("Test"));
            return _mainContainer;
        }
        
        
        FlowLayoutContainer header = new FlowLayoutContainer();
        header.add(new Label("Assignment"));

        BorderLayoutData bData = new BorderLayoutData();
        bData.setSize(1000);
        _mainContainer.setNorthWidget(header,bData);
        

        BorderLayoutContainer assigmentPanel = new BorderLayoutContainer();
        
        bData = new BorderLayoutData();
        bData.setSize(400);
        assigmentPanel.setWestWidget(new Label("Problem Listing"),bData);
        assigmentPanel.setCenterWidget(new Label("The Solution"));
        
        _mainContainer.setCenterWidget(assigmentPanel);
        return _mainContainer;
        
    }

    public void refreshData() {
        Info.display("Checking Assignments", "Checking server for any assignments");
    }
}
