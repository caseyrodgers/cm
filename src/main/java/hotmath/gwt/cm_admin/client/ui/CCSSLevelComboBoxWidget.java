package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.shared.client.model.CCSSGradeLevel;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CCSSLevelsAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.cell.core.client.LabelProviderSafeHtmlRenderer;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.form.ComboBox;

public class CCSSLevelComboBoxWidget {

	interface LevelProperties extends PropertyAccess<CCSSGradeLevel> {
	    ModelKeyProvider<CCSSGradeLevel> name();
	 
	    LabelProvider<CCSSGradeLevel> label();
    }
    static LevelProperties _levelProps = GWT.create(LevelProperties.class);

    static public interface CallbackOnSelection {
        void setSelection(String levelName);
    }
    CallbackOnSelection _callbackOnSelection;

    CallbackOnComplete _callbackOnComplete;

    String _emptyText;
    String _levelName;

	private ListStore<CCSSGradeLevel> levelStore;
	private ComboBox<CCSSGradeLevel>  _levelCombo;

    public CCSSLevelComboBoxWidget(String emptyText, CallbackOnComplete callbackOnComplete, CallbackOnSelection callbackOnSelection) {
    	this._emptyText = emptyText;
    	this._callbackOnComplete = callbackOnComplete;
    	this._callbackOnSelection = callbackOnSelection;
    	setup();
    }

    public ComboBox<CCSSGradeLevel> getlevelCombo() {
    	return _levelCombo;
    }

    private void setup() {
        levelStore = new ListStore<CCSSGradeLevel>(_levelProps.name());
        getStandardLevelsRPC(levelStore);
        _levelCombo = levelCombo(levelStore);
    }

	private ComboBox<CCSSGradeLevel> levelCombo(ListStore<CCSSGradeLevel> store) {
        LabelProviderSafeHtmlRenderer<CCSSGradeLevel> renderer = new LabelProviderSafeHtmlRenderer<CCSSGradeLevel>(_levelProps.label()) {
            public SafeHtml render(CCSSGradeLevel level) {
                SafeHtmlBuilder sb = new SafeHtmlBuilder();
                return sb.appendEscaped(level.getName()).toSafeHtml();
            }
        };
        ComboBox<CCSSGradeLevel> combo = new ComboBox<CCSSGradeLevel>(new ComboBoxCell<CCSSGradeLevel>(store, _levelProps.label(), renderer));
        combo.setForceSelection(false);
        combo.setEditable(false);
        combo.setAllowBlank(true);
        combo.setTriggerAction(TriggerAction.ALL);
        combo.setStore(store);
        combo.setTitle("Select a Strand");
        combo.setId("level-combo");
        combo.setTypeAhead(true);
        combo.setSelectOnFocus(true);
        if (_emptyText != null) combo.setEmptyText(_emptyText);
        combo.setWidth(280);
        combo.addSelectionHandler(new SelectionHandler<CCSSGradeLevel>() {

            @Override
            public void onSelection(SelectionEvent<CCSSGradeLevel> event) {
                CCSSGradeLevel level = event.getSelectedItem();
                _levelName = level.getName();
                if (_emptyText != null && _emptyText.equals(_levelName)) _levelName = null;
                _callbackOnSelection.setSelection(_levelName);
            }
        });

        return combo;
	}

    private void getStandardLevelsRPC(final ListStore<CCSSGradeLevel> store) {

        CmBusyManager.setBusy(true);

        new RetryAction<CmList<CCSSGradeLevel>>() {
            public void oncapture(CmList<CCSSGradeLevel> list) {
                try {
                    store.clear();
                    store.addAll(list);
                    _callbackOnComplete.isComplete();
                } catch (Exception e) {
                    Log.error("Error: " + list.size(), e);
                } finally {
                    CmBusyManager.setBusy(false);
                }
            }

            @Override
            public void attempt() {
                CmServiceAsync s = CmRpcCore.getCmService();

                CCSSLevelsAction action = new CCSSLevelsAction();
                setAction(action);
                s.execute(action, this);
            }
        }.register();
    }

}
