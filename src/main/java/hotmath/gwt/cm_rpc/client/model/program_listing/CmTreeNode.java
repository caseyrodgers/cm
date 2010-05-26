package hotmath.gwt.cm_rpc.client.model.program_listing;

public interface CmTreeNode {
    int getLevel();
    String getLabel();
    CmTreeNode getParent();
}
