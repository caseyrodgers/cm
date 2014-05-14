package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.BaseDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.TreeGridDragSource;
import com.sencha.gxt.dnd.core.client.TreeGridDropTarget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

public class CustomProblemTreeTable extends SimpleContainer {

	final TreeGrid<BaseDto> _tree;

	class KeyProvider implements ModelKeyProvider<BaseDto> {
		@Override
		public String getKey(BaseDto item) {
			return item.getId() + "";// (item instanceof FolderDto ? "f-" :
										// "m-") + item.getId().toString();
		}
	}

	public interface DataProperties extends PropertyAccess<BaseDto> {
		@Path("id")
		ModelKeyProvider<BaseDto> key();

		ValueProvider<BaseDto, String> name();
		// ValueProvider<BaseDto, String> value();
	}

	TreeStore<BaseDto> _store;
	CustomProblemFolderNode _root;
	private TreeTableCallback selectedCallback;

	public interface TreeTableCallback {
		void problemSelected(CustomProblemModel problem);

		void redrawUi();
	}

	public CustomProblemTreeTable(List<CustomProblemModel> problems,
			TreeTableCallback selectedCallbackIn) {
		this.selectedCallback = selectedCallbackIn;

		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("TreeGrid");
		panel.addStyleName("margin-10");
		panel.setPixelSize(600, 300);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.setBorders(true);
		panel.add(v);

		DataProperties props = GWT.create(DataProperties.class);

		_store = new TreeStore<BaseDto>(new KeyProvider());
		_root = new CustomProblemFolderNode("Teachers");
		_store.add(_root);

		// for each distinct teacher
		Map<Integer, List<CustomProblemModel>> teachers = new HashMap<Integer, List<CustomProblemModel>>();

		CustomProblemFolderNode teacherNode = null;
		for (int i = 0; i < problems.size(); i++) {
			CustomProblemModel p = problems.get(i);

			List<CustomProblemModel> teacherProbs = teachers.get(p.getTeacher()
					.getTeacherId());
			if (teacherProbs == null) {
				teacherProbs = new ArrayList<CustomProblemModel>();
				teachers.put(p.getTeacher().getTeacherId(), teacherProbs);

				teacherNode = new CustomProblemFolderNode(p.getTeacherName());
				_root.addChild(teacherNode);
			}

			if (teacherNode != null) {
				teacherNode.addChild(new CustomProblemLeafNode(p));
			}
		}
		processFolder(_store, _root);

		//
		//
		// CustomProblemFolderNode firstNode = new
		// CustomProblemFolderNode("Casey");
		// for(int i=0;i<problems.size();i++) {
		// CustomProblemModel cp = problems.get(i);
		// firstNode.addChild(new CustomProblemLeafNode(cp));
		// }
		// _root.addChild(firstNode);

		ColumnConfig<BaseDto, String> cc1 = new ColumnConfig<BaseDto, String>(
				props.name(), 120, "Problem");

		ColumnConfig<BaseDto, String> cc2 = new ColumnConfig<BaseDto, String>(
				new ValueProvider<BaseDto, String>() {
					@Override
					public String getValue(BaseDto object) {
						return object instanceof CustomProblemLeafNode ? ((CustomProblemLeafNode) object)
								.getCustomProblem().getComments() : "";
					}

					@Override
					public void setValue(BaseDto object, String value) {
						CmMessageBox.showAlert("Set value: " + value);
					}

					@Override
					public String getPath() {
						return "comment";
					}
				});
		cc2.setHeader("Comment");
		ColumnConfig<BaseDto, String> cc3 = new ColumnConfig<BaseDto, String>(
				new ValueProvider<BaseDto, String>() {
					@Override
					public String getValue(BaseDto object) {
						return object instanceof CustomProblemLeafNode ? ((CustomProblemLeafNode) object)
								.getCustomProblem().getLessonList() : "";
					}

					@Override
					public void setValue(BaseDto object, String value) {
					}

					@Override
					public String getPath() {
						return "lesson";
					}
				});
		cc3.setHeader("Lessons");

		List<ColumnConfig<BaseDto, ?>> l = new ArrayList<ColumnConfig<BaseDto, ?>>();
		l.add(cc1);
		l.add(cc2);
		l.add(cc3);
		ColumnModel<BaseDto> cm = new ColumnModel<BaseDto>(l);

		_tree = new TreeGrid<BaseDto>(_store, cm, cc1);
		_tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		_tree.getSelectionModel().addSelectionHandler(
				new SelectionHandler<BaseDto>() {
					@Override
					public void onSelection(SelectionEvent<BaseDto> event) {

						if (event.getSelectedItem() instanceof CustomProblemLeafNode) {
							selectedCallback
									.problemSelected(((CustomProblemLeafNode) event
											.getSelectedItem())
											.getCustomProblem());
						}
					}
				});

		new TreeGridDragSource(_tree);
		TreeGridDropTarget dt = new TreeGridDropTarget(_tree);
		dt.setFeedback(Feedback.BOTH);
		dt.setAllowSelfAsSource(true);

		// tree.getStyle().setLeafIcon(ExampleImages.INSTANCE.music());

		ToolBar buttonBar = new ToolBar();

		buttonBar.add(new TextButton("Expand All", new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				_tree.expandAll();
			}
		}));
		buttonBar.add(new TextButton("Collapse All", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				_tree.collapseAll();
			}
		}));

		v.add(buttonBar, new VerticalLayoutData(1, -1));
		v.add(_tree, new VerticalLayoutData(1, 1));

		// v.getElement().setAttribute("style",
		// "height: 100%;border: 3px solid black");
		setWidget(v);

	}

	public void setTreeSelections(final CustomProblemModel selectedProblem) {
		
		new Timer() {
			
			@Override
			public void run() {
				/** Expose the root items */
				List<BaseDto> rootItems = _tree.getTreeStore().getRootItems();
				_tree.setExpanded((BaseDto) rootItems.get(0), true, false);
				
				if (selectedProblem != null) {
					BaseDto selectedNode1 = (BaseDto) ((FolderDto) rootItems.get(0))
							.getChildren().get(0);
					findItemMatching((FolderDto) rootItems.get(0), selectedProblem);
				}	
				else {
					_tree.expandAll();
				}
				
				selectedCallback.redrawUi();
			}
		}.schedule(500);;

	}

	protected void expandTreeNode(CustomProblemLeafNode probNodeToExpand) {
		FolderDto teacherNode = (FolderDto) probNodeToExpand.getParent();
		_tree.setExpanded(teacherNode, true, true);

		_tree.getSelectionModel().select(probNodeToExpand, false);
		

	}

	private CustomProblemLeafNode findItemMatching(FolderDto baseDto,
			CustomProblemModel selectedProblem) {
		for (BaseDto node : baseDto.getChildren()) {
			if (node instanceof CustomProblemLeafNode) {
				CustomProblemLeafNode probNode = (CustomProblemLeafNode) node;
				if (probNode.getCustomProblem().getPid()
						.equals(selectedProblem.getPid())) {

					_tree.setExpanded(baseDto, true, true);
					_tree.setExpanded(probNode, true, true);
					_tree.getSelectionModel().select(probNode, false);

					List<BaseDto> data = _tree.getStore().getAll();
					for(int i=0;i<data.size();i++) {
						BaseDto n = data.get(i);
						if(n instanceof CustomProblemLeafNode) {
							if( ((CustomProblemLeafNode)n).getCustomProblem().getPid().equals(selectedProblem.getPid())) {
								_tree.getView().ensureVisible(i, 0, true);
								break;
							}
						}
							
					}
					
					

					return probNode;
				}
			} else if (node instanceof FolderDto) {
				node = findItemMatching((FolderDto) node, selectedProblem);
				if (node != null) {
					return (CustomProblemLeafNode) node;
				}
			}
		}
		return null;
	}

	private void processFolder(TreeStore<BaseDto> store, FolderDto folder) {
		for (BaseDto child : folder.getChildren()) {
			store.add(folder, child);
			if (child instanceof FolderDto) {
				processFolder(store, (FolderDto) child);
			}
		}
	}

	public static void doTest() {
		GWindow w = new GWindow(true);
		List<CustomProblemModel> x = new ArrayList<CustomProblemModel>();
		w.setWidget(new CustomProblemTreeTable(x, new TreeTableCallback() {
			@Override
			public void problemSelected(CustomProblemModel problem) {
				CmMessageBox.showAlert("Selected: " + problem);
			}
			
			@Override
			public void redrawUi() {
			}
		}));
		w.setVisible(true);
	}

	public TreeGrid<BaseDto> getGridView() {
		return _tree;
	}

	public TreeGrid<BaseDto> getTree() {
		return _tree;
	}

	public CustomProblemModel getSelectedCustomProblem() {
		BaseDto item = _tree.getSelectionModel().getSelectedItem();
		if (item instanceof CustomProblemLeafNode) {
			return ((CustomProblemLeafNode) item).getCustomProblem();
		} else {
			return null;
		}
	}

	/**
	 * Load problems into tree
	 * 
	 * @param problems
	 */
	public void loadProblems(List<CustomProblemModel> problems) {
		// _store.clear();
		//
		// _store = new TreeStore<BaseDto>(new KeyProvider());
		// _tree.set
		//
		// FolderDto root = new FolderDto(BaseDto.autoId++, "Root");
		// FolderDto firstNode = new CustomProblemFolderNode("Casey's Stuff");
		// _root.addChild(firstNode);

		// for(int i=0;i<100;i++) {
		// //CustomProblemModel cp = new CustomProblemModel("TEST: " +i, i, new
		// TeacherIdentity(), "TEST: " +i, ProblemType.INPUT_WIDGET);
		// //cp.getLinkedLessons().add(new LessonModel("test", "test"));
		// //firstNode.addChild(new CustomProblemLeafNode(cp));
		// }
		// processFolder(_store,root);
	}
}

class CustomProblemLeafNode extends BaseDto {
	private CustomProblemModel customProblem;

	public CustomProblemLeafNode(String name) {
		super(__nextId(), name);
	}

	public CustomProblemLeafNode(CustomProblemModel customProblem) {
		this(customProblem.getLabel());
		this.customProblem = customProblem;
	}

	public CustomProblemModel getCustomProblem() {
		return customProblem;
	}

	public void setCustomProblem(CustomProblemModel customProblem) {
		this.customProblem = customProblem;
	}
}

class CustomProblemFolderNode extends FolderDto {
	public CustomProblemFolderNode(String name) {
		super(__nextId(), name);
	}

	public String getFolderName() {
		return getName();
	}
}
