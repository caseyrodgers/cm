package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.rpc.GetLessonTreeAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.BaseDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;

public class WebLinkAddTargetsDialog extends GWindow {

    static private WebLinkAddTargetsDialog __instance;
    static public WebLinkAddTargetsDialog getSharedInstance(Callback callback) {
        if(true || __instance == null) {
            __instance = new WebLinkAddTargetsDialog();
        }
        __instance.setCallback(callback);
        __instance.setVisible(true);
        return __instance;
    }
    
    protected ProgramListing _programListing;
    private Callback callback;

    interface Callback {
        void targetsAdded(List<LessonModel> targetResults);
    }
    
    ContentPanel _main;
    private WebLinkAddTargetsDialog() {
        super(false);
        setHeadingText("Select Lesson(s)");

        setModal(true);
        setMaximizable(true);
        
        addTool(new TextButton("Collapse", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                _tree.collapseAll();
            }
        }));
        addTool(new TextButton("Expand", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                _tree.expandAll();
            }
        }));

        addButton(new TextButton("Add", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                doAddCheckedLessons();
            }
        }));
        addCloseButton();
        
        _main = new ContentPanel();
        _main.setHeaderVisible(false);
        _main.setWidget(new DefaultGxtLoadingPanel());
        setWidget(_main);
        
        getProgramListingRPC();
        setVisible(true);
    }

        
    protected void doAddCheckedLessons() {
        
        List<LessonModel> targetResults = new ArrayList<LessonModel>();
        List<BaseDto> checked = _tree.getCheckedSelection();
        List<String> subjects = new ArrayList<String>();
        List<MyLessonDto> lessons = new ArrayList<MyLessonDto>();
        for(BaseDto item: checked) {
            if(item instanceof FolderDto) {
                subjects.add(item.getName());
            }
            else {
                lessons.add((MyLessonDto)item);
            }
        }

        for(String subject: subjects) {
            targetResults.add(new LessonModel(subject + ": All lessons", null, subject));
        }
        
        
        for(MyLessonDto md: lessons) {
            if(!subjects.contains(md.getSubject())) {
                targetResults.add(new LessonModel(md.getLessonName(), md.getLessonFile(), md.getSubject()));
            }
        }
        
        callback.targetsAdded(targetResults);
        hide();
    }

    private void setCallback(Callback callback) {
        this.callback = callback;
        if(_tree != null) {
            unselectAllProblems(_tree);
        }
    }
    
    private void unselectAllProblems(Tree<BaseDto, String> tree) {
        for (BaseDto d : tree.getCheckedSelection()) {
            tree.setChecked(d, CheckState.UNCHECKED);
        }
    }

    private void getProgramListingRPC() {

        new RetryAction<CmList<LessonModel>>() {
            @Override
            public void attempt() {
                GetLessonTreeAction action = new GetLessonTreeAction();
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<LessonModel> pl) {
                buildTree(pl);
            }
        }.register();
    }

    private TreeStore<BaseDto> _treeStore;
    private FolderDto _root;
    Tree<BaseDto, String> _tree;
    
    protected void buildTree(CmList<LessonModel> pl) {
    
        _root = makeFolder("Root");
        _treeStore = new TreeStore<BaseDto>(new KeyProvider());
        
        String subject="";
        List<LessonModel> subjLessons = new ArrayList<LessonModel>();
        for (LessonModel lesson : pl) {
            if(!subject.equals(lesson.getSubject())) {
                subject=lesson.getSubject();
                if(subjLessons.size() > 0) {
                    writeNewTreeNode(subjLessons);
                }
                subjLessons.clear();
            }
            subjLessons.add(lesson);
        }
        
        if(subjLessons.size() > 0) {
            writeNewTreeNode(subjLessons);
        }
        
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

         _tree.setCheckable(true);
         _tree.setCheckStyle(Tree.CheckCascade.CHILDREN);
         
         
         _tree.addCheckChangeHandler(new CheckChangeHandler<BaseDto>() {
             @Override
            public void onCheckChange(CheckChangeEvent<BaseDto> event) {
                 if(event.getItem() instanceof FolderDto) {
                     _tree.setExpanded(event.getItem(), false);
                 }
            }
        });

         _main.setWidget(_tree);
         forceLayout();
    }
    
    /** All lessons in list are same subject, and have at last one */
    private void writeNewTreeNode(List<LessonModel> subjLessons) {
        assert(subjLessons.size() > 0);
        
        FolderDto subjFolder = makeFolder(subjLessons.get(0).getSubject());
        _treeStore.add(subjFolder);
        for(LessonModel lessonModel: subjLessons) {
            _treeStore.add(subjFolder,new MyLessonDto(++BaseDto.autoId, 0, subjFolder.getName(), lessonModel.getLessonName(), lessonModel.getLessonFile()));
        }
    }

    private static FolderDto makeFolder(String name) {
        FolderDto theReturn = new FolderDto(++BaseDto.autoId, name);
        theReturn.setChildren((List<BaseDto>) new ArrayList<BaseDto>());
        return theReturn;
    }
    

    class KeyProvider implements ModelKeyProvider<BaseDto> {
        @Override
        public String getKey(BaseDto item) {
          return item.getId() + "";
        }
      }

}

class MyLessonDto extends BaseDto {

    private String lessonName;
    private String lessonFile;
    private String subject;

    public MyLessonDto(int i, int __UNKNOWN, String name, String lessonName, String lessonFile) {
        super(i,lessonName);
        this.subject = name;
        this.lessonName = lessonName;
        this.lessonFile = lessonFile;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getLessonFile() {
        return lessonFile;
    }

    public void setLessonFile(String lessonFile) {
        this.lessonFile = lessonFile;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

}
