package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.SubMenuItem;
import hotmath.gwt.cm_tools.client.CatchupMathTools;

import java.util.ArrayList;
import java.util.List;

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
import com.google.gwt.user.client.Cookies;


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
    List<InmhItemData> resouresToRegister=new ArrayList<InmhItemData>();
    public ResourceMenuButton(final PrescriptionSessionDataResource resource) {
        super(resource.getLabel());
        this.initialValue = getText();
        this.resource = resource;            
        addStyleName("resource-button");
        setWidth(185);
        
        
        if(resource.getSubMenuItems().size() > 0) {
        	setMenu(createNewSubResourceMenu(resource));
        }
        else if(resource.getItems().size() == 0) {
           setEnabled(false);
        }
        else if(resource.getItems().size() > 1) {
            setMenu(createNewResourceMenu(resource));
        }
        else {
        	resouresToRegister.addAll(resource.getItems());
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
        PrescriptionCmGuiDefinition._registeredResources.put(resource.getType(),resouresToRegister);
        
        
        checkCompletion();
    }
    
    
    /** subtract the menu length from the menu.
     * 
     * This is to deal with GXT adding extra length
     * to buttons with menu.
     * 
     */
    public void setMenu(Menu menu) {
        super.setMenu(menu);
        
        setWidth(175);
    }
    
    
    /** Check to see if this resource has been completed, if
     * so then indicate appropriately.
     */
    public void checkCompletion() {
        updateCheckMarks();
    }

    /** Update all the check marks to on/off
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
            if(getMenu() != null) {
                MenuItem mi = (MenuItem)getMenu().getItem(i);
                if(mi instanceof CheckMenuItem) {
                    ((CheckMenuItem)mi).setChecked( id.isViewed() );
                }
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
            
            item.setHideOnClick(true);
            
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
                    
                    if(isResourceAvailable(resource)) {
                        CmHistoryManager.loadResourceIntoHistory(resource.getType(),ordinalPosition.toString());
                    }
                }
            });
        }
        
        if(resource.getType().equals("practice") && isComplete) { 
           indicateCompletion();
        }
        
        resouresToRegister.addAll(resource.getItems());
        
        return menu;
    }
    
    /** Proivide a method to have submenus.  If a submenu
     *  has a null title, then show the first item as the 
     *  a normal MenuItem without a submenu.
     *  
     * @param resource
     * @return
     */
    private Menu createNewSubResourceMenu(final PrescriptionSessionDataResource resource) {
        fixupResourceItems(resource);
        
        Menu menu = new Menu();
        setText(resource.getLabel());
        for (final SubMenuItem sid : resource.getSubMenuItems()) {
            /** if only one item in submenu, then do not add submenu
             * 
             */
            if(sid.getTitle() == null) {  
            	/** single item, no submenu */
            	final InmhItemData id = sid.getItemData().get(0);
            	resouresToRegister.add(id);
            	final MenuItem citem = new MenuItem(sid.getItemData().get(0).getTitle());
            	citem.addSelectionListener(new SelectionListener<MenuEvent>() {
                    public void componentSelected(MenuEvent ce) {
                    	resourceWasClicked(id);
                    }
                });
            	menu.add(citem);
            }
            else {
                MenuItem subMi = new MenuItem(sid.getTitle());
                menu.add(subMi);

            	if(sid.getChildren().size() > 0) {
            		subMi.setSubMenu(buildSubMenu(sid));
            	}
            	else {
            		subMi.setSubMenu(buildMenu(sid.getItemData()));
            	}
            }
        }
        return menu;
    }     

    private Menu buildSubMenu(SubMenuItem sid) {
    	Menu menu = new Menu();
    	for(SubMenuItem smi: sid.getChildren()) {
    		
    		if(smi.getTitle() == null) {  
            	/** single item, no submenu */
            	final InmhItemData id = smi.getItemData().get(0);
            	resouresToRegister.add(id);
            	final MenuItem citem = new MenuItem(smi.getItemData().get(0).getTitle());
            	citem.addSelectionListener(new SelectionListener<MenuEvent>() {
                    public void componentSelected(MenuEvent ce) {
                    	resourceWasClicked(id);
                    }
                });
            	menu.add(citem);
    		}
    		else {
	        	MenuItem item = new MenuItem(smi.getTitle());
	        	//menu.add(item);
	        	item.setSubMenu(buildMenu(smi.getItemData()));
	        	
	        	menu.add(item);
    		}
    	}
    	return menu;
    }
    

    private Menu buildMenu(List<InmhItemData> list) {
    	/** add submenu */
        Menu subMenu = new Menu();
        for(final InmhItemData id: list) {
        	resouresToRegister.add(id);
        	final MenuItem citem = new MenuItem(id.getTitle());
        	citem.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                	resourceWasClicked(id);
                }
            });
        	subMenu.add(citem);
        }
        return subMenu;
    	
    }
    private void resourceWasClicked(InmhItemData id) {
    	Integer pos=0;
    	for(InmhItemData idI: resouresToRegister) {
    		if(idI.getFile().equals(id.getFile())) {
		        if(isResourceAvailable(resource)) {
		            CmHistoryManager.loadResourceIntoHistory(resource.getType(),pos.toString());
		            return;
		        }
    		}
    		pos++;
    	}
    }
    
    private int getOrdinalPos(String name, List<InmhItemData> data) {
    	int cnt=0;
    	for(InmhItemData sid: data) {
    		if(sid.getFile().equals(name)) {
    			return cnt;
    		}
    		cnt++;
        }
    	return -1;
    }

    /** implement limits on resources.
     * 
     *  if limitGames is true, then only allow one game to be viewed per lesson.
     *  
     * @param resource
     * @return
     */
    private boolean isResourceAvailable(PrescriptionSessionDataResource resource) {
        if(UserInfo.getInstance().isLimitGames() && resource.getType().indexOf("activity") > -1) {
            /** if user has already viewed a game on this lesson, disallow */
            String lesson = PrescriptionCmGuiDefinition.__instance.context.getPrescriptionData().getCurrSession().getTopic();
            String cookie = Cookies.getCookie("cm_lesson_viewed");
            if(cookie != null && cookie.equals(lesson)) {
                CatchupMathTools.showAlert("Games Are Limited", "Please advance to the next lesson or quiz before playing another game");
                return false;
            }
            else {
                Cookies.setCookie("cm_lesson_viewed",lesson);
            }
        }
        return true;
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


class EmptyListener extends SelectionListener<ButtonEvent> {
    
    PrescriptionSessionDataResource resource;
    public EmptyListener(PrescriptionSessionDataResource resource) {
        this.resource = resource;
    }

    @Override
    public void componentSelected(ButtonEvent ce) {
        CatchupMathTools.showAlert("There are no " + resource.getLabel() + " available for the current lesson."); 
    }
}