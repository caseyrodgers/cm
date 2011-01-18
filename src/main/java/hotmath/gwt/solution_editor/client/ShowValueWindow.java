package hotmath.gwt.solution_editor.client;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Timer;


/** Display value in window fully selecting as an
 * aid in copying to clipboard.
 * 
 * @author casey
 *
 */
public class ShowValueWindow extends Window {
    public ShowValueWindow(String title, String text) {
        
        setLayout(new FitLayout());
        setSize(600,400);
        setScrollMode(Scroll.AUTO);
        setModal(true);
        setHeading(title);
        
        final TextArea ta = new TextArea();
        ta.setValue(text);
        ta.setReadOnly(true);
        add(ta);
        
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));
        
        
        setVisible(true);

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