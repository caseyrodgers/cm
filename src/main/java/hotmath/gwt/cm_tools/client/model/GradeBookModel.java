package hotmath.gwt.cm_tools.client.model;



public class GradeBookModel extends BaseModel {
    public GradeBookModel() {
    }
    
    public GradeBookModel(Integer uid, String userName, String lessonName, Integer countEntries, Integer numCorrect,
    		Integer percentCorrect, Integer cpId) {
        set("uid", uid);
        set("userName", userName);
        set("lessonName", lessonName);
        set("countEntries", countEntries);
        set("numCorrect", numCorrect);
        set("percentCorrect", percentCorrect);
        set("cpId", cpId);
    }
    
    public Integer getUid() {
        return get("uid");
    }
    
    public String getUserName() {
        return get("userName");
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

    public Integer getPercentCorrect() {
    	return get("percentCorrect");
    }

    public Integer getCpId() {
    	return get("cpId");
    }
}

