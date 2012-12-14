package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.ui.HelpWindow;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;



public class ContextChangeMessage extends GWindow {

    public ContextChangeMessage(String lesson) {
        super(false);
        
        VerticalLayoutContainer vertCont = new VerticalLayoutContainer();
        
        addStyleName("context-change-message");
        setHeadingText("Current Topic");
        setModal(true);
        setPixelSize(330, 150);

        SimpleContainer sc1 = new SimpleContainer();
        String html = "<p>Your topic for review and practice is now:</p>" + "<b>" + lesson + "</b>";
        sc1.setWidget(new HTML(html));

        vertCont.add(sc1);
        
        SimpleContainer sc2 = new SimpleContainer();
        sc2.addStyleName("suggest-div");

        html = "<ul>" + 
               "<li>Use the Help button for feedback and progress</li> " + 
               "<li>Check for new Flash Cards and Games</li> " + 
               "<li>Use our whiteboard to work out the problems</li>" + "</ul>";

        sc2.setWidget(new HTML(html));

        vertCont.add(sc2);
        
        setWidget(vertCont);
        
        TextButton help = new TextButton("Help", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new HelpWindow().setVisible(true);
            }
        });
        addButton(help);
        
        TextButton close = new TextButton("Begin Lesson", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                close();
            }
        });
        addButton(close);

        
        addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                PrescriptionCmGuiDefinition.showHelpPanel();                
                setVisible(false);
        	}
        });
        
        setVisible(true);
    }
}
