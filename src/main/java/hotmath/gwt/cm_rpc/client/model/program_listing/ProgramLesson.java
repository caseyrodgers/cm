package hotmath.gwt.cm_rpc.client.model.program_listing;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProgramLesson implements CmTreeNode, IsSerializable {
    String name;
    String file;
    CmTreeNode parent;

    public ProgramLesson() {
        
    }
    public ProgramLesson(String name, String file) {
        this.name = name;
        this.file = file;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public void setParent(CmTreeNode parent) {
    	this.parent = parent;
    }

    @Override
    public String getLabel() {
        return name;
    }
    @Override
    public int getLevel() {
        return ProgramListing.LEVEL_LESS;
    }

    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }
    @Override
	public CmTreeNode getParent() {
		return parent;
	}
    
    
    @Override
    public String toString() {
        return "ProgramLesson [name=" + name + ", file=" + file + ", parent=" + parent + "]";
    }    
}
