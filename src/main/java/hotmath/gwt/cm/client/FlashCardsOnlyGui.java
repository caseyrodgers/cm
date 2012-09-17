package hotmath.gwt.cm.client;

import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper.WrapperType;

import java.util.List;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.ui.Widget;


/** Provides Self Registration login screen and validation
 * 
 * @author casey
 *
 */
public class FlashCardsOnlyGui extends CmMainResourceWrapper {
    
    public FlashCardsOnlyGui() {
        
        super(WrapperType.OPTIMIZED);
        
        getResourceWrapper().add(new Button("Flash Cards"));
        
        ContextController.getInstance().setCurrentContext(new CmContext() {
            
            //@Override
            public void runAutoTest() {
            }
            
            //@Override
            public void resetContext() {
            }
            
            //@Override
            public List<Widget> getTools() {
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