package hotmath.gwt.cm.client.ui;

import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.Widget;

public class CmMainPanel extends LayoutContainer {

    public static CmMainPanel __lastInstance;

    public ResourceContainer _mainContent;

    // @TODO: setup event/listener to manager
    // state change
    public TopicSelector _topicSelector;
    public CmGuiDefinition cmGuiDef;
    // west panel is static to allow access
    // to the title.
    public ContentPanel _westPanel;

    /**
     * Main Catchup Math application area.
     * 
     * Including a west component with a title and a main center area with user
     * settable background image.
     * 
     * Each form/context is responsible for calling updateGui(context) when the
     * context is full ready Meaning, any async data has been fetched and
     * parsed.
     * 
     * Each time the context is changed, updateGui(context) must be called to
     * keep the UI in sync.
     * 
     * @param cmModel
     *            The GUI model to use
     */
    public CmMainPanel(final CmGuiDefinition cmGuiDef) {

        __lastInstance = this;

        setScrollMode(Scroll.AUTO);
        this.cmGuiDef = cmGuiDef;
        setLayout(new BorderLayout());
        _mainContent = new ResourceContainer();
        _westPanel = new ContentPanel();
        _westPanel.setStyleName("main-panel-west");
        _westPanel.setLayout(new BorderLayout());
        
        _westPanel.getHeader().addStyleName("cm-main-panel-header");

        addTools();

        // cp.add(new ResourceAccord(sData),new
        // BorderLayoutData(LayoutRegion.CENTER));
        _westPanel.setBorders(false);
        BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 250);
        westData.setMargins(new Margins(5, 0, 5, 5));
        westData.setSplit(true);
        westData.setCollapsible(false);
        _westPanel.add(cmGuiDef.getWestWidget(), new BorderLayoutData(LayoutRegion.CENTER));
        add(_westPanel, westData);

        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(5, 0, 5, 5));
        centerData.setSplit(true);
        add(_mainContent, centerData);

        Widget w = cmGuiDef.getCenterWidget();
        if (w != null)
            _mainContent.add(w);
    }


    
    /** Request controls from Context
     * 
     */

    private void addTools() {
        List<Component> comps = this.cmGuiDef.getContext().getTools();

        // Add the special prev/next buttons to horizontal panel
        LayoutContainer lc = new LayoutContainer();
        lc.setStyleName("cm-main-panel-button-panel");

        for(Component c:comps) {
            lc.add(c);
        }
        _westPanel.add(lc, new BorderLayoutData(LayoutRegion.NORTH, 50));
    }

    /**
     * Remove any resource
     * 
     */
    public void removeResource() {
        CmMainPanel.__lastInstance._mainContent.removeAll();
    }

}
