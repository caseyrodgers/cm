package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.CmExceptionDoNotNotify;
import hotmath.gwt.shared.client.util.CmException;

public class UserProgramIsNotActiveException extends CmException implements CmExceptionDoNotNotify{
    
    public UserProgramIsNotActiveException() {
        super();
    }
}
