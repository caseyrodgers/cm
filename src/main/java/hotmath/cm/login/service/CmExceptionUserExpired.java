package hotmath.cm.login.service;

import java.text.Format;
import java.text.SimpleDateFormat;

import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.shared.client.util.CmException;

/** Thrown when a user is currently expired
 * 
 * @author casey
 *
 */
public class CmExceptionUserExpired extends CmException {

	private static final long serialVersionUID = 4668807820551018668L;

	String message = "";

	Format _format = new SimpleDateFormat("MMM dd yyyy");

	public CmExceptionUserExpired(HaBasicUser user) {

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
