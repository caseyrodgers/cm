package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.Style.Direction;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;

public class ContextChangeMessage extends CmWindow {
    
    public ContextChangeMessage(String lesson) {
        setStyleName("context-change-message");
        setHeading("Current Topic");
        setModal(true);
        setSize(300,140);
        
        LayoutContainer lc = new LayoutContainer();
        String html = "<p>Your topic for review and practice is now:</p>" +
                      "<b>" + lesson + "</b>";

        setLayout(new FitLayout());
        lc.add(new Html(html));
        
        Anchor a = new Anchor("Suggestions>>");
        a.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showSuggestions();
            }
        });
        
        LayoutContainer lca = new LayoutContainer();
        lca.addStyleName("suggest-anchor");
        lca.add(a);
       
        lc.add(lca);
        add(lc);
        
        
        Button close = new Button("Close");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                setVisible(false);
                close();
            }
        });
        addButton(close);
        
        
        setVisible(true);
    }
    
    private void showSuggestions() {
        final CmWindow w = new CmWindow();
        w.setSize(300,150);
        w.setStyleName("context-change-message-suggestions");
        w.setModal(true);
        w.setHeading("Suggestions");
        
        String html = "<ul>" +
                      "<li>The Help button has neat features</li> " +
                      "<li>Check for new Flash Cards and Games</li> " +
                      "<li>Use our our whiteboard to work the problems</li>" +
                      "</ul>";
        w.add(new Html(html));
        
        Button close = new Button("Close");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                w.close();
            }
        });
        w.addButton(close);
        w.setVisible(true);
    }

}
