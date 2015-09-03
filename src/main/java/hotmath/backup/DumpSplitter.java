package hotmath.backup;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import com.google.common.io.Files;

/** Take an mysql dump file creates individial files
 *  for each table.
 *
 * @author casey
 *
 */
public class DumpSplitter {

    Logger logger = Logger.getLogger(DumpSplitter.class);
    FileWriter fileWriter;
    public DumpSplitter(String dumpFile) throws Exception {
        File outputHead = new File("dump_splitter");

        if(outputHead.exists()) {
            Files.deleteRecursively(outputHead);
        }
        outputHead.mkdirs();
        LineIterator it = FileUtils.lineIterator(new File(dumpFile), "UTF-8");
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                // do something with line
                if(line.indexOf("Table structure for table") > -1) {

                    if(fileWriter != null) {
                        logger.info("closing table");
                        fileWriter.close();
                    }

                    String tableName = line.split("`")[1];
                    logger.info("New table: " + tableName);

                    fileWriter = new FileWriter(new File(outputHead, tableName + ".dump"));
                }

                if(fileWriter != null) {
                    fileWriter.write(line + "\n");
                }
            }
        }
        catch(Exception e) {
            logger.error("Error", e);
        }
       finally {
            if(fileWriter != null) {
                fileWriter.close();
            }
            LineIterator.closeQuietly(it);
        }
    }


    static public void main(String as[]) {
        try {
            new DumpSplitter(as[0]);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        System.out.println("Dump Complete");
    }

}
