package hotmath.cm.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public class LinkConvertImplKahn implements LinkConvert {
    
    Logger _logger = Logger.getLogger(LinkConvertImplKahn.class);    

    @Override
    public String doConversion(String url) {
        try {
            
            if(url.indexOf("khan") == -1) {
                return null;
            }
            
            URL u = new URL(url);
            HttpURLConnection huc = (HttpURLConnection) u.openConnection();
            huc.setRequestMethod("GET");
            huc.connect();
            int rc = huc.getResponseCode();
            
            InputStream is = huc.getInputStream();
            BufferedReader reader = new BufferedReader( new InputStreamReader( is )  );
            String line = null;
            StringBuffer contents = new StringBuffer();
            while( ( line = reader.readLine() ) != null )  {
               contents.append(line);
            }
            reader.close();
            String urlContents = contents.toString();
            
            // search for the meta tag containing the video key
            String searchFor="<meta property=\"og:video\" content=\"";
            int metaS = urlContents.indexOf(searchFor);
            if(metaS > -1) {
                int metaE = urlContents.indexOf("\">", metaS);                
                String videoStr = urlContents.substring(metaS+searchFor.length(), metaE);
                String path[] = videoStr.split("/");
                String videoKey = path[path.length-1];
                if(path.length > 1) {
                    String convertedPath = "https://www.youtube.com/embed/" + videoKey + "?feature=player_embedded";
                    return convertedPath;
                }
            }
        }
        catch(Exception e) {
            _logger.error("Error during conversion", e);
        }
        return null;
    }

}
