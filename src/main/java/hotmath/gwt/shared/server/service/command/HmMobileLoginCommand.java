package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.hm_mobile.client.model.HmMobileLoginInfo;
import hotmath.gwt.hm_mobile.client.rpc.HmMobileLoginAction;
import hotmath.subscriber.HotMathExceptionLogin;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.HotMathSubscriberManager;
import hotmath.subscriber.service.HotMathSubscriberService;

import java.sql.Connection;
import java.util.Date;

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
        String accessKeyOrEmail = action.getUser();
        String password = action.getPassword();
        HotMathSubscriber sub = null;
        if(accessKeyOrEmail != null && accessKeyOrEmail.indexOf("@") > -1) {
           // login by email+password
           sub =(HotMathSubscriber)HotMathSubscriberManager.findSubscriberByEmailPassword(accessKeyOrEmail, password);
        }
        else {
            sub = HotMathSubscriberManager.findSubscriberSchoolByIdPassword(accessKeyOrEmail);
        }
        
        if (sub == null || !sub.isValid()) {
            throw new HotMathExceptionLogin(action.getUser());
        }
        
        boolean isExpired = false;
        Date dateExpire = null;
        int solutionCount=0;
        HotMathSubscriberService service = sub.getService("solution");
        if(service != null) {
            isExpired = service.isExpired();
            dateExpire= service.getDateExpire();
            solutionCount = service.getUsage();
        }
        
	    return new HmMobileLoginInfo(action.getUser(), action.getPassword(), sub.getSubscriberType(), isExpired, dateExpire, solutionCount);
	}

    public Class<? extends Action<? extends Response>> getActionType() {
        return HmMobileLoginAction.class;
    }

}
