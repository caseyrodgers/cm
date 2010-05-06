package hotmath.gwt.cm_rpc.client.model.program_listing;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProgramSubject implements IsSerializable{
    String name;

    List<String> gradeLevels;
    
    List<ProgramChapter> chapters = new ArrayList<ProgramChapter>();
    
    public ProgramSubject() {
    }
    
    public ProgramSubject(String name, List<String> gradeLevels) {
        this.name = name;
        this.gradeLevels = gradeLevels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    
}
