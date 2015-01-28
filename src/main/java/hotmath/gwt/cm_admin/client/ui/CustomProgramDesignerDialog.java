package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.CustomProgramAddQuizDialog.Callback;
import hotmath.gwt.cm_admin.client.ui.list.ListCustomLesson;
import hotmath.gwt.cm_admin.client.ui.list.ListCustomLesson.CallbackOnDoubleClick;
import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel.Type;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.model.IntValueHolder;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction.ActionType;
import hotmath.gwt.shared.client.rpc.action.CustomProgramUsageCountAction;
import hotmath.gwt.shared.client.rpc.action.DeleteCustomQuizAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.ListViewDropTarget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

public class CustomProgramDesignerDialog extends GWindow {

	CmAdminModel adminModel;
	CustomProgramModel customProgram;
	CmAsyncRequest callback;
	boolean _isEditable = false;
	boolean _verifyIsEditable = false;
	String programName = "";

	public CustomProgramDesignerDialog(CmAdminModel adminModel,
			CmAsyncRequest callback) {
		this(adminModel, callback, null, false);
	}

	public CustomProgramDesignerDialog(CmAdminModel adminModel,
			CmAsyncRequest callback, CustomProgramModel program, boolean asCopy) {
		super(false);

		this.adminModel = adminModel;
		this.callback = callback;
		this.customProgram = program;

		setStyleName("custom-prescription-dialog");
		setHeadingText("Catchup Math Custom Program Designer");

		setModal(true);
		setPixelSize(640, 480);

		// draw onto _main borderlayout
		buildGui();

		/**
		 * if debug mode, the always allow edit boolean isDebug =
		 * CmCore.isDebug() == true;
		 */

		if (customProgram != null) {
			loadCustomProgramDefinition(customProgram);
			programName = customProgram.getProgramName();
			if (asCopy) {
				_isEditable = true;
				customProgram = new CustomProgramModel();
				customProgram.setIsTemplate(false);
				customProgram.setInUseCount(0);
				customProgram.setAssignedCount(0);
				programName = "Copy of " + program.getProgramName();
				customProgram.setProgramName(programName);
			} else if (customProgram.getIsTemplate()) {
				_isEditable = false;
			} else if (customProgram.getIsArchived() == true) {
				_isEditable = false;
			} else if (customProgram.getAssignedCount() == 0) {
				_verifyIsEditable = true;
			}
		} else {
			createNewProgram();
			_programName.setValue(customProgram.getProgramName());
			_isEditable = true;
			programName = "";
		}

		if (_verifyIsEditable) {
			new RetryAction<IntValueHolder>() {
				@Override
				public void attempt() {
					CustomProgramUsageCountAction action = new CustomProgramUsageCountAction(
							customProgram.getProgramId());
					setAction(action);
					CmRpcCore.getCmService().execute(action, this);
				}

				@Override
				public void oncapture(IntValueHolder count) {
					if (count.getValue() == 0) {
						_isEditable = true;
					}
					_main.setNorthWidget(
							createInfoSection(_isEditable, programName),
							new BorderLayoutData(45));
					enableForm(_isEditable);
					setVisible(true);
				};
			}.register();
		} else {
			_main.setNorthWidget(createInfoSection(_isEditable, programName),
					new BorderLayoutData(50));
			enableForm(_isEditable);
			setVisible(true);
		}

		// if(isDebug)
		// enableForm(true);
		// else

	}

	CallbackOnDoubleClick callDbl = new CallbackOnDoubleClick() {
        @Override
        public void doubleClicked(CustomLessonModel lessonModel) {
            // CmMessageBox.showAlert("List was double clicked");
        }
    };
	ListView<CustomLessonModel, String> _listAll = new ListCustomLesson(callDbl);
	ListView<CustomLessonModel, String> _listSelected = new ListCustomLesson(callDbl);
	ListView<CustomLessonModel, String> _listCustomPrograms = new ListCustomLesson(callDbl);
	TextButton _btnClearAll, _btnSave;
	TextField _programName = new TextField();

	BorderLayoutContainer _main = new BorderLayoutContainer();

