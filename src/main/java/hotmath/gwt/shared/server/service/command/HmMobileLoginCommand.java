package hotmath.gwt.shared.server.service.command;

import hotmath.dwr.GeneralAccess;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.hm_mobile.client.model.HmMobileLoginInfo;
import hotmath.gwt.hm_mobile.client.rpc.HmMobileLoginAction;
import hotmath.subscriber.HotMathSubscriberLoginInfo;

import java.sql.Connection;

import org.apache.log4j.Logger;

/** Log user into CM and return HaBasicUser impl
 * 
 * Throws Exception on any error logging in.
 * 
 * 
 * @author bob
 *
 */
public class HmMobileLoginCommand implements ActionHandler<HmMobileLoginAction, HmMobileLoginInfo>{

	private static final Logger LOGGER = Logger.getLogger(HmMobileLoginCommand.class);

	public HmMobileLoginInfo execute(final Connection conn, HmMobileLoginAction action) throws Exception {
	    String result = new GeneralAccess().getLoginInfo(action.getUser(),action.getPassword(), null);
	    if(result == null || result.startsWith("error:")) {
	        throw new CmRpcException("Could not login: " + result);
	    }
	    HotMathSubscriberLoginInfo loginInfo = HotMathSubscriberLoginInfo.fromJson(result);
	    return new HmMobileLoginInfo(action.getUser(), action.getPassword(), loginInfo.getType());
	}

    public Class<? extends Action<? extends Response>> getActionType() {
        return HmMobileLoginAction.class;
    }

}
