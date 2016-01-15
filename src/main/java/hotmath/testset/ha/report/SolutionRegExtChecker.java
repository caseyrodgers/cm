package hotmath.testset.ha.report;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;
import sb.logger.SbLogger;
import sb.logger.SbLoggerListenerImplVerbose;
import sb.util.SbException;
import sb.util.SbUtilities;

/** Walk all solutions gathering information */
public class SolutionRegExtChecker {

    PreparedStatement _prepSt;

    Logger _logger = LoggerFactory.getLogger(SolutionRegExtChecker.class);

    /** Read through image directory and find images not used */
    public SolutionRegExtChecker(String book) throws SbException {
    	
    	Connection conn=null;
    	PreparedStatement ps=null;
    	PreparedStatement ps2=null;
        try {
             conn = HMConnectionPool.getConnection();
            
            String sql = "select DISTINCT BOOKTITLE FROM SOLUTIONS WHERE BOOKTITLE like ? order by booktitle";
            ps = conn.prepareStatement(sql);
            ps.setString(1, book);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
            	
            	System.out.println("Testing book: " + rs.getString("booktitle"));
                
                sql = "select PROBLEMINDEX, SOLUTIONXML FROM SOLUTIONS S WHERE problemindex =  BOOKTITLE = ? AND ACTIVE = 1 and solutionxml like '%correct%'";
                ps2 = conn.prepareStatement(sql);
                ps2.setString(1,  rs.getString("booktitle"));
                ResultSet rs2 = ps2.executeQuery();
                while(rs2.next()) {
                	doCompare(rs2.getString("problemindex"), rs2.getString("solutionxml"));
                }
            }
        } catch (Exception e) {
            throw new SbException(e, "Error creating Solution Checker object");
        }
        finally {
        	SqlUtilities.releaseResources(null, null, conn);
        }

        _logger.info("SolutionWalker complete");
    }

    Pattern pattern = Pattern.compile("(?s).*correct=['\"]yes.*correct=['\"]yes.*");
    private void doCompare(String pid, String xml) {
    	//xml = "\n\n<ul>correct='yes'\ncorrect=\"yes\"</ul>";
    	
    	Pattern p = Pattern.compile("(?s).*ul.*correct=['\"]yes.*correct=['\"]yes.*/ul");
    	Matcher m = p.matcher(xml);
    	boolean match = m.find();
    	if(match) {
    		System.out.println("match: " + pid);
    	}
	}

	static public void main(String as[]) {
        try {
            SbUtilities.isStandAlone(true);
            SbLogger.addLogListener(new SbLoggerListenerImplVerbose(null));
            new SolutionRegExtChecker("%");
            System.out.println("Complete!!");
        } catch (Exception e) {
            SbLogger.postMessage(e);
        }
        System.exit(0);
    }

}
