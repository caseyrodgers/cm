package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.WhiteboardTemplatesResponse;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardTemplatesAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyTextButton;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;
import hotmath.gwt.cm_tools.client.util.WhiteboardTemplatesManager;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanel2Callback;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanelCallbackDefault;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent.BeforeHideHandler;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent.MaximizeHandler;
import com.sencha.gxt.widget.core.client.event.ResizeEndEvent;
import com.sencha.gxt.widget.core.client.event.RestoreEvent;
import com.sencha.gxt.widget.core.client.event.RestoreEvent.RestoreHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Allows editing of a solution 'area'.
 * 
 * Contains a rich text area and optional whiteboard
 * 
 * @author casey
 * 
 */
public class ProblemDesignerEditor extends GWindow {

	private static final int WIN_HEIGHT_NO_WB = 230;
	private static final int WIN_HEIGHT_WITH_WB = 500;
	private String whiteboardId;
	private BorderLayoutContainer _main;
	private EditorCallback callback;
	private int _countChanges = 1;
	protected String _pidEdit;
	private CKEditorPanel _ckEditorPanel;
	private ToggleButton _showWhiteboardToggle;
	private String editorText;
	private AreaData areaData;

	public interface EditorCallback {
		void editingComplete(String pidEdit, String textPartPlusWhiteboardJson);
	}

