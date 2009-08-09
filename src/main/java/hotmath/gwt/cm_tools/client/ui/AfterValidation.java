package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.model.StudentModel;


/** Defines interface that allows to be called after validation of form
 * 
 * @author casey
 *
 */
interface AfterValidation {
    
    /** Called after validation of form allowing for alternate strategies
     * 
     */
    public void afterValidation(StudentModel student);
}
