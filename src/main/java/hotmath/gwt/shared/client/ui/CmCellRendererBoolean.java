package hotmath.gwt.shared.client.ui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/** Boolean renderer that returns yes/no instead of true/false
 * 
 * @author casey
 *
 */
public class CmCellRendererBoolean extends AbstractCell<Boolean> {
    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, Boolean value, SafeHtmlBuilder html) {
        String valOut = (value == null || value == false)?"No":"Yes";
        html.append(SafeHtmlUtils.fromString(valOut));
    }
}