	public ProblemDesignerEditor() {
		super(false);

		setWidth(600); // height depends on whiteboard shown/not shown.
		setResizable(true);
		setMaximizable(true);

		setHeadingText("Catchup Math Step Editor");
		_main = new BorderLayoutContainer();
		setWidget(_main);

		_showWhiteboardToggle = new ToggleButton("Show Whiteboard");
		_showWhiteboardToggle.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				showWhiteboardEditor(_showWhiteboardToggle.getValue());
			}
		});
		addTool(_showWhiteboardToggle);

		addButton(new MyTextButton("Save", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				doSave();
			}
		}, "Save any changes"));

		addButton(new TextButton("Cancel", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				hide();
			}
		}));

		addBeforeHideHandler(new BeforeHideHandler() {
			@Override
			public void onBeforeHide(BeforeHideEvent event) {
				if (_ckEditorPanel != null) {
					destroyEditor();
				}
			}
		});

		addMaximizeHandler(new MaximizeHandler() {
			@Override
			public void onMaximize(MaximizeEvent event) {
				resizeWidgetAndWhiteboard();
			}
		});

		addRestoreHandler(new RestoreHandler() {
			@Override
			public void onRestore(RestoreEvent event) {
				resizeWidgetAndWhiteboard();
			}
		});
	}

	private void resizeWidgetAndWhiteboard() {
		setEditorHeight();
		resizeWhiteboard();
	}

	private void destroyEditor() {
		_ckEditorPanel.destroyEditor();
		_ckEditorPanel = null;
	}

	public void show(String editorText, String whiteboardId,
			EditorCallback callbackIn) {
		this.callback = callbackIn;
		this.editorText = editorText;
		this.whiteboardId = whiteboardId;
		this.areaData = extractAreaData(editorText);

		showWhiteboardEditor(areaData.wbJson != null);

		setVisible(true);

		if (_lastWbheight > 0) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					if (_showWorkPanel != null) {
						_showWorkPanel.resizeWhiteboard(_lastWbheight);
						_ckEditorPanel.showClickToEdit(false);
					}
				}
			});
		}
	}

	@Override
	protected void onEndResize(ResizeEndEvent re) {
		super.onEndResize(re);
		resizeWidgetAndWhiteboard();
	}

	int _lastWbheight;

	private void resizeWhiteboard() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				if (_showWorkPanel != null) {
					_lastWbheight = _wbWrapper.getOffsetHeight();
					_showWorkPanel.resizeWhiteboard(_lastWbheight);
				}
			}
		});
	}

	protected void doSave() {
		String wbJson = _showWorkPanel != null ? _showWorkPanel
				.getWhiteboardCommandsAsJson() : this.areaData.wbJson;

		String wbJsonHtml = wbJson != null ? "<div class='wb_json'>" + wbJson
				+ "</div>" : "";

		String textPlusWhiteboardPlusWidget = "<div class='step_part'>"
				+ _ckEditorPanel.getEditorValue() + wbJsonHtml + "</div>";

		textPlusWhiteboardPlusWidget += this.areaData.widgetHtml;
		callback.editingComplete(_pidEdit, textPlusWhiteboardPlusWidget);
		hide();
	}

	static int _cnt;

	private void showWhiteboardEditor(final boolean showWHiteboard) {

		String textValue = "";
		if (_ckEditorPanel != null) {
			textValue = _ckEditorPanel.getEditorValue();
			destroyEditor();
		} else {
			textValue = this.areaData.textPart;
		}

		_ckEditorPanel = new CKEditorPanel("ps_editor", 120, textValue,
				new CallbackOnComplete() {

					@Override
					public void isComplete() {
						setEditorHeight();
					}
				});

		if (_showWorkPanel != null) {
			_showWorkPanel = null;
		}

		if (showWHiteboard) {

			_showWorkPanel = new ShowWorkPanel2(whiteboardCallBack, true, true,
					"wb_ps-1", 280, getWidget());

			BorderLayoutContainer bCon = new BorderLayoutContainer();

			_wbWrapper = new SimplePanel();
			_wbWrapper.setWidget(_showWorkPanel);
			bCon.setCenterWidget(_wbWrapper);

			_editorPanel = new SimplePanel();
			_editorPanel.setWidget(_ckEditorPanel);
			bCon.setNorthWidget(_editorPanel, new BorderLayoutData(.50));
			_main.setCenterWidget(bCon);
		} else {
			_editorPanel = new SimplePanel();
			_editorPanel.setWidget(_ckEditorPanel);
			_main.setCenterWidget(_editorPanel);
		}
		_showWhiteboardToggle.setValue(showWHiteboard);
		_main.forceLayout();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				/** optimize the window */
				if (!showWHiteboard) {
					setHeight(WIN_HEIGHT_NO_WB);
				} else {
					// set min height for whiteboard
					if (getElement().getHeight(true) < WIN_HEIGHT_WITH_WB) {
						setHeight(WIN_HEIGHT_WITH_WB);
					}
				}
				forceLayout();
			}
		});
	}

	SimplePanel _wbWrapper;
	SimplePanel _editorPanel;

	final ShowWorkPanel2Callback whiteboardCallBack = new ShowWorkPanelCallbackDefault() {
		@Override
		public void windowResized() {
		}

		@Override
		public void showWorkIsReady(ShowWorkPanel2 showWork) {
			resizeWhiteboard();
			if (areaData.wbJson != null) {
				showWork.loadWhiteboardFromJson(areaData.wbJson);
			}
		}

		@Override
		public Action<? extends Response> createWhiteboardSaveAction(
				String pid, CommandType commandType, String data) {
			_countChanges++;

			/** disable on any whitebaord movement */
			_ckEditorPanel.showClickToEdit(true);

			return null;
		}

		@Override
		public void saveWhiteboardAsTemplate(final ShowWorkPanel2 showWorkPanel2) {
			String tmplName = Cookies.getCookie("wb_template");
			final PromptMessageBox mb = new PromptMessageBox(
					"Save As Template", "Template Name");

			CmMessageBox.confirm("Save", "Save as template?",
					new ConfirmCallback() {
						@Override
						public void confirmed(boolean yesNo) {
							showWorkPanel2.saveAsTemplate(UserInfoBase
									.getInstance().getUid(),
									new CallbackOnComplete() {
										@Override
										public void isComplete() {
											// silent
										}
									});
						}
					});

		}

		@Override
		public void manageTemplates(ShowWorkPanel2 showWorkPanel2) {
			new WhiteboardTemplatesManager(showWorkPanel2);
		}
	};
	private ShowWorkPanel2 _showWorkPanel;

	private void setEditorHeight() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				int height = _editorPanel.getOffsetHeight();
				_ckEditorPanel.resizeEditor(height);				
			}
		});
	}

	native private String jsni_setProblemStatementHtml(String text) /*-{
																	var m = $wnd.$('.cm_problem_text');
																	if(m.length == 0) {
																	alert('no cm_problem_text element');
																	return;
																	}
																	m.html(text);
																	}-*/;

	protected void loadWhiteboardTemplates(final ShowWorkPanel2 showWork) {
		new RetryAction<WhiteboardTemplatesResponse>() {
			@Override
			public void attempt() {
				GetWhiteboardTemplatesAction action = new GetWhiteboardTemplatesAction(
						UserInfoBase.getInstance().getUid());
				setAction(action);
				CmShared.getCmService().execute(action, this);
			}

			@Override
			public void oncapture(WhiteboardTemplatesResponse templates) {
				showWork.setWhiteboardTemplates(templates
						.getJsonRepresentation());
				/** jsni_setWhiteboardTemplatesAux */
			}

		}.register();
	}

	static public void doTest() {
		ProblemDesignerEditor.getSharedWindow().show("THE HTML", "wb_id",
				new EditorCallback() {
					@Override
					public void editingComplete(String pidEdit,
							String textPlusWhiteboard) {
					}
				});
	}

	private AreaData extractAreaData(String text) {
		String checkFor = "<div class='wb_json'>";
		int p = text.indexOf(checkFor);
		String wbJson = null;
		String textPart = null;
		if (p > -1) {
			textPart = text.substring(0, p);
			wbJson = text.substring(p);
			wbJson = wbJson.substring(checkFor.length(),
					wbJson.indexOf("</div>"));
		} else {
			textPart = text;
		}

		checkFor = "<div id='hm_flash_widget'>";
		p = textPart.indexOf(checkFor);
		String widgetHtml = "";
		if (p > -1) {
			widgetHtml = text.substring(p);
			textPart = text.substring(0, p);
			// widgetHtml = wbJson.substring(checkFor.length(),
			// wbJson.indexOf("</div></div>"));
		}

		AreaData aData = new AreaData(textPart, wbJson, widgetHtml);
		return aData;
	}

	class AreaData {
		String textPart;
		String wbJson;
		String widgetHtml;

		public AreaData(String textPart, String whiteboardJson,
				String widgetHtml) {
			this.textPart = textPart;
			this.wbJson = whiteboardJson;
			this.widgetHtml = widgetHtml;
		}

	}

	static ProblemDesignerEditor __sharedInstance;

	public static ProblemDesignerEditor getSharedWindow() {
		if (__sharedInstance == null) {
			__sharedInstance = new ProblemDesignerEditor();
		}
		return __sharedInstance;
	}

}
