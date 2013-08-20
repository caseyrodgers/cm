package hotmath.gwt.cm_tools.client.ui;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Provides standard button sizing
 * 
 * @author casey
 * 
 */
public class StudentPanelButton extends TextButton {
    public StudentPanelButton(String name) {
        this(name, new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
            }
        });
    }

    public StudentPanelButton(String name, SelectHandler handler) {
        super(name, handler);
        addStyleName("student-grid-panel-button");
    }
}