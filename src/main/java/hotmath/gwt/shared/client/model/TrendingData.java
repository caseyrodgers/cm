package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class TrendingData implements Response {
    
    String lessonName;
    Integer countAssigned;
    
    public TrendingData() {}
    
    public TrendingData(String lessonName, int countAssigned) {
        this.lessonName = lessonName;
        this.countAssigned = countAssigned;
    }
    
    public String getLessonName() {
        return lessonName;
    }
    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }
    public Integer getCountAssigned() {
        return countAssigned;
    }
    public void setCountAssigned(Integer countAssigned) {
        this.countAssigned = countAssigned;
    }
}
