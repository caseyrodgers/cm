package hotmath.gwt.cm.client;

import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceContainer;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;


/** Provides Self Registration login screen and validation
 * 
 * @author casey
 *
 */
public class FlashCardsOnlyGui extends CmMainResourceContainer {
    
    public FlashCardsOnlyGui() {
        
        add(new Button("Flash Cards"));
        
        ContextController.getInstance().setCurrentContext(new CmContext() {
            
            //@Override
            public void runAutoTest() {
            }
            
            //@Override
            public void resetContext() {
            }
            
            //@Override
            public List<Component> getTools() {
                return null;
            }
            
            //@Override
            public String getStatusMessage() {
                return "Create your own personal Catchup Math password.";
            }
            
            //@Override
            public String getContextTitle() {
                // TODO Auto-generated method stub
                return "Create Self Registration Account";
            }
            
            //@Override
            public String getContextSubTitle() {
                return "";
            }
            
            //@Override
            public String getContextHelp() {
                return getStatusMessage();
            }
            
            //@Override
            public int getContextCompletionPercent() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            //@Override
            public void doPrevious() {
                // TODO Auto-generated method stub
                
            }
            
            //@Override
            public void doNext() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
}