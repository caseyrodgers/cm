package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

public class StandardWindowForCCSS extends GWindow {
    
    public StandardWindowForCCSS(String title, String desc) {
        super(true);
        setModal(true);
        
        setWidth(400);
        setHeight(200);
        setMaximizable(true);
        setResizable(true);
      
        setHeadingHtml("CCSS Definition: " + title);
        
        String html = "<p style='padding: 10px;'>" + desc + "</p>";
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.setScrollMode(ScrollMode.AUTO);
        flow.add(new HTML(html));
        setWidget(flow);
        setVisible(true);
    }

}
