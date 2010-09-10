package hotmath.gwt.cm_mobile_shared.client;

abstract public class ControlAction {
    String label;
    
    abstract public void doAction();
    
    public ControlAction(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
