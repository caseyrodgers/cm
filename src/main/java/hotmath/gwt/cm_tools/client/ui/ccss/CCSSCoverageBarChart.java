package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.shared.client.model.CCSSCoverageBar;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.axis.CategoryAxis;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOverEvent;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOverEvent.SeriesItemOverHandler;
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent;
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent.SeriesSelectionHandler;
import com.sencha.gxt.chart.client.chart.series.BarSeries;
import com.sencha.gxt.chart.client.chart.series.Series.LabelPosition;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelConfig;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelProvider;
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

public class CCSSCoverageBarChart implements IsWidget {

    String colors[] = {"#ff0000", "#00aa00", "#0000ff", "#ff9900", "#ff00ff","#ff0000", "#00aa00", "#aa00ff", "#bb9900", "#ee00ff"};

    static final RGB[] rgbColors = {
            new RGB(213, 70, 121), new RGB(44, 153, 201), new RGB(146, 6, 157), new RGB(49, 149, 0), new RGB(249, 153, 0)};
    
	public interface DataPropertyAccess extends PropertyAccess<CCSSCoverageBar> {
		ValueProvider<CCSSCoverageBar, Integer> count();

		ValueProvider<CCSSCoverageBar, Integer> assignments();

		ValueProvider<CCSSCoverageBar, Integer> quizzes();

		ValueProvider<CCSSCoverageBar, Integer> lessons();

		ValueProvider<CCSSCoverageBar, String> label();

		@Path("label")
		ModelKeyProvider<CCSSCoverageBar> labelKey();
    }

	private static final DataPropertyAccess dataAccess = GWT.create(DataPropertyAccess.class);

	List<CCSSCoverageBar> ccssData;
	String title;


	public CCSSCoverageBarChart(String title, List<CCSSCoverageBar> data) {
		this.ccssData = data;
		this.title = title;
	}

	private Widget _widget;

	@Override
	public Widget asWidget() {
		if (_widget != null) return _widget;

		final ListStore<CCSSCoverageBar> store = new ListStore<CCSSCoverageBar>(dataAccess.labelKey());
		store.addAll(this.ccssData);

		final Chart<CCSSCoverageBar> chart = new Chart<CCSSCoverageBar>();
		chart.setStore(store);
		chart.setShadowChart(true);
		chart.setAnimated(true);

	    // Allow room for rotated labels
	    chart.setDefaultInsets(40);

		NumericAxis<CCSSCoverageBar> axis = new NumericAxis<CCSSCoverageBar>();
		axis.setPosition(Position.LEFT);
		axis.addField(dataAccess.count());
		axis.setDisplayGrid(true);
		int max = getMaximum();
		axis.setInterval(max/10);
		axis.setMaximum(max);
		axis.setMinimum(0);
		axis.setDisplayGrid(true);
	    TextSprite title = new TextSprite("Standards");
	    title.setFontSize(18);
	    axis.setTitleConfig(title);
		chart.addAxis(axis);

		CategoryAxis<CCSSCoverageBar, String> catAxis = new CategoryAxis<CCSSCoverageBar, String>();
		catAxis.setPosition(Position.BOTTOM);
		catAxis.setField(dataAccess.label());
	    TextSprite sprite = new TextSprite();
	    sprite.setRotation(315);
	    sprite.setFontSize(10);
	    sprite.setTextAnchor(TextSprite.TextAnchor.START);
	    catAxis.setLabelConfig(sprite);
	    catAxis.setLabelPadding(-10);
	    catAxis.setLabelTolerance(20);
		chart.addAxis(catAxis);
	    
		final BarSeries<CCSSCoverageBar> column = new BarSeries<CCSSCoverageBar>();
		column.setYAxisPosition(Position.LEFT);
		column.addYField(dataAccess.assignments());
		column.addYField(dataAccess.lessons());
		column.addYField(dataAccess.quizzes());
		column.setColumn(true);
		column.addColor(rgbColors[0]);
	    column.addColor(rgbColors[1]);
	    column.addColor(rgbColors[2]);
	    column.setStacked(true);

	    final SeriesToolTipConfig<CCSSCoverageBar> config = new SeriesToolTipConfig<CCSSCoverageBar>();
	    config.setLabelProvider(new SeriesLabelProvider<CCSSCoverageBar>() {
	      @Override
	      public String getLabel(CCSSCoverageBar item, ValueProvider<? super CCSSCoverageBar, ? extends Number> valueProvider) {
	        return String.valueOf(valueProvider.getValue(item).intValue());
	      }
	    });
	    config.setDismissDelay(2000);
		column.setToolTipConfig(config);
/*
 		column.addSeriesItemOverHandler(new SeriesItemOverHandler<CCSSCoverageBar>() {
			@Override
			public void onSeriesOverItem(SeriesItemOverEvent<CCSSCoverageBar> event) {
				if (true) {
					int index = event.getIndex();
					String msg = ccssData.get(index).getCount() + " standards (click for details)";
					config.setBodyHtml(SafeHtmlUtils.htmlEscape(msg));
					column.setToolTipConfig(config);
				}
			}
		});
 */
		column.addSeriesSelectionHandler(new SeriesSelectionHandler<CCSSCoverageBar>() {
			@Override
			public void onSeriesSelection(SeriesSelectionEvent<CCSSCoverageBar> event) {
				if (true) {
					int index = event.getIndex();
					//showUsersWhoHaveBeenAssignedLesson(index);
				}
			}
		});

		chart.addSeries(column);

	    final Legend<CCSSCoverageBar> legend = new Legend<CCSSCoverageBar>();
	    legend.setPosition(Position.BOTTOM);
	    legend.setItemHiding(true);
	    legend.setItemHighlighting(true);
	    chart.setLegend(legend);

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
		for (CCSSCoverageBar cd : ccssData) {
			if (max < cd.getCount()) max = cd.getCount();
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
/*
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
*/
	protected void setModelData(String title, List<CCSSCoverageBar> data) {
		this.ccssData = data;
		this.title = title;
	}

}
