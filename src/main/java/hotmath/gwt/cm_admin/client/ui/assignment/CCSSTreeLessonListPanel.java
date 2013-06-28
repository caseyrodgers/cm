package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentTreeAllLessonsListingPanel.CallbackOnSelectedLesson;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.BaseDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CheckableMinLevelGxtTreeAppearance;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.CCSSData;
import hotmath.gwt.shared.client.model.CCSSDomain;
import hotmath.gwt.shared.client.model.CCSSLesson;
import hotmath.gwt.shared.client.model.CCSSGradeLevel;
import hotmath.gwt.shared.client.model.CCSSStandard;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CCSSDataAction;

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
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.dom.client.BrowserEvents;

import java.util.ArrayList;
import java.util.List;

/**
 * display selectable Tree of CCSS Grade Level -> Topic/Domain -> Standard -> Lesson -> Problem
 *  
 * @author bob
 *
 */
public class CCSSTreeLessonListPanel extends ContentPanel {

    CallbackOnSelectedLesson _callBack;

    Grid<CCSSLesson> _grid;
    Tree<BaseDto, String> _tree;

    protected CCSSData _ccssData;

    public CCSSTreeLessonListPanel(CallbackOnSelectedLesson callBack) {
        _callBack = callBack;
        
        setWidget(new HTML("<h1>Loading ...</h1>"));
    }

    public interface GridProperties extends PropertyAccess<String> {
        ModelKeyProvider<CCSSLesson> label();

        ValueProvider<CCSSLesson, String> name();
    }

    TreeStore<BaseDto> _treeStore;

    class TreeKeyProvider implements ModelKeyProvider<BaseDto> {
        @Override
        public String getKey(BaseDto item) {
        	return String.valueOf(item.getId());
        }
    }

    public void refreshData() {
        if(_treeStore != null) {
            _treeStore.clear();
        }
        readDataAndBuildTree();
    }

