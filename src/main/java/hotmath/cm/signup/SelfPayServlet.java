package hotmath.cm.signup;

import hotmath.gwt.cm_rpc_core.client.rpc.CreateAutoRegistrationAccountAction;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.shared.server.service.command.CreateAutoRegistrationAccountCommand;
import hotmath.subscriber.HotMathExceptionPurcaseException;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.HotMathSubscriberSignupInfo;
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
        HotMathSubscriber sub=null;
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
            String birthday  = getFData(formData.get("student_birth_month")) +
            		getFData(formData.get("student_birth_day"));
            CreateAutoRegistrationAccountAction action = new CreateAutoRegistrationAccountAction();
            action.setPassword(firstName+"-"+lastName+"-"+birthday);
            String uid = getFData(formData.get("uid_fld"));
            Integer userId = Integer.parseInt(getFData(formData.get("uid_fld")));
            action.setUser(lastName +", " + firstName);
            action.setUserId(userId);
            Connection conn=null;
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
            
            
            /** Return JSON containing key values
             * 
             */
            String returnJson = "{uid:'" +  newUserId +
                                  "', loginName:'" + loginName +
                                  "', password:'" + uniquePassword + "'" +
                                 "}";
            resp.getWriter().write(returnJson);

        } catch (Exception e) {
            _logger.error("*** Error creating new account", e);
            try {
                sub.addComment(e.getMessage());
            } catch (Exception ee) {
                _logger.error("*** Error adding comment", ee);
            }
            
            /** CHECK: should this throw an Exception?
             * At very least we need to check for this instance
             * in resources/js/signup.js
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
    
    
    /** Extract data from request and build SignupInfo to encapsulate request
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private HotMathSubscriberSignupInfo getSignupInfo(HttpServletRequest req) throws Exception {
        
        @SuppressWarnings("unchecked")
        Map<String, String[]> formData = req.getParameterMap();
        
        String email = getFData(formData.get("confirm_email"));
        if (email == null || email.length() == 0)
            throw new Exception("'email' cannot be null");

        String firstName = getFData(formData.get("first_name"));
        String lastName = getFData(formData.get("last_name"));
        String cardholder_address1 = getFData(formData.get("address1"));
        String cardholder_address2 = getFData(formData.get("address2"));
        String cardholder_city = getFData(formData.get("city"));
        String cardholder_zip = getFData(formData.get("zip"));
        String cardholder_state = getFData(formData.get("sel_state"));

        String ccType = getFData(formData.get("sel_cardtype"));
        String ccCvv2 = getFData(formData.get("card_ccv2"));

        // extract credit card info
        String cardNumber = getFData(formData.get("card_number"));
        String cardExpMonth = getFData(formData.get("sel_card_expire_month"));
        String cardExpYear = getFData(formData.get("sel_card_expire_year"));

        // remove any spaces in card number
        cardNumber = SbUtilities.replaceSubString(cardNumber, " ", "");

        if (lastName.equals("error")) {
            throw new HotMathExceptionPurcaseException("INTENTIONAL SERVER ERROR");
        }
        if (cardNumber == null || lastName == null) {
            throw new HotMathExceptionPurcaseException("Cardnumber and lastname must be specified");
        }

        
        /** Move signup data into persistent storage
         * 
         */
        HotMathSubscriberSignupInfo signupInfo = new HotMathSubscriberSignupInfo();

        // save non-secure info
        signupInfo.setBillingAddress(cardholder_address1 + cardholder_address2);
        signupInfo.setBillingCity(cardholder_city);
        signupInfo.setBillingState(cardholder_state);
        signupInfo.setBillingZip(cardholder_zip);
        signupInfo.setFirstName(firstName);
        signupInfo.setLastName(lastName);

        // set the secure data as variables only
        signupInfo.setCardEmail(email);
        signupInfo.setCardType(ccType);
        signupInfo.setCardNumber(cardNumber);
        signupInfo.setCardCcv(ccCvv2);
        signupInfo.setCardExpMonth(cardExpMonth);
        signupInfo.setCardExpYear(cardExpYear);
        
        
        return signupInfo;
    }

}
