package com.dkg.taskBoard;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory {
    private String dbDriverClass;
    private String dbUser;
    private String dbConnectionString;
    private String dbPassword;

    public ConnectionFactory(String dbDriverClass, String dbConnectionString, String dbUser, String dbPassword) {
		this.dbDriverClass = dbDriverClass;
		this.dbConnectionString = dbConnectionString;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;    	
    }

	public Connection getConnection() throws SQLException, ClassNotFoundException {
		Connection conn;

		Class.forName(dbDriverClass);
		conn = DriverManager.getConnection(dbConnectionString, dbUser, dbPassword);

		return conn;
	}
}
