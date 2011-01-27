package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class HighlightReportData implements Response {
    
    int uid;
    String data;
    String name;
    String label;
    
    
    int groupCount;
    int schoolCount;
    int dbCount;
    
    int activeCount;
    int loginCount;
    int lessonsViewed;
    int quizzesPassed;
    int schoolQuizzesPassed;
    
    
    public HighlightReportData(){}
    
    /** For group report */
    public HighlightReportData(Integer uid, String name, String data) {
        this.uid = uid;
        this.name = name;
        this.data = data;
    }
    
    /** for group performance */
    public HighlightReportData(String name, int activeCount, int loginCount,int lessonsViewed,int quizzesPassed,int schoolQuizzesPassed) {
        this.name = name;
        this.activeCount = activeCount;
        this.loginCount = loginCount;
        this.lessonsViewed = lessonsViewed;
        this.quizzesPassed = quizzesPassed;
        this.schoolQuizzesPassed = schoolQuizzesPassed;
    }
    
    
    public HighlightReportData(String name) {
        this.name = name;
    }
    
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    public int getGroupCount() {
        return groupCount;
    }
    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }
    public int getSchoolCount() {
        return schoolCount;
    }
    public void setSchoolCount(int schoolCount) {
        this.schoolCount = schoolCount;
    }
    public int getDbCount() {
        return dbCount;
    }
    public void setDbCount(int dbCount) {
        this.dbCount = dbCount;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public int getLessonsViewed() {
        return lessonsViewed;
    }

    public void setLessonsViewed(int lessonsViewed) {
        this.lessonsViewed = lessonsViewed;
    }

    public int getQuizzesPassed() {
        return quizzesPassed;
    }

    public void setQuizzesPassed(int quizzesPassed) {
        this.quizzesPassed = quizzesPassed;
    }

    public int getSchoolQuizzesPassed() {
        return schoolQuizzesPassed;
    }

    public void setSchoolQuizzesPassed(int schoolQuizzesPassed) {
        this.schoolQuizzesPassed = schoolQuizzesPassed;
    }
}
