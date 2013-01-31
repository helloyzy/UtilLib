package tools.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtils {

	private static void _close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				System.err.println("Failed to close file resource.");
			}
		}
	}

	public static void closeOutputStream(OutputStream oStream) {
		_close(oStream);
	}

	public static void closeInputStream(InputStream iStream) {
		_close(iStream);
	}

	public static void closeReader(Reader reader) {
		_close(reader);
	}

	public static void closeWriter(Writer writer) {
		_close(writer);
	}

	/**
	 * Get the input stream from the given file path
	 * 
	 * @param filePath
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream getInputStreamFromFilePath(String filePath)
			throws Exception {
		// loading the resource, first from class path, if failed then from file
		// path
		InputStream inputStream = FileUtils.class.getResourceAsStream(filePath);
		if (inputStream == null) {
			inputStream = new FileInputStream(filePath);
		}
		return inputStream;
	}

	public static void writeFile(String fileDir, String fileContent)
			throws Exception {
		if (fileContent.length() == 0) {
			System.out
					.println("No content to be written to the specified file directory.");
			return;
		}

		File file = new File(fileDir);
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(file));
			bufferedWriter.write(fileContent);
			bufferedWriter.flush();
		} finally {
			closeWriter(bufferedWriter);
		}
	}

	public static void writeCharsToFile(String fileDir, char[] fileContent)
			throws Exception {
		if (fileContent.length == 0) {
			System.out
					.println("No content to be written to the specified file directory.");
			return;
		}
		File file = new File(fileDir);
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(file));
			bufferedWriter.write(fileContent);
			bufferedWriter.flush();
		} finally {
			closeWriter(bufferedWriter);
		}
	}

	public static void writeObjectToFile(String fileDir, Serializable content)
			throws Exception {
		File file = new File(fileDir);
		ObjectOutputStream objOutputStream = null;
		try {
			objOutputStream = new ObjectOutputStream(new FileOutputStream(file));
			objOutputStream.writeObject(content);
			objOutputStream.flush();
		} finally {
			closeOutputStream(objOutputStream);
		}
	}

	public static String readFile(String dir) throws Exception {
		StringBuffer buffer = new StringBuffer();
		BufferedReader bufReader = null;
		try {
			InputStream in = getInputStreamFromFilePath(dir);
			bufReader = new BufferedReader(new InputStreamReader(in));
			String line = bufReader.readLine();
			while (line != null) {
				buffer.append(line);
				line = bufReader.readLine();
				if (line != null) {
					buffer.append(System.getProperty("line.separator"));
				}
			}
			return buffer.toString();
		} finally {
			closeReader(bufReader);
		}
	}

	public static Object readObjectFromFile(String fileDir) throws Exception {
		ObjectInputStream objInputStream = null;
		try {
			InputStream in = getInputStreamFromFilePath(fileDir);
			objInputStream = new ObjectInputStream(in);
			return objInputStream.readObject();
		} finally {
			closeInputStream(objInputStream);
		}
	}

	public static void copyFile(File srcFile, File destFile) throws Exception {
		int bufferSize = 2048;

		FileInputStream in = new FileInputStream(srcFile);
		FileOutputStream out = new FileOutputStream(destFile);
		FileChannel inChannel = in.getChannel();
		FileChannel outChannel = out.getChannel();

		ByteBuffer buffer = null;
		int length = -1;
		try {
			while (true) {
				if (inChannel.position() == inChannel.size()) {
					// finish copying
					break;
				} else if (inChannel.size() - inChannel.position() < length) {
					// copy last chunk of data
					length = (int) (inChannel.size() - inChannel.position());
				} else {
					length = bufferSize;
				}

				buffer = ByteBuffer.allocateDirect(length);
				inChannel.read(buffer);
				buffer.flip();
				outChannel.write(buffer);
				outChannel.force(false);
			}
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}

}
