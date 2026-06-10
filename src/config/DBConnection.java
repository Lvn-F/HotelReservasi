package config;

import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/dbreservasi_hotel";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection()
            throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
    public static void main(String[] args) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con != null) {
            System.out.println("Connected!");
            con.close();
        }
    }
}
