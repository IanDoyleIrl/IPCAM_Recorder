import dbAccess.EventType;
import org.json.simple.JSONObject;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 06/01/2013
 * Time: 13:31
 * To change this template use File | Settings | File Templates.
 */

public class ConnectionUtil {

    private static String DATE_EVENT_QUERY = "SELECT * FROM zm.Events"+
                                        "where Cause=? "+
                                        "and StartTime >= ?"+
                                        "and EndTime <= ?;" ;

    private static final String RECORDING_INSERT = "INSERT INTO test.Recording (TIMESTAMP, RECORDED_IMAGE, COMMENTS) VALUES (?, ?, ?);";

    private static final String EVENTS_INSERT = "INSERT INTO test.Events (TIMESTAMP, RECORDED_IMAGE, COMMENTS) VALUES (?, ?, ?);";

    private static final String ALL_IMAGES_SELECT = "SELECT RECORDED_IMAGE FROM test.Recording;";


    public static JSONObject TestConnection(){
        try{
            Connection conn = DBConnectionPool.getConnection();
            Statement statement = conn.createStatement();
            DatabaseMetaData meta = conn.getMetaData();
            JSONObject result = new JSONObject();
            result.put("connectionWorking", true);
            result.put("metaData", meta.toString());
            return result;
        }
        catch (SQLException e){
            System.out.println(e);
            return null;
        }
        catch (ClassNotFoundException e){
            System.out.println(e);
            return null;
        }
    }

    public static JSONObject getEventsBetweenDates(java.sql.Date startDate, java.sql.Date endDate, EventType type){
        try{
            Connection conn = DBConnectionPool.getConnection();
            PreparedStatement statement = conn.prepareStatement(DATE_EVENT_QUERY);
            statement.setString(1, type.toString());
            statement.setDate(2, startDate);
            statement.setDate(3, endDate);
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                System.out.print(rs.getInt("EventId"));
            }
            JSONObject result = new JSONObject();
            return result;
        }
        catch (SQLException e){
            System.out.println(e);
            return null;
        }
        catch (ClassNotFoundException e){
            System.out.println(e);
            return null;
        }
    }

    public static void insertRecordingIntoDB(long ts, byte[] image, String comments){
        Connection conn = null;
        try {
            conn = DBConnectionPool.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(ConnectionUtil.RECORDING_INSERT);
            pstmt.setLong(1, ts);
            pstmt.setBytes(2, image);
            pstmt.setString(3, comments);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void insertEventIntoDB(long ts, byte[] image, String comments){
        Connection conn = null;
        try {
            conn = DBConnectionPool.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(ConnectionUtil.EVENTS_INSERT);
            pstmt.setLong(1, ts);
            pstmt.setBytes(2, image);
            pstmt.setString(3, comments);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static ArrayList<byte[]> getAllImagesFromDatabaseAsList(){
        Connection conn = null;
        ArrayList<byte[]> result = new ArrayList<byte[]>();
        try {
            conn = DBConnectionPool.getConnection();
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(ConnectionUtil.ALL_IMAGES_SELECT);
            while (rs.next()){
                result.add(rs.getBytes(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return result;
    }

}
