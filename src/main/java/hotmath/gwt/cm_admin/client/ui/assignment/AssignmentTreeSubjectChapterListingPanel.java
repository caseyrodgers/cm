package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentTreeAllLessonsListingPanel.CallbackOnSelectedLesson;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapter;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSubject;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramType;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramLessonsAction;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramListingAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.BaseDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.LessonDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.SubjectDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CheckableMinLevelGxtTreeAppearance;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.util.CmLoggerWindow;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
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
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;

public class AssignmentTreeSubjectChapterListingPanel extends ContentPanel {

    CallbackOnSelectedLesson _callBack;

    Grid<LessonDto> _grid;
    Tree<BaseDto, String> _tree;

    protected ProgramListing _programLessons;

    public AssignmentTreeSubjectChapterListingPanel(CallbackOnSelectedLesson callBack) {
        _callBack = callBack;
        
        setWidget(new HTML("<h1>Loading ...</h1>"));
    }
    
    
    private void readDataAndBuildTree() {
        CatchupMathTools.setBusy(true);
        new RetryAction<ProgramListing>() {
            @Override
            public void attempt() {
                GetProgramListingAction action = new GetProgramListingAction(UserInfoBase.getInstance().getUid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(ProgramListing programLessons) {
                CatchupMathTools.setBusy(false);
                _programLessons = programLessons;
                makeTree(_programLessons);
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
            } else if (item instanceof ChapterDto) {
                return ((ChapterDto) item).getName();
            }
            else {
                return (item instanceof FolderDto ? "f-" : "m-") + item.getId().toString();
            }
        }
    }
    
    FolderDto _root;

    protected BaseDto _lastLoadConfig;
    
    private static FolderDto makeFolder(String name) {
        FolderDto theReturn = new FolderDto(++BaseDto.autoId, name);
        theReturn.setChildren((List<BaseDto>) new ArrayList<BaseDto>());
        return theReturn;
    }
    
    private static FolderDto makeChapter(int testDefId, String subject, String name) {
        ChapterDto theReturn = new ChapterDto(++BaseDto.autoId, testDefId,subject, name);
        theReturn.setChildren((List<BaseDto>) new ArrayList<BaseDto>());
        return theReturn;
    }

    
    private void addToStore(TreeStore<BaseDto> store, FolderDto folder) {
        for (BaseDto child : folder.getChildren()) {
          store.add(folder, child);
          if (child instanceof FolderDto) {
            addToStore(store, (FolderDto) child);
          }
        }
      }


    static int autoId;
    public void makeTree(ProgramListing programLessons) {
        
        
        _treeStore = setupTreeStore(programLessons);
        
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
        },new CheckableMinLevelGxtTreeAppearance(2));
        _tree.setLoader(loader);
        _tree.setWidth(300);
        _tree.setCheckable(true);
        _tree.setCheckStyle(CheckCascade.CHILDREN);        
        _tree.setWidth(300);
        
        SimpleSafeHtmlCell<String> cell = new SimpleSafeHtmlCell<String>(SimpleSafeHtmlRenderer.getInstance(), BrowserEvents.CLICK) {
            @Override
            public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
                    ValueUpdater<String> valueUpdater) {
                super.onBrowserEvent(context, parent, value, event, valueUpdater);
                if (BrowserEvents.CLICK.equals(event.getType())) {
                    CmLoggerWindow.getInstance()._info("Broswer click event");
                    BaseDto base = _tree.getSelectionModel().getSelectedItem();

                    if (base instanceof ProblemDto) {
                        ProblemDto p = (ProblemDto)base;
                        CmLoggerWindow.getInstance()._info("Viewing problem: " + p.getLabel());
                        QuestionViewerPanel.getInstance().viewQuestion(p, false);
                    }
                }
            }
        };
        _tree.setCell(cell);
        
        
        @SuppressWarnings("unused")
		final DelayedTask task = new DelayedTask() {
            @Override
            public void onExecute() {
                Log.debug(_tree.getCheckedSelection().size() + " problem(s) selected");
            }
        };
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
        @SuppressWarnings("unchecked")
		@Override
        public void load(BaseDto loadConfig, AsyncCallback<List<BaseDto>> callback) {
            _lastLoadConfig = loadConfig;
            if (loadConfig.getChildren() == null || loadConfig.getChildren().size() == 0) {
                if (loadConfig instanceof LessonDto) {
                    Log.debug("Loading lesson problems: " + loadConfig);
                    final LessonDto l = (LessonDto) loadConfig;
                    AddProblemDialog.getLessonProblemItemsRPC(l.getLessonName(),l.getLessonFile(), l.getSubject(), callback, new CallbackOnComplete() {
                        
                        @Override
                        public void isComplete() {
                            _tree.scrollIntoView(l);
                        }
                    });
                }
                else if (loadConfig instanceof SubjectDto) {
                     Log.debug("Loading lesson problems: " + loadConfig);
                     SubjectDto l = (SubjectDto) loadConfig;
                     CmMessageBox.showAlert("Getting lessons for: " + l.getName());
                 }
                 else if(loadConfig instanceof ChapterDto) {
                     getChapterInfo((ChapterDto)loadConfig, callback);
                 }
            }
            else {
                callback.onSuccess(loadConfig.getChildren());
                forceLayout();
            }
        }
    };        

    
    private TreeStore<BaseDto> setupTreeStore(ProgramListing programLessons) {
        
        ProgramType subjectType = programLessons.getProgramTypes().get(1);
        List<ProgramSubject> subjects = subjectType.getProgramSubjects();        
        
        
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
        for(ProgramSubject l: subjects) {
            FolderDto subjectNode = makeFolder(l.getLabel());
            children.add(subjectNode);
            
            // create nodes for each chapter in this subject
            for(ProgramChapter c: l.getChapters()) {
               subjectNode.addChild(makeChapter(l.getTestDefId(),l.getName(),c.getLabel())) ;
            }
            
        }
        _root.setChildren(children);
        
        FolderDto root = _root;
        for (BaseDto base : root.getChildren()) {
          treeStore.add(base);
          if (base instanceof FolderDto) {
            addToStore(treeStore, (FolderDto) base);
          }
        }
        return treeStore;
    }

    protected void getChapterInfo(final ChapterDto chapterNode, final AsyncCallback<List<BaseDto>> callback) {
        
        new RetryAction<CmList<ProgramLesson>>() {
            @Override
            public void attempt() {
                GetProgramLessonsAction action = new GetProgramLessonsAction(chapterNode.getTestDefId(),1,chapterNode.getName(),0);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<ProgramLesson> lessons) {
                CatchupMathTools.setBusy(false);
                List<BaseDto> l =  new ArrayList<BaseDto>();
                for(ProgramLesson pl: lessons) {
                    LessonDto lessonNode = new LessonDto(++BaseDto.autoId,chapterNode.getTestDefId(),chapterNode.getSubject(),pl.getName(),pl.getFile());
                    l.add(lessonNode);
                }
                callback.onSuccess(l);
            }

        }.register();

    }


    public void refreshData() {
        if(_treeStore != null) {
            _treeStore.clear();
        }
        readDataAndBuildTree();
    }
}

class ChapterDto extends FolderDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int testDefId;
    private String subject;
    public ChapterDto(int id, int testDefId, String subject, String name) {
        super(id, name);
        this.subject = subject;
        this.testDefId = testDefId;
    }
    
    public int getTestDefId() {
        return testDefId;
    }
    
    public String getSubject() {
        return subject;
    }
}