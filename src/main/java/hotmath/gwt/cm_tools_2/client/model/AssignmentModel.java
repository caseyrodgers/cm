package hotmath.gwt.cm_tools_2.client.model;

import hotmath.gwt.cm_tools.client.model.BaseModel;

@Deprecated
public class AssignmentModel extends BaseModel {

	public AssignmentModel() {
    }
    
    public AssignmentModel(String lessonName, Integer countEntries, Integer numCorrect,
    		String percentCorrect, Integer numAnswered, Integer cpId, String cpName) {
        set("lessonName", lessonName);
        set("countEntries", countEntries);
        set("numCorrect", numCorrect);
        set("percentCorrect", percentCorrect);
        set("numAnswered", numAnswered);
        set("cpId", cpId);
        set("cpName", cpName);
        set("numNotAnswered", countEntries - numAnswered);
        set("numWrong", numAnswered - numCorrect);
    }
    
    public String getLessonName() {
        return get("lessonName");
    }
    
    public Integer getCountEntries() {
        return get("countEntries");
    }

    public Integer getNumCorrect() {
    	return get("numCorrect");
    }

    public String getPercentCorrect() {
    	return get("percentCorrect");
    }

    public void setPercentCorrect(String percentCorrect) {
    	set("percentCorrect", percentCorrect);
    }

    public Integer getNumAnswered() {
    	return get("numAnswered");
    }

    public Integer getCpId() {
    	return get("cpId");
    }

    public String getCpName() {
    	return get("cpName");
    }

    public void setName(String name) {
    	set("name", name);
    }

    public String getName() {
    	return get("name");
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (obj instanceof AssignmentModel) {
			AssignmentModel asgMdl = (AssignmentModel) obj;
			return (this.getCpId() == asgMdl.getCpId() &&
					this.getLessonName().equals(asgMdl.getLessonName()));
		}
		return false;
	}

}

