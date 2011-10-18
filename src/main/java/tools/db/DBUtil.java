package tools.db;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import tools.file.FileUtils;

public class DBUtil {
	
	/**
     * Closes the supplied JDBC connection if it is not null.
     * @param connection
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception ignored) {}
        }
    }

    /**
     * Closes the supplied JDBC statement if it is not null.
     * @param statement
     */
    public static void closeStatement(Statement statement) {
    	if (statement != null) {
            try {
                statement.close();
            } catch (Exception ignored) {}
        }
    }

    /**
     * Closes the supplied JDBC resultset if it is not null.
     * @param resultSet
     */
    public static void closeResultSet(ResultSet resultSet) {
    	if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception ignored) {}
        }
    }
	
	
	/**
	 * Get a DB connection according to the given parameters
	 * @param properties
	 * @return Connection
	 * @throws Exception
	 */
	public static Connection getConnection(DBConnectionProperties properties) throws Exception{
		String driverName = properties.getDriverName();
    	String URL = properties.getDbURL();
    	String USERNAME = properties.getDbUserName(); //case sensitive
    	String PASSWORD = properties.getDbPwd(); //case sensitive
    	Class.forName(driverName);
    	return DriverManager.getConnection(URL, USERNAME, PASSWORD);
	}

	/**
	 * execute a given command(insert,update,delete) through the given
	 * connection
	 * 
	 * @param conn
	 * @param sqlStatement
	 * @throws Exception
	 */
	public static void executeCommand(Connection conn, String sqlStatement)
			throws Exception {
		if (conn == null) {
			throw new Exception(
					"DBUtil Exception(executeDBCommand) -- connection is null");
		}
		if (sqlStatement == null) {
			throw new Exception(
					"DBUtil Exception(executeDBCommand) -- sqlStatement is null");
		}
		Statement statement = null;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(sqlStatement);
		} catch (Exception e) {
			throw new Exception(
					"DBUtil Exception -- error in executeDBCommand,the sql is as follows: "
							+ sqlStatement, e);
		} finally {
			closeStatement(statement);
			statement = null;
		}
	}

	/**
	 * @param conn
	 *            Connection
	 * @param sqlStatement
	 *            sql statement to be executed
	 * @return <li>the first column of the first record's value is returned as a
	 *         string
	 */
	public static String executeAndFetchValue(Connection conn,
			String sqlStatement) throws Exception {
		if (conn == null) {
			throw new Exception(
					"DBUtil Exception(executeAndFetchValue) -- connection is null");
		}
		if (sqlStatement == null) {
			throw new Exception(
					"DBUtil Exception(executeAndFetchValue) -- sqlStatement is null");
		}
		Statement statement = null;
		try {
			statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery(sqlStatement);
			if (resultSet.next()) {
				return resultSet.getString(1);
			} else {
				throw new Exception(
						"DBUtil Exception(executeAndFetchValue) -- no result got from the sql: "
								+ sqlStatement);
			}
		} catch (Exception e) {
			throw new Exception(
					"DBUtil Exception -- error in executeAndFetchValue,the sql is as follows: "
							+ sqlStatement, e);
		} finally {
			closeStatement(statement);
			statement = null;
		}
	}

	/**
	 * This function will execute the sqlStatement and fetch value, the value
	 * will be compared with "expectedValue", if this does not pass, it will
	 * sleep for "retryInterval" and try again, after "maxSleepTime", if it
	 * still does not get the "expectedValue", return false;
	 * 
	 * @param maxSleepTime
	 * @param retryInterval
	 * @param sqlStatement
	 * @param expectedValue
	 * @return <li>true if the value fetched from DB is equal to "expectedValue"
	 *         <li>false otherwise
	 */
	public static boolean executeFetchValueAndValidate_SleepRetry(
			Connection conn, long maxSleepTime, long retryInterval,
			String sqlStatement, String expectedValue) throws Exception {
		if (conn == null) {
			throw new Exception(
					"DBUtil Exception(executeFetchValue_SleepRetry) -- connection is null");
		}
		if (sqlStatement == null) {
			throw new Exception(
					"DBUtil Exception(executeFetchValue_SleepRetry) -- sqlStatement is null");
		}
		if (maxSleepTime < 0 || retryInterval < 0) {
			throw new Exception(
					"DBUtil Exception(executeFetchValue_SleepRetry) -- maxSleepTime or retryInterval is less than 0");
		}
		if (retryInterval > maxSleepTime) {
			throw new Exception(
					"DBUtil Exception(executeFetchValue_SleepRetry) -- maxSleepTime is less than retryInterval");
		}
		long currentMillSec = System.currentTimeMillis();
		String result;
		while (currentMillSec + maxSleepTime >= System.currentTimeMillis()) {
			result = executeAndFetchValue(conn, sqlStatement);
			if (result.equalsIgnoreCase(expectedValue)) {
				return true;
			}
			try {
				Thread.sleep(retryInterval);
			} catch (Exception e) {
				// Ignore this exception
			}
		}
		return false;
	}

	/**
	 * Description -- update the Blob field
	 * 
	 * @param conn
	 * @param updateSqlStatement
	 * @param blobFieldValue
	 *            -- byte array input
	 * @throws Exception
	 */
	public static void updateBlobField(Connection conn,
			String updateSqlStatement, byte[] blobFieldValue) throws Exception {
		if (conn == null) {
			throw new Exception(
					"DBUtil Exception(updateBlob) -- connection is null");
		}
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(updateSqlStatement);
			preparedStatement.setBinaryStream(1, new ByteArrayInputStream(
					blobFieldValue), blobFieldValue.length);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new Exception(
					"DBUtil Exception(updateBlob) -- errors when executing the sql statement,the statement is as follows: "
							+ updateSqlStatement, e);
		} finally {
			closeStatement(preparedStatement);
			preparedStatement = null;
		}
	}

	/**
	 * Description -- update the Blob field
	 * 
	 * @param conn
	 * @param updateSqlStatement
	 * @param blobFieldValue
	 *            -- String input
	 * @throws Exception
	 */
	public static void updateBlobField(Connection conn,
			String updateSqlStatement, String blobFieldValue) throws Exception {
		byte[] blobFieldValue_byteArrayFormat = blobFieldValue
				.getBytes("UTF-8");
		updateBlobField(conn, updateSqlStatement,
				blobFieldValue_byteArrayFormat);
	}

	/**
	 * parse and split the script file to single command that can be executed
	 * separately
	 * 
	 * @param sqlScriptContent
	 * @return List -- a list of commands that can be executed separately
	 * @throws Exception
	 */
	private static List<String> splitScriptFileToSingleCommand(
			String scriptFileName) throws Exception {
		List<String> result = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		String line;
		BufferedReader scriptReader = null;
		try {
			scriptReader = new BufferedReader(new FileReader(scriptFileName));
			while ((line = scriptReader.readLine()) != null) {
				line = line.trim();
				// a line starting with "//" or "--" is comment, should be ignored
				if (line.startsWith("//")) {
					continue;
				}
				if (line.startsWith("--")) {
					continue;
				}
				// ignore commit because the script commands will be executed in a batch and committed by a Connection 
				if (line.contains("commit;")) {
					continue;
				}
				// if the line contains "REM" instruction, it is also a comment, should be ignored
				StringTokenizer st = new StringTokenizer(line);
				if (st.hasMoreTokens()) {
					String token = st.nextToken();
					if ("REM".equalsIgnoreCase(token)) {
						continue;
					}
				}
				sql.append(line);
				if (sql.toString().endsWith(";")) {
					// delete the ";" at the end of the command, because batch execution does not need this
					sql.deleteCharAt(sql.length() - 1);
					result.add(sql.toString());
					sql.replace(0, sql.length(), "");
				}
			}
			// Catch any statements not followed by ;
			if (sql.length() > 0) {
				result.add(sql.toString());
			}
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if (scriptReader != null) {
				try {
					scriptReader.close();
					scriptReader = null;
				} catch (Exception e) {
					System.out
							.println("DBUtil Info(splitScriptFileToSingleCommand), failed to close file");
					// Ignore this exception
				}
			}
		}
	}

	/**
	 * Description -- execute a batch of commands at a time
	 * 
	 * @param conn
	 * @param commands
	 * @throws Exception
	 */
	public static void executeBatch(Connection conn, List<String> commands)
			throws Exception {
		if (conn == null) {
			throw new Exception(
					"DBUtil Exception(executeBatch) -- connection is null");
		}
		if (commands == null) {
			throw new Exception(
					"DBUtil Exception(executeBatch) -- commands is null");
		}
		if (commands.size() == 0) {
			System.out
					.println("DBUtil Info.(executeBatch) -- no command to be executed");
			return;
		}
		Statement statement = null;
		// save the auto commit state of the connection
		boolean orginalAutoCommitState = conn.getAutoCommit();
		try {
			conn.setAutoCommit(false);
			statement = conn.createStatement();
			for (String command : commands) {
				// ignore invalid command
				if (command == null || command.trim().length() == 0) {
					continue;
				}
				statement.addBatch(command);
			}
			statement.executeBatch();
			conn.commit();
		} catch (SQLException sqlE) { // SQLException, just throw out
			try {
			     conn.rollback();
			} catch (SQLException sqlRollbackE){
				// Ignore this exception
				System.out
						.println("(Ignore) DBUtil Exception -- error in executeBatch while rollback ");
			}
			throw new Exception(
					"DBUtil Exception(executeBatch) - cause SQLException while executing the script",
					sqlE);
		} catch (Exception e) {
			try {
			     conn.rollback();
			} catch (SQLException sqlRollbackE){
				// Ignore this exception
				System.out
						.println("(Ignore) DBUtil Exception -- error in executeBatch while rollback ");
			}
			throw new Exception(
					"DBUtil Exception(executeBatch) -- error while executing the script",
					e);
		} finally {
			closeStatement(statement);
			statement = null;
			// restore the auto commit state of the connection
			conn.setAutoCommit(orginalAutoCommitState);
		}
	}

	/**
	 * Description -- execute a sql script file
	 * 
	 * @param fileName
	 * @throws Exception
	 */
	public static void executeSqlScript(Connection conn, String fileName)
			throws Exception {
		List<String> commands = splitScriptFileToSingleCommand(fileName);
		executeBatch(conn, commands);
	}

	/**
	 * Description -- read a binary field from the resultset and return it	 
	 * @param rs
	 * @param index
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] readBinaryStream(ResultSet rs, int index) throws Exception {
		InputStream is = rs.getBinaryStream(index);
		return readBinaryStream(is);
	}
	
	/**
	 * Description -- read a binary field from the resultset and return it	 
	 * @param rs
	 * @param columnName
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] readBinaryStream(ResultSet rs, String columnName) throws Exception {
		InputStream is = rs.getBinaryStream(columnName);
		return readBinaryStream(is);
	}
	
	/**
	 * Description -- read contents by bytes from an input stream and return it	 
	 * @param InputStream 
	 * @return byte[]
	 * @throws Exception
	 */
	private static byte[] readBinaryStream(InputStream binaryStream) throws Exception {
		byte[] result = null;
		if (binaryStream != null) {
			try {
				BufferedInputStream bis = new BufferedInputStream(binaryStream);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = bis.read(buffer, 0, 1024)) != -1) {
					baos.write(buffer, 0, count);
				}
				result = baos.toByteArray();
			} finally {
				FileUtils.closeInputStream(binaryStream);
			}			
		}
		return result;
	}

	/**
	 * Description -- read a clob field from the resultset and return it	 
	 * @param rs
	 * @param index
	 * @return char[]
	 * @throws Throwable
	 */
	public static char[] readCharacterStream(ResultSet rs, int index)
			throws Exception {
		Reader ir = rs.getCharacterStream(index);
		return readCharacterStream(ir);
	}
	
	/**
	 * Description -- read a clob field from the resultset and return it	 
	 * @param rs
	 * @param columnName
	 * @return char[]
	 * @throws Throwable
	 */
	public static char[] readCharacterStream(ResultSet rs, String columnName)
			throws Exception {
		Reader ir = rs.getCharacterStream(columnName);
		return readCharacterStream(ir);
	}
	
	/**
	 * Description -- read contents by chars from a reader and return it	  
	 * @param reader
	 * @return char[]
	 * @throws Throwable
	 */
	public static char[] readCharacterStream(Reader reader)
			throws Exception {
		char[] result = null;
		if (reader != null) {
			try {
				BufferedReader br = new BufferedReader(reader);
				CharArrayWriter caw = new CharArrayWriter();
				char[] buffer = new char[1024];
				int count = 0;
				while ((count = br.read(buffer, 0, 1024)) != -1) {
					caw.write(buffer, 0, count);
				}				
				result = caw.toCharArray();
			} finally {
				FileUtils.closeReader(reader);
			}
			
		}
		return result;
	}
	
	private static synchronized boolean isTimestamp(Timestamp timestamp) {
		if (timestamp.toString().indexOf(" 00:00:00") != -1)
			return false;
		return true;
	}
	
	/**
	 * Description -- read a date field from the resultset and return it	 
	 * @param rs
	 * @param index
	 * @return
	 * @throws Throwable
	 */
	public static Object readDate(ResultSet rs, int i) throws Exception {
		Object columnValue = null;
		try {
			Timestamp timestamp = rs.getTimestamp(i);
			timestamp.setNanos(0);
			columnValue=timestamp;
			if (!isTimestamp(timestamp)){
				columnValue = rs.getDate(i);
			}
		} catch (Exception e) {
			columnValue = rs.getDate(i);
		}
		return columnValue;
	}
	
}
