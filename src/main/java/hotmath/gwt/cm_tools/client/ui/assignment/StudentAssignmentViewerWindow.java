package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;

public class StudentAssignmentViewerWindow extends GWindow {
    
    
    CallbackOnComplete onComplete;
    public StudentAssignmentViewerWindow(final CallbackOnComplete onComplete) {
        super(true);
        
        setHeadingHtml("Student Assignment Manager");
        setPixelSize(800, 600);
        this.onComplete = onComplete;
        
        setCollapsible(true);
        
        add(createUi());
        setMaximizable(true);
        setVisible(true);
    }

    BorderLayoutContainer _mainContainer;
    private Widget createUi() {
        return new StudentAssignmentViewerPanel();
    }

}
