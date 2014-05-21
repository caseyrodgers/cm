package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
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
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
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
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;
import com.sencha.gxt.widget.core.client.treegrid.TreeGridView;

public class CustomProblemTreeTable extends SimpleContainer {

    private static final String EMPTY_NODE = "-- Empty --";
    final TreeGrid<BaseDto> _tree;
    CustomProblemFolderNode _dndTargetModel;
    CustomProblemLeafNode _dndSourceModel;

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

        void problemUpdated(CustomProblemModel probToMove);
    }

    public CustomProblemTreeTable(List<CustomProblemModel> problems, List<String> paths,
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
        List<CustomProblemModel> problemsInCustomFolder = new ArrayList<CustomProblemModel>();
        for (int i = 0; i < problems.size(); i++) {
            CustomProblemModel p = problems.get(i);

            List<CustomProblemModel> teacherProbs = teachers.get(p.getTeacher().getTeacherId());
            if (teacherProbs == null) {
                teacherProbs = new ArrayList<CustomProblemModel>();
                teachers.put(p.getTeacher().getTeacherId(), teacherProbs);

                teacherNode = new CustomProblemFolderNode(p.getTeacherName());
                _root.addChild(teacherNode);

                // see if this teacher has any specified nodes
                if (paths != null) {
                    for (String path : paths) {
                        String pos[] = path.split("/");
                        String pName = pos[0];
                        if (p.getTeacherName().equals(pName)) {
                            CustomProblemFolderNode customFolder = new CustomProblemFolderNode(pos[1]);
                            customFolder.setParent(teacherNode);
                            teacherNode.addChild(customFolder);

                            /**
                             * add all problems associated with this custom
                             * folder, removing from normal flow by adding to a
                             * look table.
                             * 
                             */
                            for (CustomProblemModel cm : problems) {
                                String tp = cm.getTreePath();
                                if (tp != null) {
                                    if (cm.getTeacher().getTeacherName().equals(p.getTeacherName())) {
                                        if (customFolder.getFolderName().equals(tp)) {
                                            CustomProblemLeafNode leaf = new CustomProblemLeafNode(cm, customFolder);
                                            customFolder.addChild(leaf);

                                            problemsInCustomFolder.add(cm);
                                        }
                                    }
                                }
                            }
                            if (customFolder.getChildren().size() == 0) {
                                customFolder.addChild(new BaseDto(BaseDto.__nextId(), EMPTY_NODE));
                            }

                        }
                    }
                }

            }

            if (teacherNode != null) {
                if (!problemsInCustomFolder.contains(p)) {
                    teacherNode.addChild(new CustomProblemLeafNode(p, teacherNode));
                } else {
                    // System.out.println("already in subfolder");
                }
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

        ColumnConfig<BaseDto, String> problemCol = new ColumnConfig<BaseDto, String>(props.name(), 200, "Problem");
        problemCol.setSortable(false);

        ColumnConfig<BaseDto, String> commentCol = new ColumnConfig<BaseDto, String>(new ValueProvider<BaseDto, String>() {
            @Override
            public String getValue(BaseDto object) {
                if (object == null) {
                    return "";
                } else {
                    return object instanceof CustomProblemLeafNode ? ((CustomProblemLeafNode) object)
                            .getCustomProblem().getComments() : "";
                }
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
        commentCol.setHeader("Comment");
        ColumnConfig<BaseDto, String> lessonsCol = new ColumnConfig<BaseDto, String>(new ValueProvider<BaseDto, String>() {
            @Override
            public String getValue(BaseDto object) {
                return object instanceof CustomProblemLeafNode ? ((CustomProblemLeafNode) object).getCustomProblem()
                        .getLessonList() : "";
            }

            @Override
            public void setValue(BaseDto object, String value) {
            }

            @Override
            public String getPath() {
                return "lesson";
            }
        });
        lessonsCol.setHeader("Lessons");

        List<ColumnConfig<BaseDto, ?>> l = new ArrayList<ColumnConfig<BaseDto, ?>>();
        l.add(problemCol);
        l.add(commentCol);
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
                    String folder = _dndTargetModel.getFolderName();
                    CustomProblemModel probToMove = _dndSourceModel.getCustomProblem();
                    probToMove.setTreePath(folder);

                    selectedCallback.problemUpdated(probToMove);

                    // CmMessageBox.showAlert("dragging " + _dndSourceModel +
                    // ", " + _dndTargetModel);
                } else {
                    CmMessageBox.showAlert("Invalid drag target");
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
                    CmMessageBox.showAlert("Invalid drag source");
                    event.setCancelled(true);
                } else {
                    _dndSourceModel = (CustomProblemLeafNode) source;
                }
            }
        });
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

        _tree.setView(createGridView());

        _tree.addCellDoubleClickHandler(new CellDoubleClickHandler() {
            @Override
            public void onCellClick(CellDoubleClickEvent event) {
                CustomProblemPropertiesDialog.getInstance(new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        CmRpcCore.EVENT_BUS.fireEvent(new DataBaseHasBeenUpdatedEvent(TypeOfUpdate.SOLUTION));
                    }
                }, getSelectedCustomProblem()).setVisible(true);

            }
        });

        // v.getElement().setAttribute("style",
        // "height: 100%;border: 3px solid black");
        setWidget(v);
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
                        String label = "<b>Problem: </b><br/>" + problem.getProblemName() + "</div><br/>";
                        String comments = problem.getComments() == null ? "" : "<b>Comments</b><br/>"
                                + problem.getComments() + "<br/><br/>";
                        String linkedLessons = "";
                        for (LessonModel lessonModel : problem.getLinkedLessons()) {
                            linkedLessons += "<li>" + lessonModel.getLessonName() + "</li>";
                        }
                        linkedLessons = linkedLessons.length() == 0 ? "" : "<b>Linked Lessons</b><ul>" + linkedLessons
                                + "</ul>";

                        String tip = "<div style='width: 140px;'>" + label + comments + linkedLessons + "</div>";

                        row.setAttribute("qtip", tip);
                    }
                    // row.setAttribute("qtitle", "ToolTip&nbsp;Title");
                }
            }
        };
        return view;
    }

    protected String getToolTipFor(CustomProblemLeafNode leaf) {

        CustomProblemModel link = leaf.getCustomProblem();

        // whatever tooltip you want with optional qtitle
        String label = "<b>Problem: </b><br/>" + link.getProblemName() + "</div><br/>";
        String comments = link.getComments() == null ? "" : "<b>Comments</b><br/>" + link.getComments() + "<br/><br/>";
        String linkedLessons = "";
        for (LessonModel lessonModel : link.getLinkedLessons()) {
            linkedLessons += "<li>" + lessonModel.getLessonName() + "</li>";
        }
        linkedLessons = linkedLessons.length() == 0 ? "" : "<b>Linked Lessons</b><ul>" + linkedLessons + "</ul>";

        String tip = "<div style='width: 140px;'>" + label + comments + linkedLessons + "</div>";

        return tip;
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
                    BaseDto selectedNode1 = (BaseDto) ((FolderDto) rootItems.get(0)).getChildren().get(0);
                    findItemMatching((FolderDto) rootItems.get(0), selectedPid);
                } else {
                    _tree.expandAll();
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
        for (BaseDto node : baseDto.getChildren()) {
            if (node instanceof CustomProblemLeafNode) {
                CustomProblemLeafNode probNode = (CustomProblemLeafNode) node;
                if (probNode.getCustomProblem().getPid().equals(pid)) {

                    _tree.setExpanded(baseDto, true, true);
                    _tree.setExpanded(probNode, true, true);
                    _tree.getSelectionModel().select(probNode, false);

                    List<BaseDto> data = _tree.getStore().getAll();
                    for (int i = 0; i < data.size(); i++) {
                        BaseDto n = data.get(i);
                        if (n instanceof CustomProblemLeafNode) {
                            if (((CustomProblemLeafNode) n).getCustomProblem().getPid()
                                    .equals(pid)) {
                                _tree.getView().ensureVisible(i, 0, true);
                                break;
                            }
                        }

                    }

                    return probNode;
                }
            } else if (node instanceof FolderDto) {
                node = findItemMatching((FolderDto) node, pid);
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
        w.setWidget(new CustomProblemTreeTable(x, null, new TreeTableCallback() {

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

    public void addPath(String string) {
        _root.addChild(new CustomProblemFolderNode(string));
    }

    /**
     * return the selected, custom foldername under the current teacher name or
     * null.
     * 
     * @return
     */
    public String getSelectedCustomFolderNode() {
        BaseDto node = _tree.getSelectionModel().getSelectedItem();
        if(node.getParent() == null) {
            return null;
        }
        else if(node instanceof CustomProblemFolderNode) {
            return ((CustomProblemFolderNode)node).getFolderName();
        }
        else {
            return node.getParent() != null?node.getParent().getName():null;
        }
    }
}
