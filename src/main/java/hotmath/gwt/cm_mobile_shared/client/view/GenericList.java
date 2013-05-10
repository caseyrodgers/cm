package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/** Generic Item list with count
 * 
 * @author casey
 *
 */
public class GenericList extends FlowPanel {
    GenericContainerTag listItems = new GenericContainerTag("ul");

    Label countLabel;
    public GenericList() {
        listItems.addStyleName("touch");
        listItems.addStyleName("large");
        
        countLabel = new Label("Count: " );
        countLabel.getElement().setAttribute("style",  "float: right;margin-right: 15px;font-size: .8em;color: gray;");
        listItems.getElement().setAttribute("style",  "clear: both;");
        
        getElement().setAttribute("style",  "margin: 10px");
        
        add(countLabel);
        add(listItems);
    }
    
    public GenericContainerTag getList() {
        return listItems;
    }

    public void updateCount() {
        countLabel.getElement().setInnerHTML("count: " + listItems.getWidgetCount());
    }
    
}
