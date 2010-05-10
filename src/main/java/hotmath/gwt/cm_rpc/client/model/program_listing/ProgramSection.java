package hotmath.gwt.cm_rpc.client.model.program_listing;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProgramSection implements IsSerializable{
    String name;
    int number;
    
    List<ProgramLesson> lessons = new ArrayList<ProgramLesson>();
    
    public ProgramSection() {
    }
    
    public ProgramSection(String name, int number) {
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

    public List<ProgramLesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<ProgramLesson> lessons) {
        this.lessons = lessons;
    }
    
}
