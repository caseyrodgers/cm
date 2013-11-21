package hotmath.gwt.cm_rpc.client.model;

public enum WebLinkType {
    VIDEO("Video"), GAME("Game"), ACTIVITY("Activity"),OTHER("Other");
    private String label;

    private WebLinkType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
