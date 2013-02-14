package hotmath.gwt.cm_admin.client.ui.highlights;


public class HighlightsReportModelGroupReport extends HighlightsReportModel {
    public HighlightsReportModelGroupReport(String name, int activeCount, int loginCount, int lessonsViewed, int quizzesPassed) {
        set("name", name);
        set("activeCount", activeCount);
        set("loginCount", loginCount);
        set("lessonsViewed", lessonsViewed);
        set("quizzesPassed", quizzesPassed);
    }
    
    
    @Override
    public String[] getReportLabels() {
        String labels[] = {"Name", "Active", "Logins", "Lessons", "Quizzes"};
        return labels;
    }
}
