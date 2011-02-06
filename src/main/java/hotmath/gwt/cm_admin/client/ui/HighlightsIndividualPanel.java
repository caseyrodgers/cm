package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfHighlightsReportAction;
import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Widget;


public class HighlightsIndividualPanel extends ContentPanel {

    LayoutContainer _reportOutput = new LayoutContainer();
    static HighlightsIndividualPanel __instance;
    public HighlightsIndividualPanel() {
        __instance = this;
        setHeaderVisible(false);
        
        setLayout(new BorderLayout());
        
        BorderLayoutData dData = new BorderLayoutData(LayoutRegion.WEST,200);
        dData.setSplit(true);
        dData.setCollapsible(true);
        add(createListOfAvailableReports(),dData);
        
        dData = new BorderLayoutData(LayoutRegion.CENTER);
        dData.setSplit(true);
        dData.setCollapsible(true);
        _reportOutput.setLayout(new FitLayout());
        add(_reportOutput, dData);
    }
    
    static int __lastSelectedReport=0;
    
    private native String getTemplate() /*-{ 
    return  [ 
    '<tpl for=".">', 
    '<div class="x-view-item {decorationClass}" qtip="{toolTip}">{text}</div>', 
    '</tpl>' 
    ].join(""); 
  }-*/;  
    
    
    ListView<ReportModel> _listReports = new ListView<ReportModel>();
    private LayoutContainer createListOfAvailableReports() {
        _listReports.setStore(createListStore());
        _listReports.setTemplate(getTemplate());
        
        
        List<ReportModel> list = new ArrayList<ReportModel>();
        list.add(_listReports.getStore().getAt(__lastSelectedReport));
        _listReports.getSelectionModel().setSelection(list);
        showReportOutput(list.get(0));
        
        _listReports.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ReportModel>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ReportModel> se) {
                
                /** find selected index */
                ReportModel report = _listReports.getSelectionModel().getSelectedItem();
                for(int i=0,t=_listReports.getStore().getCount();i<t;i++) {
                    ReportModel sr = _listReports.getStore().getAt(i);
                    if(sr.getText().equals(report.getText())) {
                        __lastSelectedReport = i;
                        break;
                    }
                }
                if(report != null) {
                    showReportOutput(report);
                }
            }
        });
        return new MyLayoutContainer("Available Reports", _listReports);
    }
    
    private void showReportOutput(ReportModel report) {
        
        _reportOutput.removeAll();
        _reportOutput.add(report.getReport().getWidget());
        
        layout(true);
    }

    private LayoutContainer createReportOutputPanel() {
        return new MyLayoutContainer("Report Output", new Label("Report Output"));
    }
 
    private ListStore<ReportModel> createListStore() {
        ListStore<ReportModel> s = new ListStore<ReportModel>();
        s.add(new ReportModel(new HighlightImplGreatestEffort()));
        s.add(new ReportModel(new HighlightImplLeastEffort()));
        s.add(new ReportModel(new HighlightImplMostGamesPlayed()));
        s.add(new ReportModel(new HighlightImplMostQuizzesPassed()));
        s.add(new ReportModel(new HighlightImplHighestAverageQuizScores()));
        s.add(new ReportModel(new HighlightImplMostFailuresLatestQuiz()));
        s.add(new ReportModel(new HighlightImplZeroLogins()));
        // s.add(new ReportModel(new HighlightImplComparePerformance()));
        
        /** mark these two reports as not using the summary page selection */
        ReportModel rm = new ReportModel(new HighlightImplGroupProgress());
        rm.set("decorationClass", "highlight-report-uses-summary");
        s.add(rm);
        
        rm = new ReportModel(new HighlightImplGroupUsage());
        rm.set("decorationClass", "highlight-report-uses-summary");
        s.add(rm);
        return s;
    }
    
    private void printCurrentReport() {
        if(HighlightImplDetailsPanelBase.__lastReportData == null) {
            InfoPopupBox.display("Highlight Report", "Nothing to print");
        }
        else {
            String reportName = _listReports.getStore().getAt(__lastSelectedReport).getText();
            
            HighlightReportLayout reportLayout = _listReports.getStore().getAt(__lastSelectedReport).getReport().getReportLayout();
                
            GeneratePdfHighlightsReportAction action = new GeneratePdfHighlightsReportAction(StudentGridPanel.instance._cmAdminMdl.getId(),reportName,reportLayout,StudentGridPanel.instance._pageAction);
            action.setFilterMap(StudentGridPanel.instance._pageAction.getFilterMap());
            action.setModels(HighlightImplDetailsPanelBase.__lastReportData);
            new PdfWindow(0, "Catchup Math Highlight Report", action);
        }
    }
    
    
    static {
        hotmath.gwt.shared.client.eventbus.EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_PRINT_HIGHLIGHT_REPORT) {
                    __instance.printCurrentReport();    
                }
            }
        });
    }
}




