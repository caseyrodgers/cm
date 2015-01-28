package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_admin.client.ui.CCSSLevelComboBoxWidget;
import hotmath.gwt.cm_admin.client.ui.CCSSLevelComboBoxWidget.CallbackOnSelection;
import hotmath.gwt.cm_admin.client.ui.StudentListWithCCSSDetailDialog;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.MyFieldSet;
import hotmath.gwt.shared.client.model.CCSSDetail;
import hotmath.gwt.shared.client.model.CCSSGradeLevel;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CCSSDetailAction;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction.ReportType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

/**
 * Comprised of combo-box to select a CCSS Level (strand) and a grid to display
 * the CSSS name along with count of students that have NOT covered that standard
 * 
 * @author bob
 *
 */
public class HighlightsImplCCSSRemainingPanel extends BorderLayoutContainer implements HighlightPanel {
	
	CCSSRemainingDetailsPanel _detailsPanel;
	ComboBox<CCSSGradeLevel> _comboBox;
	String _levelName;
	MyFieldSet _levelField;
	
    public HighlightsImplCCSSRemainingPanel(HighlightsImplBase base) {
        _detailsPanel = new CCSSRemainingDetailsPanel(base);
    }

    @Override
    public ReportType getReportType() {
        return _detailsPanel.getReportType();
    }

	@Override
	public String[] getReportColumns() {
		return _detailsPanel.getReportColumns();
	}

	@Override
    public String[][] getReportValues() {
    	return _detailsPanel.getReportValues();
    }

	@Override
	public void getDataFromServer(boolean force, final CallbackOnComplete callbackOnComplete) {

        if(!force && _comboBox != null) {
            callbackOnComplete.isComplete();
            return;
        }
        
    	_comboBox = new CCSSLevelComboBoxWidget("-- select a strand --",
    			new CallbackOnComplete() {
			        @Override
			        public void isComplete() {
			        	HorizontalLayoutContainer container = new HorizontalLayoutContainer();
			        	container.setBorders(true);
			        	HTML label = new HTML("Strand: ");
			        	label.addStyleName("level-combo-label");
			        	container.add(label);
			        	_comboBox.addStyleName("level-combo");
			        	container.add(_comboBox);
		    	        setNorthWidget(container, new BorderLayoutData(30));
		    	        setVisible(true);
		    	    	setCenterWidget(_detailsPanel);
		    	    	_detailsPanel.setVisible(true);
		    	    	_detailsPanel.drawTable(null);
		                callbackOnComplete.isComplete();
		                forceLayout();
			        }
      	        },
    			new CallbackOnSelection() {
			        @Override
			        public void setSelection(String levelName) {
			        	_levelName = levelName;
			        	_detailsPanel.getDataFromServer(true, new CallbackOnComplete() {

							@Override
							public void isComplete() {
		                        forceLayout();
							}
			        		
			        	});
			        }
      	        }).getlevelCombo();
	}

	class CCSSRemainingDetailsPanel extends HighlightsImplDetailsPanelBase {

		public CCSSRemainingDetailsPanel(HighlightsImplBase base) {
			super(base);
		}

	    @Override
	    protected ColumnModel<HighlightReportData> getColumns() {
	    	List<ColumnConfig<HighlightReportData, ?>> cols = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

	    	ColumnConfig<HighlightReportData, ?> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 200, "CCSS Code");
	    	column.setSortable(false);
	    	cols.add(column);

	        column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.studentCount(), 100, "# Students");
	    	column.setSortable(false);
	    	cols.add(column);

	    	return new ColumnModel<HighlightReportData>(cols);

	    }

	    @Override
		public ReportType getReportType() {
	    	return ReportType.CCSS_STRAND_NOT_COVERED;
		}

	    @Override
	    public String getLevel() {
	    	return _levelName;
	    }

	    @Override
	    public String[] getReportColumns() {
	        return new String[] {"CCSS Code", "# Students"};
	    }

	    @Override
	    protected String getGridToolTip() {
	        return "Double click for details.";
	    }

	    @Override
	    public String[][] getReportValues() {
	        CmList<HighlightReportData> hd = getHighLightData();
	        if (hd == null) {
	        	return null;
	        }
	        String[][] vals = new String[hd.size()][2];
	        for(int i=0;i<hd.size();i++) {
	            vals[i][0] = hd.get(i).getName();
	            vals[i][1] = hd.get(i).getDbCount() + "";
	        }
	        return vals;
	    }
	    
		@Override
	    protected String getNoRowsFoundMsg() {
	    	return "<h1 style='color:#1C97D1; font-size:1.2em; margin:10px; padding:10px'>Please select a Strand above.</h1>";
	    }

	    @Override
	    protected CellDoubleClickHandler getDoubleClickHandler() {
	    	return new CellDoubleClickHandler() {
	            @Override
	            public void onCellClick(CellDoubleClickEvent event) {
	                if (_grid.getSelectionModel().getSelectedItems().size() > 0) {
	                    CmLogger.debug("click handler: CCSS Coverage Details");
	                    showCCSSCoverageDetails();
	                }
	            }
	        };
	    }

	    private void showCCSSCoverageDetails() {

	        CmBusyManager.setBusy(true);
	        final HighlightReportData item = _grid.getSelectionModel().getSelectedItem();

	        new RetryAction<CmList<CCSSDetail>>() {
	            public void oncapture(CmList<CCSSDetail> detail) {
	                try {
	                	StringBuilder sb = new StringBuilder("<div style='padding-top:10px; padding-bottom:10px; margin-left:10px; margin-right:10px; font-weight:500'>");
	                	sb.append(detail.get(0).getSummary());
	                	sb.append("</div>");
	                	int height = (sb.length() / 40) * 12;
	                    StudentListWithCCSSDetailDialog dialog = new StudentListWithCCSSDetailDialog(item.getName(), height);
	                    dialog.loadStudents(item.getUidList());
	                    dialog.addCCSSDetail(sb.toString());
	                } finally {
	                    CmBusyManager.setBusy(false);
	                }
	            }

	            @Override
	            public void attempt() {
	                CmServiceAsync s = CmRpcCore.getCmService();

	                CCSSDetailAction action = new CCSSDetailAction(Arrays.asList(item.getName()));
	                setAction(action);
	                s.execute(action, this);
	            }
	        }.register();
	    }

	}

}