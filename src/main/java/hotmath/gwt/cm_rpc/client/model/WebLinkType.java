package hotmath.gwt.cm_rpc.client.model;

public enum WebLinkType {
    OTHER("Other"), VIDEO("Video"), GAME("Game");
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
