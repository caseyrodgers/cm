package hotmath.gwt.cm_rpc.client.model.program_listing;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProgramChapter implements CmTreeNode, IsSerializable{
    String name;
    int number;
    CmTreeNode parent;
    String label;
    boolean isSelected;
    
    List<ProgramSection> sections = new ArrayList<ProgramSection>();
    
    public ProgramChapter() {
    }
    
    public ProgramChapter(String name, int number) {
        this.name = name;
        this.number = number;
        this.label = buildLabel();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.label = buildLabel();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        this.label = buildLabel();
    }

    public void setParent(CmTreeNode parent) {
    	this.parent = parent;
    }

    public List<ProgramSection> getSections() {
        return sections;
    }

    public void setSections(List<ProgramSection> lessons) {
        this.sections = lessons;
    }

    public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
    public String getLabel() {
        return label;
    }

    @Override
    public int getLevel() {
        return ProgramListing.LEVEL_CHAP;
    }

	@Override
	public CmTreeNode getParent() {
		return parent;
	}
	
	private String buildLabel() {
		if (number > 0) {
		    return "Ch " + number + ": " + ((name != null)?name:"");
		}
		else {
			return (name != null)?name:"";
		}
	}
}