class ReportModel extends BaseModelData {
    HighlightImplBase report;
    public ReportModel(HighlightImplBase report) {
        this.report = report;
        set("text", report.getText());
        
        set("toolTip", report.getToolTip());
    }
    
    /** The report name 
     * 
     * @return
     */
    public String getText() {
        return get("text");
    }
    
    public HighlightImplBase getReport() {
        return report;
    }
    
}

class MyLayoutContainer extends LayoutContainer {
    
    public MyLayoutContainer(String title, Widget container) {
        setLayout(new BorderLayout());
        
        String html = "<h1>" + title + "</h1>";
        Html ohtml = new Html(html);
        add(ohtml, new BorderLayoutData(LayoutRegion.NORTH,35));
        add(container, new BorderLayoutData(LayoutRegion.CENTER));
        
        
        /** add info box describing type of reports */
        Html _infoLabel = new Html("<div class='report-legend'>" +
                                   "    <div>" +
                                   "        <div>&nbsp;</div>" +
                                   "        Uses Summary page selection" +
                                   "    </div>" +
                                   "    <div class='no-selection'>" +
                                   "        <div>&nbsp;</div>" +
                                   "        Applies to all Groups" +
                                   "    </div>" +
                                   "</div>");
        
        _infoLabel.addStyleName("info-label");
        add(_infoLabel, new BorderLayoutData(LayoutRegion.SOUTH,45));        
    }
}

abstract class HighlightImplBase  {
    String name;
    String toolTip;
    Widget _widget;
    
    HighlightImplBase(String name, String toolTip) {
        this.name = name;
        this.toolTip = toolTip;
    }
    
    public String getToolTip() {
        return toolTip;
    }
    
    public String getText() {
        return this.name;
    }
    
    public Widget getWidget() {
        if(_widget == null)
            _widget = prepareWidget();
        return _widget;
    }
    
    /** one time call to draw the gui
     * 
     * @return
     */
    abstract Widget prepareWidget();
    
    
    /** return the column labels used
     *  to print the report.
     *  
     * @return
     */
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:75", "Lessons Viewed:25"};
        HighlightReportLayout rl = new HighlightReportLayout(cols, getReportValues());
        return rl;
    }
    
    protected String[][] getReportValues() {
        return new String[0][0];
    }
}

class HighlightImplGreatestEffort extends HighlightImplBase {
    HighlightImplGreatestEffortDetailsPanel panel = new HighlightImplGreatestEffortDetailsPanel(this);
    public HighlightImplGreatestEffort() {
        super("Greatest Effort", "Displays students in order of most lessons viewed (excluding those who have viewed zero lessons)");
    }
    
    public Widget prepareWidget() {
        return panel;
    }
    
    @Override
    protected String[][] getReportValues() {
        return panel.getReportValues();
    }
}

class HighlightImplLeastEffort extends HighlightImplBase {
    HighlightImplLeastEffortDetailsPanel panel = new HighlightImplLeastEffortDetailsPanel(this);
    
    public HighlightImplLeastEffort() {
        super("Least Effort", "Displays students in order of least lessons viewed (excluding those who have viewed zero lessons)");
    }
    public Widget prepareWidget() {
        return panel;
    }
    @Override
    protected String[][] getReportValues() {
        return panel.getReportValues();
    }
}

class HighlightImplMostGamesPlayed extends HighlightImplBase {
    HighlightImplMostGamesPlayedDetailsPanel panel = new HighlightImplMostGamesPlayedDetailsPanel(this);
    public HighlightImplMostGamesPlayed() {
        super("Most Games Played","Displays students in order of most games played (excluding those who have played no games)");
    }
    public Widget prepareWidget() {
        return panel;
    }
    
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:50", "Games Played:25", "Quizzes Taken:25"};
        return new HighlightReportLayout(cols,panel.getReportValues());
    }    
}

class HighlightImplMostQuizzesPassed extends HighlightImplBase {
    HighlightImplMostQuizzesPassedDetailsPanel panel = new HighlightImplMostQuizzesPassedDetailsPanel(this);
    
