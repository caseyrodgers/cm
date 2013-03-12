package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentLessonListingPanel.CallbackOnSelectedLesson;
import hotmath.gwt.cm_rpc.client.model.AssignmentLessonData;
import hotmath.gwt.cm_rpc.client.model.assignment.BaseDto;
import hotmath.gwt.cm_rpc.client.model.assignment.FolderDto;
import hotmath.gwt.cm_rpc.client.model.assignment.LessonDto;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.SectionDto;
import hotmath.gwt.cm_rpc.client.model.assignment.SubjectDto;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSection;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSubject;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramType;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramLessonProblemsAction;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramLessonsAction;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramListingAction;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CheckChangedEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangedEvent.CheckChangedHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;

public class AddProblemDialog extends GWindow {

    static AddProblemDialog __sharedInstance;

    AddProblemsCallback _callbackOnComplete;
    AssignmentLessonData _lessonData;
    ContentPanel _treePanel;
    BorderLayoutContainer _mainContainer;
    
    TreeStore<BaseDto> _treeStore;
    AssignmentLessonListingPanel _treeFlatPanel;
    TabPanel _tabPanel; 
    public AddProblemDialog() {
        super(false);

        setHeadingHtml("Add Problems to Assignment");
        setPixelSize(700, 480);
        setMaximizable(true);
        addButton(createAddSelectionButton());
        
        TextButton btnClose = new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(getActiveTree().getCheckedSelection().size() > 0) {
                    CmMessageBox.confirm("Really Close?", "There are items checked.  Are you sure you want to cancel?",new ConfirmCallback() {
                        public void confirmed(boolean yesNo) {
                            if(yesNo) {
                                hide();
                            }
                        }
                    });
                }
                else {
                    hide();
                }
            }
        });
        addButton(btnClose);

        _treePanel = new ContentPanel();
        _mainContainer = new BorderLayoutContainer();
        
        _treeFlatPanel = new AssignmentLessonListingPanel(new CallbackOnSelectedLesson() {
            @Override
            public void lessonWasSelected() {
                Window.alert("Lesson was selected");
            }
        });


        CenterLayoutContainer centered = new CenterLayoutContainer();
        centered.setWidget(new Label("Loading data ..."));
        _treePanel.setWidget(centered);        
        
        _tabPanel = new TabPanel();
        _tabPanel.add(_treePanel, new TabItemConfig("Program Tree", false));
        _tabPanel.add(_treeFlatPanel, new TabItemConfig("All Available Lessons", false));
        _tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
            @Override
            public void onSelection(SelectionEvent<Widget> event) {
                if(_tabPanel.getActiveWidget() != _treePanel) {
                    // is flat tree
                    updateFlattenTree();
                }
            }
        });
        
        BorderLayoutData data = new BorderLayoutData();
        data.setSize(.50);
        data.setSplit(true);
        data.setCollapsible(true);

        _mainContainer.setCenterWidget(_tabPanel, data);        
        
        addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_QUESTION_VIEWER_CLOSED));
            }
        });
        
        //setupViewerGui();
        
        QuestionViewerPanel.getInstance().removeQuestion();
        
        setWidget(_mainContainer);

        readDataAndBuildTree();
    }
    
    private void updateFlattenTree() {
        _treeFlatPanel.refreshData();
    }

    public void setupViewerGui() {
        BorderLayoutData eastData = new BorderLayoutData();
        eastData.setSize(.50);
        eastData.setSplit(true);
        eastData.setCollapsible(true);

        _mainContainer.setEastWidget(QuestionViewerPanel.getInstance(),eastData);
    }
    
    private Widget createAddSelectionButton() {
        TextButton btn = new TextButton("Add Checked Problems", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                
                if(getActiveTree().getCheckedSelection().size() == 0) {
                    CmMessageBox.showAlert("There are no checked problems.");
                    return;
                }
                makeSureLessonProblemsReadMaybeAsync(true, true,getActiveTree(), _callbackOnComplete);
                hide();
            }
        });
        btn.setToolTip("Add all checked problems to current assignment.");
        return btn;
    }
    
    protected Tree<BaseDto, String> getActiveTree() {
        Tree<BaseDto, String> activeTree=null;
        if(_tabPanel.getActiveWidget() == _treePanel) {
            activeTree = _tree;
        }
        else {
            activeTree = _treeFlatPanel._tree;
        }
        
        return activeTree;
    }

    /** Some lessons might not have had their problems read from the server.
     * We do not want to make the user manually expand the problems to see them.
     * 
     * @param callback
     */
    static public void makeSureLessonProblemsReadMaybeAsync(boolean sort, boolean unselectSelections,  Tree<BaseDto, String> tree, final AddProblemsCallback callback) {
        final List<ProblemDto> problems = new ArrayList<ProblemDto>();
        List<BaseDto> checked = tree.getCheckedSelection();
        List<LessonDto> lessonsNeeded = new ArrayList<LessonDto>();
        for (BaseDto d : checked) {
            if(d instanceof LessonDto) {
                if(d.getChildren() == null || d.getChildren().size() == 0) {
                    lessonsNeeded.add((LessonDto)d);
                }
            }
            else if(d instanceof ProblemDto) {
                problems.add((ProblemDto)d);
            }
        }
        
        if(sort) {
            Collections.sort(problems, new Comparator<BaseDto>() {

                @Override
                public int compare(BaseDto o1, BaseDto o2) {
                    return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                }
            });
        }
        
        if(lessonsNeeded.size() > 0) {
           readProblemsForLessonsAndCallBack(unselectSelections, tree, lessonsNeeded,callback,problems);
        }
        else {
            
            // no need for async
            callBackToServer(unselectSelections, tree, callback,problems);
        }
    }    
    
    

    static private void readProblemsForLessonsAndCallBack(final boolean uncheckSelections, final Tree<BaseDto, String> tree, final List<LessonDto> lessonsNeeded, final AddProblemsCallback callback, final List<ProblemDto> problems) {
        new RetryAction<CmList<Response>>() {
            @Override
            public void attempt() {
                
                MultiActionRequestAction mAction = new  MultiActionRequestAction();
                for(LessonDto l: lessonsNeeded) {
                    GetProgramLessonProblemsAction action = new GetProgramLessonProblemsAction(l.getLessonName(), l.getLessonFile(),l.getSubject());
                    mAction.getActions().add(action);
                }
                setAction(mAction);
                CmShared.getCmService().execute(mAction, this);
            }

            @Override
            public void oncapture(CmList<Response> responses) {
                List<ProblemDto> data = new ArrayList<ProblemDto>();
                
                for(Response r: responses) {
                    List<ProblemDto> probs = (CmList<ProblemDto>)r;
                    for (int i = 0, t = probs.size(); i < t; i++) {
                        ProblemDto pt = probs.get(i);
                        pt.setId(++BaseDto.autoId);
                        
                        boolean found=false;
                        for(ProblemDto d: problems) {
                            if(d.getPid().equals(pt.getPid())) {
                                found=true;
                                break;
                            }
                        }
                        if(!found) {
                            data.add(pt);
                        }
                    }
                }
                problems.addAll(data);
                callBackToServer(uncheckSelections, tree, callback,problems);
            }

        }.register();        
    }

    static private void callBackToServer(boolean uncheckSelections, Tree<BaseDto, String> tree, AddProblemsCallback callback, List<ProblemDto> problems) {
        
        Log.debug("Problems added: " + problems.size());
        callback.problemsAdded(problems);
        
        if(uncheckSelections) {
            for (BaseDto d : tree.getCheckedSelection()) {
                tree.setChecked(d, CheckState.UNCHECKED);
            }
        }
    }

    private void setCallback(AddProblemsCallback callbackOnComplete) {
        _callbackOnComplete = callbackOnComplete;
    }

    class KeyProvider implements ModelKeyProvider<BaseDto> {
        @Override
        public String getKey(BaseDto item) {
            if (item instanceof SubjectDto) {
                return ((SubjectDto) item).getSubject();
            } else {
                return (item instanceof FolderDto ? "f-" : "m-") + item.getId().toString();
            }
        }
    }

    private void processFolder(TreeStore<BaseDto> store, FolderDto folder) {
        for (BaseDto child : folder.getChildren()) {
            store.add(folder, child);
            if (child instanceof FolderDto) {
                processFolder(store, (FolderDto) child);
            }
        }
    }

    Tree<BaseDto, String> _tree;

    public Widget makeTree() {
        
        _treeStore = new TreeStore<BaseDto>(new KeyProvider());

        RpcProxy<BaseDto, List<BaseDto>> proxy = new RpcProxy<BaseDto, List<BaseDto>>() {

            @Override
            public void load(BaseDto loadConfig, AsyncCallback<List<BaseDto>> callback) {

                if (loadConfig.getChildren() == null || loadConfig.getChildren().size() == 0) {

                    if (loadConfig instanceof SectionDto) {
                        SectionDto s = (SectionDto) loadConfig;
                        getLessonItemsRPC(s.getTestDefId(), s.getSubject(), s.getSection(), callback);
                    } else if (loadConfig instanceof LessonDto) {
                        LessonDto l = (LessonDto) loadConfig;
                        getLessonProblemItemsRPC(l.getLessonName(), l.getLessonFile(), l.getSubject(), callback);
                    }
                }

                else {
                    if(loadConfig instanceof StandardNode) {
                        List<BaseDto> base = new ArrayList<BaseDto>();
                        for (StandardStateDto so : ((StandardNode) loadConfig).getStates()) {
                            base.add(so);
                        }
                        callback.onSuccess(base);
                    }
                    else if (loadConfig instanceof SubjectDto) {
                        List<BaseDto> base = new ArrayList<BaseDto>();
                        for (SectionDto so : ((SubjectDto) loadConfig).getSections()) {
                            base.add(so);
                        }
                        callback.onSuccess(base);
                    } else if (loadConfig instanceof SectionDto) {
                        List<BaseDto> base = new ArrayList<BaseDto>();
                        SectionDto secDto = (SectionDto) loadConfig;
                        secDto.getLessons().add(new LessonDto(10, secDto.getTestDefId(), secDto.getSubject(), "Test", "TestFile"));

                        for (LessonDto lo : ((SectionDto) loadConfig).getLessons()) {
                            base.add(lo);
                        }
                        callback.onSuccess(base);
                    } else if (loadConfig instanceof LessonDto) {
                        Window.alert("AddProblemDialog: should not get here");
//                        List<BaseDto> base = new ArrayList<BaseDto>();
//
//                        ((LessonDto) loadConfig).getProblems().add(new ProblemDto(10, "Lesson", "Pid Label 1", "Pid1"));
//                        for (LessonDto so : ((SectionDto) loadConfig).getLessons()) {
//                            base.add(so);
//                        }
//                        callback.onSuccess(base);
                    }

                }
            }
        };

        TreeLoader<BaseDto> loader = new TreeLoader<BaseDto>(proxy) {
            @Override
            public boolean hasChildren(BaseDto parent) {
                return parent instanceof FolderDto;
            }
        };
        
        loader.addLoadHandler(new ChildTreeStoreBinding<BaseDto>(_treeStore));

        for (BaseDto base : _root.getChildren()) {
            _treeStore.add(base);
            if (base instanceof FolderDto) {
                processFolder(_treeStore, (FolderDto) base);
            }
        }

        _tree = new Tree<BaseDto, String>(_treeStore, new ValueProvider<BaseDto, String>() {

            @Override
            public String getValue(BaseDto object) {
                if(object instanceof ProblemDto) {
                    return ((ProblemDto)object).getLabelWithType();
                }
                else {
                    return object.getName();
                }
            }

            @Override
            public void setValue(BaseDto object, String value) {
            }

            @Override
            public String getPath() {
                return "name";
            }
        });
        _tree.setLoader(loader);
        _tree.setWidth(300);
        _tree.setCheckable(true);
        _tree.setCheckStyle(CheckCascade.TRI);
        //_tree.setAutoSelect(true);
        
        final DelayedTask task = new DelayedTask() {

            @Override
            public void onExecute() {
                Log.debug("Selection Info", _tree.getCheckedSelection().size() + " item(s) checked");
                
                setWindowTitleCountSelectedProblems();
            }
        };

        _tree.addCheckChangedHandler(new CheckChangedHandler<BaseDto>() {
            @Override
            public void onCheckChanged(CheckChangedEvent<BaseDto> event) {
                task.delay(100);
            }
        });

        SimpleSafeHtmlCell<String> cell = new SimpleSafeHtmlCell<String>(SimpleSafeHtmlRenderer.getInstance(), "click") {
            @Override
            public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
                    ValueUpdater<String> valueUpdater) {
                super.onBrowserEvent(context, parent, value, event, valueUpdater);
                if ("click".equals(event.getType())) {
                    BaseDto base = _tree.getSelectionModel().getSelectedItem();
                    if (base instanceof ProblemDto) {
                        ProblemDto p = (ProblemDto)base;
                        Log.debug("View Question", "Viewing " + p.getLabel());
                        
                        QuestionViewerPanel.getInstance().viewQuestion(p, false);
                    }
                    else {
                        QuestionViewerPanel.getInstance().removeQuestion();
                    }
                }
            }

        };

        _tree.setCell(cell);
   
        return _tree;
    }
    
    private void setWindowTitleCountSelectedProblems() {
        makeSureLessonProblemsReadMaybeAsync(false, false, _tree,new AddProblemsCallback() {
            @Override
            public void problemsAdded(List<ProblemDto> problemsAdded) {
                String title = problemsAdded.size() + " problem(s) checked";
                _treePanel.setHeadingHtml(title);
            }
        });
                
    }
    

    private void getLessonItemsRPC(final int testDefId, final String subject, final int sectionNumber,
            final AsyncCallback<List<BaseDto>> callback) {

        new RetryAction<CmList<ProgramLesson>>() {
            @Override
            public void attempt() {
                String chap = null;
                int sectionCount = 6;
                GetProgramLessonsAction action = new GetProgramLessonsAction(testDefId, sectionNumber, chap,
                        sectionCount);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<ProgramLesson> lessons) {

                List<BaseDto> data = new ArrayList<BaseDto>();
                for (int i = 0, t = lessons.size(); i < t; i++) {
                    ProgramLesson pt = lessons.get(i);
                    data.add(new LessonDto(++BaseDto.autoId, testDefId, subject, pt.getLabel(), pt.getFile()));
                }
                callback.onSuccess(data);
            }

        }.register();
    }

    static public void getLessonProblemItemsRPC(final String lesson, final String file, final String subject,
            final AsyncCallback<List<BaseDto>> callback) {

        new RetryAction<CmList<ProblemDto>>() {
            @Override
            public void attempt() {
                GetProgramLessonProblemsAction action = new GetProgramLessonProblemsAction(lesson, file, subject);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<ProblemDto> problems) {
                List<BaseDto> data = new ArrayList<BaseDto>();
                for (int i = 0, t = problems.size(); i < t; i++) {
                    ProblemDto pt = problems.get(i);
                    pt.setId(++BaseDto.autoId);
                    data.add(pt);
                }
                callback.onSuccess(data);
            }

        }.register();
    }

    
    public static void showDialog(AddProblemsCallback callbackOnComplete) {
        if (__sharedInstance == null) {
            __sharedInstance = new AddProblemDialog();
        }

        __sharedInstance.setupViewerGui();
        __sharedInstance.forceLayout();
        __sharedInstance.setCallback(callbackOnComplete);
        __sharedInstance.show();
    }

    private static FolderDto makeFolder(String name) {
        FolderDto theReturn = new FolderDto(++BaseDto.autoId, name);
        theReturn.setChildren((List<BaseDto>) new ArrayList<BaseDto>());
        return theReturn;
    }



    ProgramListing _programListing;

    private void readDataAndBuildTree() {

        new RetryAction<ProgramListing>() {
            @Override
            public void attempt() {
                GetProgramListingAction action = new GetProgramListingAction(0);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(ProgramListing pl) {
                _programListing = pl;
                buildTree();
            }
        }.register();
    }

    FolderDto _root;

    private void buildTree() {

        _root = makeFolder("Root");

        ProgramType profType = getProficiencyType();

        // for each subject
        List<BaseDto> children = new ArrayList<BaseDto>();
        for (ProgramSubject ps : profType.getProgramSubjects()) {
            SubjectDto subjectDto = new SubjectDto(0, ps.getTestDefId(), ps.getName(), ps.getLabel());
            subjectDto.setChildren((List<BaseDto>) new ArrayList<BaseDto>());

            for (ProgramSection section : ps.getChapters().get(0).getSections()) {
                SectionDto sectionDto = new SectionDto(++BaseDto.autoId, subjectDto.getTestDefId(), subjectDto.getSubject(),
                        section.getNumber());
                subjectDto.getSections().add(sectionDto);

                subjectDto.addChild(sectionDto);
            }

            children.add(subjectDto);
        }
        
        
        children.add(new StandardNode());
        
        
        _root.setChildren(children);

        _treePanel.setWidget(makeTree());
        _treePanel.forceLayout();
    }

    private ProgramType getProficiencyType() {
        ProgramType type = null;
        for (ProgramType t : _programListing.getProgramTypes()) {
            if (t.getLabel().contains("Proficiency")) {
                type = t;
                break;
            }
        }
        return type;
    }
    
    
    public interface AddProblemsCallback {
        void problemsAdded(List<ProblemDto> problemsAdded);
    }
}


class StandardStateDto extends FolderDto implements Response {
    String state;
    public StandardStateDto(String state) {
        super(StandardNode.counter++, "State Standards");
        this.state = state;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
}

class StandardNode extends FolderDto implements Response {

    static int counter=(int)System.currentTimeMillis();
    List<StandardStateDto> states = new ArrayList<StandardStateDto>();
    
    public StandardNode(){
        super(counter++, "State Standards");
        states.add(new StandardStateDto("Ca"));
        states.add(new StandardStateDto("Common"));

        for(StandardStateDto s: states) {
            addChild(s);
        }
    }
    public List<StandardStateDto> getStates() {
        return states;
    }

    public void setStates(List<StandardStateDto> states) {
        this.states = states;
    }
}
