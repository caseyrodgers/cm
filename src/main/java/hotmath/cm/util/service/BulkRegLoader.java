package hotmath.cm.util.service;

import hotmath.gwt.shared.client.rpc.result.AutoRegistrationEntry;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Load student/password pairs
 * 
 * @author bob
 * 
 */
public class BulkRegLoader {

	private static final Logger LOGGER = Logger.getLogger(BulkRegLoader.class);

    private int errorCount;
    private List<String> dupNames;
    private List<String> dupPasswords;
    private boolean contentIsAcceptable;
    String key;

    private List<AutoRegistrationEntry> entries = new ArrayList<AutoRegistrationEntry>();
    
    public void processUpload(List<FileItem> fileItems) throws IOException {
	    /** 
	     * There might be multiple files, but there is only one upload.
	     * 
	     * We want to collect these into a single structure.
	     * 
	     * IE always has multiple parts, with the first part the filename.
	     * 
	     */
		for (FileItem fi: fileItems) {

			String filename = fi.getName();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("filename: " + filename);
		    
			InputStream is = null;
			try {
				if (isTextOnly(fi)) {
					is = fi.getInputStream();
			    }
				else if (isExcel(fi)) {
					is = getTSVInputStream();
				}
			    if (is != null) readStream(is);
			    contentIsAcceptable = (entries.size() > 0);
			}
			finally {
			    if (is != null) is.close();
			}
		}
	
    }

    /** Read the input stream and extract entries
     * 
     *  IE includes the filename in the data, which does not conform to our 
     *  expectations.  We need to identify this problem and ignore the row.
     *  
     *  Record is a tab delimited set of columns.
     *  
     * @param is
     * @throws IOException
     */
    public void readStream(InputStream is) throws IOException {
        BufferedReader isr = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line;
        Set<String> nameSet = new HashSet<String>();
        Set<String> passwdSet = new HashSet<String>();
        dupNames = new ArrayList<String> ();
        dupPasswords = new ArrayList<String> ();
        
        while ((line = isr.readLine()) != null) {

            line = line.trim();
            if(line.startsWith("#") || line.length() == 0)
                continue;

            line = removeSpecialCharacters(line);
            String pair[] = line.split("\t");
            
            if(pair.length != 2) {
                AutoRegistrationEntry entry = new AutoRegistrationEntry();
                entry.setName(line);
                entry.setIsError(true);
                entry.setMessage("Invalid row: Must be name TAB password.");
                entries.add(entry);
            }
            else { // (pair.length == 2)
                
                /** strip spaces from passwords before processing
                 *  
                 *  NOTE: original password is used in error reporting)
                 *        
                 */
                String password = pair[1].replace(" ", "");

                AutoRegistrationEntry entry = new AutoRegistrationEntry();
                entry.setName(pair[0]);
                entry.setPassword(pair[1]);
                entries.add(entry);
                
                if (!nameSet.contains(pair[0]) && !passwdSet.contains(password) ) {
                    nameSet.add(pair[0]);
                    passwdSet.add(password);
                }
                else { // have dup name and/or password
                    entry.setIsError(true);

                    StringBuilder sb = new StringBuilder();
                    sb.append("Invalid row: ");
                	if (nameSet.contains(pair[0])) {
                		sb.append(" duplicate name");
                	}
                	if (passwdSet.contains(password)) {
                		if (nameSet.contains(pair[0])) {
                			sb.append(" and password");
                		}
                		else {
                			sb.append(" duplicate password");
                		}
                	}
            		sb.append(".");
                    entry.setMessage(sb.toString());
                }
            }
        }

        this.key = "upload_" + System.currentTimeMillis();
    }
    
    private boolean isTextOnly(FileItem fi) {

        int b;
        try {
        	InputStream is = fi.getInputStream();        	
            while ((b = is.read()) > -1) {
               if (b < 0x00 || b > 0x7f)
                   return false;
            }
        }
        catch (IOException e) {
            return false;
        }
        return true;	
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /** remove any special characters such as quotes and double quotes
     * 
     */
    private String removeSpecialCharacters(String s) {
        
        String r = s.replace("\"","");
        r = r.replace("'","");
        return r;
    }
    

    @Override
    public String toString() {
        return "BulkRegLoader [duplicateNames=" + dupNames + ", duplicatePasswords="
                + dupPasswords + ", entries=" + entries
                + ", hasNumericContent=" + hasNumericContent + ", key=" + key + "]";
    }

    public int getStudentCount() {
        return entries.size();
    }

    public int getErrorCount() {
        return errorCount;
    }

    public boolean hasErrors() {
        return (errorCount != 0);
    }

    public boolean hasDuplicateNames() {
    	return (dupNames != null && dupNames.size() > 0);
    }
    
    public boolean hasDuplicatePasswords() {
    	return (dupPasswords != null && dupPasswords.size() > 0);
    }

    public boolean hasNumericContent() {
    	return hasNumericContent;
    }

    public boolean contentIsAcceptable() {
    	return contentIsAcceptable;
    }

    public List<AutoRegistrationEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<AutoRegistrationEntry> entries) {
        this.entries = entries;
    }
    
    public List<String> getDuplicateNames() {
    	return dupNames;
    }
    
    public List<String> getDuplicatePasswords() {
    	return dupPasswords;
    }
    
    private StringBuilder tsvContents;
    private boolean hasNumericContent;
    
	private boolean isExcel(FileItem fi) throws IOException {
		InputStream is = null;
    	try {
    		Workbook wb = null;
    		is = fi.getInputStream();
    		try {
        		POIFSFileSystem fs = new POIFSFileSystem(is);
        		wb = new HSSFWorkbook(fs);
    		}
    		catch (OfficeXmlFileException oxfE) {
    			is.close();
    			is = fi.getInputStream();
    			wb = new XSSFWorkbook(is);
    		}
    		Sheet sheet = wb.getSheetAt(0);

    		// Iterate over each row in the sheet
    		Iterator<?> rows = sheet.rowIterator(); 
    		tsvContents = new StringBuilder();
    		hasNumericContent = false;
    		
    		while( rows.hasNext() ) {           
    			Row row = (Row) rows.next();

    			// Iterate over each cell in the row
    			Iterator<?> cells = row.cellIterator();
    			int cellCount = 0;
    			while (cells.hasNext()) {
    				Cell cell = (Cell) cells.next();

    				switch (cell.getCellType()) {
    				case Cell.CELL_TYPE_NUMERIC:
    					long val = new Double(cell.getNumericCellValue()).longValue();
    					tsvContents.append(val);
    					hasNumericContent = true;
    					cellCount++;
    					break;
    				case Cell.CELL_TYPE_STRING: 
    					tsvContents.append(cell.getStringCellValue());
    					cellCount++;
    					break;
    				default:
    					LOGGER.info( "unsupported cell type: " + cell.getCellType() );
    				break;
    				}
    				if (cellCount < 2) {
    					tsvContents.append("\t");
    				}
    				else {
    					tsvContents.append("\n");
    					break;
    				}
    			}

    		}
    		return true;

    	} catch ( Exception ex ) {
    		LOGGER.debug("Exception processing: " + fi.getName(), ex);
    	}
    	finally {
    		if (is != null) {
				is.close();
    		}
    	}
    	return false;
    }
    
    private InputStream getTSVInputStream() {
    	return new ByteArrayInputStream(tsvContents.toString().getBytes());
    }
}
