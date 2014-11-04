package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.solution_editor.client.rpc.GetMathMlResourceAction;
import hotmath.gwt.solution_editor.client.rpc.MathMlResource;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class SolutionResourcePanel extends BorderLayoutContainer {
    
    SolutionResource resource;
    String pid;
    public SolutionResourcePanel(final SolutionResource sr, String pid) {
        resource = sr;
        this.pid = pid;
        
        addStyleName("solution-resource-panel");
        
        String html = "<h2>" + sr.getFile() + "</h2>";
        if(sr.getUrlPath() != null) {
            html += "<img src='" + sr.getUrlPath() + "?rand=" + System.currentTimeMillis() + "'/>";
        }
        else if(sr.getDisplay() != null) {
            html += sr.getDisplay();
        }
        
        setCenterWidget(new HTML(html));

        ToolBar tb = new ToolBar();
        tb.add(new TextButton("URL", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                String url = "<img src='" + sr.getUrlPath() + "' class='solution_resource'/>";
                new ShowValueWindow("URL", url);
            }
        }));
        tb.add(new TextButton("MathML", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showMathMl();
            }
        }));
        
        setSouthWidget(tb, new BorderLayoutData(25));
    }
    
    
    private void showMathMl() {
            
            GetMathMlResourceAction action = null;
            if(pid != null) {
                action = new GetMathMlResourceAction(pid,resource.getFile());
            }
            else {
                action = new GetMathMlResourceAction(resource.getFile());
            }
            
            EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, "Reading MathML for: " + resource.getFile()));
            SolutionEditor.getCmService().execute(action, new AsyncCallback<MathMlResource>() {
                public void onSuccess(MathMlResource resource) {
                    EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, ""));
                    String mathMl = resource.getMathMl();
                    if(!mathMl.startsWith("<math")) {
                        mathMl = "<math>\n" + mathMl + "\n</math>";
                    }
                    new ShowValueWindow("MathML", mathMl);
                }
                @Override
                public void onFailure(Throwable arg0) {
                    EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, ""));
                    arg0.printStackTrace();
                    com.google.gwt.user.client.Window.alert(arg0.getLocalizedMessage());
                }
            });                
    }
    
    
    public SolutionResource getResource() {
        return resource;
    }
}

