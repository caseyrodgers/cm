package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetCmVersionInfoAction;
import hotmath.gwt.shared.client.rpc.result.CmVersionInfo;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Return current version information for CM  system
 * 
 * @author casey
 *
 */
public class GetCmVersionInfoCommand implements ActionHandler<GetCmVersionInfoAction, CmVersionInfo>{

    @Override
    public CmVersionInfo execute(Connection conn, GetCmVersionInfoAction action) throws Exception {
        VersionInfo gwtVersion = new VersionInfo(readGwtVersionInfo(action.getBaseUrl()));
        VersionInfo webappVersion = new VersionInfo(readWebappVersionInfo(action.getBaseUrl()));
        
        return new CmVersionInfo(gwtVersion.getNum(),gwtVersion.getDateStamp(),webappVersion.getNum(), webappVersion.getDateStamp());
    }
    
    
    private String readGwtVersionInfo(String baseUrl) throws Exception {
        String url = baseUrl + "/gwt-resources/build_info/cm-build-gwt-info.txt";
        return getUrlContents(url);
    }
    
    private String readWebappVersionInfo(String baseUrl) throws Exception {
        String url = baseUrl + "/gwt-resources/build_info/cm-build-webapp-info.txt";
        return getUrlContents(url);
    }

    
    private String getUrlContents(String url) throws Exception {
        URL versionInfoUrl = null;
        BufferedReader in = null;
        try {
            versionInfoUrl = new URL(url);
            in = new BufferedReader(new InputStreamReader(versionInfoUrl.openStream()));
    
            String inputLine;
    
            StringBuffer sb = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine + "\n");
            
            return sb.toString();
        }
        finally {
            in.close();
        }        
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCmVersionInfoAction.class;
    }
}


class VersionInfo {
    Integer num;
    String dateStamp;

    private static Logger logger = Logger.getLogger(GetCmVersionInfoCommand.class);

    public VersionInfo(String versionFileText) throws Exception {

        // break into lines and extract pieces
        String lines[] = versionFileText.split("\n");
        if(lines.length == 3) {
            dateStamp = lines[1].substring(1);
            try {
                DateFormat formatFrom = new SimpleDateFormat("E MMM d k:m:s z yyyy");
                Date dte = formatFrom.parse(dateStamp);
                DateFormat formatTo = new SimpleDateFormat("MM-d k:mm");
                dateStamp = formatTo.format(dte);
            }
            catch(Exception e) {
            	logger.error(String.format("*** Error in constructor for versionFileText: %s", versionFileText), e);
            }
            num = Integer.parseInt(lines[2].substring("build.number=".length()));
        }

    }

    public Integer getNum() {
        return num;
    }
    public void setNum(Integer num) {
        this.num = num;
    }
    public String getDateStamp() {
        return dateStamp;
    }
    public void setDateStamp(String dateStamp) {
        this.dateStamp = dateStamp;
    }
}