package gui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginGUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Inventory Login");
        frame.setSize(350,200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3,2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        frame.add(userLabel);
        frame.add(userField);
        frame.add(passLabel);
        frame.add(passField);
        frame.add(new JLabel(""));
        frame.add(loginButton);

        Runnable loginAction = () -> {

            String username = userField.getText();
            String password = new String(passField.getPassword());

            try {

                Connection conn = DBConnection.getConnection();

                String sql = "SELECT * FROM users WHERE username=? AND password=?";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();

                if(rs.next()){

                    JOptionPane.showMessageDialog(frame,"Login Successful");

                    frame.dispose();

                    new InventoryGUI(username);

                }else{

                    JOptionPane.showMessageDialog(frame,"Invalid Username or Password");

                }

            }catch(Exception ex){
                ex.printStackTrace();
            }
        };

        loginButton.addActionListener(e -> loginAction.run());

        passField.addActionListener(e -> loginAction.run());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}