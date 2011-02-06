package hotmath.gwt.cm_admin.client.ui;

public class HighlightReportModelGroupReport extends HighlightReportModel {
    public HighlightReportModelGroupReport(String name, int activeCount, int loginCount, int lessonsViewed, int quizzesPassed) {
        set("name", name);
        set("activeCount", activeCount);
        set("loginCount", loginCount);
        set("lessonsViewed", lessonsViewed);
        set("quizzesPassed", quizzesPassed);
    }
    
    
    @Override
    public String[] getReportLabels() {
        // TODO Auto-generated method stub
        String labels[] = {"Name", "Active", "Logins", "Lessons", "Quizzes"};
        return labels;
    }
}
