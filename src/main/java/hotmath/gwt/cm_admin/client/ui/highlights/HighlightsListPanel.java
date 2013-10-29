package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfHighlightsReportAction;
import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.ListViewCustomAppearance;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

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

        CenterLayoutContainer northContainer = new CenterLayoutContainer();
        northContainer.setBorders(true);
        northContainer.add(getHeading());

        SimpleContainer southContainer = new SimpleContainer();
        southContainer.setBorders(true);
        southContainer.add(getLegend());

        this.setNorthWidget(northContainer, new BorderLayoutData(30));
        this.setCenterWidget(createListOfAvailableReports());
        this.setSouthWidget(southContainer, new BorderLayoutData(50));
        
        this.forceLayout();
    }
    
    private void printCurrentReport() {
        HighlightsReport report = _listReports.getSelectionModel().getSelectedItem();

        if(report == null  || report.getReport().isEmpty() == true) {
            CmMessageBox.showAlert("Nothing to print");
            return;
        }
        
        if(report.getReport().hasPrintAbility() == false) {
            InfoPopupBox.display("No Report", "A printable report has not been defined for this highlight.");
            return;
        }
        
        String reportName = report.getText();
        HighlightReportLayout reportLayout = report.getReport().getReportLayout();
        
        GeneratePdfHighlightsReportAction action = new GeneratePdfHighlightsReportAction(StudentGridPanel.instance.getCmAdminMdl().getUid(),reportName,reportLayout,StudentGridPanel.instance.getPageAction());
        action.setFilterMap(StudentGridPanel.instance.getPageAction().getFilterMap());
        new PdfWindow(0, "Catchup Math Highlight Report", action);
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

    ListView<HighlightsReport, HighlightsReport> _listReports =
    	new ListView<HighlightsReport, HighlightsReport>(createListStore(), new IdentityValueProvider<HighlightsReport>() {
            @Override
            public void setValue(HighlightsReport object, HighlightsReport value) { 
            }
        });

    private ListView<HighlightsReport, HighlightsReport> createListOfAvailableReports() {

        _listReports.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		_listReports.getSelectionModel().addSelectionHandler(new SelectionHandler<HighlightsReport>() {
			@Override
			public void onSelection(SelectionEvent<HighlightsReport> event) {
				
				HighlightsReport hlReport = event.getSelectedItem();
				final HighlightsImplBase report = hlReport.getReport();
				if (report.hasPrintAbility() == true) {
			        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_ENABLE_HIGHLIGHT_PRINT));
				}
				else {
			        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_DISABLE_HIGHLIGHT_PRINT));
				}
				HighlightPanel pb = (HighlightPanel)report.prepareWidget();
				pb.getDataFromServer(false, new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        Widget widget = report.prepareWidget();
                        if (    _parent.getCenterWidget() != null) {
                            _parent.getCenterWidget().removeFromParent();
                        }
                        _parent.setCenterWidget(widget, _layoutData);
                        _parent.forceLayout();
                    }
                });
			}
		});

		SimpleSafeHtmlCell<HighlightsReport> reportCell = new SimpleSafeHtmlCell<HighlightsReport>(new AbstractSafeHtmlRenderer<HighlightsReport>() {
            @Override
            public SafeHtml render(HighlightsReport report) {
                String tip = report.getToolTip();
                String html = "<div qtip='" + tip + "'";
                if (report.isGroupReport()) { 
                    html += " style='color:red'>";
                }
                else {
                    html += ">";
                }
                
                html += report.getText();
                html += "</div>";
                return SafeHtmlUtils.fromTrustedString(html);
            }
          });
        _listReports.setCell(reportCell);

        new QuickTip(_listReports);

        return _listReports;
    }

	private ListStore<HighlightsReport> createListStore() {
        ListStore<HighlightsReport> s = new ListStore<HighlightsReport>(dataAccess.key());
        s.add(new HighlightsReport(new HighlightsImplGreatestEffort()));
        s.add(new HighlightsReport(new HighlightsImplLeastEffort()));
        s.add(new HighlightsReport(new HighlightsImplMostGamesPlayed()));
        s.add(new HighlightsReport(new HighlightsImplMostQuizzesPassed()));
        s.add(new HighlightsReport(new HighlightsImplHighestAverageQuizScores()));
        s.add(new HighlightsReport(new HighlightsImplMostFailuresLatestQuiz()));
        s.add(new HighlightsReport(new HighlightsImplZeroLogins()));
        s.add(new HighlightsReport(new HighlightsImplTimeOnTask()));
        s.add(new HighlightsReport(new HighlightsImplFirstAnswersPercent()));
        s.add(new HighlightsReport(new HighlightsImplAssignments()));
        s.add(new HighlightsReport(new HighlightsImplMostCCSSCoverage()));
        s.add(new HighlightsReport(new HighlightsImplCCSSCoverage()));
        s.add(new HighlightsReport(new HighlightsImplCCSSCoverageChart()));
        s.add(new HighlightsReport(new HighlightsImplCCSSRemaining()));
        // s.add(new HighlightsReport(new HighlightImplComparePerformance()));

        /** mark these two reports as not using the summary page selection */
        HighlightsReport rm = new HighlightsReport(new HighlightsImplGroupProgress());
        //rm.setDecorationClass("highlight-report-uses-summary");
        rm.setGroupReport(true);
        s.add(rm);
        
        rm = new HighlightsReport(new HighlightsImplGroupUsage());
        //rm.setDecorationClass("highlight-report-uses-summary");
        rm.setGroupReport(true);
        s.add(rm);

        return s;
    }    

	private HTML getHeading() {
		return new HTML("<h1 style='color:#1C97D1; font-size:100%;'>Available Reports</h1>");
	}
	
	private HTML getLegend() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class='report-legend' style='margin:5px'>");
		sb.append("<div><div>&nbsp;</div>Uses Summary Page Selection</div>");
		sb.append("<div class='no-selection'><div>&nbsp;</div>Applies to all Groups</div>");
		sb.append("</div>");
		return new HTML(sb.toString());
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



