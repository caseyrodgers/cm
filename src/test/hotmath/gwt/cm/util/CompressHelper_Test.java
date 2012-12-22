package hotmath.gwt.cm.util;


import hotmath.cm.util.CompressHelper;
import junit.framework.TestCase;

public class CompressHelper_Test extends TestCase {

	private static String GRAPHIC_IMAGE_CMD = 
			"{\"dataArr\":[{\"x\":289.5,\"addImage\":true,\"name\":\"graphImage\",\"y\":423}],\"id\":1};";

    public CompressHelper_Test(String name) {
        super(name);
    }

    public void testCompressDrawGraphicImage() throws Exception {
    	byte[] compressed = CompressHelper.compress(GRAPHIC_IMAGE_CMD.getBytes());
    	
    	String decompressed = CompressHelper.decompress(compressed);
    	
        assert(GRAPHIC_IMAGE_CMD.equals(decompressed));
    }
    
}
