package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.custom_content.problem.CustomProblemFolderNode;
import hotmath.gwt.cm_admin.client.custom_content.problem.CustomProblemLeafNode;
import hotmath.gwt.cm_admin.client.custom_content.problem.CustomProblemTreeTable;
import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.CustomProblemInfo;
import hotmath.gwt.cm_rpc.client.rpc.GetCustomProblemAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.BaseDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.LessonDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CheckableMinLevelGxtTreeAppearance;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CheckChangedEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangedEvent.CheckChangedHandler;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;

public class AssignmentTreeCustomProblemsListingPanel extends ContentPanel {

    static public interface CallbackOnSelectedCustomProblem {
        void lessonWasSelected();
        void nodeWasChecked();
    }

    CallbackOnSelectedCustomProblem _callBack;

    Grid<LessonDto> _grid;
    Tree<BaseDto, String> _tree;

    protected List<CustomProblemModel> _allLessons;


    public AssignmentTreeCustomProblemsListingPanel(CallbackOnSelectedCustomProblem callBack) {
        _callBack = callBack;
        setWidget(new DefaultGxtLoadingPanel());
    }
    

    private void readDataAndBuildTree() {
        CatchupMathTools.setBusy(true);
        new RetryAction<CustomProblemInfo>() {
            @Override
            public void attempt() {
                GetCustomProblemAction action = new GetCustomProblemAction(new TeacherIdentity(UserInfoBase.getInstance().getUid(),"", 0));
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CustomProblemInfo results) {
            	List<CustomProblemModel> lessons = results.getProblems();
                CatchupMathTools.setBusy(false);
                _allLessons = lessons;
                makeTree(_allLessons, results.getPaths());
            }

        }.register();
    }
    

    public interface GridProperties extends PropertyAccess<String> {
        ModelKeyProvider<LessonDto> id();

        ValueProvider<LessonDto, String> lessonName();
    }

    TreeStore<BaseDto> _treeStore;
    
    FolderDto _root;
    
    private static FolderDto makeFolder(String name) {
        FolderDto theReturn = new FolderDto(++BaseDto.autoId, name);
        theReturn.setChildren((List<BaseDto>) new ArrayList<BaseDto>());
        return theReturn;
    }
    
    private static FolderDto makeLesson(LessonDto lessonDto) {
        LessonDto theReturn = lessonDto;
        theReturn.setId(++BaseDto.autoId);
        theReturn.setChildren((List<BaseDto>) new ArrayList<BaseDto>());
        return theReturn;
    }



    static int autoId;
    public void makeTree(final List<CustomProblemModel> custProblems, List<String> paths) {
        
        _treeStore = CustomProblemTreeTable.createTeacherProblemMap(custProblems,  paths, null);
        _root = (custProblems.size()>0)?(CustomProblemFolderNode)_treeStore.getAll().get(0):null;

        
        TreeLoader<BaseDto> loader = new TreeLoader<BaseDto>(proxy) {
            @Override
            public boolean hasChildren(BaseDto parent) {
                return parent instanceof FolderDto;
            }
        };
        loader.addLoadHandler(new ChildTreeStoreBinding<BaseDto>(_treeStore));

        FlowLayoutContainer flowContainer = new FlowLayoutContainer();
        flowContainer.setScrollMode(ScrollMode.AUTO);
        flowContainer.addStyleName("margin-10");

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
        }, new CheckableMinLevelGxtTreeAppearance(0));
        _tree.setLoader(loader);
        _tree.setWidth(300);
        _tree.setCheckable(true);
        _tree.setCheckStyle(CheckCascade.CHILDREN);        

        SimpleSafeHtmlCell<String> cell = new SimpleSafeHtmlCell<String>(SimpleSafeHtmlRenderer.getInstance(),BrowserEvents.CLICK) {
            @Override
            public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
                    ValueUpdater<String> valueUpdater) {
                super.onBrowserEvent(context, parent, value, event, valueUpdater);
                
                
                Log.info("Browser event: " + event);
                
                if (BrowserEvents.CLICK.equals(event.getType())) {
                    ProblemDto problem=null;
                    BaseDto base = _tree.getSelectionModel().getSelectedItem();
                    if(base instanceof CustomProblemLeafNode) {
                        CustomProblemModel customProblem = ((CustomProblemLeafNode)base).getCustomProblem();
                        problem = new ProblemDto();
                        problem.setPid(customProblem.getPid());
                        problem.setLabel(customProblem.getTreePath());
                    }
                    else if (base instanceof ProblemDto) {
                        problem = (ProblemDto)base;
                    }
                    
                    if(problem != null) {
                        Log.debug("View Question", "Viewing " + problem.getLabel());
                        QuestionViewerPanel.getInstance().viewQuestion(problem, false);
                    }
                    else {
                        QuestionViewerPanel.getInstance().removeQuestion();
                    }
                    
                }
            }
            
            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, String data, SafeHtmlBuilder sb) {
                String tip=null;
                for(BaseDto n: _tree.getStore().getAll()) {
                    String nk = _tree.getStore().getKeyProvider().getKey(n);
                    if(nk.equals(context.getKey())) {
                        if(n instanceof FolderDto) {
                            // do nothing on folder nodes
                        }
                        else if(n instanceof CustomProblemLeafNode) {
                            tip = ((CustomProblemLeafNode)n).getCustomProblem().getComments();
                        }
                        else {
                            tip = n.getName();
                        }
                        break;
                    }
                }
                // BaseDto node = _tree.getStore().getAll().get(index);
                Element element = DOM.createElement("span");
                if(tip != null) {
                    element.setAttribute("qtip",  tip);
                    element.setInnerText(tip);
                }
                else {
                    element.setInnerText(data);
                }
                
                sb.appendHtmlConstant(element.getString());
            }
        };
        _tree.setCell(cell);

        _tree.addCheckChangedHandler(new CheckChangedHandler<BaseDto>() {
            @Override
            public void onCheckChanged(CheckChangedEvent<BaseDto> event) {
                _callBack.nodeWasChecked();
            }
        });
        
        // tree.getStyle().setLeafIcon(ExampleImages.INSTANCE.music());
        flowContainer.add(_tree);
        
        new QuickTip(_tree);

        setWidget(flowContainer);
        forceLayout();
        
        
        _tree.expandAll();  
    }

    RpcProxy<BaseDto, List<BaseDto>> proxy = new RpcProxy<BaseDto, List<BaseDto>>() {
        @Override
        public void load(final BaseDto loadConfig, AsyncCallback<List<BaseDto>> callback) {
        	if(loadConfig == null) {
        		return;
        	}
        	
            if (loadConfig.getChildren() == null || loadConfig.getChildren().size() == 0) {
                 if (loadConfig instanceof LessonDto) {
                     Log.info("Loading lesson problems: " + loadConfig);
                    LessonDto l = (LessonDto) loadConfig;
                    AddProblemDialog.getLessonProblemItemsRPC(l.getLessonName(),l.getLessonFile(), l.getSubject(), callback, new CallbackOnComplete() {
                        
                        @Override
                        public void isComplete() {
                            Log.info("Scrolling window into view: " + loadConfig.getId());
                            _tree.scrollIntoView(loadConfig);
                        }
                    });
                }
            }
            else {
                callback.onSuccess(loadConfig.getChildren());
            }
        }
    };

    private TreeStore<BaseDto> _store;        

    

    public void refreshData() {
        if(_treeStore != null) {
            _treeStore.clear();
        }
        readDataAndBuildTree();
    }
}


