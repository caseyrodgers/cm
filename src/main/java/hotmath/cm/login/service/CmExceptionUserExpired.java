package hotmath.cm.login.service;

import hotmath.gwt.cm_rpc.client.CmUserException;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;

import java.text.Format;
import java.text.SimpleDateFormat;

/** Thrown when a user is currently expired
 * 
 * @author casey
 *
 */
public class CmExceptionUserExpired extends CmUserException {

	private static final long serialVersionUID = 4668807820551018668L;

	String message = "";

	Format _format = new SimpleDateFormat("MMM dd yyyy");

	public CmExceptionUserExpired(HaBasicUser user) {
	    super(user.getUserKey() + " is expired");

		switch (user.getUserType()) {
		case ADMIN:
			message = String.format("Your school account expired on %s.  Please contact your Catchup Math Account Manager to renew.",
					_format.format(user.getExpireDate()));
			break;
		case STUDENT:
			if (user.getAccountType().equals("ST")) {
    			message = String.format("Your school account expired on %s.  Please contact the teacher.",
					_format.format(user.getExpireDate()));
			}
			else if (user.getAccountType().equals("PS")) {
    			message = String.format("Your personal account expired on %s.  We hope you will renew!",
    					_format.format(user.getExpireDate()));				
			}
			break;
			
		}
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
