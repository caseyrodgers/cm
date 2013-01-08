package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.model.TrendingData;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataDetailAction;
import hotmath.gwt.shared.client.util.CmAsyncCallback;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.axis.CategoryAxis;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOverEvent;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOverEvent.SeriesItemOverHandler;
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent;
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent.SeriesSelectionHandler;

import com.sencha.gxt.chart.client.chart.series.BarSeries;
import com.sencha.gxt.chart.client.chart.series.SeriesToolTipConfig;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextAnchor;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.Resizable.Dir;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class TrendingDataWindowBarChart implements IsWidget {


	public interface DataPropertyAccess extends PropertyAccess<TrendingData> {
		ValueProvider<TrendingData, Integer> countAssigned();

		ValueProvider<TrendingData, String> lessonName();

		@Path("lessonName")
		ModelKeyProvider<TrendingData> nameKey();
	}

	private static final DataPropertyAccess dataAccess = GWT.create(DataPropertyAccess.class);

	List<TrendingData> trendingData;
	String title;


	public TrendingDataWindowBarChart(String title, List<TrendingData> data) {
		this.trendingData = data;
		this.title = title;
		//int idx = 1;
		//for (TrendingData td : data) {
		//	td.setLessonName("Lesson " + idx++);
		//}
	}

	private Widget _widget;

	@Override
	public Widget asWidget() {
		if (_widget != null) return _widget;

		final ListStore<TrendingData> store = new ListStore<TrendingData>(dataAccess.nameKey());
		store.addAll(this.trendingData);

		final Chart<TrendingData> chart = new Chart<TrendingData>();
		chart.setStore(store);
		chart.setShadowChart(true);
		chart.setAnimated(true);

		NumericAxis<TrendingData> axis = new NumericAxis<TrendingData>();
		axis.setPosition(Position.LEFT);
		axis.addField(dataAccess.countAssigned());
		axis.setDisplayGrid(true);
		int max = getMaximum();
		axis.setInterval(max/10);
		axis.setMaximum(max);
		axis.setMinimum(0);
		axis.setWidth(50);
		chart.addAxis(axis);

		CategoryAxis<TrendingData, String> catAxis = new CategoryAxis<TrendingData, String>();
		catAxis.setPosition(Position.BOTTOM);
		catAxis.setField(dataAccess.lessonName());
		TextSprite sprite = new TextSprite();
		sprite.setRotation(45);
		sprite.setTextAnchor(TextAnchor.START);
		sprite.setY(catAxis.getY()+5.);

		catAxis.setLabelPadding(-10);
		catAxis.setLabelConfig(sprite);
		catAxis.setLabelTolerance(50);
		chart.addAxis(catAxis);

		final BarSeries<TrendingData> column = new BarSeries<TrendingData>();
		column.setYAxisPosition(Position.LEFT);
		column.addYField(dataAccess.countAssigned());
		column.addColor(new RGB(148,174,10));
		column.setColumn(true);
		final SeriesToolTipConfig<TrendingData> config = new SeriesToolTipConfig<TrendingData>();
		config.setLabelProvider(null);
		column.setToolTipConfig(config);
		column.addSeriesItemOverHandler(new SeriesItemOverHandler<TrendingData>() {
			@Override
			public void onSeriesOverItem(SeriesItemOverEvent<TrendingData> event) {
				if (true) {
					int index = event.getIndex();
					String msg = "Click to see list of students [" + trendingData.get(index).getCountAssigned() + "]";
					SafeHtmlUtils.htmlEscape(msg);
					config.setBodyHtml(SafeHtmlUtils.htmlEscape(msg));
					column.setToolTipConfig(config);
				}
			}
		});
		column.addSeriesSelectionHandler(new SeriesSelectionHandler<TrendingData>() {
			@Override
			public void onSeriesSelection(SeriesSelectionEvent<TrendingData> event) {
				if (true) {
					int index = event.getIndex();
					showUsersWhoHaveBeenAssignedLesson(index);
				}
			}
		});


		chart.addSeries(column);

		ContentPanel panel = new FramedPanel();
		panel.getElement().getStyle().setMargin(10, Unit.PX);
		panel.setCollapsible(true);
		panel.setHeadingText(this.title);
		panel.setPixelSize(500, 500);
		panel.setBodyBorder(true);

		final Resizable resize = new Resizable(panel, Dir.E, Dir.SE, Dir.S);
		resize.setMinHeight(400);
		resize.setMinWidth(400);

		panel.addExpandHandler(new ExpandHandler() {
			@Override
			public void onExpand(ExpandEvent event) {
				resize.setEnabled(true);
			}
		});
		panel.addCollapseHandler(new CollapseHandler() {
			@Override
			public void onCollapse(CollapseEvent event) {
				resize.setEnabled(false);
			}
		});

		new Draggable(panel, panel.getHeader()).setUseProxy(false);

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
		for (TrendingData td : trendingData) {
			if (max < td.getCountAssigned()) max = td.getCountAssigned();
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

	private void showUsersWhoHaveBeenAssignedLesson(int lessonNumber) {
		CmBusyManager.setBusy(true);

		final String lessonName = trendingData.get(lessonNumber).getLessonName();
		CmServiceAsync service = CmShared.getCmService();
		GetAdminTrendingDataDetailAction action = new GetAdminTrendingDataDetailAction(StudentGridPanel.instance._pageAction, lessonName);
		service.execute(action,
				new CmAsyncCallback<CmList<StudentModelI>>() {
			public void onSuccess(CmList<StudentModelI> students) {
				new TrendingDataStudentListDialog("Students assigned lesson '" +lessonName + "'",students);
				CmBusyManager.setBusy(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				CmBusyManager.setBusy(false);
				super.onFailure(caught);
			}
		});
	}

	protected void setModelData(String title, List<TrendingData> data) {
		this.trendingData = data;
		this.title = title;
	}

}
