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
    }
    
    static int __lastSelectedReport=0;
    String template = "<tpl for=\".\"><div class='x-view-item'>{text}</div></tpl>";    
    ListView<ReportModel> _listReports = new ListView<ReportModel>();
    private LayoutContainer createListOfAvailableReports() {
        _listReports.setStore(createListStore());
        _listReports.setTemplate(template);
        
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
        ListStore<ReportModel> store = new ListStore<ReportModel>();
        return new MyLayoutContainer("Report Output", new Label("Report Output"));
    }
 
    private ListStore<ReportModel> createListStore() {
        ListStore<ReportModel> s = new ListStore<ReportModel>();
        s.add(new ReportModel(new HighlightImplGreatestEffort()));
        s.add(new ReportModel(new HighlightImplLeastEffort()));
        s.add(new ReportModel(new HighlightImplMostGamesPlayed()));
        s.add(new ReportModel(new HighlightImplMostQuizzesPassed()));
        s.add(new ReportModel(new HighlightImplHighestAverageQuizScores()));
        s.add(new ReportModel(new HighlightImplMostQuizzesFailed()));
        s.add(new ReportModel(new HighlightImplMostFailuresLatestQuiz()));
        s.add(new ReportModel(new HighlightImplComparePerformance()));
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
        
        String html = "<h2>" + title + "</h2>";
        Html ohtml = new Html(html);
        add(ohtml, new BorderLayoutData(LayoutRegion.NORTH,35));
        add(container, new BorderLayoutData(LayoutRegion.CENTER));
    }
}

abstract class HighlightImplBase  {
    String name;
    Widget _widget;
    HighlightImplBase(String name) {
        this.name = name;
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
        super("Greatest Effort");
    }
    
    public Widget prepareWidget() {
        return new HighlightImplGreatestEffortDetailsPanel(this);
    }
}

class HighlightImplLeastEffort extends HighlightImplBase {
    public HighlightImplLeastEffort() {
        super("Least Effort");
    }
    public Widget prepareWidget() {
        return new HighlightImplLeastEffortDetailsPanel(this);
    }
}

class HighlightImplMostGamesPlayed extends HighlightImplBase {
    public HighlightImplMostGamesPlayed() {
        super("Most Games Played");
    }
    public Widget prepareWidget() {
        return new HighlightImplMostGamesPlayedDetailsPanel(this);
    }
}

class HighlightImplMostQuizzesPassed extends HighlightImplBase {
    public HighlightImplMostQuizzesPassed() {
        super("Most Quizzes Passed");
    }
    public Widget prepareWidget() {
        return new HighlightImplMostQuizzesPassedDetailsPanel(this);
    }
}

class HighlightImplHighestAverageQuizScores extends HighlightImplBase {
    public HighlightImplHighestAverageQuizScores() {
        super("Highest Average Quiz Scores");
    }
    public Widget prepareWidget() {
        return new HighlightImplHighestAverageQuizScoresDetailsPanel(this);
    }
}

class HighlightImplMostQuizzesFailed extends HighlightImplBase {
    public HighlightImplMostQuizzesFailed() {
        super("Most Quizzes Failed");
    }
    public Widget prepareWidget() {
        return new HighlightImplMostQuizzesFailedDetailsPanel(this);
    }
}

class HighlightImplMostFailuresLatestQuiz extends HighlightImplBase {
    public HighlightImplMostFailuresLatestQuiz() {
        super("Most Failures of Current Quiz");
    }
    public Widget prepareWidget() {
        return new HighlightImplMostFailuresLatestQuizDetailsPanel(this);
    }
    
}

class HighlightImplComparePerformance extends HighlightImplBase {
    public HighlightImplComparePerformance() {
        super("Compare Performance");
    }
    public Widget prepareWidget() {
        return new HighlightImplComparePerformanceDetailsPanel(this);
    }
}


