package hotmath.gwt.cm_rpc.client.model.program_listing;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/** Defines a single type of program
 *  and lists all child data
 * 
 * @author casey
 *
 */
public class ProgramType implements CmTreeNode, IsSerializable {

    List<ProgramSubject> subjects = new ArrayList<ProgramSubject>();

    String type;

    public ProgramType() {
    }

    public ProgramType(String type) {
        this();
        this.type = type;
    }

    public List<ProgramSubject> getProgramSubjects() {
        return subjects;
    }

    public void setProgramSubjects(List<ProgramSubject> subjects) {
        this.subjects = subjects;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getLabel() {
        return type;
    }

    @Override
    public int getLevel() {
        return ProgramListing.LEVEL_TYPE;
    }
}