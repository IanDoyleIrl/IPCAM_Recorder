/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 06/01/2013
 * Time: 13:31
 * To change this template use File | Settings | File Templates.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionPool {

    private static DBConnectionPool conn;
    private static Connection dbConnection;

    public static Connection getConnection() throws SQLException, ClassNotFoundException{
        if (conn == null){
            new DBConnectionPool();
        }
        return conn.dbConnection;
    }

    private DBConnectionPool() throws SQLException, ClassNotFoundException{
        Class.forName("com.mysql.jdbc.Driver");
        // Setup the connection with the DB
        dbConnection = DriverManager
                .getConnection("jdbc:mysql://127.0.0.1:3306/test");
    }

}
