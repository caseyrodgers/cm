package hotmath.cm.util;

import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.log4j.Logger;

public class CompressHelper {
	
	private static final Logger LOGGER = Logger.getLogger(CompressHelper.class);
	
	public static byte[] compress(byte[] inBytes) {

		Deflater deflater = new Deflater();
		deflater.setInput(inBytes);
		deflater.finish();
		byte[] outBytes = new byte[inBytes.length];
		int compressedLen = deflater.deflate(outBytes);
		if (LOGGER.isDebugEnabled()) LOGGER.debug("compress(): in bytes: " + inBytes.length + ", out bytes: " + compressedLen);
		return outBytes;
	}

	public static String decompress(byte[] inBytes, int offset, Inflater inflater) throws DataFormatException, UnsupportedEncodingException {

		StringBuffer sb = new StringBuffer();
		if (inflater == null) {
    		inflater = new Inflater();
    		inflater.setInput(inBytes);
		}
		byte[] outBytes = new byte[5000];
		int byteCount = inflater.inflate(outBytes, offset, outBytes.length);
		sb.append(new String(outBytes, 0, byteCount, "UTF-8"));
		if (inflater.finished() == false) {
			sb.append(decompress(inBytes, 0, inflater));
		}
		else {
    		inflater.end();
		}
		return sb.toString();
	}

}
