package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetCmVersionInfoAction;
import hotmath.gwt.shared.client.rpc.result.CmVersionInfo;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    
    public VersionInfo(String versionFileText) throws Exception {
        
        // break into lines and extract pieces
        String lines[] = versionFileText.split("\n");
        if(lines.length == 3) {
            dateStamp = lines[1].substring(1);
            DateFormat df = new SimpleDateFormat();
            try {
                DateFormat formatFrom = new SimpleDateFormat("E MMM d k:m:s z yyyy");
                Date dte = formatFrom.parse(dateStamp);
                DateFormat formatTo = new SimpleDateFormat("MM-d k:mm");
                dateStamp = formatTo.format(dte);
            }
            catch(Exception e) {
                e.printStackTrace();
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