    public HighlightImplMostQuizzesPassed() {
        super("Most Quizzes Passed","Displays students in order of most quizzes passed (which correlates to most Sections completed as Auto-Enroll quizzes are not counted)");
    }
    public Widget prepareWidget() {
        return panel;
    }
    
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:75", "Quizzes Passed:25"};
        return new HighlightReportLayout(cols, panel.getReportValues());
    }    
}

class HighlightImplHighestAverageQuizScores extends HighlightImplBase {
    HighlightImplHighestAverageQuizScoresDetailsPanel panel = new HighlightImplHighestAverageQuizScoresDetailsPanel(this);
    
    public HighlightImplHighestAverageQuizScores() {
        super("Highest Average Quiz Score","Displays students in order of their average quiz score, including passed and failed quizzes, but excluding Auto-Enroll quizzes.");
    }
    public Widget prepareWidget() {
        return panel;
    }
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:50", "Average:20", "Quizzes Taken:20"};
        return new HighlightReportLayout(cols, panel.getReportValues());
    }    
}

class HighlightImplMostQuizzesFailed extends HighlightImplBase {
    HighlightImplMostQuizzesFailedDetailsPanel panel = new HighlightImplMostQuizzesFailedDetailsPanel(this);
    public HighlightImplMostQuizzesFailed() {
        super("Most Quizzes Failed","Displays students in order of most quizzes failed, a possible indicator of students in a program above their level");
    }
    public Widget prepareWidget() {
        return panel;
    }
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:75", "Failures:25"};
        return new HighlightReportLayout(cols, panel.getReportValues());
    }    
}

class HighlightImplMostFailuresLatestQuiz extends HighlightImplBase {
    HighlightImplMostFailuresLatestQuizDetailsPanel panel = new HighlightImplMostFailuresLatestQuizDetailsPanel(this);
    public HighlightImplMostFailuresLatestQuiz() {
        super("Most Failures of Current Quiz","Displays students who have failed their current quiz at least once, in rank order - a possible indicator of needing teacher assistance");
    }
    public Widget prepareWidget() {
        return panel; 
    }
    @Override
    protected String[][] getReportValues() {
        return panel.getReportValues();
    }
}

class HighlightImplLoginsWeek extends HighlightImplBase {
    public HighlightImplLoginsWeek() {
        super("Average Logins Week","Average number of logins per week");
    }
    public Widget prepareWidget() {
        return new HighlightImplLoginsWeekDetailsPanel(this);
    }
}

class HighlightImplGroupProgress extends HighlightImplBase {
    HighlightImplGroupProgressDetailsPanel panel = new HighlightImplGroupProgressDetailsPanel(this);
    public HighlightImplGroupProgress() {
        super("Group Progress","Shows number of active students (logged in at least once), total logins, lessons viewed, and quizzes passed for each group and entire school. Groups with no active students are omitted.");
    }
    public Widget prepareWidget() {
        return panel;
    }
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:50", "Active:12", "Logins:12", "Lessons:12", "Passed:12"};
        return new HighlightReportLayout(cols, panel.getReportValues());
    }    
}

class HighlightImplComparePerformance extends HighlightImplBase {
    HighlightImplComparePerformanceDetailsPanel panel = new HighlightImplComparePerformanceDetailsPanel(this);
    public HighlightImplComparePerformance() {
        super("Compare Performance","Various metrics for comparing group performance with the entire school and the total community of Catchup Math students nationwide");
    }
    public Widget prepareWidget() {
        return panel;
    }
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:50", "Active:12", "Videos:12", "Games:12", "Activities:12"};
        return new HighlightReportLayout(cols,panel.getReportValues());
    }    
}

class HighlightImplZeroLogins extends HighlightImplBase {
    HighlightImplZeroLoginsDetailsPanel panel = new HighlightImplZeroLoginsDetailsPanel(this);
    public HighlightImplZeroLogins() {
        super("Zero Logins","List students who did not log in during the date range.");
    }
    public Widget prepareWidget() {
        return panel;
    }
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:100"};
        return new HighlightReportLayout(cols, panel.getReportValues());
    }    
}

class HighlightImplGroupUsage extends HighlightImplBase {
    HighlightImplGroupUsageDetailsPanel panel = new HighlightImplGroupUsageDetailsPanel(this);
    public HighlightImplGroupUsage() {
        super("Group Usage","Shows the usage of optional learning resources for groups with at least one active student.");
    }
    public Widget prepareWidget() {
        return panel;
    }
}
