package hotmath.cm.util;

import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.log4j.Logger;

public class CompressHelper {
	
	private static final Logger LOGGER = Logger.getLogger(CompressHelper.class);
	
	public static byte[] compress(byte[] inBytes) {
        long startTime = System.currentTimeMillis();
		Deflater deflater = new Deflater();
		deflater.setInput(inBytes);
		deflater.finish();
		byte[] outBytes = new byte[inBytes.length];
		int compressedLen = deflater.deflate(outBytes);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(String.format("compress(): in: %d, out: %d, took: %d msec",
					inBytes.length, compressedLen, System.currentTimeMillis() - startTime));
		return outBytes;
	}

	public static String decompress(byte[] inBytes) throws DataFormatException, UnsupportedEncodingException {
		long startTime = System.currentTimeMillis();
		String result = decompressOuter(inBytes);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(String.format("decompress(): in: %d, out: %d, took: %d msec",
					inBytes.length, result.length(), System.currentTimeMillis() - startTime));
		return result;
	}

	private static String decompressOuter(byte[] inBytes) throws DataFormatException, UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
    	Inflater inflater = new Inflater();
    	inflater.setInput(inBytes);
		while (inflater.finished() == false) {
			sb.append(decompressInner(inBytes, inflater));
		}
    	inflater.end();
		return sb.toString();
	}

	private static String decompressInner(byte[] inBytes, Inflater inflater) throws DataFormatException, UnsupportedEncodingException {
		byte[] outBytes = new byte[10000];
		int byteCount = inflater.inflate(outBytes, 0, outBytes.length);
		return new String(outBytes, 0, byteCount, "UTF-8");
	}


}
