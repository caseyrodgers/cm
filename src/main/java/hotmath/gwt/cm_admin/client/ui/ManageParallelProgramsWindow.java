package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.ParallelProgramModel;
import hotmath.gwt.cm_tools.client.model.ParallelProgramModelProperties;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.ParallelProgramSetup;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.RetryActionManager;
import hotmath.gwt.shared.client.rpc.action.DeleteParallelProgramAction;
import hotmath.gwt.shared.client.rpc.action.GetParallelProgramsAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentForParallelProgramAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;



public class ManageParallelProgramsWindow extends GWindow {
    
    boolean cancelled;
    Grid<ParallelProgramModel> grid;
    ListStore<ParallelProgramModel> store;
    CmAdminModel adminModel;
    StudentModelI stuMdl;
    int width = 535;
    
    ParallelProgramModelProperties __props = GWT.create(ParallelProgramModelProperties.class);
    public ManageParallelProgramsWindow(CmAdminModel adminModel) {
        super(false);
        
        store = new ListStore<ParallelProgramModel>(__props.id());
        this.adminModel = adminModel;
        setPixelSize(width,300);
        setHeadingText("Manage Parallel Programs");    
        
        readRpcData(adminModel.getUid(), true);
        
        if (! cancelled) {
            drawGui();

            getButtonBar().setWidth(width-20);

            addCloseButton();

            setModal(true);
            setResizable(false);
            setVisible(true);
        }
    }

    private ParallelProgramModel getGridItem() {
        ParallelProgramModel mdl = grid.getSelectionModel().getSelectedItem();
        if (mdl == null) {
            CmMessageBox.showAlert("Please make a selection first");
        }
        return mdl;
    }
    
    public Widget getGrid() {
        return grid;
    }

