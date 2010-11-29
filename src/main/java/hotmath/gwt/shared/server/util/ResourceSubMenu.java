package hotmath.gwt.shared.server.util;


public class ResourceSubMenu extends ResourceMenuItem {
    public ResourceSubMenu() {
    }
    
    public void addSubMenuItem(ResourceMenuItem rmi) {
        subMenuItems.add(rmi);
    }
    
    @Override
    public void setLabel(String label) {
        super.setLabel(label);
    }
}
