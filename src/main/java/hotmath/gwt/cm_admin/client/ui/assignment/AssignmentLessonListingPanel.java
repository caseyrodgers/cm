package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.BaseDto;
import hotmath.gwt.cm_rpc.client.model.assignment.LessonDto;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.SubjectDto;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentAvailableLessonsAction;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CheckChangedEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangedEvent.CheckChangedHandler;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;

public class AssignmentLessonListingPanel extends ContentPanel {

    static public interface CallbackOnSelectedLesson {
        void lessonWasSelected();
    }

    CallbackOnSelectedLesson _callBack;

    Grid<LessonDto> _grid;
    BorderLayoutContainer _main;
    Tree<BaseDto, String> _tree;

    public AssignmentLessonListingPanel(CallbackOnSelectedLesson callBack) {
        _callBack = callBack;
        
        setHeadingHtml("All Available Lessons");
        
        _main = new BorderLayoutContainer();
        setWidget(_main);
    }
    
    
    private void readDataAndBuildTree() {

        new RetryAction<CmList<LessonDto>>() {
            @Override
            public void attempt() {
                GetAssignmentAvailableLessonsAction action = new GetAssignmentAvailableLessonsAction();
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<LessonDto> lessons) {
                makeTree(lessons);
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
        _treeStore = new TreeStore<BaseDto>(new TreeKeyProvider());
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
        TreeLoader<BaseDto> loader = new TreeLoader<BaseDto>(proxy) {
            @Override
            public boolean hasChildren(BaseDto parent) {
                return parent instanceof FolderDto;
            }
        };
        loader.addLoadHandler(new ChildTreeStoreBinding<BaseDto>(_treeStore));

        _root = makeFolder("Root");
        List<BaseDto> children = new ArrayList<BaseDto>();
        for(LessonDto l: lessons) {
            children.add(makeLesson(l));  // new LessonDto(autoId++,0,"All",l.getLessonName()));
        }
        _root.setChildren(children);
        
        FolderDto root = _root;
        for (BaseDto base : root.getChildren()) {
          _treeStore.add(base);
          if (base instanceof FolderDto) {
            processFolder(_treeStore, (FolderDto) base);
          }
        }

        FlowLayoutContainer con = new FlowLayoutContainer();
        con.setScrollMode(ScrollMode.AUTOY);
        con.addStyleName("margin-10");

        _tree = new Tree<BaseDto, String>(_treeStore, new ValueProvider<BaseDto, String>() {

            @Override
            public String getValue(BaseDto object) {
                return object.getName();
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
        
        
        final DelayedTask task = new DelayedTask() {
            @Override
            public void onExecute() {
                Log.debug(_tree.getCheckedSelection().size() + " problem(s) selected");
            }
        };
        _tree.addCheckChangedHandler(new CheckChangedHandler<BaseDto>() {
            @Override
            public void onCheckChanged(CheckChangedEvent<BaseDto> event) {
                task.delay(100);
            }
        });
        
        
        
        // tree.getStyle().setLeafIcon(ExampleImages.INSTANCE.music());
        con.add(_tree);
        
        
        StoreFilterField<BaseDto> filter = new StoreFilterField<BaseDto>() {
            
            @Override
            protected boolean doSelect(Store<BaseDto> store, BaseDto parent, BaseDto item, String filter) {
              if (item instanceof FolderDto) {
                  String name = item.getName();
                  name = name.toLowerCase();
                  if (name.startsWith(filter.toLowerCase())) {
                    return true;
                  }
                  return false;
              }
              else {
                  return false;
              }
            }
          };
       //   filter.bind(_treeStore);
        
        //_main.setCenterWidget(con);
        setWidget(con);
        
        //BorderLayoutData blc = new BorderLayoutData(70);
        //_main.setNorthWidget(filter, blc);
        
        forceLayout();
    }

    public void refreshData() {
        if(_treeStore != null) {
            _treeStore.clear();
        }
        readDataAndBuildTree();
    }

}