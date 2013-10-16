package hotmath.gwt.cm_rpc.client.model.program_listing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProgramSubject implements CmTreeNode,IsSerializable{
    String name;
    String label;
    boolean isSelected;
    CmTreeNode parent;
    
    int testDefId;

    List<String> gradeLevels;
    Map<String,String> labelMap = new HashMap<String,String>();
    
    List<ProgramChapter> chapters = new ArrayList<ProgramChapter>();
    
    public ProgramSubject() {
        
        /** TODO: look up from table 
         * 
         */
        labelMap.put("Pre-Alg", "Pre-Algebra");
        labelMap.put("Alg 1", "Algebra 1");
        labelMap.put("Alg 2", "Algebra 2");
        labelMap.put("Geom", "Geometry");
        labelMap.put("Ess", "Essentials");
        labelMap.put("ElemAlg", "Elementary Algebra");
    }
    
    public ProgramSubject(String name, List<String> gradeLevels) {
        super();
        this.name = name;
        this.gradeLevels = gradeLevels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLabel(String label) {
		this.label = label;
	}

    @Override
    public String getLabel() {
    	if (label != null) return label;
    	return (labelMap.get(name) != null)?labelMap.get(name) : name;
    }

	public int getTestDefId() {
		return testDefId;
	}

	public void setTestDefId(int testDefId) {
		this.testDefId = testDefId;
	}

	public List<String> getGradeLevels() {
        return gradeLevels;
    }

    public void setGradeLevels(List<String> gradeLevels) {
        this.gradeLevels = gradeLevels;
    }

	public List<ProgramChapter> getChapters() {
		return chapters;
	}

	public void setChapters(List<ProgramChapter> chapters) {
		this.chapters = chapters;
	}

	public void setParent(CmTreeNode parent) {
		this.parent = parent;
	}

    public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public CmTreeNode getParent() {
		return parent;
	}

	@Override
    public int getLevel() {
        return ProgramListing.LEVEL_SUBJ;
    }
}
