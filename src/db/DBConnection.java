package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        try {

            String url = "jdbc:mysql://localhost:3306/inventory_db";
            String user = "root";
            String password = "YOUR_PASSWORD";   // change if your MySQL password is different

            Connection conn = DriverManager.getConnection(url, user, password);

            System.out.println("Database Connected");

            return conn;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}