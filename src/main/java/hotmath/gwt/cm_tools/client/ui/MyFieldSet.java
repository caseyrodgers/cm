package hotmath.gwt.cm_tools.client.ui;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldSet;

/** Profiles a vertical panel to hold fields
 * 
 * @author casey
 *
 */
public class MyFieldSet extends FieldSet {
    VerticalLayoutContainer vertPanel = new VerticalLayoutContainer();
    
    public MyFieldSet(String headingText) {
        this(headingText, 0);
    }
    public MyFieldSet(String headingText, int width) {
        setHeadingText(headingText);
        setWidget(vertPanel);
        if(width > 0) {
            setWidth(width);
        }
    }
    
    /** Add a widget to the vertical panel
     * 
     * @param child
     */
    public void addThing(IsWidget child) {
        vertPanel.add(child);
    }
    
    /** Clear the main vertical panel
     * 
     */
    public void clearThings() {
        vertPanel.clear();
    }
    
    @Override
    public Widget getItemByItemId(String itemId) {
        return vertPanel.getItemByItemId(itemId);
    }
    
    @Override
    public void clear() {
        vertPanel.clear();
    }
}

