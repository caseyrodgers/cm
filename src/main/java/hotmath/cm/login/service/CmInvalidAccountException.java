package hotmath.cm.login.service;

import java.text.Format;
import java.text.SimpleDateFormat;

import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.shared.client.util.CmException;

/** Thrown when a user does not have access to CM
 * 
 * @author bob
 *
 */
public class CmInvalidAccountException extends CmException {

	private static final long serialVersionUID = -7167634053238984537L;

	String message = "";

	Format _format = new SimpleDateFormat("MMM dd yyyy");

	public CmInvalidAccountException(HaBasicUser user) {

		message = "The Login Name and Password are valid; however, the referenced account does not have access to Catchup Math";
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
