package hotmath.gwt.cm_rpc.client.model.program_listing;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProgramSection implements CmTreeNode, IsSerializable{
    String name;
    int testDefId;
    int number;
    CmTreeNode parent;
    
    List<ProgramLesson> lessons = new ArrayList<ProgramLesson>();
    
    public ProgramSection() {
    }
    
    public ProgramSection(String name, int testDefId, int number) {
        this.name = name;
        this.testDefId = testDefId;
        this.number = number;
    }

    public int getTestDefId() {
        return testDefId;
    }

    public void setTestDefId(int testDefId) {
        this.testDefId = testDefId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setParent(CmTreeNode parent) {
    	this.parent = parent;
    }

    public List<ProgramLesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<ProgramLesson> lessons) {
        this.lessons = lessons;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public int getLevel() {
        return ProgramListing.LEVEL_SECT;
    }

	@Override
	public CmTreeNode getParent() {
		return parent;
	}
    
}
