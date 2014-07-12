package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_admin.client.custom_content.problem.CustomProblemTreeTable.TreeTableCallback;
import hotmath.gwt.cm_admin.client.teacher.TeacherManager;
import hotmath.gwt.cm_admin.client.teacher.TeacherManager.Callback;
import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_core.client.util.CmAlertify.PromptCallback;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler.TypeOfUpdate;
import hotmath.gwt.cm_rpc.client.model.CustomProblemInfo;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.AddCustomProblemTreePathAction;
import hotmath.gwt.cm_rpc.client.rpc.CopyCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.DeleteCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.DeleteCustomProblemTreePathAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.BaseDto;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyTextButton;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

public class CustomProblemManager extends GWindow {
    BorderLayoutContainer _main;
    CustomProblemTreeTable _treeTable;
    GridProperties _gridProps = GWT.create(GridProperties.class);
    private DefaultGxtLoadingPanel _emptyPage;
    private CheckBox _showAllTeachers;
    private StoreFilter<CustomProblemModel> _filter;
    private String _selectedSolution;
    protected List<CustomProblemModel> _allProblems;
    protected List<TeacherIdentity> _allTeachers;
    private ToggleButton _filterButton;

    private static CustomProblemManager __instance;

    private CustomProblemManager() {
        super(true);

        __instance = this;
        setLocalTitle();

        setPixelSize(800, 600);
        setMaximizable(true);

        buildGui();
        readFromServer();
        setVisible(true);
    }

    private void setLocalTitle() {
        setHeadingText("Custom Problem Manager: " + TeacherManager.getTeacher().getTeacherName());
    }

    private void buildTreeGrid(List<CustomProblemModel> problems, List<String> paths) {
        String selectedProblem = null;
        if (_selectedSolution == null && _treeTable != null && _treeTable.getSelectedCustomProblem() != null) {
            selectedProblem = _treeTable.getSelectedCustomProblem().getPid();
        }
        else {
            selectedProblem = _selectedSolution;
        }

        _treeTable = new CustomProblemTreeTable(problems, paths, _allTeachers, new TreeTableCallback() {

            @Override
            public void problemSelected(CustomProblemModel problem) {
                showProblem(problem);
            }

            @Override
            public void redrawUi() {
                forceLayout();
            }

            @Override
            public void problemUpdated(CustomProblemModel problem) {
                updateProblemToServer(problem);
            }
        });
        new QuickTip(_treeTable.getTree());
        _gridPanel.setWidget(_treeTable);

        _treeTable.setTreeSelections(selectedProblem);

        forceLayout();
    }

