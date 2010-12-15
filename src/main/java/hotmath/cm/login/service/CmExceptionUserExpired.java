package hotmath.cm.login.service;

import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.shared.client.util.CmException;

import java.text.Format;
import java.text.SimpleDateFormat;

/** Thrown when a user is currently expired
 * 
 * @author casey
 *
 */
public class CmExceptionUserExpired extends CmException {

	String message;
	Format _format = new SimpleDateFormat("dd MMM yyyy");
	public CmExceptionUserExpired(HaBasicUser user) {
		if (user.getExpireDate() != null)
			message = "The entered account expired " + _format.format(user.getExpireDate()) + ".<br/>";
		else
			message = "The entered account is not valid.";

		if (user.getAccountType().equals("ST")) {
			message += "Please tell the appropriate faculty or administrator.";
		}
		if (user.getAccountType().equals("PS")) {
			message += "We hope you decide to renew!";
		}
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
