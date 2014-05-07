package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

public class WidgetEditorImplMultiChoice extends ContentPanel implements
		WidgetEditor {

	private WidgetDefModel widgetDef;

	public WidgetEditorImplMultiChoice(WidgetDefModel widgetDef) {
		this.widgetDef = widgetDef;
	}

	MultiGridProps props = GWT.create(MultiGridProps.class);

	boolean areChanges;

	Grid<MultiValue> _grid;

	protected void buildUi() {
		final ListStore<MultiValue> store = new ListStore<MultiValue>(
				props.key());
		List<ColumnConfig<MultiValue, ?>> cols = new ArrayList<ColumnConfig<MultiValue, ?>>();
		ColumnConfig<MultiValue, String> valCol = new ColumnConfig<MultiValue, String>(props.valueDecoded(), 150, "Value");
		valCol.setToolTip(SafeHtmlUtils.fromString("Use drag and drop to order the choices"));
		ColumnConfig<MultiValue, Boolean> correctCol = new ColumnConfig<MultiValue, Boolean>(props.correct(), 40, "Is Correct?");
		correctCol.setCell(new SimpleSafeHtmlCell<Boolean>(new AbstractSafeHtmlRenderer<Boolean>() {
		        @Override
		        public SafeHtml render(Boolean object) {
		          return SafeHtmlUtils.fromString(object ? "True" : "False");
		        }
		      }));
		correctCol.setToolTip(SafeHtmlUtils.fromString("Which choice is the correct one?"));

		cols.add(valCol);
		cols.add(correctCol);

		store.setAutoCommit(true);

		ColumnModel<MultiValue> colModel = new ColumnModel<MultiValue>(cols);
		_grid = new Grid<MultiValue>(store, colModel);
		_grid.setHeight(130);
		_grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		_grid.getView().setAutoExpandColumn(colModel.getColumn(0));
		_grid.getView().setAutoFill(true);
		_grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellClick(CellDoubleClickEvent event) {
				editSelectedChoice();
			}
		});

		GridEditing<MultiValue> editor = new GridInlineEditing<MultiValue>(_grid);
		editor.addCompleteEditHandler(new CompleteEditHandler<MultiValue>() {
			@Override
			public void onCompleteEdit(CompleteEditEvent<MultiValue> event) {
				areChanges = true;
				MultiValue ch = store.getAll()
						.get(event.getEditCell().getRow());

				/** only one can be selected */
				if (ch.isCorrect()) {
					for (MultiValue s : store.getAll()) {
						if (s != ch) {
							s.setCorrect(false);
							store.update(s);
						}
					}
				}
			}
		});
		editor.addEditor(correctCol, new CheckBox());

		Grid<MultiValue> eg = editor.getEditableGrid();
		eg.getView().setAutoFill(true);
		eg.getView().setAutoExpandColumn(cols.get(0));
		setWidget(editor.getEditableGrid());

		/** enable drag and drop */
		new GridDragSource(_grid);
		GridDropTarget dt = new GridDropTarget(_grid);
		dt.setFeedback(Feedback.BOTH);
		dt.setAllowSelfAsSource(true);

		addTool(new TextButton("Add Choice", new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				addChoice();
			}

		}));
		addTool(new TextButton("Remove Choice", new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				removeSelectedChoice();
			}
		}));

		addTool(new TextButton("Edit", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				editSelectedChoice();
			}
		}));
	}

	@Override
	public void setupValue() {
		String choiceData = getWidgetDef().getValue();
		if (choiceData == null) {
			choiceData = "";
		}
		String c[] = choiceData.split("\\|");
		int correctIndex = 0;
		if (c.length > 1) {
			correctIndex = Integer.parseInt(c[c.length - 1]);
			// convert to one base
			if (correctIndex > 0) {
				correctIndex--;
			}
			for (int i = 0, t = c.length; i < (t - 1); i++) {
				_grid.getStore().add(new MultiValue(c[i], false));
			}
			if (correctIndex < _grid.getStore().size()) {
				_grid.getStore().get(correctIndex).setCorrect(true);
			}
		}
	}

	protected void editSelectedChoice() {
		final MultiValue sel = _grid.getSelectionModel().getSelectedItem();
		if (sel != null) {
			ProblemDesignerEditor.getSharedWindow().show(
					CmGwtUtils.jsni_decodeBase64(sel.getValue()),
					"widget_multi_choice",
					new ProblemDesignerEditor.EditorCallback() {
						@Override
						public void editingComplete(String pidEdit,
								String textPartPlusWhiteboardJson) {
							sel.setValue(CmGwtUtils
									.jsni_encodeBase64(textPartPlusWhiteboardJson));
							_grid.getStore().update(sel);
						}
					});
		}
	}

	@Override
	public Widget asWidget() {
		buildUi();
		return this;
	}

	protected String getWidgetType() {
		return "mChoice";
	}

	protected void removeSelectedChoice() {
		if (selected() != null) {

			int index = _grid.getStore().indexOf(selected());
			_grid.getStore().remove(selected());

			if (_grid.getStore().size() > 0) {
				if (index > _grid.getStore().size() - 1) {
					index = _grid.getStore().size() - 1;
				}
				_grid.getSelectionModel().select(index, false);
			}

		}
	}

	private void addChoice() {
		ProblemDesignerEditor.getSharedWindow().show("", "widget_multi_choice",
				new ProblemDesignerEditor.EditorCallback() {
					@Override
					public void editingComplete(String pidEdit,
							String textPartPlusWhiteboardJson) {
						_grid.getStore()
								.add(new MultiValue(
										CmGwtUtils
												.jsni_encodeBase64(textPartPlusWhiteboardJson),
										false));
					}
				});
	}

	private MultiValue selected() {
		if (_grid.getSelectionModel().getSelectedItem() == null) {
			CmMessageBox.showAlert("no value selected");
			return null;
		} else {
			return _grid.getSelectionModel().getSelectedItem();
		}
	}

	protected WidgetDefModel createWidgetDefModel() {
		WidgetDefModel wd = new WidgetDefModel();
		wd.setType(getWidgetType());
		wd.setValue(getValueString());
		wd.setUseBase64(true);
		return wd;
	}

	private String getValueString() {

		String value = "";
		List<MultiValue> vals = _grid.getStore().getAll();
		int correctSel = 0;
		for (int i = 0, t = vals.size(); i < t; i++) {
			if (value.length() > 0) {
				value += "|";
			}

			MultiValue v = vals.get(i);
			value += v.getValue();

			if (v.isCorrect) {
				correctSel = i;
			}
		}
		value += "|" + (correctSel + 1); // one base
		return value;
	}

	static class MultiValue {
		static int __key;
		int key;
		String value;
		private boolean isCorrect;

		public MultiValue(String value, boolean isCorrect) {
			this.value = value;
			this.isCorrect = isCorrect;
			this.key = __key++;
		}

		public int getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public boolean isCorrect() {
			return isCorrect;
		}

		public void setCorrect(boolean isCorrect) {
			this.isCorrect = isCorrect;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getValueDecoded() {

			Element elem = DOM.createElement("div");
			String value = CmGwtUtils.jsni_decodeBase64(this.value);
			elem.setInnerHTML(value);

			String t = elem.getInnerText();
			int s = t.indexOf("[{");
			if (s > -1) {
				int e = t.indexOf("}]");
				if (e > -1) {
					t = t.substring(0, s) + " [Whiteboard]";
				}
			}

			if (value.toLowerCase().indexOf("wiris") > -1) {
				t = "[Equation] " + t;
			}

			return t;
		}
	}

	interface MultiGridProps extends PropertyAccess<String> {
		ModelKeyProvider<MultiValue> key();

		ValueProvider<MultiValue, String> valueDecoded();

		ValueProvider<MultiValue, Boolean> correct();

		ValueProvider<MultiValue, String> value();
	}

	@Override
	public String getWidgetJson() {
		WidgetDefModel widget = createWidgetDefModel();
		return widget.getJson();
	}

	@Override
	public WidgetDefModel getWidgetDef() {
		return widgetDef;
	}

	@Override
	public String checkValid() {
		if (_grid.getStore().getAll().size() < 2) {
			return "There must be at least two choices.";
		}

		int correctAnswers = 0;
		for (MultiValue mv : _grid.getStore().getAll()) {
			if (mv.isCorrect) {
				correctAnswers++;
			}
		}
		if (correctAnswers != 1) {
			return "There must be exactly one correct answer.";
		}
		return null;
	}

	@Override
	public String getDescription() {
		return "Select from a set of choices.<br/><br/><br/><br/>Use drag-and-drop to order choices";
	}

	@Override
	public String getValueLabel() {
		return null;
	}

	@Override
	public String getWidgetTypeLabel() {
		return "Multiple Choice";
	}

	@Override
	public String getWidgetValueLabel() {
		String choiceData = getWidgetDef().getValue();
		if (choiceData == null) {
			choiceData = "";
		}
		String c[] = choiceData.split("\\|");
		return "1 of " + (c.length - 1);
	}
}
