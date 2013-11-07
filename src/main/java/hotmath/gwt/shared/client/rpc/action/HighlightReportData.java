package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction.ReportType;

public class HighlightReportData implements Response {
    
	int uid;
    String data;
    String name;
    String label;
    
    int quizzesTaken;
    
    int groupCount;
    int schoolCount;
    int studentCount;
    int dbCount;
    
    int activeCount;
    int activeTime;
    int assignmentAverage;
    int assignmentCount;
    int lessonCount;
    int lessonsViewed;
    int loginCount;
    int quizzesPassed;
    int quizAverage;
    int quizCount;
    int firstTimeCorrectPercent;
    
    int videosViewed;
    int gamesViewed;
    int activitiesViewed;
    int flashCardsViewed;

    CmList<Integer> uidList;

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
            case ACTIVE_TIME:
            	this.activeTime = value[0];
            	break;
            case FIRST_TIME_CORRECT:
            	this.firstTimeCorrectPercent = value[0];
            	break;
            case ASSIGNMENTS:
            	assignmentCount = value[0];
            	assignmentAverage = value[1];
            	break;
            case CCSS_COVERAGE:
            	this.studentCount = value[0];
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
            case MOST_CCSS_COVERAGE:
            	assignmentCount = value[0];
            	lessonCount = value[1];
            	quizCount = value[2];
                dbCount = assignmentCount + lessonCount + quizCount;
            	break;
            case SCHOOL_COMPARE:
            case NATIONWIDE_COMPARE:
            case COMPARE_PERFORMANCE:
            case FAILED_QUIZZES:
            case LOGINS_PER_WEEK:
    		case CCSS_COVERAGE_CHART:
    		case CCSS_STRAND_NOT_COVERED:
            	// not implemented
            	break;
    		default:
	    		break;
        }
    }    

    /** For group report */
    public HighlightReportData(Integer uid, String name, String data) {
        this.uid = uid;
        this.name = name;
        this.data = data;
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

    public int getAssignmentAverage() {
		return assignmentAverage;
	}

	public void setAssignmentAverage(int assignmentAverage) {
		this.assignmentAverage = assignmentAverage;
	}

	public int getAssignmentCount() {
		return assignmentCount;
	}

	public void setAssignmentCount(int assignmentCount) {
		this.assignmentCount = assignmentCount;
	}

	public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public int getLessonCount() {
		return lessonCount;
	}

	public void setLessonCount(int lessonCount) {
		this.lessonCount = lessonCount;
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

    public int getQuizCount() {
		return quizCount;
	}

	public void setQuizCount(int quizCount) {
		this.quizCount = quizCount;
	}

	public int getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(int activeTime) {
		this.activeTime = activeTime;
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

    public CmList<Integer> getUidList() {
		return uidList;
	}

	public void setUidList(CmList<Integer> uidList) {
		this.uidList = uidList;
	}

	public int getQuizzesTaken() {
        return quizzesTaken;
    }

    public void setQuizzesTaken(int quizzesTaken) {
        this.quizzesTaken = quizzesTaken;
    }
}
