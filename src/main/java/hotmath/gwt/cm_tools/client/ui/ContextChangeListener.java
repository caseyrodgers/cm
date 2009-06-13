package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.ui.context.CmContext;

public interface ContextChangeListener {
    
    /** Be notified when the context changes
     * 
     * @param context
     */
    void contextChanged(CmContext context);
}
