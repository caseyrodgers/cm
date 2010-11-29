package hotmath.gwt.shared.server.util;

import java.util.ArrayList;
import java.util.List;

public class ResourceMenu {

    String type;
    String description;
    
    List<ResourceMenuItem> menuItems = new ArrayList<ResourceMenuItem>();
    
    public ResourceMenu() {
        
    }

    public List<ResourceMenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<ResourceMenuItem> menuItems) {
        this.menuItems = menuItems;
    }
    
    public void addMenuItem(ResourceMenuItem rmi) {
        this.menuItems.add(rmi);
    }

    public void addMenuItem(ResourceSubMenu rmi) {
        this.menuItems.add(rmi);
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
