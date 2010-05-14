package hotmath.gwt.cm_rpc.client.model.program_listing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProgramSubject implements CmTreeNode,IsSerializable{
    String name;
    String label;
    
    int testDefId;

    List<String> gradeLevels;
    Map<String,String> labelMap = new HashMap<String,String>();
    
    List<ProgramChapter> chapters = new ArrayList<ProgramChapter>();
    
    public ProgramSubject() {
        labelMap.put("Pre-Alg", "Pre-Algebra");
        labelMap.put("Alg 1", "Algebra 1");
        labelMap.put("Alg 2", "Algebra 2");
        labelMap.put("Geom", "Geometry");
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

    @Override
    public String getLabel() {
        return labelMap.get(name);
    }

    @Override
    public int getLevel() {
        // TODO Auto-generated method stub
        return ProgramListing.LEVEL_SUBJ;
    }
}
