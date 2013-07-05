package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.TrendingData;
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
import com.sencha.gxt.chart.client.chart.series.Series.LabelPosition;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelConfig;
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

public class TrendingDataWindowBarChart implements IsWidget {

    String colors[] = {"#ff0000", "#00aa00", "#0000ff", "#ff9900", "#ff00ff","#ff0000", "#00aa00", "#aa00ff", "#bb9900", "#ee00ff"};

    static final RGB[] rgbColors = {
            new RGB(213, 70, 121), new RGB(44, 153, 201), new RGB(146, 6, 157), new RGB(49, 149, 0), new RGB(249, 153, 0)};
    
	public interface DataPropertyAccess extends PropertyAccess<TrendingData> {
		ValueProvider<TrendingData, Integer> countAssigned();

		ValueProvider<TrendingData, String> lessonName();

		ValueProvider<TrendingData, String> label();

		@Path("lessonName")
		ModelKeyProvider<TrendingData> lessonNameKey();
    }

	private static final DataPropertyAccess dataAccess = GWT.create(DataPropertyAccess.class);

	List<TrendingData> trendingData;
	String title;


	public TrendingDataWindowBarChart(String title, List<TrendingData> data) {
		this.trendingData = data;
		this.title = title;
	}

	private Widget _widget;

	@Override
	public Widget asWidget() {
		if (_widget != null) return _widget;

		final ListStore<TrendingData> store = new ListStore<TrendingData>(dataAccess.lessonNameKey());
		store.addAll(this.trendingData);

		final Chart<TrendingData> chart = new Chart<TrendingData>();
		chart.setStore(store);
		chart.setShadowChart(true);
		chart.setAnimated(true);

	    // Allow room for rotated labels
	    chart.setDefaultInsets(40);

		NumericAxis<TrendingData> axis = new NumericAxis<TrendingData>();
		axis.setPosition(Position.LEFT);
		axis.addField(dataAccess.countAssigned());
		axis.setDisplayGrid(true);
		int max = getMaximum();
		axis.setInterval(max/10);
		axis.setMaximum(max);
		axis.setMinimum(0);
		//axis.setWidth(50);
		chart.addAxis(axis);

		CategoryAxis<TrendingData, String> catAxis = new CategoryAxis<TrendingData, String>();
		catAxis.setPosition(Position.BOTTOM);
		catAxis.setField(dataAccess.lessonName());
	    TextSprite sprite = new TextSprite();
	    sprite.setRotation(315);
	    sprite.setFontSize(10);
	    sprite.setTextAnchor(TextSprite.TextAnchor.START);
	    catAxis.setLabelConfig(sprite);
	    catAxis.setLabelPadding(-10);
	    catAxis.setLabelTolerance(20);

		chart.addAxis(catAxis);

		final BarSeries<TrendingData> column = new BarSeries<TrendingData>();
		column.setYAxisPosition(Position.LEFT);
		column.addYField(dataAccess.countAssigned());
		column.setColumn(true);
	    column.setRenderer(new SeriesRenderer<TrendingData>() {
	      @Override
	      public void spriteRenderer(Sprite sprite, int index, ListStore<TrendingData> store) {
	        sprite.setFill(rgbColors[index % 5]);
	        sprite.redraw();
	      }
	    });

	    SeriesLabelConfig<TrendingData> labelConfig = new SeriesLabelConfig<TrendingData>();
	    labelConfig.setLabelPosition(LabelPosition.OUTSIDE);
	    column.setLabelConfig(labelConfig);

	    final SeriesToolTipConfig<TrendingData> config = new SeriesToolTipConfig<TrendingData>();
		config.setLabelProvider(null);
		column.setToolTipConfig(config);
		column.addSeriesItemOverHandler(new SeriesItemOverHandler<TrendingData>() {
			@Override
			public void onSeriesOverItem(SeriesItemOverEvent<TrendingData> event) {
				if (true) {
					//int index = event.getIndex();
					//String msg = trendingData.get(index).getLessonName() + " (click to see students)";
					config.setBodyHtml(SafeHtmlUtils.htmlEscape("Click to see students"));
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
				new StudentListDialog("Students assigned lesson '" +lessonName + "'").addStudents(students);
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
