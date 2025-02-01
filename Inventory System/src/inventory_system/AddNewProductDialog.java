package inventory_system;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


public class AddNewProductDialog implements DBInfo, ActionListener, Serializable {


    JDialog dialog;
    JLabel nameLabel, priceLabel, quantityLabel;
    JTextField nameField, priceField, quantityField;
    JButton addButton;
    DefaultTableModel model;
    JFrame frame;


    public AddNewProductDialog(JFrame frame, DefaultTableModel model)
    {
        dialog = new JDialog(frame);
        nameLabel = new JLabel("Name");
        priceLabel = new JLabel("Price ( $ )");
        quantityLabel = new JLabel("Quantity");
        nameField = new JTextField();
        priceField = new JTextField();
        quantityField = new JTextField();
        addButton = new JButton("Add Product", new ImageIcon(this.getClass().getResource("/images/insert.png")));
        this.model = model;
        this.frame = frame;


        nameLabel.setBounds(150, 30, 80, 40);
        nameField.setBounds(150, 70, 270, 40);

        priceLabel.setBounds(150, 120, 80, 40);
        priceField.setBounds(150, 160, 270, 40);

        quantityLabel.setBounds(150,210,80,40);
        quantityField.setBounds(150,250,270,40);


        addButton.setBounds(2, 410, 500, 60);

        addButton.setForeground(Color.white);
        addButton.setBackground(Color.black);
        addButton.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameField.setFont(new Font("Arial", Font.BOLD, 15));
        nameField.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceField.setFont(new Font("Arial", Font.BOLD, 15));
        priceField.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));
        quantityLabel.setFont(new Font("Arial", Font.BOLD, 16));
        quantityField.setFont(new Font("Arial", Font.BOLD, 15));
        quantityField.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));

        dialog.add(nameLabel);
        dialog.add(priceLabel);
        dialog.add(quantityLabel);
        dialog.add(nameField);
        dialog.add(priceField);
        dialog.add(quantityField);
        dialog.add(addButton);

        addButton.addActionListener(this);

        dialog.setLayout(null);
        dialog.setSize(520, 520);
        dialog.setTitle("Add New Product");
        dialog.setModal(false);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(frame);
    }


    public void show() {
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("");
        dialog.setVisible(true);
    }


    private Connection getConnection()
    {
        Connection con;

        try {
            con = DriverManager.getConnection(DBInfo.DB_NAME_WITH_ENCODING, DBInfo.USER, DBInfo.PASSWORD);
            return con;
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Connection Error: connecting to server failed.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }


    private void viewProductsInTheTable()
    {
        ArrayList<Product> productList = new ArrayList<>();

        Connection con = getConnection();
        String query = "SELECT * FROM products";

        Statement st;
        ResultSet rs;

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);

            Product product;

            while(rs.next())
            {
                product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        Float.parseFloat(rs.getString("price")),
                        Integer.parseInt(rs.getString("quantity")),
                        Float.parseFloat(rs.getString("totalPrice"))
                );

                productList.add(product);
            }

            con.close();
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Error creating 'SELECT ALL FROM products' statement", "Refresh Table Error", JOptionPane.ERROR_MESSAGE);
        }

        model.setRowCount(0);

        Object[] row = new Object[5];

        for(int i = 0; i<productList.size(); i++)
        {
            row[0] = productList.get(i).getId();
            row[1] = productList.get(i).getName();
            row[2] = productList.get(i).getPrice();
            row[3] = productList.get(i).getQuantity();
            row[4] = productList.get(i).getTotalPrice();

            model.addRow(row);
        }

    }


    private boolean checkInputs() {
        if(nameField.getText().isEmpty() && priceField.getText().isEmpty() && quantityField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Name, Price and Quantity fields cannot be empty !", "", JOptionPane.PLAIN_MESSAGE);
            return false;
        }

        else if(nameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Please enter product name", "", JOptionPane.PLAIN_MESSAGE);
            return false;
        }

        else if(priceField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Please enter product price", "", JOptionPane.PLAIN_MESSAGE);
            return false;
        }

        try {
            Float.parseFloat(priceField.getText());
        }
        catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(dialog,
                    "Price should be decimal number\nExample:\n40.5 or 40",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(quantityField.getText());
            return true;
        }
        catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(dialog,
                    "Quantity should be Integer number\nExample:\n5 or 40",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }


    }


    private void insertProduct()
    {
        if(checkInputs()) {

            Product product = new Product(0,nameField.getText(),Float.parseFloat(priceField.getText()), Integer.parseInt(quantityField.getText()));
            sendToServer("ADD", product);
            nameField.setText("");
            priceField.setText("");
            quantityField.setText("");
            viewProductsInTheTable();

        }

    }

    private void sendToServer(String action, Product product) {
        try (Socket socket = new Socket("localhost", 9000);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject(action);
            output.writeObject(product);

            String response = (String) input.readObject();
            JOptionPane.showMessageDialog(dialog, response);

        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(dialog, "Can not send to server.\n"+e.getMessage(),"Send to Server Error",JOptionPane.ERROR_MESSAGE);
        }
    }



    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == addButton)
            insertProduct();
    }

}