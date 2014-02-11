package hotmath.gwt.cm_tools.client.ui;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.theme.blue.client.tree.BlueTreeAppearance;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;
import com.sencha.gxt.widget.core.client.tree.Tree.Joint;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;
import com.sencha.gxt.widget.core.client.tree.TreeView.TreeViewRenderMode;

/** Allows for optional checkable levels.  
 * 
 *  Only levels that are equal to or greater than minCheckableLevel
 *  will be checkable.
 *  
 * @author casey
 *
 */
public class CheckableMinLevelGxtTreeAppearance extends BlueTreeAppearance {

    private int minCheckableLevel;

    public CheckableMinLevelGxtTreeAppearance(int minCheckableLevel) {
        this.minCheckableLevel = minCheckableLevel;
    }
    
    int times;
    
    @Override
    public void renderNode(SafeHtmlBuilder sb, String id, SafeHtml text, TreeStyle ts, ImageResource icon,
        boolean checkable, CheckState checked, Joint joint, int level, TreeViewRenderMode renderMode) {

        /** only enable checking on named level and below
         * 
         */
        checkable = level >= minCheckableLevel;
        super.renderNode(sb, id, text, ts, icon, checkable, checked, joint, level, renderMode);
    }
    
    public void setMinCheckableLevel(int level) {
        this.minCheckableLevel = level;
    }

    public int getMinCheckableLevel() {
        return this.minCheckableLevel;
    }
  }

