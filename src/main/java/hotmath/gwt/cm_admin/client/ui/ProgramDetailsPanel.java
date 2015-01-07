package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_rpc.client.model.program_listing.CmTreeNode;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapter;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSection;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSubject;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramType;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramLessonsAction;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramListingAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.ProgListModel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.ui.TopicExplorerWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfProgramDetailsReportAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;

public class ProgramDetailsPanel extends GWindow {

    static ProgramDetailsPanel instance;
    static int WIDTH = 435;

    TreeStore<ProgListModel> store;
    TreeLoader<ProgListModel> loader;
    Tree<ProgListModel, String> _tree;

    CmAdminModel cmAdminMdl;
    ProgramListing _programListing;

    private ProgramDetailsPanel(CmAdminModel cmAdminMdl) {
        super(true);
        setModal(true);
        setResizable(true);

        this.cmAdminMdl = cmAdminMdl;
        buildGui();
        getProgramListingRPC();
    }

    public static void showPanel(CmAdminModel cmAdminMdl) {
        if (instance == null) {
            instance = new ProgramDetailsPanel(cmAdminMdl);
        }
        instance.setVisible(true);
    }

    private void buildGui() {
        setHeadingText("Lesson Topics Covered in Programs");
        setPixelSize(WIDTH, 400);

        TextButton eBtn = createExploreLessonButton();
        getHeader().addTool(eBtn);

        TextButton cBtn = new TextButton("Collapse All", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                _tree.collapseAll();
            }
        });
        // cBtn.setStyleName("program-details-collapse-btn");
        getHeader().addTool(cBtn);
        getHeader().setStyleName("program-details-header");

        Component pBtn = buildPrintButton();
        getHeader().addTool(pBtn);

        CenterLayoutContainer c = new CenterLayoutContainer();
        c.setWidget(new Label("Loading Program Information.."));
        setWidget(c);
    }

    private Component buildPrintButton() {
        IconButton btn = new IconButton("printer-icon");
        // btn.setIconStyle("printer-icon");
        btn.setToolTip("Display a printable report");
        btn.addStyleName("student-details-panel-pr-btn");

        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                resetSelected();

                TreeStore<ProgListModel> ts = _tree.getStore();
                List<ProgListModel> list = ts.getAll();

                int count = 0;
                for (ProgListModel m : list) {
                    if (_tree.isExpanded(m)) {
                        setSelected(m);
                        count++;
                    }
                }
                if (count == 0) {
                    CmMessageBox.showAlert("Please expand a node before selecting Print.");
                } else {
                    // display printable report
                    new PdfWindow(cmAdminMdl.getUid(), "Catchup Math Program Details",
                            new GeneratePdfProgramDetailsReportAction(cmAdminMdl.getUid(), _programListing));

                }
            }
        });
        return btn;
    }

    protected void resetSelected() {
        List<ProgramType> ptList = _programListing.getProgramTypes();
        for (ProgramType pt : ptList) {
            pt.setSelected(false);
            List<ProgramSubject> psList = pt.getProgramSubjects();
            for (ProgramSubject ps : psList) {
                ps.setSelected(false);
                List<ProgramChapter> pcList = ps.getChapters();
                for (ProgramChapter pc : pcList) {
                    pc.setSelected(false);
                    List<ProgramSection> sectList = pc.getSections();
                    for (ProgramSection sect : sectList) {
                        sect.setSelected(false);
                    }
                }
            }
        }
    }

    ProgramType progType = null;
    ProgramSubject progSubj = null;
    ProgramChapter progChap = null;
    ProgramSection progSect = null;

    private void setSelected(ProgListModel m) {
        if (m.getLevel() == ProgramListing.LEVEL_TYPE) {
            progType = findProgramType(m.getLabel());
            progType.setSelected(true);
        } else if (m.getLevel() == ProgramListing.LEVEL_SUBJ) {
            progType = findProgramType(m.getData().getParent().getLabel());
            progSubj = findProgramSubject(m.getLabel());
            progSubj.setSelected(true);
        } else if (m.getLevel() == ProgramListing.LEVEL_CHAP) {
            progType = findProgramType(m.getData().getParent().getParent().getLabel());
            progSubj = findProgramSubject(m.getData().getParent().getLabel());
            progChap = findProgramChapter(m.getLabel());
            progChap.setSelected(true);
        } else if (m.getLevel() == ProgramListing.LEVEL_SECT) {
            CmTreeNode node = m.getData().getParent().getParent().getParent();
            if (node != null) {
                progType = findProgramType(m.getData().getParent().getParent().getParent().getLabel());
                progSubj = findProgramSubject(m.getData().getParent().getParent().getLabel());
                progChap = findProgramChapter(m.getData().getParent().getLabel());
            } else {
                progType = findProgramType(m.getData().getParent().getParent().getLabel());
                progSubj = findProgramSubject(m.getData().getParent().getLabel());
                progChap = progSubj.getChapters().get(0);
            }

            List<ProgramSection> list = progChap.getSections();

            for (ProgramSection ps : list) {
                if (ps.getLabel().equals(m.getLabel())) {
                    ps.setSelected(true);
                    break;
                }
            }
        }

    }

    private ProgramChapter findProgramChapter(String label) {
        List<ProgramChapter> list = progSubj.getChapters();
        for (ProgramChapter pc : list) {
            if (pc.getLabel().equals(label)) {
                return pc;
            }
        }
        return null;
    }

    private ProgramSubject findProgramSubject(String label) {
        List<ProgramSubject> list = progType.getProgramSubjects();
        for (ProgramSubject ps : list) {
            if (ps.getLabel().equals(label)) {
                return ps;
            }
        }
        return null;
    }

    private ProgramType findProgramType(String label) {
        List<ProgramType> list = _programListing.getProgramTypes();
        for (ProgramType pt : list) {
            if (pt.getLabel().equals(label)) {
                return pt;
            }
        }
        return null;
    }

    private TextButton createExploreLessonButton() {
        TextButton btn = new TextButton("Explore Lesson", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {

            	ProgListModel base = _tree.getSelectionModel().getSelectedItem();
            	if (base == null || base.getLevel() != ProgramListing.LEVEL_LESS) {
                    CmMessageBox.showAlert("Please click on a lesson to select it.");
                    return;
                }
                String name = base.getLabel();
                String file = base.getPath();
                Topic topic = new Topic(name, file, null);
                
                new TopicExplorerWindow(topic, true);
            }
        });
        btn.setToolTip("Explore selected lesson.");
        return btn;
    }

    interface Props extends PropertyAccess<ProgListModel> {
        ModelKeyProvider<ProgListModel> id();

        ValueProvider<ProgListModel, String> label();
    }

    static Props props = GWT.create(Props.class);

    private void buildTree() {

    	store = new TreeStore<ProgListModel>(props.id());
    	_tree = new Tree<ProgListModel, String>(store, props.label());
    	// tree.setWidth(290);
    	// tree.setDisplayProperty("label");
    	// _tree.getView().setAutoHeight(true);

    	List<ProgramType> progList = this._programListing.getProgramTypes();
    	for (ProgramType pt : progList) {
    	    globalId += pt.getItemCount();
    	}
    	globalId += 50;

    	RpcProxy<ProgListModel, List<ProgListModel>> proxy = dataProxy();

    	final TreeLoader<ProgListModel> loader = new TreeLoader<ProgListModel>(proxy) {
    		@Override
    		public boolean hasChildren(ProgListModel parent) {
    			return parent.getLevel() != ProgramListing.LEVEL_LESS;
    		}
    	};

    	loader.addLoadHandler(new ChildTreeStoreBinding<ProgListModel>(store));
    	_tree.setLoader(loader);

    	_tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    	FramedPanel fp = new FramedPanel();
    	fp.setHeaderVisible(false);
    	
    	/** work around tree selection color conflicting with FramedPanel */
    	_tree.getElement().setAttribute("style",  "background: white;");
    	fp.setWidget(_tree);
    	setWidget(fp);
    	
        
    	forceLayout();
    }

    private RpcProxy<ProgListModel, List<ProgListModel>> dataProxy() {

        RpcProxy<ProgListModel, List<ProgListModel>> proxy = new RpcProxy<ProgListModel, List<ProgListModel>>() {

            @Override
            public void load(ProgListModel loadConfig, AsyncCallback<List<ProgListModel>> callback) {
                ProgListModel m = (ProgListModel) loadConfig;
                if (m != null && (m.getLevel() == ProgramListing.LEVEL_SECT || isBuiltInCustomProg(m))) {
                    /**
                     * obtain lesson data asynchronously
                     */
                    getLessonItemsRPC(m.getData(), callback);
                } else {
                    /**
                     * process local data
                     */
                    List<ProgListModel> models = new ArrayList<ProgListModel>();
                    if (m == null) {
                        /** root node */
                        List<ProgramType> types = _programListing.getProgramTypes();
                        for (ProgramType pType : types) {
                            models.add(new ProgListModel(pType));
                        }
                    } else if (m.getLevel() == ProgramListing.LEVEL_TYPE) {
                        ProgramType pType = (ProgramType) m.getData();
                        List<ProgramSubject> subjects = pType.getProgramSubjects();
                        for (ProgramSubject ps : subjects) {
                            models.add(new ProgListModel(ps));
                            ps.setParent(pType);
                        }
                    } else if (m.getLevel() == ProgramListing.LEVEL_SUBJ) {

                        ProgramSubject pSubj = (ProgramSubject) m.getData();
                        ProgramType pType = (ProgramType) pSubj.getParent();
                        List<ProgramChapter> chaps = pSubj.getChapters();

                        if (pType.getLabel().indexOf("Proficiency") < 0 && pType.getLabel().indexOf("Graduation") < 0
                                && pType.getLabel().toLowerCase().indexOf("built-in") < 0) {
                            // not Proficiency, Grad Prep, or Built-in Custom
                            // Program, add Chapters
                            for (ProgramChapter pc : chaps) {
                                models.add(new ProgListModel(pc));
                                pc.setParent(pSubj);
                            }
                        } else if (pType.getLabel().toLowerCase().indexOf("built-in") < 0) {
                            // Proficiency or Grad Prep Program, add sections
                            ProgramChapter pc = chaps.get(0);
                            pc.setParent(pSubj);
                            List<ProgramSection> l = pc.getSections();
                            for (ProgramSection ps : l) {
                                ps.setParent(pc);
                                models.add(new ProgListModel(ps));
                            }
                        } else {
                            // Built-in Custom Program
                            models.add(new ProgListModel(pSubj));
                        }

                    } else if (m.getLevel() == ProgramListing.LEVEL_CHAP) {
                        ProgramChapter pc = (ProgramChapter) m.getData();
                        List<ProgramSection> l = pc.getSections();
                        for (ProgramSection ps : l) {
                            ps.setParent(pc);
                            models.add(new ProgListModel(ps));
                        }
                    }
                    callback.onSuccess(models);
                }

                // service.getFolderChildren((FileModel) loadConfig, callback);
            }

            private boolean isBuiltInCustomProg(ProgListModel m) {
                CmTreeNode node = m.getData();
                if (node instanceof ProgramSubject) {
                    ProgramType pType = (ProgramType) node.getParent();
                    String label = pType.getLabel();
                    return (label.toLowerCase().indexOf("built-in") > -1);
                }
                return false;
            }

        };
        
        return proxy;
    }

    int globalId = 0;

    private void getLessonItemsRPC(final CmTreeNode node, final AsyncCallback<List<ProgListModel>> callback) {

        new RetryAction<CmList<ProgramLesson>>() {
            @Override
            public void attempt() {
                GetProgramLessonsAction action;
                if (node instanceof ProgramSection) {
                    ProgramSection section = (ProgramSection) node;
                    ProgramChapter chapter = (ProgramChapter) section.getParent();
                    String chap = chapter.getLabel();
                    int sectionCount = chapter.getSections().size();
                    action = new GetProgramLessonsAction(section.getTestDefId(), section.getNumber(), chap,
                            sectionCount);
                } else { // has to be Built-in CP
                    ProgramSubject pSubj = (ProgramSubject) node;
                    String chap = pSubj.getLabel();
                    int programId = 0;
                    try {
                        programId = Integer.parseInt(pSubj.getName());
                    }
                    catch (Exception e) {
                    }
                    action = new GetProgramLessonsAction(programId, 1, chap, 0);
                    action.setBuiltInCustomProg(true);
                }

                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<ProgramLesson> lessons) {
                List<ProgListModel> models = new ArrayList<ProgListModel>();
                for (int i = 0, t = lessons.size(); i < t; i++) {
                    ProgramLesson pl = lessons.get(i);
                    pl.setId(globalId++);
                    ProgListModel plm = new ProgListModel(pl);
                    plm.setPath(pl.getFile());
                    models.add(plm);
                }
                callback.onSuccess(models);
            }

        }.register();
    }

    private void getProgramListingRPC() {

    	globalId = 0;

        new RetryAction<ProgramListing>() {
            @Override
            public void attempt() {
                GetProgramListingAction action = new GetProgramListingAction(cmAdminMdl.getUid());
                action.setIncludeBuiltInCustomProgs(true);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            public void oncapture(ProgramListing pl) {
                _programListing = pl;
                buildTree();
            }
        }.register();
    }
    
    
    static public void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                CmAdminModel cmAdmin = new CmAdminModel();
                cmAdmin.setUid(2);
                new ProgramDetailsPanel(cmAdmin).setVisible(true);
            }
        });
    }
}