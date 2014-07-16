package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_admin.client.teacher.TeacherManager;
import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler.TypeOfUpdate;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.BaseDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.Insert;
import com.sencha.gxt.dnd.core.client.TreeGridDragSource;
import com.sencha.gxt.dnd.core.client.TreeGridDropTarget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;
import com.sencha.gxt.widget.core.client.treegrid.TreeGridView;

public class CustomProblemTreeTable extends SimpleContainer {

    final TreeGrid<BaseDto> _tree;
    CustomProblemFolderNode _dndTargetModel;
    CustomProblemLeafNode _dndSourceModel;

    static public class KeyProvider implements ModelKeyProvider<BaseDto> {
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
    }

    TreeStore<BaseDto> _store;
    CustomProblemFolderNode _root;
    private TreeTableCallback selectedCallback;

    public interface TreeTableCallback {
        void problemSelected(CustomProblemModel problem);

        void redrawUi();

        void problemUpdated(CustomProblemModel probToMove);
    }

    public CustomProblemTreeTable(List<CustomProblemModel> problems, List<String> paths,
            List<TeacherIdentity> _allTeachers, TreeTableCallback selectedCallbackIn) {
        this.selectedCallback = selectedCallbackIn;

        FramedPanel panel = new FramedPanel();
        panel.setHeadingText("TreeGrid");
        panel.addStyleName("margin-10");
        panel.setPixelSize(600, 300);

        VerticalLayoutContainer v = new VerticalLayoutContainer();
        v.setBorders(true);
        panel.add(v);

        DataProperties props = GWT.create(DataProperties.class);

        _store = createTeacherProblemMap(problems, paths, _allTeachers);
        //
        //
        // CustomProblemFolderNode firstNode = new
        // CustomProblemFolderNode("Casey");
        // for(int i=0;i<problems.size();i++) {
        // CustomProblemModel cp = problems.get(i);
        // firstNode.addChild(new CustomProblemLeafNode(cp));
        // }
        // _root.addChild(firstNode);

        ColumnConfig<BaseDto, String> problemCol = new ColumnConfig<BaseDto, String>(
                new ValueProvider<BaseDto, String>() {
                    @Override
                    public String getValue(BaseDto object) {
                        if (object == null) {
                            return "";
                        } else if (object instanceof CustomProblemLeafNode) {
                            return ((CustomProblemLeafNode) object).getCustomProblem().getComments();
                        } else {
                            return object.getName();
                        }
                    }

                    @Override
                    public void setValue(BaseDto object, String value) {
                        CmMessageBox.showAlert("Set value: " + value);
                    }

                    @Override
                    public String getPath() {
                        return "problem";
                    }
                });
        problemCol.setHeader("Teachers");
        problemCol.setWidth(200);
        problemCol.setSortable(false);

        ColumnConfig<BaseDto, String> lessonsCol = new ColumnConfig<BaseDto, String>(
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
        lessonsCol.setToolTip(SafeHtmlUtils.fromString("Number of lessons correlated"));
        lessonsCol.setHeader("Lessons");
        lessonsCol.setSortable(false);
        lessonsCol.setMenuDisabled(true);

        List<ColumnConfig<BaseDto, ?>> l = new ArrayList<ColumnConfig<BaseDto, ?>>();
        l.add(problemCol);
        // l.add(commentCol);
        l.add(lessonsCol);
        ColumnModel<BaseDto> cm = new ColumnModel<BaseDto>(l);

        _tree = new TreeGrid<BaseDto>(_store, cm, problemCol);

        _tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        _tree.getSelectionModel().addSelectionHandler(new SelectionHandler<BaseDto>() {
            @Override
            public void onSelection(SelectionEvent<BaseDto> event) {

                if (event.getSelectedItem() instanceof CustomProblemLeafNode) {
                    selectedCallback.problemSelected(((CustomProblemLeafNode) event.getSelectedItem())
                            .getCustomProblem());
                }
            }
        });

        final TreeGridDragSource s = new TreeGridDragSource(_tree);
        s.addDropHandler(new DndDropHandler() {
            @Override
            public void onDrop(DndDropEvent event) {
                Element itemDroppedOnElement = (Element) event.getDragEndEvent().getNativeEvent().getEventTarget()
                        .cast();
                BaseDto target = (BaseDto) _tree.findNode(itemDroppedOnElement).getModel();

                if (target instanceof CustomProblemFolderNode) {
                    _dndTargetModel = (CustomProblemFolderNode) target;

                    if (!_dndTargetModel.getTeacher().getTeacherName()
                            .equals(TeacherManager.getTeacher().getTeacherName())) {
                        CmMessageBox
                                .showAlert("You can only drag and drop into your own teacher's problems.   You can however 'Copy' other problems in.");
                        return;
                    }

                    String folder = _dndTargetModel.getFolderName();
                    CustomProblemModel probToMove = _dndSourceModel.getCustomProblem();
                    probToMove.setTreePath(folder);

                    selectedCallback.problemUpdated(probToMove);

                    // CmMessageBox.showAlert("dragging " + _dndSourceModel +
                    // ", " + _dndTargetModel);
                } else {
                    Log.info("Invalid drag target");
                }

            }
        });

        s.addDragStartHandler(new DndDragStartHandler() {
            @Override
            public void onDragStart(DndDragStartEvent event) {
                Element itemDroppedOnElement = (Element) event.getDragStartEvent().getNativeEvent().getEventTarget()
                        .cast();
                BaseDto source = (BaseDto) _tree.findNode(itemDroppedOnElement).getModel();
                if (source instanceof CustomProblemFolderNode) {
                    Log.info("Invalid drag source");
                    event.setCancelled(true);
                } else if (!((CustomProblemLeafNode) source).getCustomProblem().getTeacher().getTeacherName()
                        .equals(TeacherManager.getTeacher().getTeacherName())) {
                    Log.info("Invalid drag source");
                    event.setCancelled(true);
                } else {
                    _dndSourceModel = (CustomProblemLeafNode) source;
                }
            }
        });
        TreeGridDropTarget dt = new TreeGridDropTarget(_tree) {

            @SuppressWarnings("unchecked")
            protected void showFeedback(com.sencha.gxt.dnd.core.client.DndDragMoveEvent event) {
                TreeNode<? extends BaseDto> item = super.getWidget().findNode(
                        event.getDragMoveEvent().getNativeEvent().getEventTarget().<Element> cast());
                BaseDto node = item.getModel();
                boolean allowDrop = false;
                if (node instanceof CustomProblemFolderNode) {
                    if (((CustomProblemFolderNode) node).getTeacher().getTeacherName()
                            .equals(TeacherManager.getTeacher().getTeacherName())) {
                        allowDrop = true;
                    }
                }

                if (allowDrop) {
                    super.showFeedback(event);
                } else {
                    status = -1;
                    activeItem = null;
                    appendItem = null;
                    Insert.get().hide();
                    event.getStatusProxy().setStatus(false);
                }
            }
        };
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

        _tree.setView(createGridView());

        _tree.addCellDoubleClickHandler(new CellDoubleClickHandler() {
            @Override
            public void onCellClick(CellDoubleClickEvent event) {

                if (getSelectedCustomProblem() != null) {
                    CustomProblemPropertiesDialog.getInstance(new CallbackOnComplete() {
                        @Override
                        public void isComplete() {
                            CmRpcCore.EVENT_BUS.fireEvent(new DataBaseHasBeenUpdatedEvent(TypeOfUpdate.SOLUTION));
                        }
                    }, getSelectedCustomProblem()).setVisible(true);
                }

            }
        });

        // v.getElement().setAttribute("style",
        // "height: 100%;border: 3px solid black");
        setWidget(v);
    }

    /**
     * Create a map of each distinct teacher and their problems
     * 
     * @TODO: change from static,public to private
     * 
     * @param problems
     * @param paths
     * @return
     */
    public static TreeStore<BaseDto> createTeacherProblemMap(List<CustomProblemModel> problems,
            List<String> existingCustomPaths, List<TeacherIdentity> allTeachers) {
        TreeStore<BaseDto> store = new TreeStore<BaseDto>(new KeyProvider());
        CustomProblemFolderNode root = new CustomProblemFolderNode("Teachers", null);

        // for each distinct teacher

        boolean teachersProvided = true;
        if (allTeachers == null) {
            allTeachers = getListOfDistinctTeachers(problems);
            teachersProvided = false;
        }

        List<CustomProblemFolderNode> folders = new ArrayList<CustomProblemFolderNode>();
        for (TeacherIdentity teacher : allTeachers) {
            CustomProblemFolderNode teacherNode = new CustomProblemFolderNode(teacher.getTeacherName(), teacher);
            root.addChild(teacherNode);

            folders.add(teacherNode);
            /**
             * add any custom paths to teacher node
             * 
             */
            if (existingCustomPaths != null) {
                for (String path : existingCustomPaths) {
                    String pos[] = path.split("/");
                    String pName = pos[0];
                    if (teacher.getTeacherName().equals(pName)) {
                        CustomProblemFolderNode customFolder = new CustomProblemFolderNode(pos[1], teacher);
                        teacherNode.addChild(customFolder);
                        customFolder.setParent(teacherNode);

                        folders.add(customFolder);
                    }
                }
            }
        }

        for (int i = 0; i < problems.size(); i++) {
            CustomProblemModel problem = problems.get(i);

            String path = problem.getTreePath();

            boolean found = false;
            for (CustomProblemFolderNode folder : folders) {

                if (!folder.getTeacher().getTeacherName().equals(problem.getTeacher().getTeacherName())) {
                    continue;
                }

                String folderPath = folder.getFolderName();

                if (path == null) {
                    CustomProblemLeafNode probNode = new CustomProblemLeafNode(problem, folder);
                    folder.addChild(probNode);
                    found = true;
                    break;
                } else if (path.equals(folderPath)) {
                    CustomProblemLeafNode probNode = new CustomProblemLeafNode(problem, folder);
                    folder.addChild(probNode);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Log.warn("No folder found for problem: " + problem);
            }
        }

        for (BaseDto base : root.getChildren()) {
            if(base.getChildren().size()==0) {
                if(!teachersProvided) {
                    /** not showing all teachers, only ones with problems
                     * 
                     */
                    continue; // skip
                }
            }
            store.add(base);
            if (base instanceof FolderDto) {
                processFolder(store, (FolderDto) base, teachersProvided);
            }
        }

        return store;
    }

    private static List<TeacherIdentity> getListOfDistinctTeachers(List<CustomProblemModel> problems) {

        List<TeacherIdentity> teachers = new ArrayList<TeacherIdentity>();
        for (CustomProblemModel prob : problems) {
            if (!teachers.contains(prob.getTeacher())) {
                teachers.add(prob.getTeacher());
            }
        }
        return teachers;
    }

    private TreeGridView<BaseDto> createGridView() {
        TreeGridView<BaseDto> view = new TreeGridView<BaseDto>() {
            @Override
            protected void processRows(int startRow, boolean skipStripe) {
                super.processRows(startRow, skipStripe);

                NodeList<Element> rows = getRows();
                for (int i = 0, len = rows.getLength(); i < len; i++) {
                    Element row = rows.getItem(i).cast();
                    BaseDto link = ds.get(i);

                    if (link instanceof CustomProblemLeafNode) {

                        CustomProblemModel problem = ((CustomProblemLeafNode) link).getCustomProblem();

                        // whatever tooltip you want with optional qtitle
                        String comments = problem.getComments() == null ? "" : "<b>Comments</b><br/>"
                                + problem.getComments() + "<br/><br/>";
                        String linkedLessons = "";
                        for (LessonModel lessonModel : problem.getLinkedLessons()) {
                            linkedLessons += "<li>" + lessonModel.getLessonName() + "</li>";
                        }
                        linkedLessons = linkedLessons.length() == 0 ? "" : "<b>Linked Lessons</b><ul>" + linkedLessons
                                + "</ul>";

                        String tip = "<div style='width: 140px;'>" + comments + linkedLessons + "</div>";

                        row.setAttribute("qtip", tip);
                    }
                    // row.setAttribute("qtitle", "ToolTip&nbsp;Title");
                }
            }
        };
        return view;
    }

    static private BaseDto createEmptyNode() {
        return new BaseDto(BaseDto.__nextId(), "--EMPTY--");
    }

    public void setTreeSelections(final String selectedPid) {

        new Timer() {

            @Override
            public void run() {
                /** Expose the root items */
                _tree.expandAll();
                List<BaseDto> rootItems = _tree.getTreeStore().getRootItems();
                _tree.setExpanded((BaseDto) rootItems.get(0), true, false);

                if (selectedPid != null) {
                    BaseDto nodeToSelect = findItemMatching(_root, selectedPid);
                    _tree.getSelectionModel().select(nodeToSelect, false);
                    _tree.setExpanded(nodeToSelect, true);
                    _tree.getSelectionModel().select(nodeToSelect, false);
                } else {
                    // _tree.expandAll();
                }

                selectedCallback.redrawUi();
            }
        }.schedule(1000);
        ;

    }

    protected void expandTreeNode(CustomProblemLeafNode probNodeToExpand) {
        FolderDto teacherNode = (FolderDto) probNodeToExpand.getParent();
        _tree.setExpanded(teacherNode, true, true);

        _tree.getSelectionModel().select(probNodeToExpand, false);

    }

    private CustomProblemLeafNode findItemMatching(FolderDto baseDto, String pid) {
        for (BaseDto bto : _tree.getTreeStore().getAll()) {
            if (bto instanceof CustomProblemLeafNode) {
                CustomProblemModel problem = ((CustomProblemLeafNode) bto).getCustomProblem();
                if (problem.getPid().equals(pid)) {
                    return (CustomProblemLeafNode) bto;
                }
            }
        }

        return null;

        //
        // for (BaseDto node : baseDto.getChildren()) {
        // if (node instanceof CustomProblemLeafNode) {
        // CustomProblemLeafNode probNode = (CustomProblemLeafNode) node;
        // if (probNode.getCustomProblem().getPid().equals(pid)) {
        //
        // _tree.setExpanded(baseDto, true, true);
        // _tree.setExpanded(probNode, true, true);
        // _tree.getSelectionModel().select(probNode, false);
        //
        // List<BaseDto> data = _tree.getStore().getAll();
        // for (int i = 0; i < data.size(); i++) {
        // BaseDto n = data.get(i);
        // if (n instanceof CustomProblemLeafNode) {
        // if (((CustomProblemLeafNode) n).getCustomProblem().getPid()
        // .equals(pid)) {
        // _tree.getView().ensureVisible(i, 0, true);
        // break;
        // }
        // }
        //
        // }
        //
        // return probNode;
        // }
        // } else if (node instanceof FolderDto) {
        // node = findItemMatching((FolderDto) node, pid);
        // if (node != null) {
        // return (CustomProblemLeafNode) node;
        // }
        // }
        // }
        // return null;
    }

    static private void processFolder(TreeStore<BaseDto> store, FolderDto folder, boolean createEmpty) {

        if (folder.getChildren().size() == 0) {
            folder.addChild(createEmptyNode());
        }
        for (BaseDto child : folder.getChildren()) {
            store.add(folder, child);
            if (child instanceof FolderDto) {
                processFolder(store, (FolderDto) child, createEmpty);
            }
        }
    }

    public static void doTest() {
        GWindow w = new GWindow(true);
        List<CustomProblemModel> x = new ArrayList<CustomProblemModel>();
        w.setWidget(new CustomProblemTreeTable(x, null, null, new TreeTableCallback() {

            @Override
            public void problemSelected(CustomProblemModel problem) {
                CmMessageBox.showAlert("Selected: " + problem);
            }

            @Override
            public void redrawUi() {
            }

            @Override
            public void problemUpdated(CustomProblemModel probToMove) {
                // TODO Auto-generated method stub

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

    public BaseDto getSelectedNode() {
        return _tree.getSelectionModel().getSelectedItem();
    }

    public void addPath(String value, TeacherIdentity teacher) {
        _root.addChild(new CustomProblemFolderNode(value, teacher));
    }

    /**
     * return the selected, custom foldername under the current teacher name or
     * null.
     * 
     * @return
     */
    public String getSelectedCustomFolderNode() {
        BaseDto node = _tree.getSelectionModel().getSelectedItem();
        if (node == null || node.getParent() == null) {
            return null;
        } else if (node instanceof CustomProblemFolderNode) {
            return ((CustomProblemFolderNode) node).getFolderName();
        } else {
            return node.getParent() != null ? node.getParent().getName() : null;
        }
    }
}
