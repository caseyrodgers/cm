package hotmath.gwt.solution_editor.client;


import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.Timer;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;


/** Display value in window fully selecting as an
 * aid in copying to clipboard.
 * 
 * @author casey
 *
 */
public class ShowValueWindow extends GWindow {
    public ShowValueWindow(String title, String text) {
        this(title,text,true);
    }
    
    public ShowValueWindow(String title, String text, boolean selectAll) {
        super(false);
        
        setPixelSize(600,400);
        setModal(true);
        setHeadingText(title);
        setMaximizable(true);
        
        final TextArea ta = new TextArea();
        ta.setValue(text);
        ta.setReadOnly(true);
        add(ta);
        
        addButton(new TextButton("Close", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        
        
        setVisible(true);

        if(selectAll) {
            /** delay setting focus */
            new Timer() {
                @Override
                public void run() {
                    ta.focus();
                    ta.selectAll();
                }
            }.schedule(500);
        }

    }
}