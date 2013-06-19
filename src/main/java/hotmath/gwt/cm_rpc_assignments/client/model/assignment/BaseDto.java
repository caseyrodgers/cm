package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import java.io.Serializable;
import java.util.List;

public class BaseDto implements Serializable {

	private static final long serialVersionUID = -5386056685165452829L;

	private Integer id;
    private String name;
    private BaseDto parent;
    private Integer level;
    
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

    public Integer getLevel() {
    	return level;
    }

    public void setLevel(Integer level) {
    	this.level = level;
    }

//    @Override
//    public BaseDto getData() {
//      return this;
//    }
//
    public List getChildren() {
      return null;
    }
    
    @Override
    public String toString() {
      return name != null ? name : super.toString();
    }
    
    
    public static int autoId;
  }