package hotmath.cm.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import sb.util.SbUtilities;

/* Extracted from CmMultiLinePropertyReader
* 
* Provides a means of reading in multi-line property files without having to
* provide line continuation escapes.
* 
* Each entry is marked as follows:
* 
* .key=THE_KEY_NAME
* 
* ## comment lines
* 
* ANY TEXT WITH MULTI LINES MORE TXT dfdf ...
* 
* 
* .key=ANOTHER_KEY
* 
* The other text until EOF or new .key
* 
* EOF
* 
* You can cause a re-read of the propertes by flushing the server via
* loading resources/util/cm_system_flush.jsp.
* 
*/

public abstract class AbstractCmMultiLinePropertyReader extends Properties {
    
    private static final long serialVersionUID = 6706889922304528657L;

    abstract protected Logger getLogger();

    protected void loadProps(String propFile) {

        try {
            /**
             * Read in the multi-line property file
             * 
             */
            FileReader fileReader = new FileReader(propFile);
            BufferedReader br = new BufferedReader(fileReader);
            String line = null;

            String value = "";
            String name = null;
    
            while ((line = br.readLine()) != null) {
    
                if (line == null || line.length() == 0 || line.startsWith("#"))
                    continue;
    
                if (line.startsWith(".key=")) {
                    if (value.length() > 0) {
                        
                        /** Make sure this entry does not already exist
                         * 
                         */
                        if(containsKey(name)) {
                            getLogger().warn("property already exists: " + name);
                        }
                        put(name, value);
                        value = "";
                    }
                    name = line.substring(5).trim();
                } else {
                    value += (line + "\n");
                }
            }
    
            /**
             * put last value
             * 
             */
            if (value.length() > 0)
                put(name, value);
        }
        catch(Exception e) {
            getLogger().error("Error reading multi line property file: " + propFile, e);
        }
    }
    
    public String getProperty(String key, Map<String,String> replacements) {
        String val = getProperty(key);
        return SbUtilities.replaceTokens(val, replacements);
    }
    
    public String getProperty(String key, String[] replacements) {
        Map<String,String> map = new HashMap<String, String>();
        for(String pair: replacements) {
            String row[] = pair.split("\\|");
            if(row.length == 2) {
                map.put(row[0], row[1]);
            }
        }
        return getProperty(key, map); 
    }    
}
