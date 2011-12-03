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

    String label;
    String type;
    boolean isSelected;

    public ProgramType() {
    }

    public ProgramType(String type,String label) {
        this();
        this.type = type;
        this.label = label;
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
        return ProgramListing.LEVEL_TYPE;
    }

	@Override
	public CmTreeNode getParent() {
		return null;
	}

}