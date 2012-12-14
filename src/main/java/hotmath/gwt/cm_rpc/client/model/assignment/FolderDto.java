package hotmath.gwt.cm_rpc.client.model.assignment;


import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class FolderDto extends BaseDto {

    private List<BaseDto> children = new ArrayList<BaseDto>();

    public FolderDto(){}
    
    public FolderDto(Integer id, String name) {
        super(id, name);
    }

    public List<BaseDto> getChildren() {
        return children;
    }

    public void setChildren(List<BaseDto> children) {
        this.children.clear();
        this.children.addAll(children);
        
        // set parent node for all children
        for(BaseDto d: children) {
            d.setParent(this);
        }
    }

    public void addChild(BaseDto child) {
        getChildren().add(child);
    }
}
