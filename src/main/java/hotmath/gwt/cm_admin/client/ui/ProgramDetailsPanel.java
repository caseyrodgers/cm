package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.model.program_listing.CmTreeNode;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapter;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSection;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSubject;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramType;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramLessonsAction;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramListingAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.ProgListModel;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools_2.client.util.CmMessageBoxGxt2;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfProgramDetailsReportAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ProgramDetailsPanel extends CmWindow {

    TreeStore<ProgListModel> store;
    TreeLoader<ProgListModel> loader;

    CmAdminModel cmAdminMdl;
    
    static ProgramDetailsPanel instance;

    static int WIDTH = 435;
    
    TreePanel<ProgListModel> tree;
    ProgramListing _programListing;
    LayoutContainer _mainPanel = new LayoutContainer();

    private ProgramDetailsPanel(CmAdminModel cmAdminMdl) {
        this.cmAdminMdl = cmAdminMdl;
        getProgramListingRPC();

        buildGui();
    }

    public static void showPanel(CmAdminModel cmAdminMdl) {
    	if (instance == null) {
    		instance = new ProgramDetailsPanel(cmAdminMdl);
    	}
        instance.setVisible(true);    	
    }

    private void buildGui() {
        setHeading("Lesson Topics Covered in Programs");
        setSize(WIDTH, 400);

        setLayout(new BorderLayout());
        
        Button cBtn = new Button("Collapse All", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                tree.collapseAll();
            }
        });
        //cBtn.setStyleName("program-details-collapse-btn");
        getHeader().addTool(cBtn);
        getHeader().setStyleName("program-details-header");
        
        Button pBtn = buildPrintButton();
        getHeader().addTool(pBtn);

        _mainPanel = new LayoutContainer();
        _mainPanel.setLayout(new CenterLayout());
        _mainPanel.add(new Label("Loading Program Information.."));

        BorderLayoutData bld = new BorderLayoutData(LayoutRegion.CENTER);
        _mainPanel.setStyleName("program-details-tree-panel");
        _mainPanel.setId("program-details-panel");
        add(_mainPanel, bld);

        addCloseButton();

        setModal(true);
        setResizable(true);
    }

	private Button buildPrintButton() {
		Button btn = new Button();

        btn.setIconStyle("printer-icon");
        btn.setToolTip("Display a printable report");
        btn.addStyleName("student-details-panel-pr-btn");

		btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
		    public void componentSelected(ButtonEvent ce) {

		    	resetSelected();
		    	
			    TreeStore<ProgListModel> ts = tree.getStore();
			    List<ProgListModel> list = ts.getAllItems();
			    
			    int count=0;
			    for (ProgListModel m : list) {
				    if (tree.isExpanded(m)) {
				    	setSelected(m);
				    	count++;
				    }
			    }
			    if (count == 0) {
			    	CmMessageBoxGxt2.showAlert("Please expand a node before selecting Print.");
			    }
			    else {
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

	ProgramType    progType = null;
	ProgramSubject progSubj = null;
	ProgramChapter progChap = null;
	ProgramSection progSect = null;
	
	private void setSelected(ProgListModel m) {
		if (m.getLevel() == ProgramListing.LEVEL_TYPE) {
			progType = findProgramType(m.getLabel());
			progType.setSelected(true);
		}
		else if (m.getLevel() == ProgramListing.LEVEL_SUBJ) {
			progType = findProgramType(m.getData().getParent().getLabel());
			progSubj = findProgramSubject(m.getLabel());
			progSubj.setSelected(true);
		}
		else if (m.getLevel() == ProgramListing.LEVEL_CHAP) {
			progType = findProgramType(m.getData().getParent().getParent().getLabel());
            progSubj = findProgramSubject(m.getData().getParent().getLabel());
            progChap = findProgramChapter(m.getLabel());
			progChap.setSelected(true);
		}
		else if (m.getLevel() == ProgramListing.LEVEL_SECT) {
			CmTreeNode node = m.getData().getParent().getParent().getParent();
			if (node != null) {
    			progType = findProgramType(m.getData().getParent().getParent().getParent().getLabel());
                progSubj = findProgramSubject(m.getData().getParent().getParent().getLabel());
                progChap = findProgramChapter(m.getData().getParent().getLabel());
			}
			else {
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

	private void buildTree() {

        store = new TreeStore<ProgListModel>();
        tree = new TreePanel<ProgListModel>(store);
        tree.setWidth(290);
        tree.setDisplayProperty("label");
        tree.setAutoHeight(true);

        RpcProxy<List<ProgListModel>> proxy = dataProxy();

        loader = new BaseTreeLoader<ProgListModel>(proxy) {
            @Override
            public boolean hasChildren(ProgListModel parent) {
                return parent.getLevel() != ProgramListing.LEVEL_LESS;
            }
        };

        // trees store
        TreeStore<ProgListModel> store = new TreeStore<ProgListModel>(loader);
        tree = new TreePanel<ProgListModel>(store);
        tree.setDisplayProperty("label");

        _mainPanel.removeAll();
        _mainPanel.setLayout(new FitLayout());
        _mainPanel.add(tree);
        layout();
    }

	private RpcProxy<List<ProgListModel>> dataProxy() {
		return new RpcProxy<List<ProgListModel>>() {
            @Override
            protected void load(Object loadConfig, final AsyncCallback<List<ProgListModel>> callback) {
                ProgListModel m = (ProgListModel) loadConfig;
                if (m != null && m.getLevel() == ProgramListing.LEVEL_SECT) {
                    /**
                     * obtain lesson data asynchronously
                     */
                    final ProgramSection section = (ProgramSection) m.getData();
                    getLessonItemsRPC(section, callback);
                } else {
                    /**
                     *  process local data
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
                        
                    	if (pType.getLabel().indexOf("Proficiency") < 0 && pType.getLabel().indexOf("Graduation") < 0) {
                    		// not Proficiency Program or Grad Prep Program, add Chapters
                            for (ProgramChapter pc : chaps) {
                                models.add(new ProgListModel(pc));
                                pc.setParent(pSubj);
                            }
                    	}
                    	else {
                    		// Proficiency or Grad Prep Program, add sections
                        	ProgramChapter pc = chaps.get(0);
                        	pc.setParent(pSubj);
                            List<ProgramSection> l = pc.getSections();
                            for (ProgramSection ps : l) {
                                ps.setParent(pc);
                                models.add(new ProgListModel(ps));
                            }
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
        };
	}

    private void getLessonItemsRPC(final ProgramSection section, final AsyncCallback<List<ProgListModel>> callback) {

        new RetryAction<CmList<ProgramLesson>>() {
            @Override
            public void attempt() {
            	ProgramChapter chapter = (ProgramChapter) section.getParent();
            	String chap = chapter.getLabel();
            	int sectionCount = chapter.getSections().size();
                GetProgramLessonsAction action = new GetProgramLessonsAction(section.getTestDefId(), section.getNumber(), chap, sectionCount);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<ProgramLesson> lessons) {
                List<ProgListModel> models = new ArrayList<ProgListModel>();
                for (int i = 0, t = lessons.size(); i < t; i++) {
                    ProgramLesson pt = lessons.get(i);
                    models.add(new ProgListModel(pt));
                }
                callback.onSuccess(models);
            }

        }.register();
    }

    private void getProgramListingRPC() {

        new RetryAction<ProgramListing>() {
            @Override
            public void attempt() {
                GetProgramListingAction action = new GetProgramListingAction(cmAdminMdl.getUid());
                action.setIncludeBuiltInCustomProgs(true);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(ProgramListing pl) {
                _programListing = pl;
                buildTree();
            }
        }.register();
    }
}