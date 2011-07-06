package hotmath.cm.util;

import java.io.File;

/**
 * Extracted from GeneratePdfCommand
 *  
 * @author bob
 *
 */

public class FileUtil {
	
    public static String ensureOutputDir(String outputBase, String unique) {

        File file = new File(outputBase, unique);
    	if (! file.exists()) {
    		file.mkdirs();
    	}
    	return file.getPath();
    }


}
