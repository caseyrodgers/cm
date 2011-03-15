package hotmath.gwt.shared.client.model;

import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.CmExceptionDoNotNotify;

public class UserProgramIsNotActiveException extends CmException implements CmExceptionDoNotNotify{
    
    public UserProgramIsNotActiveException() {
        super();
    }
}
