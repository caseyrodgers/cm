package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_rpc.client.model.assignment.BaseDto;
import hotmath.gwt.cm_rpc.client.model.assignment.FolderDto;
import hotmath.gwt.cm_rpc.client.model.assignment.LessonDto;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.SubjectDto;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentAvailableLessonsAction;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.DelayedTask;
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
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;

public class AssignmentTreeAllLessonsListingPanel extends ContentPanel {

    static public interface CallbackOnSelectedLesson {
        void lessonWasSelected();
        void nodeWasChecked();
    }

    CallbackOnSelectedLesson _callBack;

    Grid<LessonDto> _grid;
    Tree<BaseDto, String> _tree;

    protected CmList<LessonDto> _allLessons;

    private FilterSearchField _searchField;

    public AssignmentTreeAllLessonsListingPanel(CallbackOnSelectedLesson callBack) {
        _callBack = callBack;
        
        _searchField = new FilterSearchField();
        addTool(new MyFieldLabel(_searchField,  "Search",  50,100));
        setWidget(new HTML("<h1>Loading ...</h1>"));
    }
    
    
    private void readDataAndBuildTree() {

        CatchupMathTools.setBusy(true);
        new RetryAction<CmList<LessonDto>>() {
            @Override
            public void attempt() {
                GetAssignmentAvailableLessonsAction action = new GetAssignmentAvailableLessonsAction();
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<LessonDto> lessons) {
                CatchupMathTools.setBusy(false);
                _allLessons = lessons;
                makeTree(_allLessons);
            }

        }.register();
    }
    

    public interface GridProperties extends PropertyAccess<String> {
        ModelKeyProvider<LessonDto> id();

        ValueProvider<LessonDto, String> lessonName();
    }

    TreeStore<BaseDto> _treeStore;

    class TreeKeyProvider implements ModelKeyProvider<BaseDto> {
        @Override
        public String getKey(BaseDto item) {
            if (item instanceof SubjectDto) {
                return ((SubjectDto) item).getSubject();
            } else {
                return (item instanceof FolderDto ? "f-" : "m-") + item.getId().toString();
            }
        }
    }
    
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


    
    private void processFolder(TreeStore<BaseDto> store, FolderDto folder) {
        for (BaseDto child : folder.getChildren()) {
          store.add(folder, child);
          if (child instanceof FolderDto) {
            processFolder(store, (FolderDto) child);
          }
        }
      }


    static int autoId;
    public void makeTree(List<LessonDto> lessons) {
        
        _treeStore = setupTreeStore(lessons);
        
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
        });
        _tree.setLoader(loader);
        _tree.setWidth(300);
        _tree.setCheckable(true);
        _tree.setCheckStyle(CheckCascade.TRI);        
        _tree.setWidth(300);
        
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
                }
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

        setWidget(flowContainer);
        forceLayout();
    }

    RpcProxy<BaseDto, List<BaseDto>> proxy = new RpcProxy<BaseDto, List<BaseDto>>() {
        @Override
        public void load(BaseDto loadConfig, AsyncCallback<List<BaseDto>> callback) {
            if (loadConfig.getChildren() == null || loadConfig.getChildren().size() == 0) {
                 if (loadConfig instanceof LessonDto) {
                     Log.debug("Loading lesson problems: " + loadConfig);
                    LessonDto l = (LessonDto) loadConfig;
                    AddProblemDialog.getLessonProblemItemsRPC(l.getLessonName(),l.getLessonFile(), l.getSubject(), callback);
                }
            }
        }
    };        

    
    private TreeStore<BaseDto> setupTreeStore(List<LessonDto> lessons) {
        
        List<LessonDto> ll2 = new ArrayList<LessonDto>();
        String filter = _searchField.getCurrentValue();
        if(filter != null) {
            filter = filter.toLowerCase();
            for(LessonDto l: lessons) {
                if(l.getName().toLowerCase().indexOf(filter) != -1) {
                    ll2.add(l);
                }
            }
            lessons = ll2;
        }

        TreeStore<BaseDto> treeStore = new TreeStore<BaseDto>(new TreeKeyProvider());
        TreeLoader<BaseDto> loader = new TreeLoader<BaseDto>(proxy) {
            @Override
            public boolean hasChildren(BaseDto parent) {
                return parent instanceof FolderDto;
            }
        };
        loader.addLoadHandler(new ChildTreeStoreBinding<BaseDto>(treeStore));

        _root = makeFolder("Root");
        List<BaseDto> children = new ArrayList<BaseDto>();
        for(LessonDto l: lessons) {
            children.add(makeLesson(l));  // new LessonDto(autoId++,0,"All",l.getLessonName()));
        }
        _root.setChildren(children);
        
        FolderDto root = _root;
        for (BaseDto base : root.getChildren()) {
          treeStore.add(base);
          if (base instanceof FolderDto) {
            processFolder(treeStore, (FolderDto) base);
          }
        }
        return treeStore;
    }

    public void refreshData() {
        if(_treeStore != null) {
            _treeStore.clear();
        }
        readDataAndBuildTree();
    }
    
    
    class FilterSearchField extends TextField {
        public FilterSearchField() {
            addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    makeTree(_allLessons);
                }
            });
        }
    }
}


