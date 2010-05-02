package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class ContextChangeMessage extends CmWindow {

    public ContextChangeMessage(String lesson) {
        setStyleName("context-change-message");
        setHeading("Current Topic");
        setModal(true);
        setSize(300, 180);

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

        Button close = new Button("Close");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                
                InmhItemData itemData = PrescriptionCmGuiDefinition.__instance._guiWidget.registeredResources.get(0).getItems().get(0);
                
                if(CmMainPanel.__lastInstance._mainContent.getItemCount() == 0)
                   CmMainPanel.__lastInstance._mainContent.showResource(itemData);   
                
                setVisible(false);
                close();
            }
        });
        addButton(close);
        setVisible(true);
    }
}
