package tools.db;

public class DBConnectionProperties {
	
	enum Type {
		Oracle, DB2
	}
	
	public static String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
	
	public static String ORACLE_DEF_PORT = "1521";
	
	public static String DB2_DRIVER = "com.ibm.db2.jcc.DB2Driver";
	
	public static String DB2_DEF_PORT = "50000";
	
	private String driverName;	

	private String dbURL;
	
	private String dbUserName;
	
	private String dbPwd;
	
	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDbURL() {
		return dbURL;
	}

	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	}
	
	public void setOracleDbURL(String ipAddr, String port, String serviceName) {
		String protocal = "jdbc:oracle:thin:@";
		String portNr = port;
		if (portNr == null) {
			portNr = ORACLE_DEF_PORT;
		}
		setDbURL(Type.Oracle, protocal, ipAddr, portNr, serviceName);
	}
	
	public void setDB2DbURL(String ipAddr, String port, String serviceName) {
		String protocal = "jdbc:db2://";
		String portNr = port;
		if (portNr == null) {
			portNr = DB2_DEF_PORT;
		}
		setDbURL(Type.DB2, protocal, ipAddr, portNr, serviceName);
	}
	
	private void setDbURL(Type type, String protocal, String ipAddr, String port, String serviceName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(protocal);
		buffer.append(ipAddr);
		buffer.append(":");
		buffer.append(port);
		switch (type) {
		case Oracle:
			buffer.append(":");
			break;
		case DB2:
			buffer.append("/");
			break;
		default:
			break;
		}		
		buffer.append(serviceName);
		this.dbURL = buffer.toString();
	}

	public String getDbUserName() {
		return dbUserName;
	}

	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}

	public String getDbPwd() {
		return dbPwd;
	}

	public void setDbPwd(String dbPwd) {
		this.dbPwd = dbPwd;
	}

}
