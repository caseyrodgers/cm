package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.model.CCSSCoverageBar;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.axis.CategoryAxis;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent;
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent.SeriesSelectionHandler;
import com.sencha.gxt.chart.client.chart.series.BarSeries;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelProvider;
import com.sencha.gxt.chart.client.chart.series.SeriesToolTipConfig;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

public class CCSSCoverageBarChart implements IsWidget {

    static final RGB[] rgbColors = {
            new RGB(213, 70, 121), new RGB(44, 153, 201), new RGB(146, 6, 157), new RGB(49, 149, 0), new RGB(249, 153, 0)};

	static final DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");

	public interface DataPropertyAccess extends PropertyAccess<CCSSCoverageBar> {
		ValueProvider<CCSSCoverageBar, Integer> count();

		ValueProvider<CCSSCoverageBar, Integer> assignments();

		ValueProvider<CCSSCoverageBar, Integer> quizzes();

		ValueProvider<CCSSCoverageBar, Integer> lessons();

		ValueProvider<CCSSCoverageBar, String> label();

		ValueProvider<CCSSCoverageBar, CmList<String>> assignmentStdNames();

		ValueProvider<CCSSCoverageBar, CmList<String>> lessonStdNames();

		ValueProvider<CCSSCoverageBar, CmList<String>> quizStdNames();

		@Path("label")
		ModelKeyProvider<CCSSCoverageBar> labelKey();
    }

	private static final DataPropertyAccess dataAccess = GWT.create(DataPropertyAccess.class);

	List<CCSSCoverageBar> ccssData;
	String _title;


	public CCSSCoverageBarChart(String title, List<CCSSCoverageBar> data) {
		this.ccssData = data;
		this._title = title;
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

		column.addSeriesSelectionHandler(new SeriesSelectionHandler<CCSSCoverageBar>() {

			@Override
			public void onSeriesSelection(
					SeriesSelectionEvent<CCSSCoverageBar> event) {

				int index = event.getIndex()/3;
	            ValueProvider<? super CCSSCoverageBar, ? extends Number> valueProvider = event.getValueProvider();
	            CCSSCoverageBar ccssBar = store.get(index);
	            int value = valueProvider.getValue(ccssBar).intValue();
	            List<String> stdNames = null;
	            String label = "";
	            if (value == ccssBar.getAssignments()) {
	            	label = "Assignments - ";
	            	stdNames = ccssBar.getAssignmentStdNames();
	            }
	            else if (value == ccssBar.getLessons()) {
	            	label = "Lessons - ";
	            	stdNames = ccssBar.getLessonStdNames();
	            }
	            else if (value == ccssBar.getQuizzes()) {
	            	label = "Quizzes - ";
	            	stdNames = ccssBar.getQuizStdNames();
	            }
	            StandardListDialog dialog = new StandardListDialog(label + ccssBar.getLabel());
	            dialog.loadStandards(stdNames);
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

		BorderLayoutContainer container = new BorderLayoutContainer();
		
        CenterLayoutContainer northContainer = new CenterLayoutContainer();
        northContainer.add(new HTML(_title));
        northContainer.setStyleName("bar-chart-title");
        container.setNorthWidget(northContainer, new BorderLayoutData(15));

		chart.setLayoutData(new VerticalLayoutData(1, 1));
		container.setCenterWidget(chart);

		panel.add(container);

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

		int fudge = 1;
		
		do {
			fudge *= 10;
		} while (fudge < max);

		if (fudge > 10) {
			int adjust = fudge/10;
			 while (fudge > (max+adjust)) {
				fudge -= adjust;
			}
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
		this._title = title;
	}

}
