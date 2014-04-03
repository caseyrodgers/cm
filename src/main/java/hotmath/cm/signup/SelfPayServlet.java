package hotmath.cm.signup;

import hotmath.cm.util.service.PaymentService;
import hotmath.gwt.cm_rpc_core.client.rpc.CreateAutoRegistrationAccountAction;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.shared.server.service.command.CreateAutoRegistrationAccountCommand;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import sb.util.SbUtilities;

/**
 * 
 * @author bob
 *
 */

public class SelfPayServlet extends HttpServlet {

	private static final long serialVersionUID = 5980128995266337940L;

	static Logger _logger = Logger.getLogger(SelfPayServlet.class.getName());

    public SelfPayServlet() {
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	doPost(req, resp);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String remoteIP = req.getRemoteAddr();
            _logger.info("Attempting to create a new Self-Pay account for: " + remoteIP);

            String uniquePassword = "";
            String loginName = "";
            int newUserId = 0;
            String key = "";

            /** Extract form data
             */
            @SuppressWarnings("unchecked")
            Map<String, String[]> formData = req.getParameterMap();
            String studentId = getFData(formData.get("student_id"));
            String firstName = getFData(formData.get("student_first_name"));
            String lastName  = getFData(formData.get("student_last_name"));
            CreateAutoRegistrationAccountAction action = new CreateAutoRegistrationAccountAction();
            if (studentId != null && studentId.trim().length() > 0) {
                action.setPassword(firstName+"-"+lastName+"-"+studentId.trim());
            }
            else {
                String birthday  = getFData(formData.get("student_birth_month")) +
                		getFData(formData.get("student_birth_day"));
                action.setPassword(firstName+"-"+lastName+"-"+birthday);
            }
            String uid = getFData(formData.get("uid_fld"));
            Integer userId = Integer.parseInt(uid);
            action.setUser(lastName +", " + firstName);
            action.setUserId(userId);
            Connection conn = null;
            try {
                conn = HMConnectionPool.getConnection();
                CreateAutoRegistrationAccountCommand command = new CreateAutoRegistrationAccountCommand();
                RpcData data = command.execute(conn, action);
                key = data.getDataAsString("key");

                newUserId = data.getDataAsInt("uid");

                loginName = data.getDataAsString("loginName");
                uniquePassword = data.getDataAsString("password");
            }
            finally {
                SqlUtilities.releaseResources(null,null,conn);
            }

            String ccNum  = getFData(formData.get("card_number"));
            ccNum.replaceAll(" ", "");
            String ccType = getFData(formData.get("sel_cardtype"));
            String ccv2   = getFData(formData.get("card_ccv2"));
            String expMon = getFData(formData.get("sel_card_expire_month"));
            String expYr  = getFData(formData.get("sel_card_expire_year"));
            String ccZip  = getFData(formData.get("zip"));
            String ccState = getFData(formData.get("sel_state"));
            String ccAddr1 = getFData(formData.get("address1"));
            String ccAddr2 = getFData(formData.get("address2"));
            String ccCity  = getFData(formData.get("city"));
            String ccFname = getFData(formData.get("first_name"));
            String ccLname = getFData(formData.get("last_name"));
            String email   = getFData(formData.get("fld_student_email"));

            PaymentService.doPurchase(remoteIP, 29.00, ccNum, ccType, ccv2, expMon, expYr,
            		ccZip, ccState, ccAddr1, ccAddr2, ccCity, ccFname, ccLname,
            		newUserId, email, loginName, uniquePassword);
            
            /** Return JSON containing key values
             * 
             */
            String returnJson = "{uid:'" +  newUserId +
                                  "', loginName:'" + loginName +
                                  "', password:'" + uniquePassword + "'" +
                                 "}";
            resp.getWriter().write(returnJson);

        } catch (Exception e) {
            _logger.error("*** Error creating new user", e);
            
            /** TODO: should this throw an Exception?
             * At very least we need to check for this instance
             * in cmsignup.js
             *  
             */
            resp.getWriter().write("error:" + e.getMessage());
        }
    }

    private String getFData(Object o) {
        if (o instanceof String[]) {
            return ((String[]) o)[0];
        } else {
            return SbUtilities.getStringValue(o);
        }
    }

}
