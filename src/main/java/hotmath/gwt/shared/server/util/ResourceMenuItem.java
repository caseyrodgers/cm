package hotmath.gwt.shared.server.util;

import java.util.ArrayList;
import java.util.List;

public class ResourceMenuItem {
    String label;
    String file;
    
    List<ResourceMenuItem> subMenuItems = new ArrayList<ResourceMenuItem>();
    
    public ResourceMenuItem() {
        System.out.println("Test");
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<ResourceMenuItem> getSubMenuItems() {
        return subMenuItems;
    }

    public void setSubMenuItems(List<ResourceMenuItem> subMenuItems) {
        this.subMenuItems = subMenuItems;
    }
    
    public void addSubMenuItem(ResourceMenuItem subItem) {
        this.subMenuItems.add(subItem);
    }
}
