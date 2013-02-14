package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.TrendingData;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfHighlightsReportAction;
import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

import java.util.ArrayList;
import java.util.List;

//import com.extjs.gxt.ui.client.Style.LayoutRegion;
//import com.extjs.gxt.ui.client.data.BaseModelData;
//import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
//import com.extjs.gxt.ui.client.event.SelectionChangedListener;
//import com.extjs.gxt.ui.client.store.ListStore;
//import com.extjs.gxt.ui.client.widget.ContentPanel;
//import com.extjs.gxt.ui.client.widget.Html;
//import com.extjs.gxt.ui.client.widget.Label;
//import com.extjs.gxt.ui.client.widget.LayoutContainer;
//import com.extjs.gxt.ui.client.widget.ListView;
//import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
//import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
//import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.ListViewCustomAppearance;

import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;

/**
 * 
 * @author bob
 *
 */

public class HighlightsListPanel extends BorderLayoutContainer {
	
    interface HighlightsDataAccess extends PropertyAccess<HighlightsReport> {
		 
		    @Path("text")
		    ValueProvider<HighlightsReport, String> name();

		    @Path("text")
		    ModelKeyProvider<HighlightsReport> key();
		  }

	private static final HighlightsDataAccess dataAccess = GWT.create(HighlightsDataAccess.class);

    static HighlightsListPanel __instance;
    BorderLayoutContainer _parent;
    BorderLayoutData _layoutData;

    public HighlightsListPanel(BorderLayoutContainer parent, BorderLayoutData layoutData) {
        __instance = this;
        _parent = parent;
        _layoutData = layoutData;

        SimpleContainer northContainer = new SimpleContainer();
        northContainer.setBorders(true);
        northContainer.setHeight("35px");

        SimpleContainer southContainer = new SimpleContainer();
        southContainer.setBorders(true);
        southContainer.setHeight("45px");

        this.setNorthWidget(northContainer);
        this.setCenterWidget(createListOfAvailableReports());
        this.setSouthWidget(southContainer);

        CmLogger.debug("setup report list");

    }
    
    static int __lastSelectedReport=0;
    
    private native String getTemplate() /*-{ 
    return  [ 
    '<tpl for=".">', 
    '<div class="x-view-item {decorationClass}" qtip="{toolTip}">{text}</div>', 
    '</tpl>' 
    ].join(""); 
  }-*/;  
    
    ListViewCustomAppearance<HighlightsReport> appearance = new ListViewCustomAppearance<HighlightsReport>("") {
 	   
		@Override
		public void renderItem(SafeHtmlBuilder builder, SafeHtml content) {
			boolean haveGroup = (content.toString().indexOf("Group") > -1);
	        if (haveGroup == true)  builder.appendHtmlConstant("<div style='color:red'>");
	        builder.append(content);
	        if (haveGroup == true) builder.appendHtmlConstant("</div>");
		}
   
      };

    // ListView<HighlightsReport, String> _listReports = new ListView<HighlightsReport, String>(createListStore(), dataAccess.name(), appearance);
    ListView<HighlightsReport, String> _listReports = new ListView<HighlightsReport, String>(createListStore(), dataAccess.name());

    private ListView<HighlightsReport, String> createListOfAvailableReports() {
        //_listReports.setStore(createListStore());
        //_listReports.setTemplate(getTemplate());
        
        _listReports.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		_listReports.getSelectionModel().addSelectionHandler(new SelectionHandler<HighlightsReport>() {
			@Override
			public void onSelection(SelectionEvent<HighlightsReport> event) {
				
				HighlightsReport hlReport = event.getSelectedItem();
				HighlightsImplBase report = hlReport.getReport();
				Widget widget = report.prepareWidget();
				if (_parent.getCenterWidget() != null) _parent.getCenterWidget().removeFromParent();
		        _parent.setCenterWidget(widget, _layoutData);
		        _parent.forceLayout();

			}
		});

        return _listReports;
    }

	private ListStore<HighlightsReport> createListStore() {
        ListStore<HighlightsReport> s = new ListStore<HighlightsReport>(dataAccess.key());
        s.add(new HighlightsReport(new HighlightsImplGreatestEffort()));
        /*
        s.add(new HighlightsReport(new HighlightsImplLeastEffort()));
        s.add(new HighlightsReport(new HighlightsImplMostGamesPlayed()));
        s.add(new HighlightsReport(new HighlightsImplMostQuizzesPassed()));
        s.add(new HighlightsReport(new HighlightsImplHighestAverageQuizScores()));
        s.add(new HighlightsReport(new HighlightsImplMostFailuresLatestQuiz()));
        s.add(new HighlightsReport(new HighlightsImplZeroLogins()));
        s.add(new HighlightsReport(new HighlightsImplTimeOnTask()));
        */
        // // s.add(new HighlightsReport(new HighlightImplComparePerformance()));
        
        /** mark these two reports as not using the summary page selection */
        /*
        HighlightsReport rm = new HighlightsReport(new HighlightsImplGroupProgress());
        //rm.setDecorationClass("highlight-report-uses-summary");
        rm.setGroupReport(true);
        s.add(rm);
        
        rm = new HighlightsReport(new HighlightsImplGroupUsage());
        //rm.setDecorationClass("highlight-report-uses-summary");
        rm.setGroupReport(true);
        s.add(rm);
        */
        return s;
    }    

/*
    private void showReportOutput(ReportModel report) {
        
        _reportOutput.removeAll();
        _reportOutput.add(report.getReport().getWidget());
        
        layout(true);
    }

    private LayoutContainer createReportOutputPanel() {
        return new MyLayoutContainer("Report Output", new Label("Report Output"));
    }

	public interface DataPropertyAccess extends PropertyAccess<HighlightImplBase> {
		ValueProvider<HighlightImplBase, String> getText();

		ValueProvider<HighlightImplBase, String> getToolTip();

		@Path("text")
		ModelKeyProvider<HighlightImplBase> textKey();
    }

	private ListView<ReportModel, String> createListStore() {
        ListView<ReportModel, String> s = new ListView<ReportModel, String>();
        s.add(new ReportModel(new HighlightImplGreatestEffort()));
        s.add(new ReportModel(new HighlightImplLeastEffort()));
        s.add(new ReportModel(new HighlightImplMostGamesPlayed()));
        s.add(new ReportModel(new HighlightImplMostQuizzesPassed()));
        s.add(new ReportModel(new HighlightImplHighestAverageQuizScores()));
        s.add(new ReportModel(new HighlightImplMostFailuresLatestQuiz()));
        s.add(new ReportModel(new HighlightImplZeroLogins()));
        s.add(new ReportModel(new HighlightImplTimeOnTask()));
        // s.add(new ReportModel(new HighlightImplComparePerformance()));
        
        /** mark these two reports as not using the summary page selection */ /*
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
                
            GeneratePdfHighlightsReportAction action = new GeneratePdfHighlightsReportAction(StudentGridPanel.instance._cmAdminMdl.getUid(),reportName,reportLayout,StudentGridPanel.instance._pageAction);
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
*/
}



