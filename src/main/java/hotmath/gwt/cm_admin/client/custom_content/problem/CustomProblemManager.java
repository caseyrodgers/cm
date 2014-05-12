package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_admin.client.teacher.TeacherManager;
import hotmath.gwt.cm_admin.client.teacher.TeacherManager.Callback;
import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler.TypeOfUpdate;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.CopyCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.DeleteCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.ui.MyTextButton;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.core.client.Style.SelectionMode;
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
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

public class CustomProblemManager extends GWindow {
    BorderLayoutContainer _main;
    Grid<CustomProblemModel> _grid;
    GridProperties _gridProps = GWT.create(GridProperties.class);
    private DefaultGxtLoadingPanel _emptyPage;
    private CheckBox _showAllTeachers;
    private StoreFilter<CustomProblemModel> _filter;
    private String _selectedSolution;
    protected CmList<CustomProblemModel> _allProblems;
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

    private void buildGui() {
       
        _main = new BorderLayoutContainer();
        ListStore<CustomProblemModel> store = new ListStore<CustomProblemModel>(_gridProps.key());
        List<ColumnConfig<CustomProblemModel, ?>> cols = new ArrayList<ColumnConfig<CustomProblemModel, ?>>();
        
        cols.add(new ColumnConfig<CustomProblemModel, String>(_gridProps.label(), 70, "Problem"));
        // cols.add(new ColumnConfig<CustomProblemModel, Integer>(_gridProps.problemNumber(), 25, "#"));
        cols.add(new ColumnConfig<CustomProblemModel, String>(_gridProps.comments(), 100, "Comments"));
        cols.add(new ColumnConfig<CustomProblemModel, String>(_gridProps.lessonList(), 100, "Lessons"));
        
        ColumnModel<CustomProblemModel> colModel = new ColumnModel<CustomProblemModel>(cols);
        _grid = new Grid<CustomProblemModel>(store,  colModel, createGridView());
        //_grid.getView().setAutoExpandColumn(cols.get(cols.size()-1));
        // _grid.getView().setAutoFill(true);
        
        _grid.getSelectionModel().addSelectionHandler(new SelectionHandler<CustomProblemModel>() {
            @Override
            public void onSelection(SelectionEvent<CustomProblemModel> event) {
                showProblem(event.getSelectedItem());
            }
        });
        _grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        _emptyPage = new DefaultGxtLoadingPanel("Click on a problem or create a new one");
        
        _main.setCenterWidget(_emptyPage);

        ContentPanel gridPanel = new ContentPanel();
        gridPanel.setWidget(_grid);
        
        gridPanel.addTool(new TextButton("New",new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                
                if(TeacherManager.getTeacher().isUnknown()) {
                    new TeacherManager(new Callback() {
                        @Override
                        public void teacherSet(TeacherIdentity teacher) {
                            showAddNewProblemDialog();
                        }
                    });
                }
                else {
                    showAddNewProblemDialog();
                }
            }
        }));
        gridPanel.addTool(new TextButton("Del", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                deleteSelectedProblem();
            }
        }));
        
        gridPanel.addTool(new TextButton("Copy", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                copySelectedProblem();
            }
        }));

        
        
        _filterButton = new ToggleButton("Filter");
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
        gridPanel.addTool(_filterButton);

        
        TextButton btn = new MyTextButton("Properties", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
            	CustomProblemModel customProblem = _grid.getSelectionModel().getSelectedItem();
            	if(customProblem == null) {
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
        
        gridPanel.addTool(btn);
        
        
        if(CmShared.getQueryParameter("debug") != null) {
            addTool(new TextButton("Refresh", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    readFromServer();
                }
            }));
        }

        BorderLayoutContainer gridCont = new BorderLayoutContainer();
        gridCont.setCenterWidget(gridPanel);
        HorizontalPanel flow = new HorizontalPanel();
        _showAllTeachers = new CheckBox();
        _showAllTeachers.setToolTip("If selected, all teachers will be listed");
        _showAllTeachers.setValue(true);
        _showAllTeachers.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                _grid.getStore().setEnableFilters(_showAllTeachers.getValue());
            }
        });
        flow.add(new MyFieldLabel(_showAllTeachers,"All Teachers",75, 20));
        
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
        flow.getElement().setAttribute("style",  "padding-left: 3px;");
        gridCont.setSouthWidget(flow, new BorderLayoutData(30));
        
        BorderLayoutData bld = new BorderLayoutData(300);
        bld.setSplit(true);
        // bld.setCollapseMini(true);
        _main.setWestWidget(gridCont,bld );
        
        
        _filter = new StoreFilter<CustomProblemModel>() {
            @Override
            public boolean select(Store<CustomProblemModel> store, CustomProblemModel parent, CustomProblemModel item) {
                if(item.getTeacher().getTeacherId() == TeacherManager.getTeacher().getTeacherId()) {
                    return true;
                }
                else {
                    return false;
                }
            }
        };
        
        new QuickTip(_grid);
        
        setWidget(_main);
    }
    
    private void showAddNewProblemDialog() {
        CustomProblemModel problem = new CustomProblemModel(null,0,TeacherManager.getTeacher(), null, null);
        new CustomProblemPropertyEditor(problem,new CustomProblemPropertyEditor.Callback() {
            @Override
            public void solutionCreated(SolutionInfo solution) {
                _selectedSolution = solution.getPid();
                readFromServer();
            }
        });
    }


    /** Return the distinct list of lessons associated with 
     *  custom problems in store.
     * @return 
     */
    private List<LessonModel> getLessonsInStore() {
        List<LessonModel> lessonsInStore = new ArrayList<LessonModel>();
        
        
        List<CustomProblemModel> toToFiltered = _showAllTeachers.getValue()?_allProblems:getLessonsOnlyThisAdmin(_allProblems);
        
        for(CustomProblemModel cm: toToFiltered) {
            for(LessonModel lm: cm.getLinkedLessons()) {
                
                boolean found=false;
                for(LessonModel lmIn: lessonsInStore){
                    if(lmIn.getLessonFile().equals(lm.getLessonFile())) {
                        found=true;
                        break;
                    }
                }
                
                if(!found) {
                    lessonsInStore.add(lm);
                }
            }
        }
        
        return lessonsInStore;
    }

    
    private List<CustomProblemModel> getLessonsOnlyThisAdmin(CmList<CustomProblemModel> all) {
        List<CustomProblemModel> list = new ArrayList<CustomProblemModel>();
        for(CustomProblemModel cp: all) {
            if(cp.getTeacher().getTeacherId() == TeacherManager.getTeacher().getTeacherId()) {
                list.add(cp);
            }
        }
        return list;
    }


    private GridView<CustomProblemModel> createGridView() {
        GridView<CustomProblemModel> view = new GridView<CustomProblemModel>() {
            @Override
            protected void processRows(int startRow, boolean skipStripe) {
                super.processRows(startRow, skipStripe);
                
                NodeList<Element> rows = getRows();
                for (int i = 0, len = rows.getLength(); i < len; i++) {
                    Element row = rows.getItem(i).cast();
                    CustomProblemModel link = ds.get(i);
                    
                    // whatever tooltip you want with optional qtitle
                    String label = "<b>Problem: </b><br/>" + link.getLabel() + "</div><br/>";
                    String comments = link.getComments()==null?"":"<b>Comments</b><br/>" + link.getComments() + "<br/><br/>";
                    String linkedLessons = "";
                    for(LessonModel lessonModel: link.getLinkedLessons()) {
                        linkedLessons += "<li>" + lessonModel.getLessonName() + "</li>";
                    }
                    linkedLessons =  linkedLessons.length()==0?"":"<b>Linked Lessons</b><ul>" + linkedLessons + "</ul>";
                    
                    String tip = "<div style='width: 140px;'>" + label + comments + linkedLessons + "</div>";
                    
                    row.setAttribute("qtip", tip);
                    // row.setAttribute("qtitle", "ToolTip&nbsp;Title");
                }
            }
        };
        return view;
    }

    

    private void applyFilter(List<? extends LessonModel> models, String comments) {
        
        if(models == null || (models.size() == 0 && comments == null)) {
            setGridStore(_allProblems);
            _filterButton.setValue(false);
            return;
        }
        
        List<CustomProblemModel> listFiltered = new ArrayList<CustomProblemModel>();
        List<CustomProblemModel> list = _allProblems;
        boolean found=false;
        
        if(models.size() == 0) {
        	listFiltered.addAll(_allProblems);
        }
        else {
	        for(CustomProblemModel m: list) {
	        	
	            for(LessonModel lm: m.getLinkedLessons()) {
	                
	                for(LessonModel modelToCheck: models) {
	                    if(modelToCheck.getLessonFile().equals(lm.getLessonFile())) {
	                        
	                        // this store record has at least one of the selected models
	                        listFiltered.add(m);
	                        found=true;
	                        break;
	                    }
	                    if(found) {
	                        break;
	                    }
	                }
	            }
	        }
        }
        
        if(comments != null) {
        	for(int i=listFiltered.size()-1;i>-1;i--) {
        		CustomProblemModel pm = listFiltered.get(i);
        		if(pm.getComments() == null || !(pm.getComments().toLowerCase().contains(comments.toLowerCase()))) {
        			listFiltered.remove(i);
        		}
        	}
        }
        
        setGridStore(listFiltered);
    }

    private void setGridStore(List<CustomProblemModel> problems) {
        _grid.getStore().clear();
        _grid.getStore().addAll(problems);
        _grid.getStore().removeFilters();
        _grid.getStore().addFilter(_filter);
        if(!_showAllTeachers.getValue()) {
            _grid.getStore().setEnableFilters(true);
        }            
    }


    protected void deleteSelectedProblem() {
        final CustomProblemModel problem = _grid.getSelectionModel().getSelectedItem();
        if(problem == null) {
            CmMessageBox.showAlert("Select a problem to delete");
            return;
        }
        
        CmMessageBox.confirm("Delete Problem",  "Are you sure you want to delete this problem?", new ConfirmCallback() {
            @Override
            public void confirmed(boolean yesNo) {
                if(yesNo) {
                    doDeleteProblem(problem);
                }
            }
        });
    }



    protected void doDeleteProblem(final CustomProblemModel problem) {
        new RetryAction<RpcData>() {

            @Override
            public void attempt() {
                DeleteCustomProblemAction action = new DeleteCustomProblemAction(problem);
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }

            @Override
            public void oncapture(RpcData data) {
                readFromServer();
            }
        }.register();        
    }


    protected void copySelectedProblem() {
        final CustomProblemModel problem = _grid.getSelectionModel().getSelectedItem();
        if(problem == null) {
            CmMessageBox.showAlert("Select a problem to copy");
            return;
        }

        CmMessageBox.confirm("Copy Problem", "Copy problem?",new ConfirmCallback() {
            @Override
            public void confirmed(boolean yesNo) {
                if(yesNo) {
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
                CmShared.getCmService().execute(action,  this);
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
        
        if(selectedItem == null) {
            _main.setCenterWidget(_emptyPage);
            forceLayout();
            return;
        }
        else {
            
            _selectedSolution = selectedItem.getPid();
            ProblemDesigner problemDesigner = new ProblemDesigner(new CallbackOnComplete() {
				@Override
				public void isComplete() {
					_main.setCenterWidget(_emptyPage);				}
			});
            _main.setCenterWidget(problemDesigner);
            forceLayout();
            problemDesigner.loadProblem(selectedItem, 0);
        }
    }

    private void readFromServer() {
        
        showProblem(null);
        
        new RetryAction<CmList<CustomProblemModel>>() {

            @Override
            public void attempt() {
                GetCustomProblemAction action = new GetCustomProblemAction(new TeacherIdentity(UserInfoBase.getInstance().getUid(),  "TEST", -1));
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }

            @Override
            public void oncapture(CmList<CustomProblemModel> problems) {
                
                _allProblems = problems;
                
                setGridStore(problems);

                if(_selectedSolution != null) {
                    for(int i=0;i<problems.size();i++) {
                        CustomProblemModel m = problems.get(i);
                        if(m.getPid().equals(_selectedSolution)) {
                            _grid.getSelectionModel().select(m, false);
                            break;
                        }
                    }
                }
                
                forceLayout();
                
            }
        }.register();
    }
    
    /** 
     *                                 
                                new TeacherIdentity(UserInfoBase.getInstance().getUid(), "", 0)
                                new TeacherIdentity(_cmAdminMdl.getUid(), "", 0));

     */
    public static void showManager() {
        new CustomProblemManager();
    }
    
    public static void startTest() {
        CustomProblemManager.showManager();
        //new CustomProblemManager(new TeacherIdentity(2, "casey_1",1));
    }
    
    static {
    	CmRpcCore.EVENT_BUS.addHandler(DataBaseHasBeenUpdatedEvent.TYPE, new DataBaseHasBeenUpdatedHandler() {
    		@Override
    		public void databaseUpdated(TypeOfUpdate type) {
    			if(type == TypeOfUpdate.SOLUTION) {
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