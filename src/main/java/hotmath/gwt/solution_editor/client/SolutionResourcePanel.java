package hotmath.gwt.solution_editor.client;

import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class SolutionResourcePanel extends LayoutContainer {
    
    SolutionResource resource;
    public SolutionResourcePanel(SolutionResource sr) {
        resource = sr;
        
        addStyleName("solution-resource-panel");
        
        String html = "<h2>" + sr.getFile() + "</h2>";
        if(sr.getUrlPath() != null) {
            html += "<img src='" + sr.getUrlPath() + "?rand=" + System.currentTimeMillis() + "'/>";
        }
        else if(sr.getDisplay() != null) {
            html += sr.getDisplay();
        }
        add(new Html(html));
    }
    
    public SolutionResource getResource() {
        return resource;
    }
}
