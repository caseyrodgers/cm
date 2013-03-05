package hotmath.gwt.cm_admin.client.ui.assignment;

import com.google.gwt.user.client.ui.Label;

import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;

public class ProblemDisplayPanel extends BorderLayoutContainer {
    
    ContentPanel center;
    public ProblemDisplayPanel() {
        center = new ContentPanel();

        center.setHeadingHtml("Assignment Problem");

        setCenterWidget(center);
    }

    public void setShowProblem(String problemLabel, String pid) {
        center.setWidget(new Label("Problem: "+ pid));

        center.setHeadingHtml("Assignment Problem: " + problemLabel);
    }

}
