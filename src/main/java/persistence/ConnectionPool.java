package persistence;

import service.ConfigService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPool {

    private static ConnectionPool instance;
    private static Connection con;

    private ConnectionPool() throws ClassNotFoundException, IOException, SQLException {
        // load oracle driver
        Class.forName("oracle.jdbc.driver.OracleDriver");
        // load parameter from config
        String url = ConfigService.readFromConfig("url");
        String user = ConfigService.readFromConfig("user");
        String password = ConfigService.readFromConfig("password");
        // generate connection
        con = DriverManager.getConnection(url, user, password);
    }

    public static ConnectionPool getInstance() throws Exception {
        if(ConnectionPool.instance == null) {
            ConnectionPool.instance = new ConnectionPool();
        } else {
            throw new Exception("Connection is used!");
        }
        return ConnectionPool.instance;
    }

    public Connection getConnection() {
        return con;
    }

    public void releaseConnection() throws SQLException {
        con.close();
        ConnectionPool.instance = null;
    }
}