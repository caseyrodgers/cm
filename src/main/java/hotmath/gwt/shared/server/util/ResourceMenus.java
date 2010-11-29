package hotmath.gwt.shared.server.util;

import java.util.ArrayList;
import java.util.List;

public class ResourceMenus {
    
    List<ResourceMenu> resourceMenus = new ArrayList<ResourceMenu>();
    

    public ResourceMenus() {
    }

    public List<ResourceMenu> getResourceMenus() {
        return resourceMenus;
    }


    public void setResourceMenus(List<ResourceMenu> resourceMenus) {
        this.resourceMenus = resourceMenus;
    }
    
    public void addResourceMenu(ResourceMenu rm) {
        this.resourceMenus.add(rm);
    }
    
    public ResourceMenu getMenuFor(String label) {
        for(ResourceMenu rm: resourceMenus) {
            if(rm.getType().equals(label))
                return rm;
        }
        return null;
    }
}
