package com.framework.lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private static Connection con;
    /**
     * Private constructor for the DatabaseConnection class.
     * This constructor is responsible for establishing a connection to the Oracle database.
     * The environment and credentials used for the connection are determined based on system properties and properties file.
     * If an exception occurs during the connection process, the connection is closed and the exception is logged and rethrown.
     *
     * @throws Exception if any error occurs during the creation of the DatabaseConnection instance or the connection
     * @author Satyajit
     */
    private DatabaseConnection() throws Exception {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String env = System.getProperty("env.environment") != null ? System.getProperty("env.environment") : Util.getInstance().prop.getProperty("env.environment");
            if (env.equalsIgnoreCase("freduat")) {
                env = "freduat2";
            }
            if (env.equalsIgnoreCase("ffeuat")) {
                con = DriverManager.getConnection("jdbc:oracle:thin:@" + env + ".oracle.eagle.org:1521:" + env,
                		Util.getInstance().prop.getProperty("env.dbusername"), Util.getInstance().prop.getProperty("env.dbusername").toUpperCase() + "2018");
            } else {
                con = DriverManager.getConnection("jdbc:oracle:thin:@" + env + ".oracle.eagle.org:1521:" + env,
                		Util.getInstance().prop.getProperty("env.dbusername"), env + "2020");
            }
            //stmt = con.createStatement();
        } catch (Exception e) {
            closeConnection();
            ExtentReportMGR.getInstance().getExtentTest().error("Database Connection Error: " + e.getMessage());
        }
    }
    /**
     * This method is used to get an instance of the DatabaseConnection class.
     * It follows the Singleton Design Pattern, which ensures that only one instance of the class is created.
     * The instance is created only when this method is called for the first time.
     * 
     * @return instance of the DatabaseConnection class
     * @throws Exception if any error occurs during the creation of the DatabaseConnection instance
     * @author Satyajit
     */
	public static DatabaseConnection getInstance() throws Exception {
		synchronized (Connection.class) {
			if (instance == null) {
					instance = new DatabaseConnection();
			}
			return instance;
		}
	}
	/**
	 * This method is used to get a connection to the database.
	 * If the connection is null, it calls the getInstance method of the DatabaseConnection class to create a new connection.
	 *
	 * @return Connection to the database
	 * @throws Exception if any error occurs during the creation of the DatabaseConnection instance or the connection
	 * @author Satyajit
	 */
    public Connection getConnection() throws Exception {
    	if(con == null) {
    		getInstance();
    	}
        return con;
    }
	public static void closeConnection() {
		try {		
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			// Log the exception or handle it as necessary
		}
	}

	public static void closeResources(ResultSet rs, Statement stmt) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}