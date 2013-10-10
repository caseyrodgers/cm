package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.SubMenuItem;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;


/** Create a button with an optional attached menu
 *  to display CM resources where the proper placement is calculated
 *  dynamically and positioned correctly in the available space
 *  
 *  
 * @author casey
 *
 */
class ResourceMenuButton extends TextButton {
    PrescriptionSessionDataResource resource;
    
    String initialValue;
    List<InmhItemData> resouresToRegister=new ArrayList<InmhItemData>();
    public ResourceMenuButton(final PrescriptionSessionDataResource resource) {
        super(resource.getLabel());
        this.initialValue = getText();
        this.resource = resource;            
        //addStyleName("resource-button");
        setWidth(175);
        
        
        if(resource.getSubMenuItems().size() > 0) {
        	setMenu(createNewSubResourceMenu(resource));
        }
        else if(resource.getItems().size() == 0) {
           setEnabled(false);
        }
        else if(resource.getItems().size() > 1) {
            if (isResourceAvailable(resource)) {
            	Menu menu = createNewResourceMenu(resource);
                setMenu(menu);
            }
            else {
            	this.disable();
                //this.setToolTip("Sorry, " + resource.getLabel() + " are not available.");            	
            }
        }
        else {
        	resouresToRegister.addAll(resource.getItems());
        	addSelectHandler(new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    CmHistoryManager.loadResourceIntoHistory(resource.getType().label(),"0");                }
            });
        }


        /** Setup listener to update menu's isViewed state.
         * 
         * @TODO: need a MVC pattern for menu items
         */
        
        addHandler(new BeforeSelectionHandler<String>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<String> event) {
                
                Window.alert("Before Select ResourceMenuButton");
                
                if(getMenu() == null)
                    return;
              
                checkCompletion();
            }
        },BeforeSelectionEvent.getType());
        
        /** Register resource items for this type.  This allows
         * the history mechanism to locate the proper resource to show.
         * 
         */
        PrescriptionCmGuiDefinition._registeredResources.put(resource.getType().label(),resouresToRegister);
        
        
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
    public boolean checkCompletion() {
        return updateCheckMarks();
    }

    /** Update all the check marks to on/off
     * 
     */
    public boolean updateCheckMarks() {
        if(!resource.getType().equals(CmResourceType.PRACTICE) && !resource.getType().equals(CmResourceType.CMEXTRA))
            return false;
        
        
        boolean isComplete=true;
        int viewed=0;
        // make sure each MenuItem shows correct state
        for(int i=0,t=resource.getItems().size();i<t;i++) {
            // for each RPP
            InmhItemData rpp = resource.getItems().get(i);
            if(!rpp.isViewed())
                isComplete=false;
            else 
                viewed++;
            if(getMenu() != null) {
                MenuItem mi = (MenuItem)getMenu().getWidget(i);
                if(mi instanceof CheckMenuItem) {
                    ((CheckMenuItem)mi).setChecked( rpp.isViewed() );
                }
            }
        }   
        
        if(resource.getItems().size() > 0) {
            if(viewed == resource.getItems().size())
                setText(initialValue);
            else
                setText(initialValue + " (" + viewed + "/" + resource.getItems().size() + ")");
            
            if(isComplete) {
                indicateCompletion();
            }
        }
        return isComplete;
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


    	Menu menu = new Menu();

    	/** all resources are viewed by default
    	 * 
    	 */
    	boolean isComplete=true;

    	fixupResourceItems(resource);

    	for (final InmhItemData id : resource.getItems()) {
    		/** complete only if all items are viewed
    		 *  
    		 */
    		if(id.isViewed() == false)
    			isComplete = false;

    		MenuItem item = null;
    		if(resource.getType().equals(CmResourceType.PRACTICE) || resource.getType().equals(CmResourceType.CMEXTRA)) {
    			final CheckMenuItem citem = new CheckMenuItem(id.getTitle());
    			citem.setChecked(id.isViewed(), true);
    			item = citem;
    			

    			item.addHandler(new CheckChangeHandler<CheckMenuItem>() {

    		        @Override
    		        public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
    		            citem.setChecked(id.isViewed(),true);
    		        }
    		      },CheckChangeEvent.getType());

    			
    		}
    		else {
    			item = new MyMenuItem(id.getTitle());
    		}            

    		item.setHideOnClick(true);

    		menu.add(item);
    		item.addSelectionHandler(new SelectionHandler<Item>() {
                @Override
                public void onSelection(SelectionEvent<Item> event) {
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
                    CmHistoryManager.loadResourceIntoHistory(resource.getType().label(),ordinalPosition.toString());
                }
            });
    	}

    	if(resource.getType().equals(CmResourceType.PRACTICE) && isComplete) { 
    		indicateCompletion();
    	}

    	resouresToRegister.addAll(resource.getItems());

    	return menu;
    }
    
    /** Provide a method to have submenus.  If a submenu
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
            	final MenuItem citem = new MyMenuItem(sid.getItemData().get(0).getTitle());
            	citem.addSelectionHandler(new SelectionHandler<Item>() {
            	    @Override
            	    public void onSelection(SelectionEvent<Item> event) {
            	        resourceWasClicked(id);            	        
            	    }
                });
            	menu.add(citem);
            }
            else {
                MenuItem subMi = new MyMenuItem(sid.getTitle());
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
            	final MenuItem citem = new MyMenuItem(smi.getItemData().get(0).getTitle());
            	citem.addSelectionHandler(new SelectionHandler<Item>() {
            	    @Override
            	    public void onSelection(SelectionEvent<Item> event) {
            	        resourceWasClicked(id);            	        
            	    }
                });
            	menu.add(citem);
    		}
    		else {
	        	MenuItem item = new MyMenuItem(smi.getTitle());

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
        	final MenuItem citem = new MyMenuItem(id.getTitle());
        	citem.addSelectionHandler(new SelectionHandler<Item>() {
        	    @Override
        	    public void onSelection(SelectionEvent<Item> event) {
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
		            CmHistoryManager.loadResourceIntoHistory(resource.getType().label(),pos.toString());
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
     *  - if limitGames is true, then games are not available
     *  
     * @param resource
     * @return
     */
    private boolean isResourceAvailable(PrescriptionSessionDataResource resource) {
    	return (! UserInfo.getInstance().isLimitGames() || resource.getType().label().indexOf("activity") < 0);
    }
    
    
    /** Make button indicate that this resource is complete
     * 
     */
    private void indicateCompletion() {
        if(getText().indexOf("complete") == -1) {
            setText(getText() + " (complete)");
        }
        //setIconStyle("resource-menu-button-complete-icon");
//        VerticalLayoutContainer vp = (VerticalLayoutContainer)getParent();
//        if(vp != null) {
//           vp.forceLayout();
//        }
    }
}



class MyMenuItem extends MenuItem {
    public MyMenuItem(String text) {
        super();
        setHTML(SafeHtmlUtils.htmlEscapeAllowEntities(text));
    }
}