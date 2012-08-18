package hotmath.gwt.cm_rpc.client.model.assignment;

import java.io.Serializable;
import java.util.List;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;

public class BaseDto implements Serializable, TreeStore.TreeNode<BaseDto> {

    private Integer id;
    private String name;
    private BaseDto parent;
    
    protected BaseDto() {
      
    }
    
    public BaseDto getParent() {
        return parent;
    }

    public void setParent(BaseDto parent) {
        this.parent = parent;
    }

    public BaseDto(Integer id, String name) {
      this.id = id;
      this.name = name;
    }

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public BaseDto getData() {
      return this;
    }

    @Override
    public List<? extends TreeNode<BaseDto>> getChildren() {
      return null;
    }
    
    @Override
    public String toString() {
      return name != null ? name : super.toString();
    }
    
    
    static int autoId;
  }