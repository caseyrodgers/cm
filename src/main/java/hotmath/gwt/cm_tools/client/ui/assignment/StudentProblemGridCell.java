package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/** Creates standard way of showing a problem 
 * 
 * @author casey
 *
 */
public class StudentProblemGridCell extends AbstractCell<String> {
    
        private ProblemGridCellCallback callBack;
        
        public static interface ProblemGridCellCallback {
            ProblemDto getProblem(int which);
        }

        public StudentProblemGridCell(ProblemGridCellCallback callback) {
            this.callBack = callback;
        }
        
        @Override
        public void render(Context context, String value, SafeHtmlBuilder sb) {
            ProblemDto studProb = callBack.getProblem(context.getIndex());
            String colHtml = studProb.getLabel();
            sb.appendHtmlConstant(colHtml);
        }

}
