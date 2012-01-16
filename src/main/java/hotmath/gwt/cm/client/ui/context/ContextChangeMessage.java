package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.ui.HelpWindow;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class ContextChangeMessage extends CmWindow {

    public ContextChangeMessage(String lesson) {
        setStyleName("context-change-message");
        setHeading("Current Topic");
        setModal(true);
        setSize(330, 150);

        LayoutContainer lc = new LayoutContainer();
        String html = "<p>Your topic for review and practice is now:</p>" + "<b>" + lesson + "</b>";

        setLayout(new FitLayout());
        lc.add(new Html(html));

        LayoutContainer lca = new LayoutContainer();
        lca.addStyleName("suggest-div");

        html = "<ul>" + 
               "<li>The Help button has neat features</li> " + 
               "<li>Check for new Flash Cards and Games</li> " + 
               "<li>Use our whiteboard to work the problems</li>" + "</ul>";

        lca.add(new Html(html));

        lc.add(lca);

        add(lc);

        
        Button help = new Button("Help");
        help.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                new HelpWindow().setVisible(true);
            }
        });
        addButton(help);
        
        Button close = new Button("Begin Lesson");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        addButton(close);

        
        /** Perform operations when ever the window is dismissed
         * 
         */
        addWindowListener(new WindowListener() {
        	@Override
        	public void windowHide(WindowEvent we) {
                PrescriptionCmGuiDefinition.showHelpPanel();                
                setVisible(false);
        	}
        });
        
        setVisible(true);
    }
}