    public void makeTree(CCSSData ccssData) {
        _treeStore = setupTreeStore(ccssData);
        
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
            public String getValue(BaseDto dto) {
                if(dto instanceof ProblemDto) {
                    return ((ProblemDto)dto).getLabelWithType();
                }
                else {
                    return dto.getName();
                }
            }

            @Override
            public void setValue(BaseDto dto, String value) {
            }

            @Override
            public String getPath() {
                return "name";
            }

        }, new CheckableMinLevelGxtTreeAppearance(3));
        _tree.setLoader(loader);
        _tree.setWidth(350);
        _tree.setCheckable(true);
        _tree.setCheckStyle(CheckCascade.CHILDREN);        
        
        SimpleSafeHtmlCell<String> cell = new SimpleSafeHtmlCell<String>(SimpleSafeHtmlRenderer.getInstance(),
        		BrowserEvents.CLICK) {
            @Override
            public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
                    ValueUpdater<String> valueUpdater) {
                super.onBrowserEvent(context, parent, value, event, valueUpdater);
                BaseDto dto = _tree.getSelectionModel().getSelectedItem();
                if (BrowserEvents.CLICK.equalsIgnoreCase(event.getType())) {
                    if (dto instanceof ProblemDto) {
                    	ProblemDto p = (ProblemDto)dto;
                        Log.debug("View Question", "Viewing " + p.getLabel());

                        QuestionViewerPanel.getInstance().viewQuestion(p, false);
                    }
                    if (dto != null && dto.getLevel() == CCSSData.STANDARD) {
                    	CCSSStandard ccss = (CCSSStandard)dto;
                    	MessageBox msgBox = new MessageBox(ccss.getName(), ccss.getSummary());
                    	msgBox.show();
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

        flowContainer.add(_tree);
        setWidget(flowContainer);
        forceLayout();
    }

    RpcProxy<BaseDto, List<BaseDto>> proxy = new RpcProxy<BaseDto, List<BaseDto>>() {
        @SuppressWarnings("unchecked")
		@Override
        public void load(BaseDto node, AsyncCallback<List<BaseDto>> callback) {
            if (node.getLevel() == CCSSData.LESSON) {
                CCSSLesson l = (CCSSLesson) node;
                Log.debug("Loading problems for lesson: " + l);
                AddProblemDialog.getLessonProblemItemsRPC(l.getName(), l.getFile(), null, callback, null);
            }
            else {
                callback.onSuccess(node.getChildren());
                forceLayout();
            }
        }
    };

    @SuppressWarnings("unchecked")
	public void load(BaseDto nodeDto, AsyncCallback<List<BaseDto>> callback) {
        if (nodeDto.getChildren() == null || nodeDto.getChildren().size() == 0) {
            if (nodeDto.getLevel() == CCSSData.LESSON) {
                Log.debug("Loading lesson problems: " + nodeDto);
                CCSSLesson l = (CCSSLesson) nodeDto;
                CmMessageBox.showAlert("Getting problems for: " + l.getName());
                AddProblemDialog.getLessonProblemItemsRPC(l.getName(), l.getFile(), null, callback, null);
            }
            else if (nodeDto.getLevel() == CCSSData.STANDARD) {
                 Log.debug("Loading lessons: " + nodeDto);
                 CCSSStandard s = (CCSSStandard) nodeDto;
                 CmMessageBox.showAlert("Getting lessons for: " + s.getName());
             }
             else if(nodeDto.getLevel() == CCSSData.DOMAIN) {
                 Log.debug("Loading standards: " + nodeDto);
                 CCSSDomain d = (CCSSDomain) nodeDto;
                 CmMessageBox.showAlert("Getting standards for: " + d.getName());
             }
             else if(nodeDto.getLevel() == CCSSData.GRADE) {
                 Log.debug("Loading domains/topics: " + nodeDto);
                 CCSSGradeLevel l = (CCSSGradeLevel) nodeDto;
                 CmMessageBox.showAlert("Getting domains for: " + l.getName());
             }
        }
        else {
            callback.onSuccess(nodeDto.getChildren());
            forceLayout();
        }
    }

	private FolderDto _root;

	private TreeStore<BaseDto> setupTreeStore(CCSSData ccssData) {
        
        List<CCSSGradeLevel> levels =  ccssData.getLevels();

        TreeStore<BaseDto> treeStore = new TreeStore<BaseDto>(new TreeKeyProvider());

        TreeLoader<BaseDto> loader = new TreeLoader<BaseDto>(proxy) {
            @Override
            public boolean hasChildren(BaseDto parent) {
                return parent instanceof FolderDto;
            }
        };
        loader.addLoadHandler(new ChildTreeStoreBinding<BaseDto>(treeStore));

        _root = makeFolder("Root");
        List<BaseDto> base = new ArrayList<BaseDto>();
        for(CCSSGradeLevel level: levels) {
            initFolder(level);
            base.add(level);

            // init nodes for each domain/topic at this level
            if (level.getDomains() == null) continue;
            for(CCSSDomain domain: level.getDomains()) {
            	initFolder(domain);
            	level.addChild(domain);

            	// init nodes for each standard in this domain/topic
            	if (domain.getStandards() == null) continue;
                for(CCSSStandard standard: domain.getStandards()) {
                	initFolder(standard);
                	domain.addChild(standard);

                	// init nodes for each Lesson associated with this Standard (CCSS)
                	if (standard.getLessons() == null) continue;
                    for(CCSSLesson lesson: standard.getLessons()) {
                    	initFolder(lesson);
                    	standard.addChild(lesson);
                    }
                }
            }
            
        }
        _root.setChildren(base);
        
        FolderDto root = _root;
        for (BaseDto dto : root.getChildren()) {
          treeStore.add(dto);
          if (dto instanceof FolderDto) {
            addToStore(treeStore, (FolderDto) dto);
          }
        }
        return treeStore;
    }

    private void addToStore(TreeStore<BaseDto> store, FolderDto folder) {
        for (BaseDto child : folder.getChildren()) {
            try {
                store.add(folder, child);
                if (child instanceof FolderDto) {
                    addToStore(store, (FolderDto) child);
                }
            }
            catch (AssertionError ae) {
            	String name = folder.getName();
                CmMessageBox.showAlert("Error adding: " + name + ", ID: " + folder.getId());
            }
        }
      }

    private static FolderDto makeFolder(String name) {
        FolderDto theReturn = new FolderDto(++BaseDto.autoId, name);
        theReturn.setChildren((List<BaseDto>) new ArrayList<BaseDto>());
        return theReturn;
    }

    private static FolderDto initFolder(FolderDto dto) {
    	dto.setId(++BaseDto.autoId);
        dto.setChildren((List<BaseDto>) new ArrayList<BaseDto>());
        return dto;
    }

    private void readDataAndBuildTree() {
        CatchupMathTools.setBusy(true);
        new RetryAction<CCSSData>() {
            @Override
            public void attempt() {
                CCSSDataAction action = new CCSSDataAction();
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CCSSData data) {
                CatchupMathTools.setBusy(false);
                _ccssData = data;
                makeTree(_ccssData);
            }

        }.register();
    }

}
