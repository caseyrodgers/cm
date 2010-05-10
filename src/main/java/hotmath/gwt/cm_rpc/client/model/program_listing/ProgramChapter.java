package hotmath.gwt.cm_rpc.client.model.program_listing;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProgramChapter implements IsSerializable{
    String name;
    int number;
    
    List<ProgramSection> sections = new ArrayList<ProgramSection>();
    
    public ProgramChapter() {
    }
    
    public ProgramChapter(String name, int number) {
        this.name = name;
        this.number = number;
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

    public List<ProgramSection> getSections() {
        return sections;
    }

    public void setSections(List<ProgramSection> lessons) {
        this.sections = lessons;
    }
    
}
