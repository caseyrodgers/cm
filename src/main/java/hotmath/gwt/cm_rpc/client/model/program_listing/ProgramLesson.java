package hotmath.gwt.cm_rpc.client.model.program_listing;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProgramLesson implements IsSerializable{
    String name;
    public ProgramLesson() {
        
    }
    public ProgramLesson(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