    protected void updateProblemToServer(final CustomProblemModel problem) {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                SaveCustomProblemAction action = new SaveCustomProblemAction(problem);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData value) {
                readFromServer();
            }
        }.register();
    }

    ContentPanel _gridPanel;
    private List<String> _lastPaths;

    private void buildGui() {

        _main = new BorderLayoutContainer();

        _gridPanel = new ContentPanel();
        _gridPanel.setWidget(new DefaultGxtLoadingPanel());

        // _grid.getView().setAutoExpandColumn(cols.get(cols.size()-1));
        // _grid.getView().setAutoFill(true);

        // _grid.getSelectionModel().addSelectionHandler(new
        // SelectionHandler<CustomProblemModel>() {
        // @Override
        // public void onSelection(SelectionEvent<CustomProblemModel> event) {
        // showProblem(event.getSelectedItem());
        // }
        // });

        _emptyPage = new DefaultGxtLoadingPanel("Click on a problem or create a new one");

        _main.setCenterWidget(_emptyPage);

        TextButton menuButton = new TextButton("New");
        menuButton.setToolTip("Create a new Custom Problem or Folder");
        
        Menu menu = new Menu();
        MenuItem newButn = new MenuItem("Problem");
        newButn.setToolTip("Create a new Custom Problem");
        newButn.addSelectionHandler(new SelectionHandler<Item>() {

            @Override
            public void onSelection(SelectionEvent<Item> event) {
                if (TeacherManager.getTeacher().isUnknown()) {
                    new TeacherManager(new Callback() {
                        @Override
                        public void teacherSet(TeacherIdentity teacher) {
                            setLocalTitle();
                            showAddNewProblemDialog();
                        }
                    });
                } else {
                    showAddNewProblemDialog();
                }
            }
        });
        menu.add(newButn);

        MenuItem newFolder = new MenuItem("Folder");
        newFolder.setToolTip("Create a new Folder");
        newFolder.addSelectionHandler(new SelectionHandler<Item>() {

            @Override
            public void onSelection(SelectionEvent<Item> event) {
                TeacherIdentity teacher = TeacherManager.getTeacher();
                if (teacher == null || teacher.isUnknown()) {
                    CmMessageBox.showAlert("You must select a teacher first");
                    return;
                }

                if (TeacherManager.getTeacher().isUnknown()) {
                    new TeacherManager(new Callback() {
                        @Override
                        public void teacherSet(TeacherIdentity teacher) {
                            showAddNewFolder(teacher);
                        }
                    });
                } else {
                    showAddNewFolder(TeacherManager.getTeacher());
                }

            }
        });
        menu.add(newFolder);

        menuButton.setMenu(menu);

        _gridPanel.addTool(menuButton);
        _gridPanel.addTool(new MyTextButton("Del", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                deleteSelectedNode();
            }
        },"Delete selected problem or folder"));

        _gridPanel.addTool(new MyTextButton("Copy", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                copySelectedProblem();
            }
        }, "Copy selected problem"));

        _filterButton = new ToggleButton("Filter");
        _filterButton.setToolTip("Apply filters to list of problems");
        _filterButton.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {

                CustomProblemSearchDialog.getInstance(getLessonsInStore(), new CustomProblemSearchDialog.Callback() {
                    @Override
                    public void selectionChanged(List<? extends LessonModel> models, String comments) {
                        applyFilter(models, comments);
                    }
                }).setVisible(true);
                _filterButton.setValue(true);
            }
        });
        _gridPanel.addTool(_filterButton);

        TextButton btn = new MyTextButton("Properties", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                CustomProblemModel customProblem = _treeTable.getSelectedCustomProblem();
                if (customProblem == null) {
                    CmMessageBox.showAlert("Please select a problem first.");
                    return;
                }
                CustomProblemPropertiesDialog.getInstance(new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        CmRpcCore.EVENT_BUS.fireEvent(new DataBaseHasBeenUpdatedEvent(TypeOfUpdate.SOLUTION));
                    }
                }, customProblem).setVisible(true);
            }
        }, "Edit comments and link to lessons");

        _gridPanel.addTool(btn);

        if (CmShared.getQueryParameter("debug") != null) {
            addTool(new TextButton("Refresh", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    readFromServer();
                }
            }));
        }

        BorderLayoutContainer gridCont = new BorderLayoutContainer();
        gridCont.setCenterWidget(_gridPanel);
        HorizontalPanel flow = new HorizontalPanel();
        _showAllTeachers = new CheckBox();
        _showAllTeachers.setToolTip("If selected, all teachers will be listed");
        _showAllTeachers.setValue(true);
        _showAllTeachers.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                _treeTable.getTree().getStore().setEnableFilters(_showAllTeachers.getValue());
            }
        });
        // flow.add(new MyFieldLabel(_showAllTeachers,"All Teachers",75, 20));

        TextButton selTeach = new TextButton("Select Teacher", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new TeacherManager(new Callback() {

                    @Override
                    public void teacherSet(TeacherIdentity teacher) {
                        setLocalTitle();
                        readFromServer();
                    }
                });
            }
        });
        selTeach.setToolTip("Choose your teacher identity");
        flow.add(selTeach);
        flow.getElement().setAttribute("style", "padding-left: 3px;");
        gridCont.setSouthWidget(flow, new BorderLayoutData(30));

        BorderLayoutData bld = new BorderLayoutData(300);
        bld.setSplit(true);
        // bld.setCollapseMini(true);
        _main.setWestWidget(gridCont, bld);

        _filter = new StoreFilter<CustomProblemModel>() {
            @Override
            public boolean select(Store<CustomProblemModel> store, CustomProblemModel parent, CustomProblemModel item) {
                if (item.getTeacher().getTeacherId() == TeacherManager.getTeacher().getTeacherId()) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        setWidget(_main);
    }

    protected void showAddNewFolder(final TeacherIdentity teacher) {

        CmMessageBox.prompt("Create Folder In " + teacher.getTeacherName(), "Folder Name", "New Folder",
                new PromptCallback() {
                    @Override
                    public void promptValue(String value) {
                        if (value != null) {
                            addFolderToTeacherNode(teacher, value);
                        }
                    }

                });

    }

    private void addFolderToTeacherNode(final TeacherIdentity teacher, final String value) {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                AddCustomProblemTreePathAction action = new AddCustomProblemTreePathAction(teacher, value);
                setAction(action);

                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData value) {
                readFromServer();
            }
        }.register();
    }

    private void showAddNewProblemDialog() {

        String folderNode = _treeTable.getSelectedCustomFolderNode();

        List<String> paths = getPathsUsedByProblems();
        CustomProblemModel problem = new CustomProblemModel(null, 0, TeacherManager.getTeacher(), getDateTimeStamp(), null,folderNode != null ? folderNode: "");
        new CustomProblemPropertyEditor(problem, paths, new CustomProblemPropertyEditor.Callback() {
            @Override
            public void solutionCreated(SolutionInfo solution) {
                _selectedSolution = solution.getPid();
                readFromServer();
            }
        });
    }

    private List<String> getPathsUsedByProblems() {
        List<String> paths = new ArrayList<String>();
        ListStore<BaseDto> ls = _treeTable.getTree().getStore();
        for(BaseDto l: ls.getAll()) {
            if(l instanceof CustomProblemFolderNode) {
                CustomProblemFolderNode folder = (CustomProblemFolderNode)l;
                if(folder.getParent() != null) {
                    paths.add(folder.getParent().getName() + "/" + folder.getFolderName());
                }
            }
        }
        return paths;
    }

    private String getDateTimeStamp() {
        return DateTimeFormat.getFormat("yyyy-MM-dd 'at' HH:mm").format(new Date());
    }

    /**
     * Return the distinct list of lessons associated with custom problems in
     * store.
     * 
     * @return
     */
    private List<LessonModel> getLessonsInStore() {
        List<LessonModel> lessonsInStore = new ArrayList<LessonModel>();

        List<CustomProblemModel> toToFiltered = _showAllTeachers.getValue() ? _allProblems
                : getLessonsOnlyThisAdmin(_allProblems);

        for (CustomProblemModel cm : toToFiltered) {
            for (LessonModel lm : cm.getLinkedLessons()) {

                boolean found = false;
                for (LessonModel lmIn : lessonsInStore) {
                    if (lmIn.getLessonFile().equals(lm.getLessonFile())) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    lessonsInStore.add(lm);
                }
            }
        }

        return lessonsInStore;
    }

    private List<CustomProblemModel> getLessonsOnlyThisAdmin(List<CustomProblemModel> all) {
        List<CustomProblemModel> list = new ArrayList<CustomProblemModel>();
        for (CustomProblemModel cp : all) {
            if (cp.getTeacher().getTeacherId() == TeacherManager.getTeacher().getTeacherId()) {
                list.add(cp);
            }
        }
        return list;
    }


    private void applyFilter(List<? extends LessonModel> models, String comments) {

        if (models == null || (models.size() == 0 && comments == null)) {
            setGridStore(_allProblems, _lastPaths);
            _filterButton.setValue(false);

            forceLayout();
            return;
        }

        List<CustomProblemModel> listFiltered = new ArrayList<CustomProblemModel>();
        List<CustomProblemModel> list = _allProblems;
        boolean found = false;

        if (models.size() == 0) {
            listFiltered.addAll(_allProblems);
        } else {
            for (CustomProblemModel m : list) {

                for (LessonModel lm : m.getLinkedLessons()) {

                    for (LessonModel modelToCheck : models) {
                        if (modelToCheck.getLessonFile().equals(lm.getLessonFile())) {

                            // this store record has at least one of the
                            // selected models
                            listFiltered.add(m);
                            found = true;
                            break;
                        }
                        if (found) {
                            break;
                        }
                    }
                }
            }
        }

        if (comments != null) {
            for (int i = listFiltered.size() - 1; i > -1; i--) {
                CustomProblemModel pm = listFiltered.get(i);
                if (pm.getComments() == null || !(pm.getComments().toLowerCase().contains(comments.toLowerCase()))) {
                    listFiltered.remove(i);
                }
            }
        }

        setGridStore(listFiltered, _lastPaths);
        _treeTable.setTreeSelections(null);
    }

    private void setGridStore(List<CustomProblemModel> problems, List<String> paths) {
        buildTreeGrid(problems, paths);
        // _treeTable.loadProblems(problems);
    }

    protected void deleteSelectedNode() {

        TeacherIdentity teacher = TeacherManager.getTeacher();
        if (teacher == null || teacher.isUnknown()) {
            CmMessageBox.showAlert("You must select a teacher first");
            return;
        }

        final BaseDto node = _treeTable.getSelectedNode();
        if (node instanceof CustomProblemLeafNode) {
            final CustomProblemModel problem = _treeTable.getSelectedCustomProblem();
            if (problem == null) {
                CmMessageBox.showAlert("Select a problem to delete");
                return;
            }

            CmMessageBox.confirm("Delete Problem", "Are you sure you want to delete this problem?",
                    new ConfirmCallback() {
                        @Override
                        public void confirmed(boolean yesNo) {
                            if (yesNo) {
                                doDeleteProblem(problem);
                            }
                        }
                    });
        } else {

            if (isCurrentTeacherSubfolder(node)) {
                CmMessageBox.confirm("Delete", "Delete Folder '" + node.getName() + "' and all problems in folder?",
                        new ConfirmCallback() {

                            @Override
                            public void confirmed(boolean yesNo) {
                                if (yesNo) {
                                    doDeletePath(getTeacherFromNode(node), node.getName(), false);
                                }
                            }
                        });
            } else if (isTeacherFolderAndAllowedToDelete(node)) {
                CmMessageBox.confirm("Delete", "Delete teacher '" + node.getName() + "?",
                        new ConfirmCallback() {

                            @Override
                            public void confirmed(boolean yesNo) {
                                if (yesNo) {
                                    CustomProblemFolderNode folderNode = (CustomProblemFolderNode)node;
                                    doDeletePath(folderNode.getTeacher(), node.getName(), true);
                                }
                            }
                        });
            } else {
                CmMessageBox.showAlert("You can only delete custom folders in your teacher folder '"
                        + teacher.getTeacherName() + "'");
                return;
            }
        }
    }

    protected TeacherIdentity getTeacherFromNode(BaseDto node) {
        if(node instanceof CustomProblemFolderNode) {
            return ((CustomProblemFolderNode)node).getTeacher();
        }
        else if(node instanceof CustomProblemLeafNode) {
            return ((CustomProblemLeafNode)node).getCustomProblem().getTeacher();
        }
        else {
            return null;
        }
    }

    private boolean isCurrentTeacherSubfolder(BaseDto node) {
        if (node != null) {
            BaseDto parent = node.getParent();
            if (parent != null) {
                if(getIsSuperTeacher()) {
                    return true;
                }
                else if(parent.getName().equals(TeacherManager.getTeacher().getTeacherName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Is the current teacher a super-teacher
     * 
     * @return
     */
    private boolean getIsSuperTeacher() {
        return true;
    }

    /**
     * Is the current teacher's node
     * 
     * @param node
     * @return
     */
    private boolean isTeacherFolderAndAllowedToDelete(BaseDto node) {
        if (node != null) {
            BaseDto parent = node.getParent();
            if (parent == null) {
                if (node instanceof CustomProblemFolderNode) {
                    if(getIsSuperTeacher() || ((CustomProblemFolderNode) node).getFolderName().equals(TeacherManager.getTeacher().getTeacherName())) {
                            return true;
                    }
                }
            }
        }

        return false;
    }

    protected void doDeletePath(final TeacherIdentity problemTeacher, final String name, final boolean isTeacherNode) {

        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                DeleteCustomProblemTreePathAction action = new DeleteCustomProblemTreePathAction(problemTeacher, name, isTeacherNode);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData value) {
                readFromServer();
            }
        }.register();

    }

    protected void doDeleteProblem(final CustomProblemModel problem) {
        new RetryAction<RpcData>() {

            @Override
            public void attempt() {
                DeleteCustomProblemAction action = new DeleteCustomProblemAction(problem);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData data) {
                readFromServer();
            }
        }.register();
    }

    protected void copySelectedProblem() {
        final CustomProblemModel problem = _treeTable.getSelectedCustomProblem();
        if (problem == null) {
            CmMessageBox.showAlert("Select a problem to copy");
            return;
        }

        CmMessageBox.confirm("Copy Problem", "Copy problem?", new ConfirmCallback() {
            @Override
            public void confirmed(boolean yesNo) {
                if (yesNo) {
                    doCopySelectProblem(problem);
                }
            }
        });
    }

    private void doCopySelectProblem(final CustomProblemModel problem) {
        CmBusyManager.setBusy(true);
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CopyCustomProblemAction action = new CopyCustomProblemAction(problem.getPid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData data) {
                CmBusyManager.setBusy(false);
                _selectedSolution = data.getDataAsString("pid");
                readFromServer();
            }
        }.register();
    }

    protected void showProblem(CustomProblemModel selectedItem) {

        if (selectedItem == null) {
            _main.setCenterWidget(_emptyPage);
            forceLayout();
            return;
        } else {

            _selectedSolution = selectedItem.getPid();
            ProblemDesigner problemDesigner = new ProblemDesigner(new CallbackOnComplete() {
                @Override
                public void isComplete() {
                    _treeTable.getTree().getSelectionModel().deselectAll();
                    _main.setCenterWidget(_emptyPage);
                }
            });
            _main.setCenterWidget(problemDesigner);
            forceLayout();
            problemDesigner.loadProblem(selectedItem, 0);
        }
    }

    private void readFromServer() {

        showProblem(null);

        CmBusyManager.setBusy(true);
        new RetryAction<CustomProblemInfo>() {

            @Override
            public void attempt() {
                GetCustomProblemAction action = new GetCustomProblemAction(new TeacherIdentity(UserInfoBase
                        .getInstance().getUid(), "TEST", -1));
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CustomProblemInfo customProblemInfo) {
                CmBusyManager.setBusy(false);
                _lastPaths = customProblemInfo.getPaths();
                _allProblems = customProblemInfo.getProblems();
                _allTeachers = customProblemInfo.getTeachers();
                setGridStore(_allProblems, _lastPaths);
                forceLayout();
            }
        }.register();
    }

    /**
     * 
     new TeacherIdentity(UserInfoBase.getInstance().getUid(), "", 0) new
     * TeacherIdentity(_cmAdminMdl.getUid(), "", 0));
     */
    public static void showManager() {
        new CustomProblemManager();
    }

    public static void startTest() {
        CustomProblemManager.showManager();
        // new CustomProblemManager(new TeacherIdentity(2, "casey_1",1));
    }

    static {
        CmRpcCore.EVENT_BUS.addHandler(DataBaseHasBeenUpdatedEvent.TYPE, new DataBaseHasBeenUpdatedHandler() {
            @Override
            public void databaseUpdated(TypeOfUpdate type) {
                if (type == TypeOfUpdate.SOLUTION) {
                    __instance.readFromServer();
                }
            }
        });
    }

}

interface GridProperties extends PropertyAccess<String> {

    @Path("pid")
    ModelKeyProvider<CustomProblemModel> key();

    ValueProvider<CustomProblemModel, String> label();

    ValueProvider<CustomProblemModel, String> lessonList();

    ValueProvider<CustomProblemModel, String> comments();

    ValueProvider<CustomProblemModel, Integer> problemNumber();

    @Path("teacher.teacherName")
    ValueProvider<CustomProblemModel, String> teacherName();

    ValueProvider<CustomProblemModel, String> pid();
}