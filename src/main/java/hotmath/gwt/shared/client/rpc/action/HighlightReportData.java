package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;

public class HighlightReportData implements Response {
    
    int uid;
    String data;
    String name;
    String label;
    
    int quizzesTaken;
    
    int groupCount;
    int schoolCount;
    int dbCount;
    
    int activeCount;
    int loginCount;
    int lessonsViewed;
    int quizzesPassed;
    
    int videosViewed;
    int gamesViewed;
    int activitiesViewed;
    int flashCardsViewed;
    
    CmList<String> columnLabels = new CmArrayList<String>();
    

    public HighlightReportData(){}
    
    /** For group report */
    public HighlightReportData(Integer uid, String name, String data) {
        this.uid = uid;
        this.name = name;
        this.data = data;
    }
    
    /** For standard report with quizzes viewed */
    public HighlightReportData(Integer uid, String name, String data, int quizzesTaken) {
        this(uid,name,data);
        this.quizzesTaken = quizzesTaken;
    }
    
    /** for group performance */
    public HighlightReportData(String name, int activeCount, int loginCount,int lessonsViewed,int quizzesPassed) {
        this.name = name;
        this.activeCount = activeCount;
        this.loginCount = loginCount;
        this.lessonsViewed = lessonsViewed;
        this.quizzesPassed = quizzesPassed;
    }
    
    
    /** for group compare 
     *       vv.videos_viewed,
       gv.games_viewed,
       av.activities_viewed,
       fc.flash_cards_viewed
       
     * */
    public HighlightReportData(String name, int activeCount, int videosViewed, int gamesViewed, int activitiesViewed, int flashCardsViewed ) {
        this.name = name;
        this.activeCount = activeCount;
        this.videosViewed = videosViewed;
        this.gamesViewed = gamesViewed;
        this.activitiesViewed = activitiesViewed;
        this.flashCardsViewed = flashCardsViewed;
    }    
    
    
    public HighlightReportData(String name) {
        this.name = name;
    }
    
    
    public CmList<String> getColumnLabels() {
        return columnLabels;
    }

    public void setColumnLabels(CmList<String> columnLabels) {
        this.columnLabels = columnLabels;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getVideosViewed() {
        return videosViewed;
    }

    public void setVideosViewed(int videosViewed) {
        this.videosViewed = videosViewed;
    }

    public int getGamesViewed() {
        return gamesViewed;
    }

    public void setGamesViewed(int gamesViewed) {
        this.gamesViewed = gamesViewed;
    }

    public int getActivitiesViewed() {
        return activitiesViewed;
    }

    public void setActivitiesViewed(int activitiesViewed) {
        this.activitiesViewed = activitiesViewed;
    }

    public int getFlashCardsViewed() {
        return flashCardsViewed;
    }

    public void setFlashCardsViewed(int flashCardsViewed) {
        this.flashCardsViewed = flashCardsViewed;
    }

    public int getQuizzesTaken() {
        return quizzesTaken;
    }

    public void setQuizzesTaken(int quizzesTaken) {
        this.quizzesTaken = quizzesTaken;
    }
}
