package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;

public class SaveSolutionAsDialog extends CmWindow {
    Callback callback;
    
    static private SaveSolutionAsDialog __instance;
    static public SaveSolutionAsDialog getSharedInstance() {
        __instance = null;
        if(__instance == null)
            __instance = new SaveSolutionAsDialog();
        return __instance;
    }
    private SaveSolutionAsDialog() {
        setHeading("Save Solution As");
        setSize(350,300);
        setResizable(false);
        
        setModal(true);
        
        FormPanel form = new FormPanel();
        form.setBodyBorder(false);
        form.setBorders(false);
        form.setHeaderVisible(false);
        form.setLabelWidth(100);
        form.setFieldWidth(200);
        
        book.setFieldLabel("Book");
        book.setAllowBlank(false);
        chap.setFieldLabel("Chapter");
        chap.setAllowBlank(false);
        sect.setFieldLabel("Section");
        sect.setAllowBlank(false);
        probSet.setFieldLabel("Problem Set");
        probSet.setAllowBlank(false);
        probNum.setFieldLabel("Problem Number");
        probNum.setAllowBlank(false);
        pageNum.setFieldLabel("Page Number");        
        pageNum.setAllowBlank(false);
        
        form.add(book);
        form.add(chap);
        form.add(sect);
        form.add(probSet);
        form.add(probNum);
        form.add(pageNum);
        
        Button save = new Button("Save", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                String newPid = buildNewPid();
                callback.saveSolutionAs(newPid);
                hide();
            }
        });
        FormButtonBinding binding = new FormButtonBinding(form);
        binding.addButton(save);
        
        addButton(save);
        add(new Html("<h1 style='margin: 10px 10px;'>Save Solution to New Location</h1>"));
        add(form);
        addCloseButton();
    }

    private void loadPid(String pid) {
        String p[] = pid.split("_");
        book.setValue(p[0]);
        chap.setValue(p[1]);
        sect.setValue(p[2]);
        probSet.setValue(p[3]);
        probNum.setValue(p[4]);
        pageNum.setValue(p[5]);
        
        layout();
    }
    
    private String buildNewPid() {
        String b = book.getValue();
        String c = chap.getValue();
        String s = sect.getValue();
        String ps = probSet.getValue();
        String pn = probNum.getValue();
        String pg = pageNum.getValue();
        return (b + "_" + c + "_" + s + "_" + ps + "_" + pn + "_" + pg).toLowerCase();
    }

    public void setCallback(Callback callback, String pidToSaveAs) {
        this.callback = callback;
        loadPid(pidToSaveAs);
        
        setVisible(true);
    }
    
    TextField<String> book = new TextField<String>();
    TextField<String> chap = new TextField<String>();
    TextField<String> sect = new TextField<String>();
    TextField<String> probSet = new TextField<String>();
    TextField<String> probNum = new TextField<String>();
    TextField<String> pageNum = new TextField<String>();
    
    public interface Callback {
        /** called when new pid name */
        void saveSolutionAs(String pid);
    }
}
