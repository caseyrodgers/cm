package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventType;

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
import com.sencha.gxt.theme.base.client.listview.ListViewCustomAppearance;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
//import hotmath.gwt.shared.client.rpc.action.GeneratePdfStudentCCSSReportAction;

/**
 * 
 * @author bob
 *
 */

public class CCSSCoverageListPanel extends BorderLayoutContainer {
	
    interface CCSSCoverageDataAccess extends PropertyAccess<CCSSCoverageReport> {
		 
		    @Path("text")
		    ValueProvider<CCSSCoverageReport, String> name();

		    @Path("text")
		    ModelKeyProvider<CCSSCoverageReport> key();
		  }

	private static final CCSSCoverageDataAccess dataAccess = GWT.create(CCSSCoverageDataAccess.class);

    static CCSSCoverageListPanel __instance;
    
    BorderLayoutContainer _parent;
    BorderLayoutData _layoutData;
    int _uid;   // either student or group UID
    int _adminId;
    boolean _isGroupReport;

    static ListView<CCSSCoverageReport, CCSSCoverageReport> _listReports;

    public CCSSCoverageListPanel(BorderLayoutContainer parent, BorderLayoutData layoutData,
    		int uid, int adminId, boolean isGroupReport) {
        __instance = this;
        _parent = parent;
        _layoutData = layoutData;
        _uid = uid;
        _adminId = adminId;
        _isGroupReport = isGroupReport;

        _listReports =
            new ListView<CCSSCoverageReport, CCSSCoverageReport>(createListStore(), new IdentityValueProvider<CCSSCoverageReport>() {
                @Override
                public void setValue(CCSSCoverageReport object, CCSSCoverageReport value) { 
                }
            });

        CenterLayoutContainer northContainer = new CenterLayoutContainer();
        northContainer.setBorders(true);
        northContainer.add(getHeading());

        this.setNorthWidget(northContainer, new BorderLayoutData(30));
        this.setCenterWidget(createListOfAvailableReports());

        this.forceLayout();
    }

    int countdown;
    
    public void refreshReportList() {
    	countdown--;
    	if (countdown <= 0) {
    		_listReports.refresh();
    	}
    }

    private void printCurrentReport() {
        CCSSCoverageReport report = _listReports.getSelectionModel().getSelectedItem();
        if(report == null) {
            CmMessageBox.showAlert("Nothing to print");
            return;
        }

        String reportName = report.getText();
        //TODO: 
        //GeneratePdfStudentCCSSReportAction action = new GeneratePdfStudentCCSSReportAction(StudentGridPanel.instance.getCmAdminMdl().getUid(), reportName,reportLayout, StudentGridPanel.instance.getPageAction());
        //action.setFilterMap(StudentGridPanel.instance.getPageAction().getFilterMap());
        //new PdfWindow(0, "Catchup Math CCSS Coverage Report", action);
    }
        
    
    static int __lastSelectedReport=0;
    
    private native String getTemplate() /*-{ 
    return  [ 
    '<tpl for=".">', 
    '<div class="x-view-item {decorationClass}" qtip="{toolTip}">{text}</div>', 
    '</tpl>' 
    ].join(""); 
  }-*/;  
    
    ListViewCustomAppearance<CCSSCoverageReport> appearance = new ListViewCustomAppearance<CCSSCoverageReport>("") {
 	   
		@Override
		public void renderItem(SafeHtmlBuilder builder, SafeHtml content) {
			boolean haveGroup = (content.toString().indexOf("Group") > -1);
	        if (haveGroup == true)  builder.appendHtmlConstant("<div style='color:red'>");
	        builder.append(content);
	        if (haveGroup == true) builder.appendHtmlConstant("</div>");
		}
   
      };

    private ListView<CCSSCoverageReport, CCSSCoverageReport> createListOfAvailableReports() {

        _listReports.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		_listReports.getSelectionModel().addSelectionHandler(new SelectionHandler<CCSSCoverageReport>() {
			@Override
			public void onSelection(SelectionEvent<CCSSCoverageReport> event) {
				
				CCSSCoverageReport crReport = event.getSelectedItem();
				CCSSCoverageImplBase report = crReport.getReport();
				Widget widget = report.prepareWidget();
				if (_parent.getCenterWidget() != null) _parent.getCenterWidget().removeFromParent();
		        _parent.setCenterWidget(widget, _layoutData);
		        _parent.forceLayout();

			}
		});

		SimpleSafeHtmlCell<CCSSCoverageReport> reportCell = new SimpleSafeHtmlCell<CCSSCoverageReport>(new AbstractSafeHtmlRenderer<CCSSCoverageReport>() {
            @Override
            public SafeHtml render(CCSSCoverageReport report) {
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

	private ListStore<CCSSCoverageReport> createListStore() {
        ListStore<CCSSCoverageReport> s = new ListStore<CCSSCoverageReport>(dataAccess.key());
        CallbackOnComplete callback = new CallbackOnComplete() {
            @Override
            public void isComplete() {
            	refreshReportList();
            }
        };
        if (_isGroupReport == false) {
        	countdown = 4;
            s.add(new CCSSCoverageReport(new CCSSCoverageImplStudentQuizzedPassed(_uid, callback)));
            s.add(new CCSSCoverageReport(new CCSSCoverageImplStudentReviewed(_uid, callback)));
            s.add(new CCSSCoverageReport(new CCSSCoverageImplStudentAssignedCompleted(_uid, callback)));
            s.add(new CCSSCoverageReport(new CCSSCoverageImplStudentCombined(_uid, callback)));
        }
        else {
        	countdown = 3;
            s.add(new CCSSCoverageReport(new CCSSCoverageImplGroupByAllStudents(_uid, _adminId, callback)));
            s.add(new CCSSCoverageReport(new CCSSCoverageImplGroupBy50to99Percent(_uid, _adminId, callback)));
            //s.add(new CCSSCoverageReport(new CCSSCoverageImplGroupBy75to99Percent(_uid, _adminId, callback)));
            //s.add(new CCSSCoverageReport(new CCSSCoverageImplGroupBy50to74Percent(_uid, _adminId, callback)));
            //s.add(new CCSSCoverageReport(new CCSSCoverageImplGroupBy25to49Percent(_uid, _adminId, callback)));
            s.add(new CCSSCoverageReport(new CCSSCoverageImplGroupBy00to49Percent(_uid, _adminId, callback)));
            //s.add(new CCSSCoverageReport(new CCSSCoverageImplGroupBy00to24Percent(_uid, _adminId, callback)));
        }
        
        return s;
    }    

	private HTML getHeading() {
		return new HTML("<h1 style='color:#1C97D1; font-size:100%;'>Categories</h1>");
	}
	
    static {
        hotmath.gwt.shared.client.eventbus.EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_PRINT_CCSS_COVERAGE_REPORT) {
                    __instance.printCurrentReport();    
                }
            }
        });
    }
    
}



