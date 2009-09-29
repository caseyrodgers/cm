package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.data.PrescriptionSessionDataResource;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;


/** Create a button with an optional attached menu
 *  to display Cm resources where the proper placement is calculated
 *  dynamically and positioned correctly in the available space
 *  
 *  
 * @author casey
 *
 */
class ResourceMenuButton extends Button {
    
    PrescriptionSessionDataResource resource;
    
    String initialValue;
    public ResourceMenuButton(final PrescriptionSessionDataResource resource) {
        super(resource.getLabel());
        this.initialValue = getText();
        this.resource = resource;            
        addStyleName("resource-button");
        setWidth(185);
        
        if(resource.getItems().size() > 1) {
            setMenu(createNewResourceMenu(resource));
        }
        else {
            addSelectionListener(new SelectionListener<ButtonEvent>() {
                 public void componentSelected(ButtonEvent ce) {
                     CmHistoryManager.loadResourceIntoHistory(resource.getType(),"0");
                 };
            });
        }
        

        /** Setup listener to update menu's isViewed state.
         * 
         * @TODO: need a MVC pattern for menu items
         */
        addListener(Events.BeforeSelect, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                if(getMenu() == null)
                    return;
              
                checkCompletion();
            }
        });
        
        /** Register resource items for this type.  This allows
         * the history mechanism to locate the proper resource to show.
         * 
         */
        PrescriptionCmGuiDefinition._registeredResources.put(resource.getType(),resource.getItems());
        
        
        checkCompletion();
    }
    
    
    /** Check to see if this resource has been completed, if
     * so then indicate appropriately.
     */
    public void checkCompletion() {
        updateCheckMarks();
    }

    /** Update all the checkmarks to on/off
     * 
     */
    public void updateCheckMarks() {
        
        if(!resource.getType().equals("practice"))
            return;
        
        
        boolean isComplete=true;
        int viewed=0;
        // make sure each MenuItem is show correct state
        for(int i=0,t=resource.getItems().size();i<t;i++) {
            InmhItemData id = resource.getItems().get(i);
            if(!id.isViewed())
                isComplete=false;
            else 
                viewed++;
            
            MenuItem mi = (MenuItem)getMenu().getItem(i);
            if(mi instanceof CheckMenuItem) {
                ((CheckMenuItem)mi).setChecked( id.isViewed() );
            }
        }   
        
        if(viewed == resource.getItems().size())
            setText(initialValue);
        else
            setText(initialValue + " (" + viewed + "/" + resource.getItems().size() + ")");
        
        if(isComplete) {
            indicateCompletion();
        }
    }
    
    
    /** Provide any last minute modification to the list items
     *  Such as assigning unique titles, as with the the cmextra
     *  
     *  @TODO: perhaps, assign a title when setting up .inmh_link file
     * @param resource
     */
    private void fixupResourceItems(PrescriptionSessionDataResource resource) {
        
        if(resource.getType().equals("cmextra")) {
            // create sequenced titles
            int cnt=0;
            for(InmhItemData id: resource.getItems()) {
                id.setTitle("Extra Problem " + (++cnt));
            }
        }
    }
    
    private Menu createNewResourceMenu(final PrescriptionSessionDataResource resource) {

        
        fixupResourceItems(resource);

        
        /** all resources are viewed by default
         * 
         */
        boolean isComplete=true;
        
        Menu menu = new Menu();

        menu.setToolTip(resource.getDescription());

        for (final InmhItemData id : resource.getItems()) {
            /** complete only if all items are viewed
             *  
             */
            if(id.isViewed() == false)
                isComplete = false;
            
            MenuItem item = null;
            if(resource.getType().equals("practice")) {
                final CheckMenuItem citem = new CheckMenuItem(id.getTitle());
                citem.setChecked(id.isViewed(), true);
                item = citem;
                item.addListener(Events.CheckChange, new Listener<BaseEvent>() {
                     @Override
                    public void handleEvent(BaseEvent be) {
                         citem.setChecked(id.isViewed(),true);
                    }
                });
            }
            else {
                item = new MenuItem(id.getTitle());
            }
            
            item.setHideOnClick(false);
            
            menu.add(item);
            item.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    
                    /** Remove any existing resource viewers 
                     * 
                     * @TODO: This needs to be centralized  (throw a CLOSE event?)
                     */
                    Integer ordinalPosition=-1;
                    for(int i=0,t=resource.getItems().size();i<t;i++) {
                        if(resource.getItems().get(i).getFile().equals(id.getFile())) {
                            ordinalPosition=i;
                            break;
                        }
                    }
                    
                    CmHistoryManager.loadResourceIntoHistory(resource.getType(),ordinalPosition.toString());
                }
            });
        }
        
        if(resource.getType().equals("practice") && isComplete) { 
           indicateCompletion();
        }
        
        return menu;
    } 
    
    /** Make button indicate that this resource is complete
     * 
     */
    public void indicateCompletion() {
        setIconStyle("resource-menu-button-complete-icon");
        VerticalPanel vp = (VerticalPanel)getParent();
        if(vp != null) {
           vp.layout(true);
        }
    }
}