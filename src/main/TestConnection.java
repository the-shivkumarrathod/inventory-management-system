package main;

import java.sql.Connection;
import db.DBConnection;

public class TestConnection {

    public static void main(String[] args) {

        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            System.out.println("Connection Successful");
        } else {
            System.out.println("Connection Failed");
        }
    }
}
