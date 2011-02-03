package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfHighlightsReportAction;

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
        setHeading("");
        
        setLayout(new BorderLayout());
        
        BorderLayoutData dData = new BorderLayoutData(LayoutRegion.WEST,200);
        dData.setSplit(true);
        dData.setCollapsible(true);
        add(createListOfAvailableReports(),dData);
        
        dData = new BorderLayoutData(LayoutRegion.CENTER);
        dData.setSplit(true);
        dData.setCollapsible(true);

        add(_reportOutput, dData);
        
        layout();
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
        _reportOutput.setLayout(new FitLayout());
        _reportOutput.add(report.getReport().getWidget());
        _reportOutput.layout();
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
            GeneratePdfHighlightsReportAction action = new GeneratePdfHighlightsReportAction(StudentGridPanel.instance._cmAdminMdl.getId(),reportName,StudentGridPanel.instance._pageAction);
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
}

class HighlightImplGreatestEffort extends HighlightImplBase {
    public HighlightImplGreatestEffort() {
        super("Greatest Effort", "Displays students in order of most lessons viewed (excluding those who have viewed zero lessons)");
    }
    
    public Widget prepareWidget() {
        return new HighlightImplGreatestEffortDetailsPanel(this);
    }
}

class HighlightImplLeastEffort extends HighlightImplBase {
    public HighlightImplLeastEffort() {
        super("Least Effort", "Displays students in order of least lessons viewed (excluding those who have viewed zero lessons)");
    }
    public Widget prepareWidget() {
        return new HighlightImplLeastEffortDetailsPanel(this);
    }
}

class HighlightImplMostGamesPlayed extends HighlightImplBase {
    public HighlightImplMostGamesPlayed() {
        super("Most Games Played","Displays students in order of most games played (excluding those who have played no games)");
    }
    public Widget prepareWidget() {
        return new HighlightImplMostGamesPlayedDetailsPanel(this);
    }
}

class HighlightImplMostQuizzesPassed extends HighlightImplBase {
    public HighlightImplMostQuizzesPassed() {
        super("Most Quizzes Passed","Displays students in order of most quizzes passed (which correlates to most Sections completed as Auto-Enroll quizzes are not counted)");
    }
    public Widget prepareWidget() {
        return new HighlightImplMostQuizzesPassedDetailsPanel(this);
    }
}

class HighlightImplHighestAverageQuizScores extends HighlightImplBase {
    public HighlightImplHighestAverageQuizScores() {
        super("Highest Average Quiz Score","Displays students in order of their average quiz score, including passed and failed quizzes, but excluding Auto-Enroll quizzes.");
    }
    public Widget prepareWidget() {
        return new HighlightImplHighestAverageQuizScoresDetailsPanel(this);
    }
}

class HighlightImplMostQuizzesFailed extends HighlightImplBase {
    public HighlightImplMostQuizzesFailed() {
        super("Most Quizzes Failed","Displays students in order of most quizzes failed, a possible indicator of students in a program above their level");
    }
    public Widget prepareWidget() {
        return new HighlightImplMostQuizzesFailedDetailsPanel(this);
    }
}

class HighlightImplMostFailuresLatestQuiz extends HighlightImplBase {
    public HighlightImplMostFailuresLatestQuiz() {
        super("Most Failures of Current Quiz","Displays students who have failed their current quiz at least once, in rank order - a possible indicator of needing teacher assistance");
    }
    public Widget prepareWidget() {
        return new HighlightImplMostFailuresLatestQuizDetailsPanel(this);
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
    public HighlightImplGroupProgress() {
        super("Group Progress","Shows number of active students (logged in at least once), total logins, lessons viewed, and quizzes passed for each group and entire school. Groups with no active students are omitted.");
    }
    public Widget prepareWidget() {
        return new HighlightImplGroupProgressDetailsPanel(this);
    }
}

class HighlightImplComparePerformance extends HighlightImplBase {
    public HighlightImplComparePerformance() {
        super("Compare Performance","Various metrics for comparing group performance with the entire school and the total community of Catchup Math students nationwide");
    }
    public Widget prepareWidget() {
        return new HighlightImplComparePerformanceDetailsPanel(this);
    }
}

class HighlightImplZeroLogins extends HighlightImplBase {
    public HighlightImplZeroLogins() {
        super("Zero Logins","List students who did not log in during the date range.");
    }
    public Widget prepareWidget() {
        return new HighlightImplZeroLoginsDetailsPanel(this);
    }
}

class HighlightImplGroupUsage extends HighlightImplBase {
    public HighlightImplGroupUsage() {
        super("Group Usage","Shows the usage of optional learning resources for groups with at least one active student.");
    }
    public Widget prepareWidget() {
        return new HighlightImplGroupUsageDetailsPanel(this);
    }
}
