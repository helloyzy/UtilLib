package tools.sleepretry;

import java.sql.Connection;

import tools.db.DBUtil;

public class DBSleepRetry extends SleepRetry {
	
	private Connection conn;
	
	private String statement;

	public DBSleepRetry(Connection conn, String statement, String expected) {
		super(expected);
		this.conn = conn;
		this.statement = statement;
	}
	
	public DBSleepRetry(Connection conn, long interval, long duration, String statement, String expected) {
		super(interval, duration, expected);
		this.conn = conn;
		this.statement = statement;
	}
	
	@Override
	protected Object doFunction() throws Exception {
		return DBUtil.executeAndFetchValue(conn, statement);
	}

}