	private void buildGui() {

		// String template =
		// "<tpl for=\".\"><div class='x-view-item {customProgramItemClass}'><span style='font-size:.5em;width: 5px;' class='{subjectStyleClass}'>&nbsp;</span>&nbsp;{"
		// + "customProgramItem" + "}</div></tpl>";
		// _listAll.setTemplate(template);
		// set cell

		// _listSelected.setTemplate(template);
		// set cell

		new ListViewDropTarget(_listAll);
		ListViewDropTarget target = new ListViewDropTarget(_listSelected);
		target.setFeedback(Feedback.INSERT);
		target.setAllowSelfAsSource(true);

		// RowData data = new RowData(.5, 1);
		// data.setMargins(new Margins(5));

		MyListContainer sectionList = new MyListContainer(_listSelected,
				"Selections in Program", false);

		MyListContainer allList = new MyListContainer(_listAll,
				"All Available Lessons", true);

		BorderLayoutContainer centerPanel = new BorderLayoutContainer();
		centerPanel.setWestWidget(allList, new BorderLayoutData(300));
		BorderLayoutData bdata = new BorderLayoutData();
		bdata.setSplit(true);
		centerPanel.setCenterWidget(sectionList, bdata);

		_main.setCenterWidget(centerPanel);

		_btnClearAll = new TextButton("Clear All", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				getAllLessonData();
				_listSelected.getStore().clear();
			}
		});
		_btnClearAll.setEnabled(false);

		addButton(_btnClearAll);

		_btnSave = new TextButton("Save", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				saveChanges(customProgram);
			}
		});
		_btnSave.setEnabled(false);
		addButton(_btnSave);

		addButton(new TextButton("Cancel", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				close();
			}
		}));

		BorderLayoutContainer bLayout = new BorderLayoutContainer();
		bLayout.setCenterWidget(_main);
		bLayout.setSouthWidget(getLegend(), new BorderLayoutData(40));

		getAllLessonData();

		setWidget(bLayout);
	}

	static public Widget getLegend() {
		String legend = "<div qtip='This color indicates highest appropriate level for the lesson' style='text-align: center;width: 350px;'>"
				+ "<div style='margin-right: 3px;float: left;' class='ess'>Essentials</div>"
				+ "<div style='margin-right: 3px;float: left;' class='pre-alg'>Pre-Algebra</div>"
				+ "<div style='margin-right: 3px;float: left;' class='alg1'>Algebra 1</div>"
				+ "<div style='margin-right: 3px;float: left;' class='geom'>Geometry</div>"
				+ "<div style='margin-right: 3px;float: left;' class='alg2'>Algebra 2</div>"
				+ "</div>";

		CenterLayoutContainer clc = new CenterLayoutContainer();

		HTML html = new HTML(legend);
		QuickTip tip = new QuickTip(html);

		clc.setWidget(html);

		return clc;
	}

	private void addCustomQuizAuto() {
		try {
			addCustomQuizAutoAux();
		} catch (Exception e) {
			CmLogger.error("Error creating custom quiz: ", e);
			CmMessageBox.showAlert("Error creating custom quiz");
		}
	}

	private void addCustomQuizAutoAux() throws Exception {
		List<CustomLessonModel> cpLessons = _listSelected.getStore().getAll();
		if (cpLessons.size() == 0) {
			throw new Exception("Custom program must have at least one lesson.");
		}

		if (cpLessons.get(cpLessons.size() - 1).getCustomProgramType() == Type.QUIZ) {
			throw new Exception(
					"Custom program already has a quiz as last item.");
		}

		CustomLessonModel autoQuiz = new CustomLessonModel(0, "Auto Quiz",
				true, false, false, null);
		_listSelected.getStore().add(autoQuiz);
	}

	/**
	 * Sets up a legend in the button bar showing the various subject levels
	 * 
	 * @param bar
	 */
	static public void createButtonBarLedgend(ButtonBar bar) {
		String legend = "<div style='position: absolute; top: 2px; right: 500px;width: 350px;'>"
				+ "<div style='margin-right: 3px;float: left;' class='ess'>Essentials</div>"
				+ "<div style='margin-right: 3px;float: left;' class='pre-alg'>Pre-Algebra</div>"
				+ "<div style='margin-right: 3px;float: left;' class='alg1'>Algebra 1</div>"
				+ "<div style='margin-right: 3px;float: left;' class='geom'>Geometry</div>"
				+ "<div style='margin-right: 3px;float: left;' class='alg2'>Algebra 2</div>"
				+ "</div>";

		bar.getElement().getParentElement().getStyle()
				.setProperty("position", "relative");
		HTML html = new HTML(legend);
		QuickTip tip = new QuickTip(html);
		tip.setToolTip("This color indicates highest appropriate level for the lesson.");
		bar.add(html);
	}

	/**
	 * Enable form and prepare for editing.
	 * 
	 * initially form is read-only, only after checking that the program is not
	 * 'in-use' is it enabled.
	 */
	private void enableForm(boolean yn) {

		if (CmCore.isDebug() == true) {
			yn = true;
		}

		if (yn) {
			new ListViewDragSource(_listAll);
			new ListViewDragSource(_listSelected);
			new ListViewDragSource(_listCustomPrograms);

			_btnSave.setEnabled(true);
			_btnClearAll.setEnabled(true);
			_programName.setEnabled(true);
		} else {

		}
	}

	private Widget createInfoSection(boolean isEditable, String programName) {

		FramedPanel frame = new FramedPanel();
		frame.setWidth(340);
		frame.setHeaderVisible(false);
		frame.setBorders(false);
		frame.setBodyBorder(false);
		// _programName.setFieldLabel("Program Name");
		_programName.setValue(programName);
		_programName.setEnabled(false);

		BorderLayoutContainer borLay = new BorderLayoutContainer();

		String msg = null;
		if (isEditable)
			msg = "Drag and drop lessons from the left side to create and reorder the custom program";
		else {
			if (customProgram != null && customProgram.getIsTemplate())
				msg = "<span style='color: red;font-weight: bold'>This is a Built-in Custom Program.  You can make a copy to customize.</span>";
			else if (customProgram.getIsArchived() == false)
				msg = "<span style='color: red;font-weight: bold'>This program is in use and may not be edited.  You can archive it or make a copy to customize.</span>";
			else
				msg = "<span style='color: red;font-weight: bold'></span>";
		}
		HTML html = new HTML(
				"<p style='float: right;width: 300px;margin-left: 20px;padding: 0px 10px; '>"
						+ msg + "</p>");
		borLay.setEastWidget(html, new BorderLayoutData(.60));
		borLay.setWestWidget(new FieldLabel(_programName, "Program Name"),
				new BorderLayoutData(.4));
		_programName.focus();
		frame.setWidget(borLay);

		return frame;
	}

	/**
	 * Create a new empty custom program
	 * 
	 */
	private void createNewProgram() {
		_programName.setValue("");
		_listSelected.getStore().clear();
		this.customProgram = new CustomProgramModel();
		this.customProgram.setProgramName("");
	}

	static CmList<CustomLessonModel> __allLessons;

	private void getAllLessonData() {
		if (__allLessons != null) {
			_listAll.getStore().clear();
			_listAll.getStore().addAll(__allLessons);
			return;
		}

		new RetryAction<CmList<CustomLessonModel>>() {
			@Override
			public void attempt() {
				CmBusyManager.setBusy(true);
				CustomProgramAction action = new CustomProgramAction(
						ActionType.GET_ALL_LESSONS);
				setAction(action);
				CmRpcCore.getCmService().execute(action, this);
			}

			@Override
			public void oncapture(CmList<CustomLessonModel> allLessons) {
				CmBusyManager.setBusy(false);
				__allLessons = allLessons;
				_listAll.getStore().clear();
				_listAll.getStore().addAll(__allLessons);
			}
		}.register();
	}

	private void loadCustomProgramDefinition(final CustomProgramModel program) {

		if (_programName != null)
			_programName.setValue(program.getProgramName());

		new RetryAction<CmList<CustomLessonModel>>() {
			@Override
			public void attempt() {
				CmBusyManager.setBusy(true);
				CustomProgramAction action = new CustomProgramAction(
						ActionType.GET_CUSTOM_PROGRAM);
				action.setProgramId(program.getProgramId());
				setAction(action);
				CmRpcCore.getCmService().execute(action, this);
			}

			@Override
			public void oncapture(CmList<CustomLessonModel> lessons) {
				/**
				 * make sure lessons selected are NOT in all
				 * 
				 */
				ListStore<CustomLessonModel> s = _listAll.getStore();
				for (int i = 0, t = lessons.size(); i < t; i++) {
					CustomLessonModel clm = lessons.get(i);
					for (int ii = 0, tt = s.size(); ii < tt; ii++) {
						CustomLessonModel clm2 = s.get(ii);
						if (clm2 == null)
							continue;

						if (clm2.equals(clm)) {
							s.remove(clm2);
						}
					}
				}
				_listSelected.getStore().clear();
				_listSelected.getStore().addAll(lessons);

				CmBusyManager.setBusy(false);
			}
		}.register();
	}

	private void saveChanges(final CustomProgramModel program) {

		// validate ..
		String pn = _programName.getValue();
		if (pn == null || pn.length() == 0) {
			CmMessageBox.showAlert("Please enter a custom program name");
			return;
		}

		if (_listSelected.getStore().size() == 0) {
			CmMessageBox
					.showAlert("There are no lessons assigned to this custom program.");
			return;
		}

		new RetryAction<CmList<CustomLessonModel>>() {
			@Override
			public void attempt() {
				CmBusyManager.setBusy(true);
				CustomProgramAction action = new CustomProgramAction();
				if (program.getProgramId() == null)
					action.setAction(ActionType.CREATE);
				else
					action.setAction(ActionType.SAVE);
				action.setAdminId(adminModel.getUid());
				action.setProgramId(program.getProgramId());
				action.setProgramName(_programName.getValue());
				action.setLessons(new CmArrayList<CustomLessonModel>(
						_listSelected.getStore().getAll()));
				setAction(action);
				CmRpcCore.getCmService().execute(action, this);
			}

			public void onFailure(Throwable error) {
				CmBusyManager.setBusy(false);
				String msg = error.getMessage();
				if (msg != null) {
					if (msg.indexOf("Duplicate") > -1) {
						CmMessageBox
								.showAlert("Could not save Custom Program: duplicate program name");
					} else if (msg.indexOf("invalid") > -1) {
						CmMessageBox.showAlert(msg);
					} else {
						super.onFailure(error);
					}
				} else {
					super.onFailure(error);
				}
			}

			@Override
			public void oncapture(CmList<CustomLessonModel> value) {
				CmLogger.info("CustomProgramModel Save complete: " + value);
				CmBusyManager.setBusy(false);
				callback.requestComplete("refresh");
				close();
			}
		}.attempt();
	}

	private void loadCustomQuizDefinitions() {

		new RetryAction<CmList<CustomQuizDef>>() {
			@Override
			public void attempt() {
				CmBusyManager.setBusy(true);
				GetCustProgQuizDefsAction action = new GetCustProgQuizDefsAction(
						adminModel.getUid());
				setAction(action);
				CmRpcCore.getCmService().execute(action, this);
			}

			@Override
			public void oncapture(CmList<CustomQuizDef> defs) {
				CmBusyManager.setBusy(false);
				List<CustomLessonModel> gmodels = new ArrayList<CustomLessonModel>();
				for (int i = 0, t = defs.size(); i < t; i++) {
					gmodels.add(new CustomLessonModel(defs.get(i).getQuizId(),
							defs.get(i).getQuizName(), defs.get(i)
									.isAnswersViewable(),
							defs.get(i).isInUse(), defs.get(i).isArchived(),
							defs.get(i).getArchiveDate()));
				}
				_listCustomPrograms.getStore().clear();
				_listCustomPrograms.getStore().addAll(gmodels);
			}
		}.register();
	}

	private void removeCustomQuiz(final CustomQuizDef def) {

		new RetryAction<RpcData>() {
			@Override
			public void attempt() {
				CmBusyManager.setBusy(true);
				DeleteCustomQuizAction action = new DeleteCustomQuizAction(
						adminModel.getUid(), def.getQuizId());
				setAction(action);
				CmRpcCore.getCmService().execute(action, this);
			}

			@Override
			public void oncapture(RpcData result) {
				CmBusyManager.setBusy(false);
				loadCustomQuizDefinitions();
			}
		}.register();
	}

	private ContentPanel createCustomQuizzesPanel() {

		ContentPanel cpPanel = new ContentPanel();

		cpPanel.getHeader().addTool(
				new TextButton("Create", new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						new CustomProgramAddQuizDialog(adminModel.getUid(), new Callback() {
							@Override
							public void quizCreated() {
								loadCustomQuizDefinitions();
							}
						}, null, false);
					}
				}));

		cpPanel.getHeader().addTool(new TextButton("Edit", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				CustomLessonModel quiz = _listCustomPrograms
						.getSelectionModel().getSelectedItem();
				CustomQuizDef def = new CustomQuizDef(quiz.getQuizId(), quiz
						.getQuiz(), adminModel.getUid(), quiz
						.getIsAnswersViewable(), quiz.getIsInUse(), quiz
						.getIsArchived(), quiz.getArchiveDate());
				new CustomProgramAddQuizDialog(adminModel.getUid(), new Callback() {
					@Override
					public void quizCreated() {
						loadCustomQuizDefinitions();
					}
				}, def, false);
			}
		}));

		cpPanel.getHeader().addTool(
				new TextButton("Delete", new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						CustomLessonModel quiz = _listCustomPrograms
								.getSelectionModel().getSelectedItem();
						final CustomQuizDef def = new CustomQuizDef(quiz
								.getQuizId(), quiz.getQuiz(), adminModel
								.getUid(), quiz.getIsAnswersViewable(), quiz
								.getIsInUse(), quiz.getIsArchived(), quiz
								.getArchiveDate());

						CmMessageBox.confirm("Delete Custom Quiz?",
								"Are you sure you want to delete custom quiz '"
										+ def.getQuizName() + "'?",
								new ConfirmCallback() {
									@Override
									public void confirmed(boolean yesNo) {
										if (yesNo) {
											removeCustomQuiz(def);
										}
									}
								});
					}
				}));

		// _listCustomPrograms.setDisplayProperty("customProgramItem");
		// _listCustomPrograms.setStore(new ListStore<CustomLessonModel>());
		cpPanel.setWidget(_listCustomPrograms);

		return cpPanel;
	}

	static class MyListContainer extends ContentPanel {
		MyListContainer(ListView<CustomLessonModel, String> listView,
				String title, boolean showFilter) {
			super();

			setHeadingText(title);
			setWidget(listView);

			if (showFilter) {
				getHeader().addTool(new MyFilterBox(listView));
			}
		}
	}

	static class MyFilterBox extends FlowLayoutContainer {

		List<CustomLessonModel> selectedLessons = new ArrayList<CustomLessonModel>();

		int lastChecked = 0;

		ListView<CustomLessonModel, String> listView;

		private TextField _filterText;

		MyFilterBox(ListView<CustomLessonModel, String> listView) {

			this.listView = listView;
			setWidth(80);
			
			_filterText = new TextField();
			_filterText.setWidth(50);
			_filterText.setEmptyText("--find--");

            TextButton btn = new TextButton("!", new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					doFilter();
				}
			});
			btn.getElement().getStyle().setFloat(Float.RIGHT);
            add(btn);
            add(_filterText);
            
			setToolTip("Search for a lesson containing text");

			_filterText.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					doFilter();
				}
			});
		}

		private void doFilter() {
			String value = _filterText.getValue();
			if (value == null || value.length() == 0) {
				listView.getStore().clear();
				listView.getStore().addAll(__allLessons);
				return;
			}

			selectedLessons.clear();
			for (CustomLessonModel model : __allLessons) {
				if (model.getCustomProgramItem().toLowerCase()
						.indexOf(value.toLowerCase()) > -1) {
					selectedLessons.add(model);
				}
			}
			listView.getStore().clear();
			listView.getStore().addAll(selectedLessons);
		}
	}

}
