package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;



public class HighlightsGroupPanel extends ContentPanel {
    
    
    LayoutContainer _reportOutput = new LayoutContainer();
    
    public HighlightsGroupPanel() {
        setHeading("Group Reports");
        
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
    
    String template = "<tpl for=\".\"><div class='x-view-item'>{text}</div></tpl>";    
    ListView<ReportModelData> _listReports = new ListView<ReportModelData>();
    private LayoutContainer createListOfAvailableReports() {
        _listReports.setStore(createListStore());
        _listReports.setTemplate(template);
        
        List<ReportModelData> list = new ArrayList<ReportModelData>();
        list.add(_listReports.getStore().getAt(0));
        _listReports.getSelectionModel().setSelection(list);
        showReportOutput(list.get(0));
        
        _listReports.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ReportModelData>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ReportModelData> se) {
                ReportModelData report = _listReports.getSelectionModel().getSelectedItem();
                if(report != null) {
                    showReportOutput(report);
                }
            }
        });
        return new LocalLayoutContainer("Available Reports", _listReports);
    }
    
    private void showReportOutput(ReportModelData report) {
        _reportOutput.removeAll();
        _reportOutput.setLayout(new FitLayout());
        _reportOutput.add(report.getReport().getWidget());
        _reportOutput.layout();
    }

    private LayoutContainer createReportOutputPanel() {
        ListStore<ReportModelData> store = new ListStore<ReportModelData>();
        return new LocalLayoutContainer("Report Output", new Label("Report Output"));
    }
 
    private ListStore<ReportModelData> createListStore() {
        ListStore<ReportModelData> s = new ListStore<ReportModelData>();
        s.add(new ReportModelData(new HighlightImplSchoolCompare()));
        s.add(new ReportModelData(new HighlightImplNationwideCompare()));
        return s;
    }
}


class ReportModelData extends BaseModelData {

	private static final long serialVersionUID = 8862392093036236817L;

	AbstractHighlightImpl report;

	public ReportModelData(AbstractHighlightImpl report) {
        this.report = report;
        set("text", report.getText());
    }
    
    public String getText() {
        return get("text");
    }
    
    public AbstractHighlightImpl getReport() {
        return report;
    }
    
}

class LocalLayoutContainer extends LayoutContainer {
    
    public LocalLayoutContainer(String title, Widget container) {
        setLayout(new BorderLayout());
        
        String html = "<h2>" + title + "</h2>";
        Html ohtml = new Html(html);
        add(ohtml, new BorderLayoutData(LayoutRegion.NORTH,35));
        add(container, new BorderLayoutData(LayoutRegion.CENTER));
    }
}

class HighlightImplSchoolCompare extends AbstractHighlightImpl {
    public HighlightImplSchoolCompare() {
        super("Compare with entire school");
        type = AbstractHighlightImpl.HIGHLIGHT_TYPE.SCHOOL_COMPARE;
    }
    
    public Widget prepareWidget() {
        return new HighlightImplComparePanel(this);
    }
}

class HighlightImplNationwideCompare extends AbstractHighlightImpl {
    public HighlightImplNationwideCompare() {
        super("Compare with nationwide performance");
        type = AbstractHighlightImpl.HIGHLIGHT_TYPE.NATIONWIDE_COMPARE;
    }

    public Widget prepareWidget() {
        return new HighlightImplComparePanel(this);
    }
    
}