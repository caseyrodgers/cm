package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.model.program_listing.CmTreeNode;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapter;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSection;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSubject;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramType;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramLessonsAction;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramListingAction;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseModelData;
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

    TreePanel<ProgListModel> tree;
    ProgramListing _programListing;
    LayoutContainer _mainPanel = new LayoutContainer();

    public ProgramDetailsPanel(CmAdminModel cmAdminMdl) {
        this.cmAdminMdl = cmAdminMdl;
        getProgramListingRPC();

        buildGui();
        setVisible(true);
    }

    private void buildGui() {
        setHeading("Catchup Math Programs");
        setSize(400, 400);

        setLayout(new BorderLayout());

        if(CmShared.getQueryParameter("debug") != null) {
            getHeader().addTool(new Button("Expand All", new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    tree.expandAll();
                }
            }));
        }
        getHeader().addTool(new Button("Collapse All", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                tree.collapseAll();
            }
        }));

        _mainPanel = new LayoutContainer();
        _mainPanel.setLayout(new CenterLayout());
        _mainPanel.add(new Label("Loading Program Information.."));

        add(_mainPanel, new BorderLayoutData(LayoutRegion.CENTER));

        addCloseButton();

        setModal(true);
        setResizable(true);
    }

    private void buildTree() {

        store = new TreeStore<ProgListModel>();
        tree = new TreePanel<ProgListModel>(store);
        tree.setWidth(290);
        tree.setDisplayProperty("label");
        tree.setAutoHeight(true);

        // data proxy
        RpcProxy<List<ProgListModel>> proxy = new RpcProxy<List<ProgListModel>>() {
            @Override
            protected void load(Object loadConfig, final AsyncCallback<List<ProgListModel>> callback) {
                ProgListModel m = (ProgListModel) loadConfig;
                if (m != null && m.getLevel() == ProgramListing.LEVEL_SECT) {
                    /**
                     * lesson async call needed
                     * 
                     */
                    final ProgramSection section = (ProgramSection) m.getData();
                    getLessonItemsRPC(section, callback);
                } else {
                    /** process local data */
                    List<ProgListModel> models = new ArrayList<ProgListModel>();
                    if (m == null) {
                        /** root node */
                        List<ProgramType> types = _programListing.getProgramTypes();
                        for (int i = 0, t = types.size(); i < t; i++) {
                            ProgramType pt = types.get(i);
                            models.add(new ProgListModel(pt));
                        }
                    } else if (m.getLevel() == ProgramListing.LEVEL_TYPE) {
                    	ProgramType pType = (ProgramType) m.getData();
                        List<ProgramSubject> subjects = pType.getProgramSubjects();
                        for (int i = 0, t = subjects.size(); i < t; i++) {
                            ProgramSubject ps = subjects.get(i);
                            models.add(new ProgListModel(ps));
                            ps.setParent(pType);
                        }
                    } else if (m.getLevel() == ProgramListing.LEVEL_SUBJ) {
                    	
                        List<ProgramChapter> chaps = ((ProgramSubject) m.getData()).getChapters();
                    	ProgramSubject pSubj = (ProgramSubject) m.getData();
                    	ProgramType pType = (ProgramType) pSubj.getParent();
                        
                    	if (pType.getLabel().indexOf("Proficiency") < 0 && pType.getLabel().indexOf("Graduation") < 0) {
                    		// not Proficiency Program or Grad Prep Program, add Chapters
                            for (int i = 0, t = chaps.size(); i < t; i++) {
                                ProgramChapter pc = chaps.get(i);
                                models.add(new ProgListModel(pc));
                                pc.setParent(pSubj);
                            }
                    	}
                    	else {
                    		// Proficiency or Grad Prep Program, add sections
                        	ProgramChapter pc = chaps.get(0);
                            List<ProgramSection> l = pc.getSections();
                            for (int i = 0, t = l.size(); i < t; i++) {
                                ProgramSection ps = l.get(i);
                                ps.setParent(pc);
                                models.add(new ProgListModel(ps));
                            }
                    	}
                        
                    } else if (m.getLevel() == ProgramListing.LEVEL_CHAP) {
                    	ProgramChapter pc = (ProgramChapter) m.getData();
                        List<ProgramSection> l = pc.getSections();
                        for (int i = 0, t = l.size(); i < t; i++) {
                            ProgramSection ps = l.get(i);
                            ps.setParent(pc);
                            models.add(new ProgListModel(ps));
                        }
                    }
                    callback.onSuccess(models);
                }

                // service.getFolderChildren((FileModel) loadConfig, callback);
            }
        };

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

    private void getLessonItemsRPC(final ProgramSection section, final AsyncCallback<List<ProgListModel>> callback) {

        new RetryAction<CmList<ProgramLesson>>() {
            @Override
            public void attempt() {
            	ProgramChapter chapter = (ProgramChapter) section.getParent();
            	String chap = chapter.getLabel();
            	int sectionCount = chapter.getSections().size();
                GetProgramLessonsAction action = new GetProgramLessonsAction(
                		section.getTestDefId(), section.getNumber(), chap, sectionCount);
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
                GetProgramListingAction action = new GetProgramListingAction(cmAdminMdl.getId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(ProgramListing pl) {
                _programListing = pl;
                buildTree();
            }
        }.register();
    }
/*
    private void getProgramListingRPC2() {

        new RetryAction<ProgramListing>() {
            @Override
            public void attempt() {
                GetProgramListingAction action = new GetProgramListingAction(cmAdminMdl.getId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(ProgramListing pl) {

                List<ProgramType> progTypeList = pl.getProgramTypes();
                CmTreeModel ctMdl = new CmTreeModel();

                for (ProgramType pt : progTypeList) {
                    CmTreeModel ctm0 = new CmTreeModel(pt.getType());
                    ctMdl.add(ctm0);

                    for (ProgramSubject ps : pt.getProgramSubjects()) {

                        CmTreeModel ctm1 = new CmTreeModel(ps.getName());
                        ctm1.setId(ps.getTestDefId());
                        ctm0.add(ctm1);

                        for (ProgramChapter pc : ps.getChapters()) {

                            if (pc instanceof ProgramChapterAll) {
                                for (ProgramSection s : pc.getSections()) {

                                    CmTreeModel ctm2 = new CmTreeModel(s.getName());
                                    ctm2.setId(ctm1.getId());
                                    ctm2.setNumber(s.getNumber());
                                    ctm1.add(ctm2);

                                    // add place holder Lesson Item to Section
                                    // (will be loaded asynchronously)
                                    CmTreeModel ctm3 = new CmTreeModel("Lessons");
                                    ctm2.add(ctm3);
                                }
                            } else {
                                CmTreeModel ctm2 = new CmTreeModel(String.valueOf(pc.getNumber()) + " " + pc.getName());
                                ctm1.add(ctm2);

                                for (ProgramSection s : pc.getSections()) {
                                    CmTreeModel ctm3 = new CmTreeModel(s.getName());
                                    ctm3.setId(ctm1.getId());
                                    ctm3.setNumber(s.getNumber());
                                    ctm2.add(ctm3);

                                    // add place holder Lesson Item to Section
                                    // (will be loaded asynchronously)
                                    CmTreeModel ctm4 = new CmTreeModel("Lessons");
                                    ctm3.add(ctm4);
                                }
                            }
                        }
                    }
                }

                // store.add(ctMdl.getChildren(), true);
                tree.enable();
            }
        }.register();
    }
*/
}

class ProgListModel extends BaseModelData {

    CmTreeNode data;

    protected ProgListModel() {
    }

    public ProgListModel(CmTreeNode data) {
        set("label", data.getLabel());
        this.data = data;
    }

    public CmTreeNode getData() {
        return data;
    }

    public void setLabel(String label) {
        set("label", label);
    }

    public void setPath(String path) {
        set("path", path);
    }

    public String getPath() {
        return get("path");
    }

    public String getLabel() {
        return get("label");
    }

    public int getLevel() {
        return data.getLevel();
    }

    public void setParent(ProgListModel model) {
    	
    }
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ProgListModel) {
            ProgListModel mobj = (ProgListModel) obj;
            boolean b = mobj.getData() == data;
            return b;
        }
        return super.equals(obj);
    }
}
