package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAssessmentReportAction;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataAction;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.core.client.GWT;

/**
 * Provide a window to display trending data
 * 
 * @author casey
 * 
 */
public class HighlightsDataWindow extends CmWindow {
    
    static HighlightsDataWindow __instance;
    
    Integer adminId;

    TabPanel _tabPanel;
    public HighlightsDataWindow(Integer adminId) {
        this.adminId = adminId;
        
        __instance = this;
        
        setHeading("Statical Highlights");
        setWidth(600);
        setHeight(500);
        
        _tabPanel = new TabPanel();

        setLayout(new FillLayout());
        
        _tabPanel.add(new HighlightImplGreatestEffort());
        _tabPanel.add(new HighlightImplLeastEffort());
        _tabPanel.add(new HighlightImplMostGamesPlayed());
        _tabPanel.add(new HighlightImplMostQuizzesPassed());       
        _tabPanel.add(new HighlightImplHighestAverageQuizScores());
        _tabPanel.add(new HighlightImplMostQuizzesFailed());        
        _tabPanel.add(new HighlightImplMostFailuresLatestQuiz());
        _tabPanel.setAnimScroll(true);
        _tabPanel.setTabScroll(true);
        add(_tabPanel);
        
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));

        getHeader().addTool(new Button("Refresh", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                loadTrendDataAsync();
            }
        }));

        getHeader().addTool(new Button("Print Report", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                reportButton();
            }
        }));
        
        
        /**
         * turn on after data retrieved
         * 
         */
        setVisible(true);
    }
    
    private void resetGui() {
        removeAll();
        setLayout(new FillLayout());
    }

    private void reportButton() {
        GWT.runAsync(new CmRunAsyncCallback() {

            @Override
            public void onSuccess() {
            	GeneratePdfAssessmentReportAction action = new GeneratePdfAssessmentReportAction(adminId,StudentGridPanel.instance._pageAction);
            	action.setFilterMap(StudentGridPanel.instance._pageAction.getFilterMap());
                new PdfWindow(adminId, "Catchup Math Group Assessment Report", action);
            }
        });

    }

    CmAdminTrendingDataI _trendingData;

    private void loadTrendDataAsync() {
        new RetryAction<CmAdminTrendingDataI>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetAdminTrendingDataAction action = null; // new GetAdminTrendingDataAction(onlyActiveOrFullHistory(), adminId, StudentGridPanel.instance._pageAction);
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }
            
            public void oncapture(CmAdminTrendingDataI trendingData) {
                _trendingData = trendingData;

                CmBusyManager.setBusy(false);
                
                resetGui();
                setVisible(true);
                layout(true);
            }
        }.register();
    }

    static {
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_STUDENT_GRID_FILTERED) {
                if(__instance != null && __instance.isVisible()) {
                    __instance.loadTrendDataAsync();
                }
            }
        }});
    }
}



class HighlightImplBase extends TabItem {
    HighlightImplBase(String name) {
        super(name);
    }
}

class HighlightImplGreatestEffort extends HighlightImplBase {
    public HighlightImplGreatestEffort() {
        super("Greatest Effort");
        add(new Label(getText()));
    }
}

class HighlightImplLeastEffort extends HighlightImplBase {
    public HighlightImplLeastEffort() {
        super("Least Effort");
        add(new Label(getText()));
    }
}

class HighlightImplMostGamesPlayed extends HighlightImplBase {
    public HighlightImplMostGamesPlayed() {
        super("Most Games Played");
        add(new Label(getText()));
    }
}

class HighlightImplMostQuizzesPassed extends HighlightImplBase {
    public HighlightImplMostQuizzesPassed() {
        super("Most Quizzes Passed");
        add(new Label(getText()));
    }
}
class HighlightImplHighestAverageQuizScores extends HighlightImplBase {
    public HighlightImplHighestAverageQuizScores() {
        super("Highest Average Quiz Scores");
        add(new Label(getText()));
    }
}

class HighlightImplMostQuizzesFailed extends HighlightImplBase {
    public HighlightImplMostQuizzesFailed() {
        super("Most Quizzes Failed");
        add(new Label(getText()));
    }
}

class HighlightImplMostFailuresLatestQuiz extends HighlightImplBase {
    public HighlightImplMostFailuresLatestQuiz() {
        super("Most Failures of Latest Quiz");
        add(new Label(getText()));
    }
}
