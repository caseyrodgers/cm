package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc.client.model.WebLinkModel.AvailableOn;


public class AvailableDevice {
    String device;
    private int id;
    private AvailableOn availableWhen;
    
    public AvailableDevice(String label, AvailableOn available) {
        this.availableWhen = available;
        device = label;
        if(available != null) {
            id = available.ordinal();
        }
    }
    
    public AvailableOn getAvailWhen() {
        return availableWhen;
    }

    public String getDevice() {
        return device;
    }
    
    public int getId() {
        return this.id;
    }
}
