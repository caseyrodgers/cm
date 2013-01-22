package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class TrendingData implements Response {
    
    String lessonName;
    Integer countAssigned;
    int number;
    
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

    public String getLabel() {
    	return lessonName;
    }

    public Integer getCountAssigned() {
        return countAssigned;
    }
    public void setCountAssigned(Integer countAssigned) {
        this.countAssigned = countAssigned;
    }

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
