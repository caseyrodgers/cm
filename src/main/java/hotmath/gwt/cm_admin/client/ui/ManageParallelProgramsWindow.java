package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.ParallelProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.ui.ParallelProgramSetup;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.RetryActionManager;
import hotmath.gwt.shared.client.rpc.action.GetParallelProgramsAction;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

public class ManageParallelProgramsWindow extends CmWindow {
    
    boolean cancelled;
    Grid<ParallelProgramModel> grid;
    ListStore<ParallelProgramModel> store = new ListStore<ParallelProgramModel>();
    CmAdminModel adminModel;
    int width = 400;
    
    public ManageParallelProgramsWindow(CmAdminModel adminModel) {
        this.adminModel = adminModel;
        setSize(width,300);
        setHeading("Manage Parallel Programs");    
        
        readRpcData(adminModel.getId(), true);
        
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
            CatchupMathTools.showAlert("Please make a selection first");
        }
        return mdl;
    }

    private void drawGui() {

        setLayout(new BorderLayout());

        grid = defineGrid(store, defineColumns());
        BorderLayoutData bld = new BorderLayoutData(LayoutRegion.WEST,190);
        add(grid, bld);

        LayoutContainer lc = new LayoutContainer();
        lc.addStyleName("manage-groups-window-buttons");

        lc.add(new StdButton("Add", "Create a new Parallel Program.", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                CmAsyncRequest callback = new CmAsyncRequestImplDefault() {
                    public void requestComplete(String requestData) {
                        readRpcData(adminModel.getId(), false);
                    }
                };
                new ParallelProgramSetup(callback, adminModel).setVisible(true);
            }
        }));

        lc.add(new StdButton("Modify", "Modify selected Parallel Program.",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                
                final ParallelProgramModel mdl = getGridItem();
                
                if (mdl != null) {
                    if (mdl.getStudentCount() > 0) {
                        CatchupMathTools.showAlert("Selection is in use and cannot be modified.");
                    }
                /*
                        public void requestComplete(String requestData) {
                            updateGroupRPC(mdl.getAdminId(),mdl.getId(), requestData);
                        }
                    };
                    GroupInfoModel gm = new GroupInfoModel();
                    gm.setId(mdl.getId());
                    gm.setGroupName(mdl.getName());
                    new GroupWindow(callback, adminModel, null, false, gm).setVisible(true);
                */
                }
                
            }
        }));

        lc.add(new StdButton("Remove", "Remove selected Parallel Program.",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                final ParallelProgramModel ppm = getGridItem();
                
                if (ppm != null) {
                    if (ppm.getStudentCount() > 0) {
                        CatchupMathTools.showAlert("Selection is in use and cannot be removed.");
                    }
                    /*        
                    MessageBox.confirm("Remove Parallel Program", "Are you sure you want to remove '" + ppm.getName() + "'?", new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            if (be.getButtonClicked().getText().equalsIgnoreCase("yes"))
                                deleteParallelProgram(adminModel.getId(), ppm.getId());
                        }
                    });
                    */
                }
            }
        }));

        lc.add(new StdButton("Usage", "Display usage of selected Parallel Program.",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                final ParallelProgramModel ppm = getGridItem();
                
                if (ppm != null) {

                    if (ppm.getStudentCount() < 1) {
                        CatchupMathTools.showAlert("Selection has no usage, nothing to display.");
                    }

                	new ParallelProgramUsageWindow(ppm);
                }
                
            }
        }));

        add(lc, new BorderLayoutData(LayoutRegion.EAST,200));
    }

    private Grid<ParallelProgramModel> defineGrid(final ListStore<ParallelProgramModel> store, ColumnModel cm) {
        final Grid<ParallelProgramModel> grid = new Grid<ParallelProgramModel>(store, cm);
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.getSelectionModel().addListener(Events.RowDoubleClick, new Listener<SelectionEvent<StudentModelExt>>() {
            public void handleEvent(SelectionEvent<StudentModelExt> se) {

                if (grid.getSelectionModel().getSelectedItems().size() > 0) {
                    CatchupMathTools.showAlert("On click");
                }
            }
        });

        grid.setWidth("200px");
        grid.setHeight("250px");
        return grid;
    }

    /** Return string that deals with singular/plural of student count
     * 
     */
    private String getCountString(ParallelProgramModel ppm) {
        return ppm.getStudentCount() + " " + (ppm.getStudentCount() == 1?"student":"students");
    }

    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig name = new ColumnConfig();
        name.setId(ParallelProgramModel.NAME);
        name.setHeader("Name");
        name.setWidth(135);
        name.setSortable(true);
        name.setRenderer(new GridCellRenderer<ParallelProgramModel>() {
			@Override
			public Object render(ParallelProgramModel ppm, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ParallelProgramModel> store, Grid<ParallelProgramModel> grid) {

				return ppm.getName();
			}
        });
        configs.add(name);

        ColumnConfig count = new ColumnConfig();
        count.setId(ParallelProgramModel.STUDENT_COUNT);
        count.setHeader("Count");
        count.setToolTip("Students in Parallel Program");
        count.setWidth(50);
        count.setSortable(true);
        configs.add(count);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }

    private void readRpcData(final Integer adminId, final boolean closeOnCancel) {
    	
    	final CmWindow cmw = this;
    	
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
                store.removeAll();
                store.add(result);
                CmBusyManager.setBusy(false);
            }

            @Override
            public void onCancel() {
            	if (closeOnCancel)
            		cmw.close();
            }
        }.register();
    }

    private void deleteGroup(final Integer adminId, final Integer groupId) {
    	groupActionRPC(adminId, groupId, null, GroupManagerAction.ActionType.DELETE);
    }

    protected void updateGroupRPC(final int adminUid, Integer groupId, String groupName) {
    	groupActionRPC(adminUid, groupId, groupName, GroupManagerAction.ActionType.UPDATE);
    }

    private void groupActionRPC(final Integer adminId, final Integer groupId, final String groupName, final GroupManagerAction.ActionType actionType) {

    	new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GroupManagerAction action = new GroupManagerAction(actionType, adminId);
                setAction(action);
                action.setGroupId(groupId);
                action.setGroupName(groupName);
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
                
                if(caught.getMessage().indexOf("you entered") > 0) {
                    CatchupMathTools.showAlert("Problem renaming group", caught.getMessage());
                    RetryActionManager.getInstance().requestComplete(this);
                    return;
                }
                super.onFailure(caught);
            }

        }.register();

    }

}

/** Provide standard button sizes and configuration
 * 
 * @author casey
 *
 */
class StdButton extends Button {
    public StdButton(String name, String tooltip,SelectionListener<ButtonEvent> listener) {
        super(name);
        addStyleName("manage-groups-window-buttons-button");
        setToolTip(tooltip);
        if (listener != null)
            addSelectionListener(listener);
        setWidth("150px");
    }
}