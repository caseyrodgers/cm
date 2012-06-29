package hotmath.gwt.cm_tools.client.model;



public class GradeBookModel extends BaseModel {
    public GradeBookModel() {
    }
    
    public GradeBookModel(Integer uid, String userName, String lessonName,Integer countEntries) {
        set("uid", uid);
        set("userName", userName);
        set("lessonName", lessonName);
        set("countEntries", countEntries);
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
}

