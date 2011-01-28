package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;

import java.util.Date;

public class HighlightsGetReportAction implements Action<CmList<HighlightReportData>> {
    
    ReportType type;
    Integer adminId;
    Date from;
    Date to;
    
    GetStudentGridPageAction studentGridPageAction;

    public HighlightsGetReportAction() {
    }

    public HighlightsGetReportAction(GetStudentGridPageAction studentGridAction, ReportType type, Integer adminId, Date from, Date to) {
        this.studentGridPageAction = studentGridAction;
        this.type = type;
        this.adminId = adminId;
        this.from = from;
        this.to = to;
    }
    
    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }


    public GetStudentGridPageAction getStudentGridPageAction() {
        return studentGridPageAction;
    }

    public void setStudentGridPageAction(GetStudentGridPageAction studentGridPageAction) {
        this.studentGridPageAction = studentGridPageAction;
    }

    @Override
    public String toString() {
        return "HighlightsGetReportAction [type=" + type + ", adminId=" + adminId + ", from=" + from + ", to=" + to
                + ", _studentGridPageAction=" + studentGridPageAction + "]";
    }


    public enum ReportType{GREATEST_EFFORT,LEAST_EFFORT,MOST_GAMES,NATIONWIDE_COMPARE,SCHOOL_COMPARE,
                           MOST_QUIZZES_PASSED,AVERAGE_QUIZ_SCORES,FAILED_QUIZZES,FAILED_CURRENT_QUIZZES,
                           COMPARE_PERFORMANCE,LOGINS_PER_WEEK,GROUP_PERFORMANCE,GROUP_USAGE};
}
