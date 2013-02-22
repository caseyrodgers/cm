package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction.ReportType;

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
    int quizAverage;
    int timeOnTask;
    int firstTimeCorrectPercent;
    
    int videosViewed;
    int gamesViewed;
    int activitiesViewed;
    int flashCardsViewed;
    
    CmList<String> columnLabels = new CmArrayList<String>();

    public HighlightReportData(){}
    
    public HighlightReportData(Integer uid, String name, ReportType reportType, int... value) {
        this.uid = uid;
        this.name = name;
        switch(reportType) {
            case GREATEST_EFFORT:
            case LEAST_EFFORT:
            	lessonsViewed = value[0];
        	    break;
            case MOST_GAMES:
            	gamesViewed = value[0];
            	quizzesTaken = value[1];
            	break;
            case MOST_QUIZZES_PASSED:
            	quizzesPassed = value[0];
            	quizzesTaken = value[1];
            	break;
            case AVERAGE_QUIZ_SCORES:
            	quizAverage = value[0];
            	quizzesTaken = value[1];
            	break;
            case FAILED_CURRENT_QUIZZES:
            	quizzesTaken = value[0];
        	    break;
            case ZERO_LOGINS:
            	// nothing to set
            	break;
            case TIME_ON_TASK:
            	this.timeOnTask = value[0];
            	break;
            case FIRST_TIME_CORRECT:
            	this.firstTimeCorrectPercent = value[0];
            	break;
            case GROUP_PERFORMANCE:
            	activeCount = value[0];
            	loginCount = value[1];
            	lessonsViewed = value[2];
            	quizzesPassed = value[3];
            	break;
            case GROUP_USAGE:
            	activeCount = value[0];
            	videosViewed = value[1];
            	gamesViewed = value[2];
            	activitiesViewed = value[3];
            	flashCardsViewed = value[4];
            	break;

            case SCHOOL_COMPARE:
            case NATIONWIDE_COMPARE:
            case COMPARE_PERFORMANCE:
            case FAILED_QUIZZES:
            case LOGINS_PER_WEEK:
            	// not implemented
            	break;
        }
    }    
    
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
    	//return 99;
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

    public int getQuizAverage() {
		return quizAverage;
	}

	public void setQuizAverage(int quizAverage) {
		this.quizAverage = quizAverage;
	}

	public int getTimeOnTask() {
		return timeOnTask;
	}

	public void setTimeOnTask(int timeOnTask) {
		this.timeOnTask = timeOnTask;
	}

	public int getFirstTimeCorrectPercent() {
		return firstTimeCorrectPercent;
	}

	public void setFirstTimeCorrectPercent(int firstTimeCorrectPercent) {
		this.firstTimeCorrectPercent = firstTimeCorrectPercent;
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
