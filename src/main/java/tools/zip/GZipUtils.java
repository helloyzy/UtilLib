package tools.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sun.misc.BASE64Decoder;

/**
 * The class represents a set of utility classes for 
 * zipping, unzipping, encoding and decoding arbitrary string
 * or binary contents using GZip.
 */
public class GZipUtils {

	 /**
     * Compresses the input byte array using GZIP.
     *
     * @param binaryInput Array of bytes that should be compressed.
     * @return Compressed bytes
     * @throws IOException
     */
    public static byte[] gzip(byte[] binaryInput) throws IOException {
        ByteArrayOutputStream baos    = new ByteArrayOutputStream();
        GZIPOutputStream      gzipOut = new GZIPOutputStream(baos);

        gzipOut.write(binaryInput);
        gzipOut.finish();
        gzipOut.close();

        return baos.toByteArray();
    }

    /**
     * Compresses the input BASE64 encoded byte array using GZIP.
     *
     * @param base64Input BASE64 encoded bytes that should be compressed
     * @return Compressed bytes
     * @exception IOException
     */
    public static byte[] gzip(String base64Input) throws IOException {
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[]        binaryInput   = base64Decoder.decodeBuffer(base64Input);

        return gzip(binaryInput);
    }

    /**
     * Decompresses the input stream using GZIP.
     *
     * @param inputStream Stream those content should be decompressed.
     * @return Decompressed bytes
     * @throws IOException
     */
    public static byte[] gunzip(InputStream inputStream) throws IOException {
        byte[]                buffer = new byte[1024];
        int                   count;
        ByteArrayOutputStream baos   = new ByteArrayOutputStream();
        GZIPInputStream       gzipIn = new GZIPInputStream(inputStream);

        while ((count = gzipIn.read(buffer)) != -1) {
            baos.write(buffer, 0, count);
        }

        gzipIn.close();

        return baos.toByteArray();
    }

    /**
     * Decompresses the input byte array using GZIP.
     *
     * @param binaryInput Array of bytes that should be decompressed.
     * @return Decompressed bytes
     * @throws IOException
     */
    public static byte[] gunzip(byte[] binaryInput) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(binaryInput);
        return gunzip(bais);
    }

    /**
     * Decompresses the input byte array using GZIP.
     *
     * @param base64Input BASE64 encoded bytes to be decompressed.
     * @return Decompressed bytes
     * @exception IOException
     */
    public static byte[] gunzip(String base64Input) throws IOException {
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[]        binaryInput   = base64Decoder.decodeBuffer(base64Input);
        return gunzip(binaryInput);
    }
    
}
