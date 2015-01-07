package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.shared.client.model.ProgramData;
import hotmath.gwt.shared.client.model.ProgramSegmentData;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataDetailAction;
import hotmath.gwt.shared.client.util.CmAsyncCallback;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.axis.CategoryAxis;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOverEvent;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOverEvent.SeriesItemOverHandler;
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent;
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent.SeriesSelectionHandler;
import com.sencha.gxt.chart.client.chart.series.BarSeries;
import com.sencha.gxt.chart.client.chart.series.SeriesRenderer;
import com.sencha.gxt.chart.client.chart.series.SeriesToolTipConfig;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

/**
 * 
 * @author bob
 *
 */

public class ProgramBarChart implements IsWidget {

    static final RGB[] rgbColors = {
        new RGB(213, 70, 121), new RGB(44, 153, 201), new RGB(146, 6, 157), new RGB(49, 149, 0), new RGB(249, 153, 0)};

	public interface DataPropertyAccess extends PropertyAccess<ProgramSegmentData> {
		ValueProvider<ProgramSegmentData, Integer> countCompleted();

		ValueProvider<ProgramSegmentData, String> label();

		@Path("label")
		ModelKeyProvider<ProgramSegmentData> labelKey();
	}

	private static final DataPropertyAccess dataAccess = GWT.create(DataPropertyAccess.class);

	List<ProgramSegmentData> programSegmentList;
	String title;
	int testDefId;

	public ProgramBarChart(ProgramData program, List<ProgramSegmentData> data) {
		this.programSegmentList = data;
		this.title = program.getProgramName();
		this.testDefId = program.getTestDefId();
		initLabels();
	}

	private void initLabels() {
		if (programSegmentList == null) return;
		for (ProgramSegmentData ps : programSegmentList) {
			ps.setLabel("Section " + (ps.getSegment() + 1));
		}
	}

	private Widget _widget;

	@Override
	public Widget asWidget() {
		if (_widget != null) return _widget;

		final ListStore<ProgramSegmentData> store = new ListStore<ProgramSegmentData>(dataAccess.labelKey());
		store.addAll(this.programSegmentList);

		final Chart<ProgramSegmentData> chart = new Chart<ProgramSegmentData>();
		chart.setStore(store);
		chart.setShadowChart(true);
		chart.setAnimated(true);

	    // Allow room for rotated labels
	    chart.setDefaultInsets(50);

		NumericAxis<ProgramSegmentData> axis = new NumericAxis<ProgramSegmentData>();
		axis.setPosition(Position.LEFT);
		axis.addField(dataAccess.countCompleted());
		axis.setDisplayGrid(true);
		int max = getMaximum();
		axis.setInterval(max/10);
		axis.setMaximum(max);
		axis.setMinimum(0);
		//axis.setWidth(50);
		chart.addAxis(axis);

		CategoryAxis<ProgramSegmentData, String> catAxis = new CategoryAxis<ProgramSegmentData, String>();
		catAxis.setPosition(Position.BOTTOM);
		catAxis.setField(dataAccess.label());
	    TextSprite sprite = new TextSprite();
	    sprite.setRotation(315);
	    sprite.setFontSize(10);
	    //sprite.setTextAnchor(TextSprite.TextAnchor.START);
	    catAxis.setLabelConfig(sprite);
	    catAxis.setLabelPadding(-10);
	    catAxis.setLabelTolerance(20);
		chart.addAxis(catAxis);

		final BarSeries<ProgramSegmentData> column = new BarSeries<ProgramSegmentData>();
		column.setYAxisPosition(Position.LEFT);
		column.addYField(dataAccess.countCompleted());
		column.setColumn(true);
	    column.setRenderer(new SeriesRenderer<ProgramSegmentData>() {
		      @Override
		      public void spriteRenderer(Sprite sprite, int index, ListStore<ProgramSegmentData> store) {
		        sprite.setFill(rgbColors[index % 5]);
		        sprite.redraw();
		      }
		    });

		final SeriesToolTipConfig<ProgramSegmentData> config = new SeriesToolTipConfig<ProgramSegmentData>();
		config.setLabelProvider(null);
		column.setToolTipConfig(config);
		column.addSeriesItemOverHandler(new SeriesItemOverHandler<ProgramSegmentData>() {
			@Override
			public void onSeriesOverItem(SeriesItemOverEvent<ProgramSegmentData> event) {
				if (true) {
					//int index = event.getIndex();
					//String msg = "Click to see list of students [" + programSegmentList.get(index).getCountCompleted() + "]";
					config.setBodyHtml(SafeHtmlUtils.htmlEscape("Click to see list of students"));
					column.setToolTipConfig(config);
				}
			}
		});
		column.addSeriesSelectionHandler(new SeriesSelectionHandler<ProgramSegmentData>() {
			@Override
			public void onSeriesSelection(SeriesSelectionEvent<ProgramSegmentData> event) {
				if (true) {
					int index = event.getIndex();
					showUsersWhoHaveCompletedSegment(index);
				}
			}
		});

		chart.addSeries(column);

		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.getElement().getStyle().setMargin(20, Unit.PX);
		panel.setCollapsible(true);
		panel.setPixelSize(500, 500);
		panel.setBodyBorder(false);
		panel.setBorders(false);

		VerticalLayoutContainer layout = new VerticalLayoutContainer();
		panel.add(layout);

		chart.setLayoutData(new VerticalLayoutData(1, 1));
		layout.add(chart);

		_widget = panel;
		return panel;
	}

	/** determine the max value shown by chart.
	 * 
	 * @param max
	 * @return
	 */
	protected int getMaximum() {
		int max = 0;
		for (ProgramSegmentData td : programSegmentList) {
			if (max < td.getCountCompleted()) max = td.getCountCompleted();
		}

		int fudge=0;
		if(max < 10) {
			fudge = 10;
		}
		else if(max < 50) {
			fudge = 50;
		}
		else if(max < 100) {
			fudge = 100;
		}
		else { 
			fudge = 1000;
		}

		int add = (max % fudge);
		max += (fudge - add);
		return max;
	}

	private void showUsersWhoHaveCompletedSegment(int index) {
		CmBusyManager.setBusy(true);

		final int segmentNo = programSegmentList.get(index).getSegment() + 1;

		CmServiceAsync service = CmRpcCore.getCmService();
		GetAdminTrendingDataDetailAction action = new GetAdminTrendingDataDetailAction(StudentGridPanel.instance._cmAdminMdl.getUid(),
                StudentGridPanel.instance._pageAction, testDefId, segmentNo);
		        service.execute(action,
				    new CmAsyncCallback<CmList<StudentModelI>>() {
			            public void onSuccess(CmList<StudentModelI> students) {
				            new StudentListDialog("Students in " + title + " Segment " + segmentNo).addStudents(students);
				                CmBusyManager.setBusy(false);
			            }

			        @Override
			        public void onFailure(Throwable caught) {
				        CmBusyManager.setBusy(false);
				        super.onFailure(caught);
			        }
		        });

	}

}