    private void drawGui() {

        BorderLayoutContainer mainBor = new BorderLayoutContainer();
        setWidget(mainBor);
        
        grid = defineGrid(store, defineColumns());
        BorderLayoutData bld = new BorderLayoutData(325);
        mainBor.setWestWidget(grid, bld);

        VerticalLayoutContainer mainVer = new VerticalLayoutContainer();
        mainVer.addStyleName("manage-groups-window-buttons");

        mainVer.add(new StdButton("Help", "Parallel Program Help.", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new ParallelProgramHelpWindow().setVisible(true);
            }
        }));

        mainVer.add(new StdButton("New Parallel Program", "Create a new Parallel Program.", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                CmAsyncRequest callback = new CmAsyncRequestImplDefault() {
                    public void requestComplete(String requestData) {
                        readRpcData(adminModel.getUid(), false);
                    }
                };
                new ParallelProgramSetup(callback, adminModel).setVisible(true);
            }
        }));

        StdButton modifyBtn = new StdButton("Modify", "Modify selected Parallel Program.",new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                final ParallelProgramModel mdl = getGridItem();
                
                if (mdl != null) {
                    modifyParallelProgram(mdl);
                }
                
            }

        });
        
        
        if (CmShared.getQueryParameter("debug_pp") != null) {
            mainVer.add(modifyBtn);
        }

        mainVer.add(new StdButton("Remove", "Remove selected Parallel Program.",new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                final ParallelProgramModel ppm = getGridItem();
                
                if (ppm != null) {
                    if (ppm.getStudentCount() > 0) {
                        CmMessageBox.showAlert("Selection is in use and cannot be removed.");
                        return;
                    }
                    CmMessageBox.confirm("Remove Parallel Program", "Are you sure you want to remove '" + ppm.getName() + "'?", new ConfirmCallback() {
                        @Override
                        public void confirmed(boolean yesNo) {
                            if (yesNo) {
                                deleteParallelProgram(adminModel.getUid(), ppm.getId());
                            }
                        }
                    });
                }
            }
        }));

        mainVer.add(new StdButton("Usage", "Display usage of selected Parallel Program.",new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                final ParallelProgramModel ppm = getGridItem();
                
                if (ppm != null) {

                    if (ppm.getStudentCount() < 1) {
                        CmMessageBox.showAlert("Selection has no usage, nothing to display.");
                        return;
                    }

                	new ParallelProgramUsageWindow(ppm);
                }

            }
        }));

        mainBor.setEastWidget(mainVer, new BorderLayoutData(200));
    }

	private void modifyParallelProgram(final ParallelProgramModel mdl) {
    	new RetryAction<StudentModelExt>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetStudentForParallelProgramAction action = new GetStudentForParallelProgramAction(mdl.getId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(StudentModelExt result) {

                CmBusyManager.setBusy(false);

                // now show Parallel Program Setup
                showParallelProgramSetup(result, mdl);
            	
            }

			@Override
            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);
                CmMessageBox.showAlert("Problem getting Parallel Program", caught.getMessage());
                RetryActionManager.getInstance().requestComplete(this);
            }

        }.register();

	}

	private void showParallelProgramSetup(StudentModelI sm, ParallelProgramModel mdl) {

        CmAsyncRequest callback = new CmAsyncRequestImplDefault() {
            public void requestComplete(String requestData) {
                readRpcData(adminModel.getUid(), false);
            }
        };
        
		new ParallelProgramSetup(callback, adminModel, mdl, sm).setVisible(true);
    }
    
    private Grid<ParallelProgramModel> defineGrid(final ListStore<ParallelProgramModel> store, ColumnModel<ParallelProgramModel> cm) {
        final Grid<ParallelProgramModel> grid = new Grid<ParallelProgramModel>(store, cm);
        grid.setBorders(true);
        grid.getView().setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.addHandler(new DoubleClickHandler() {
            public void onDoubleClick(DoubleClickEvent event) {
                if (grid.getSelectionModel().getSelectedItems().size() > 0) {
                    //CmMessageBox.showAlert("On click");
                }
            }
        }, DoubleClickEvent.getType());
        return grid;
    }


    private ColumnModel<ParallelProgramModel> defineColumns() {
        List<ColumnConfig<ParallelProgramModel, ?>> configs = new ArrayList<ColumnConfig<ParallelProgramModel, ?>>();

        configs.add(new ColumnConfig<ParallelProgramModel, String>(__props.name(),135, "Name"));
        configs.add(new ColumnConfig<ParallelProgramModel, String>(__props.programName(),135, "Program"));
        configs.add(new ColumnConfig<ParallelProgramModel, Integer>(__props.studentCount(),50, "Count"));
        configs.get(configs.size()-1).setToolTip(SafeHtmlUtils.fromString("Students in Parallel Program"));
        
        ColumnModel<ParallelProgramModel> cm = new ColumnModel<ParallelProgramModel>(configs);
        return cm;
    }

    private void deleteParallelProgram(final Integer adminId, final Integer ppId) {

    	new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                DeleteParallelProgramAction action = new DeleteParallelProgramAction(adminId, ppId);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData result) {
                readRpcData(adminId, false);
                CmAdminDataReader.getInstance().fireRefreshData();
            	CmBusyManager.setBusy(false);
            }

            @Override
            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);
                CmMessageBox.showAlert("Problem removing Parallel Program", caught.getMessage());
                RetryActionManager.getInstance().requestComplete(this);
            }

        }.register();

    }
    
    private void readRpcData(final Integer adminId, final boolean closeOnCancel) {
    	
    	final GWindow cmw = this;
    	
        new RetryAction<CmList<ParallelProgramModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetParallelProgramsAction action = new GetParallelProgramsAction(adminId);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<ParallelProgramModel> result) {
                store.clear();
                store.addAll(result);
                CmBusyManager.setBusy(false);
            }

            @Override
            public void onCancel() {
            	if (closeOnCancel)
            		cmw.close();
            }
        }.register();
    }

}

/** Provide standard button sizes and configuration
 * 
 * @author casey
 *
 */
class StdButton extends TextButton {
    public StdButton(String name, String tooltip,SelectHandler handler) {
        super(name);
        addStyleName("manage-groups-window-buttons-button");
        setToolTip(tooltip);
        if (handler != null)
            addSelectHandler(handler);
        setWidth("150px");
    }
}
