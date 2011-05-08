package hotmath.gwt.shared.server.service.command.stress;

import hotmath.cm.server.listener.ContextListener;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction.FlowType;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.data.HaUserLoginInfo;
import hotmath.gwt.shared.client.rpc.action.LoginAction;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import sb.util.SbUtilities;

/**
 * Stand alone tool and called from /assets/util to stress test the Login Process
 *
 * @author casey
 *
 */
public class CmStress extends Thread {

    String user, pass;
    int delay;

    static int __counter;


    public CmStress(String user, String pass, int delay) {
        this.user = user;
        this.pass = pass;
        this.delay = delay;
    }

    int id = __counter++;
    public void startTest() {
        System.out.println("Login test " + id + ": " + this);
        try {
            Thread.sleep(delay);
            start();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "CmStress [user=" + user + ", pass=" + pass + ", delay=" + delay + "]";
    }
    
    @Override
        public void run() {


        long start = System.currentTimeMillis();
        try {


            int delayTime = SbUtilities.getRandomNumber(10) * delay;

            System.out.println(id + " test start (delay=" + delayTime + ")");

            LoginAction login = new LoginAction(this.user, this.pass);
            HaUserLoginInfo loginInfo = ActionDispatcher.getInstance().execute(login);

            int userId = loginInfo.getHaLoginInfo().getUserId();
            Thread.sleep(delayTime);


            GetCmProgramFlowAction flowAction = new GetCmProgramFlowAction(userId,FlowType.ACTIVE);
            CmProgramFlowAction flowActive = ActionDispatcher.getInstance().execute(flowAction);

            switch(flowActive.getPlace()) {
            case AUTO_ADVANCED_PROGRAM:
                break;

            case AUTO_PLACEMENT:
                break;

            case END_OF_PROGRAM:
                break;

            case PRESCRIPTION:
                assert(flowActive.getPrescriptionResponse() != null);
                break;

            case QUIZ:
                assert(flowActive.getQuizResult()!=null);
                break;
            }

            System.out.println(id + " test complete, time: " + (System.currentTimeMillis() - start) / 1000);

        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
        }
    }

    static public void main(String as[]) {

        new ContextListener();
        SbUtilities.addOptions(as);

        int count = SbUtilities.getInt(SbUtilities.getOption("1000", "-count"));
        int delay = SbUtilities.getInt(SbUtilities.getOption("1000", "-delay"));

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String sql = "select a.user_name, u.user_passcode "
                + "from HA_ADMIN a "
                + " JOIN SUBSCRIBERS s on s.id = a.subscriber_id "
                + " JOIN HA_USER u on u.admin_id = a.aid "
                + " where u.is_active = 1 "
                + " and admin_id != 13 "
                + " and is_auto_create_template = 0 "
                + " order by rand() "
                + " limit " + count;
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String uName = rs.getString("user_name");
                String uPass = rs.getString("user_passcode");

                new CmStress(uName, uPass, delay).startTest();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
    }

}
