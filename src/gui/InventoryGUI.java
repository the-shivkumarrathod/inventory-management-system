package gui;

import db.DBConnection;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InventoryGUI {

    public InventoryGUI(String username) {

        JFrame frame = new JFrame("Inventory Management System");
        frame.setSize(750,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // WELCOME PANEL
        JLabel welcomeLabel = new JLabel("Welcome " + username);
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> {
            frame.dispose();
            LoginGUI.main(null);
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // FIX 1 → welcome panel must be NORTH
        frame.add(topPanel, BorderLayout.NORTH);

        // INPUT PANEL
        JPanel panel = new JPanel(new GridLayout(5,2));

        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField();

        JLabel qtyLabel = new JLabel("Quantity:");
        JTextField qtyField = new JTextField();

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField();

        JButton addButton = new JButton("Add Item");
        JButton updateButton = new JButton("Update Item");
        JButton deleteButton = new JButton("Delete Item");
        JButton loadButton = new JButton("Load Items");
        JButton lowStockButton = new JButton("Low Stock Alert");

        panel.add(nameLabel);
        panel.add(nameField);

        panel.add(qtyLabel);
        panel.add(qtyField);

        panel.add(priceLabel);
        panel.add(priceField);

        panel.add(addButton);
        panel.add(updateButton);

        panel.add(deleteButton);
        panel.add(loadButton);

        // TABLE MODEL
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        
     // SEARCH FUNCTIONALITY
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Quantity");
        model.addColumn("Price");

        // LOW STOCK HIGHLIGHT
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(
                    JTable table,Object value,boolean isSelected,
                    boolean hasFocus,int row,int column){

                Component c = super.getTableCellRendererComponent(
                        table,value,isSelected,hasFocus,row,column);

                int quantity = (int)table.getModel().getValueAt(row,2);

                if(quantity < 5){
                    c.setBackground(Color.PINK);
                }else{
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        });

        // AUTO FILL TEXTFIELDS WHEN ROW CLICKED
        table.getSelectionModel().addListSelectionListener(e -> {

            int row = table.getSelectedRow();

            if(row >= 0){
                nameField.setText(model.getValueAt(row,1).toString());
                qtyField.setText(model.getValueAt(row,2).toString());
                priceField.setText(model.getValueAt(row,3).toString());
            }

        });

     // SEARCH BAR
        JPanel searchPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Search: ");
        JTextField searchField = new JTextField();

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }

            private void search() {
                String text = searchField.getText();
                if (text.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(tablePanel, BorderLayout.CENTER);

        // FIX 2 → create bottom panel for input + alert button
        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.add(panel, BorderLayout.CENTER);
        bottomPanel.add(lowStockButton, BorderLayout.SOUTH);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // LOAD ITEMS
        loadButton.addActionListener(e -> {

            try{

                model.setRowCount(0);

                Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();

                ResultSet rs = st.executeQuery("SELECT * FROM items");

                while(rs.next()){

                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getDouble("price")
                    });

                }

            }catch(Exception ex){
                ex.printStackTrace();
            }

        });

        // ADD ITEM
        addButton.addActionListener(e -> {

            try{

                String name = nameField.getText();
                int quantity = Integer.parseInt(qtyField.getText());
                double price = Double.parseDouble(priceField.getText());

                Connection conn = DBConnection.getConnection();

                String checkSQL = "SELECT * FROM items WHERE name=?";
                PreparedStatement check = conn.prepareStatement(checkSQL);
                check.setString(1,name);

                ResultSet rs = check.executeQuery();

                if(rs.next()){
                    JOptionPane.showMessageDialog(frame,"Item already exists!");
                    return;
                }

                String sql = "INSERT INTO items(name,quantity,price) VALUES(?,?,?)";

                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1,name);
                ps.setInt(2,quantity);
                ps.setDouble(3,price);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame,"Item Added");

                nameField.setText("");
                qtyField.setText("");
                priceField.setText("");

                loadButton.doClick();

            }catch(Exception ex){
                ex.printStackTrace();
            }

        });

        // UPDATE ITEM
        updateButton.addActionListener(e -> {

            int row = table.getSelectedRow();

            if(row == -1){
                JOptionPane.showMessageDialog(frame,"Select item first");
                return;
            }

            try{

                int id = (int)model.getValueAt(row,0);

                Connection conn = DBConnection.getConnection();

                String sql = "UPDATE items SET name=?,quantity=?,price=? WHERE id=?";

                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1,nameField.getText());
                ps.setInt(2,Integer.parseInt(qtyField.getText()));
                ps.setDouble(3,Double.parseDouble(priceField.getText()));
                ps.setInt(4,id);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame,"Item Updated");

                nameField.setText("");
                qtyField.setText("");
                priceField.setText("");

                loadButton.doClick();

            }catch(Exception ex){
                ex.printStackTrace();
            }

        });

        // DELETE ITEM
        deleteButton.addActionListener(e -> {

            int row = table.getSelectedRow();

            if(row == -1){
                JOptionPane.showMessageDialog(frame,"Select item first");
                return;
            }

            try{

                int id = (int)model.getValueAt(row,0);

                Connection conn = DBConnection.getConnection();

                String sql = "DELETE FROM items WHERE id=?";

                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setInt(1,id);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame,"Item Deleted");

                nameField.setText("");
                qtyField.setText("");
                priceField.setText("");

                loadButton.doClick();

            }catch(Exception ex){
                ex.printStackTrace();
            }

        });

        // LOW STOCK ALERT
        lowStockButton.addActionListener(e -> {

            try{

                Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();

                ResultSet rs = st.executeQuery(
                        "SELECT * FROM items WHERE quantity < 5");

                String msg = "Low Stock Items\n\n";

                while(rs.next()){

                    msg += rs.getString("name")
                            +"  Qty: "
                            +rs.getInt("quantity")
                            +"\n";

                }

                JOptionPane.showMessageDialog(frame,msg);

            }catch(Exception ex){
                ex.printStackTrace();
            }

        });

        frame.setVisible(true);
    }
}