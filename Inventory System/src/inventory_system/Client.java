package inventory_system;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

public class Client extends JFrame implements DBInfo, ActionListener, Serializable {

    JPanel panel;
    JLabel idLabel, nameLabel, priceLabel, quantityLabel;
    JTextField idField, nameField, priceField, quantityFiled;
    JButton insertButton, updateButton, deleteButton ,exitButton,
            selectFirstButton, selectNextButton, selectPreviousButton, selectLastButton;
    JTable table;
    JScrollPane tableScroller;
    DefaultTableModel model;
    JMenuBar  menuBar;
    JMenu menu;
    JMenuItem refreshItem, uploadItem, downloadItem, exitItem;

    AddNewProductDialog addProductDialog;

    public Client() throws IOException {
        createSchema();
        createAndShowGUI();
        updateToAll();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new Client();
                } catch (IOException | NullPointerException e) {
                    System.out.println("Error creating GUI: "+e.getMessage());
                }
            }
        });
    }

    protected void createAndShowGUI() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            JOptionPane.showMessageDialog(this, "Can not create GUI.\n"+ e, "Create GUI Error", JOptionPane.ERROR_MESSAGE);
        }

        panel = new JPanel(null);
        model = new DefaultTableModel();
        table = new JTable(model);
        tableScroller = new JScrollPane(table);
        menuBar = new JMenuBar();
        idLabel = new JLabel("ID");
        nameLabel = new JLabel("Name");
        priceLabel = new JLabel("Price");
        quantityLabel = new JLabel("Quantity");
        idField = new JTextField();
        nameField = new JTextField();
        priceField = new JTextField();
        quantityFiled = new JTextField();
        menu = new JMenu("Menu");
        insertButton = new JButton("Add New", new ImageIcon(this.getClass().getResource("/images/insert.png")));
        updateButton = new JButton("Update", new ImageIcon(this.getClass().getResource("/images/update.png")));
        deleteButton = new JButton("Delete", new ImageIcon(this.getClass().getResource("/images/delete.png")));
        selectFirstButton = new JButton(new ImageIcon(this.getClass().getResource("/images/first.png")));
        selectLastButton = new JButton(new ImageIcon(this.getClass().getResource("/images/last.png")));
        selectNextButton = new JButton(new ImageIcon(this.getClass().getResource("/images/next.png")));
        selectPreviousButton = new JButton(new ImageIcon(this.getClass().getResource("/images/previous.png")));
        downloadItem = new JMenuItem("Download", new ImageIcon(this.getClass().getResource("/images/download.png")));
        uploadItem = new JMenuItem("Upload", new ImageIcon(this.getClass().getResource("/images/upload.png")));
        refreshItem = new JMenuItem("Refresh", new ImageIcon(this.getClass().getResource("/images/refresh-data.png")));
        exitItem = new JMenuItem("Exit", new ImageIcon(this.getClass().getResource("/images/exit.png")));
        exitButton = new JButton("Exit", new ImageIcon(this.getClass().getResource("/images/exit.png")));
        addProductDialog = new AddNewProductDialog(this, model);

        idLabel.setBounds(20, 55, 50, 40);
        idField.setBounds(100, 55, 250, 40);

        nameLabel.setBounds(20, 105, 50, 40);
        nameField.setBounds(100, 105, 250, 40);

        priceLabel.setBounds(20, 155, 50, 40);
        priceField.setBounds(100, 155, 250, 40);

        quantityLabel.setBounds(20, 205, 100, 40);
        quantityFiled.setBounds(100, 205, 250, 40);

        deleteButton.setBounds(80, 275, 130, 40);
        updateButton.setBounds(220, 275, 130, 40);

        tableScroller.setBounds(377, 40, 680, 505);

        insertButton.setBounds(150, 350, 130, 60);

        selectFirstButton.setBounds(470, 560, 50, 50);
        selectNextButton.setBounds(670, 560, 50, 50);
        selectPreviousButton.setBounds(570, 560, 50, 50);
        selectLastButton.setBounds(770, 560, 50, 50);

        exitButton.setBounds(920, 575, 135, 40);

        idLabel.setFont(new Font("Arial", Font.BOLD, 16));
        idField.setFont(new Font("Arial", Font.BOLD, 15));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameField.setFont(new Font("Arial", Font.BOLD, 15));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceField.setFont(new Font("Arial", Font.BOLD, 15));
        quantityLabel.setFont(new Font("Arial", Font.BOLD, 16));
        quantityFiled.setFont(new Font("Arial", Font.BOLD, 15));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 16));
        updateButton.setFont(new Font("Arial", Font.BOLD, 16));
        insertButton.setFont(new Font("Arial", Font.BOLD, 16));
        selectFirstButton.setFont(new Font("Arial", Font.BOLD, 16));
        selectLastButton.setFont(new Font("Arial", Font.BOLD, 16));
        selectNextButton.setFont(new Font("Arial", Font.BOLD, 16));
        selectPreviousButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        menuBar.setFont(new Font("Arial", Font.BOLD, 16));
        menu.setFont(new Font("Arial", Font.BOLD, 16));
        refreshItem.setFont(new Font("Arial", Font.BOLD, 14));
        uploadItem.setFont(new Font("Arial", Font.BOLD, 14));
        downloadItem.setFont(new Font("Arial", Font.BOLD, 14));
        exitItem.setFont(new Font("Arial", Font.BOLD, 14));



        idField.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));
        nameField.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));
        priceField.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));
        quantityFiled.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));

        idField.setEditable(false);
        idField.setBackground(new Color(240, 240, 240));


        table.setColumnSelectionAllowed(false);
        table.getParent().setBackground(Color.white);
        tableScroller.setViewportView(table);


        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Price ($)");
        model.addColumn("Quantity");
        model.addColumn("Total Price");


        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getTableHeader().setReorderingAllowed(false);



        viewProductsInTheTable();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int index = table.getSelectedRow();
                showProduct(index);
            }
        });



        table.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN )
                    showProduct(table.getSelectedRow());
                else if (e.getKeyCode() == KeyEvent.VK_F4)
                    System.exit(0);
                else if (e.getKeyCode() == KeyEvent.VK_F2)
                    uploadProducts();
                else if (e.getKeyCode() == KeyEvent.VK_F3)
                    downloadProducts();
                else if (e.getKeyCode() == KeyEvent.VK_F5)
                    viewProductsInTheTable();

            }

            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) { }

        });



        insertButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        selectFirstButton.addActionListener(this);
        selectLastButton.addActionListener(this);
        selectNextButton.addActionListener(this);
        selectPreviousButton.addActionListener(this);
        exitButton.addActionListener(this);
        refreshItem.addActionListener(this);
        uploadItem.addActionListener(this);
        downloadItem.addActionListener(this);
        exitItem.addActionListener(this);




        panel.add(idLabel);
        panel.add(idField);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(priceLabel);
        panel.add(priceField);
        panel.add(quantityLabel);
        panel.add(quantityFiled);
        panel.add(insertButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(tableScroller);
        panel.add(selectFirstButton);
        panel.add(selectLastButton);
        panel.add(selectNextButton);
        panel.add(selectPreviousButton);
        panel.add(exitButton);

        menuBar.add(menu);
        menu.add(refreshItem);
        menu.add(uploadItem);
        menu.add(downloadItem);
        menu.addSeparator();
        menu.add(exitItem);



        showFirstProduct();

        panel.setPreferredSize(new Dimension(1070, 640));
        panel.setMinimumSize(new Dimension(1070, 640));

        setContentPane(new JPanel(new GridBagLayout()));

        add(panel);
        setJMenuBar(menuBar);
        setTitle("Inventory Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private Connection getConnection()
    {
        Connection con;

        try {
            con = DriverManager.getConnection(DBInfo.DB_NAME_WITH_ENCODING, DBInfo.USER, DBInfo.PASSWORD);
            return con;
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Connection Error: connecting to server failed.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void createSchema(){
        String createSchema = "CREATE DATABASE IF NOT EXISTS `"+DBInfo.Schema+"`";

        try(Connection con = DriverManager.getConnection(DBInfo.URL2, DBInfo.USER, DBInfo.PASSWORD);
            Statement statement = con.createStatement()){

            statement.execute(createSchema);
            createTable();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error creating schema.", "Create Schema Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void createTable(){

        String createTable = "CREATE TABLE IF NOT EXISTS `products` (`id` int(11) NOT NULL PRIMARY KEY," +
                "`name` varchar(200) NOT NULL," +
                "`price` float NOT NULL," +
                "`quantity` int(11) NOT NULL," +
                "`totalPrice` float GENERATED ALWAYS AS (ROUND(`price` * `quantity`, 2)) STORED)";


        String alterTableModifyId = "ALTER TABLE `products`" +
                "MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;";

        try(Connection con = getConnection();
            Statement st = con.createStatement()) {

            st.execute(createTable);
            st.execute(alterTableModifyId);


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error creating table", "Create Table Error", JOptionPane.ERROR_MESSAGE);
        }

    }


    private void viewProductsInTheTable()
    {
        ArrayList<Product> productList= new ArrayList<>();

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
        catch (SQLException | NullPointerException e) {
            JOptionPane.showMessageDialog(this, "Error creating 'SELECT ALL FROM products' statement", "Refresh Table Error", JOptionPane.ERROR_MESSAGE);
        }

        model.setRowCount(0);

        Object[] row = new Object[5];


        for (Product product : productList) {
            row[0] = product.getId();
            row[1] = product.getName();
            row[2] = product.getPrice();
            row[3] = product.getQuantity();
            row[4] = product.getTotalPrice();


            model.addRow(row);
        }

        showFirstProduct();
    }



    private boolean checkInputs()
    {
        if(nameField.getText().isEmpty() || priceField.getText().isEmpty() || quantityFiled.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(this,
                    "Product information are not updated because one or more fields are empty",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else {
            try {
                Float.parseFloat(priceField.getText());
                return true;
            }
            catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please check the data and try again.", "Update Product Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }




    private void addNewProduct()
    {
        addProductDialog.show();
    }


    private void updateProduct()
    {
        if (checkInputs() && idField.getText() != null) {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                float price = Float.parseFloat(priceField.getText());
                int quantity = Integer.parseInt(quantityFiled.getText());

                Product product = new Product(id, name, price, quantity);
                sendToServer("UPDATE", product);

                viewProductsInTheTable();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please check the data and try again.", "Update Product Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private ArrayList<Product> getProductList()
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
        catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error creating 'SELECT ALL FROM products' statement", "Get Product List Error", JOptionPane.ERROR_MESSAGE);
        }

        return productList;
    }


    private void showProduct(int index)
    {
        idField.setText(Integer.toString(getProductList().get(index).getId()));
        nameField.setText(getProductList().get(index).getName());
        priceField.setText(Float.toString(getProductList().get(index).getPrice()));
        quantityFiled.setText(Integer.toString(getProductList().get(index).getQuantity()));

    }



    private void deleteProduct()
    {
        if (table.getSelectedRow() == - 1){
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "Error", JOptionPane.WARNING_MESSAGE);
        }else {

            try{
                int id = Integer.parseInt(idField.getText());
                Product product = new Product(id, null, 0, 0);
                sendToServer("DELETE", product);
                viewProductsInTheTable();
                showFirstProduct();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid product ID.", "Delete Product Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void showFirstProduct()
    {
        if(table.getRowCount() != 0) {
            table.setRowSelectionInterval(0, 0);
            showProduct(0);
        }
    }


    private void showLastProduct()
    {
        if(table.getRowCount() != 0) {
            table.setRowSelectionInterval(table.getRowCount()-1, table.getRowCount()-1);
            showProduct(table.getRowCount()-1);
        }
    }


    private void showNextProdut()
    {
        if(table.getSelectedRow() < table.getRowCount()-1) {
            int currentSelectedRow = table.getSelectedRow()+1;
            table.setRowSelectionInterval(currentSelectedRow, currentSelectedRow);
            showProduct(currentSelectedRow);
        }
    }


    private void showPreviousProduct()
    {
        if(table.getSelectedRow() > 0) {
            int currentSelectedRow = table.getSelectedRow()-1;
            table.setRowSelectionInterval(currentSelectedRow, currentSelectedRow);
            showProduct(currentSelectedRow);
        }
    }



    private void downloadProducts() {
        JFileChooser folder = new JFileChooser(new File(System.getProperty("user.home")));
        folder.setSelectedFile(new File("Products List.csv"));
        int result = folder.showSaveDialog(this);


        if (result == JFileChooser.APPROVE_OPTION){
            try (FileWriter fileWriter = new FileWriter(folder.getSelectedFile())) {

                ArrayList<Product> productList = getProductList();

                fileWriter.write("ID,Name,Price,Quantity,Total Price\n");
                for (Product value : productList) {
                    String product = value.getId() + "," +
                            value.getName() + "," +
                            value.getPrice() + "," +
                            value.getQuantity() + "," +
                            value.getTotalPrice();
                    fileWriter.write(product+"\n");
                    fileWriter.flush();
                }


            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Could not create file.", "Download Products Error", JOptionPane.ERROR_MESSAGE);
            }

            JOptionPane.showMessageDialog(this, "Your products list has been Downloaded");
        }

        else if(result == JFileChooser.CANCEL_OPTION){
            JOptionPane.showMessageDialog(this, "Download process has been canceled","Message",JOptionPane.ERROR_MESSAGE);
        }

    }

    private void uploadProducts() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Select a CSV File", "csv"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try (Scanner scanner = new Scanner(new FileReader(selectedFile));
                 Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement("INSERT INTO products(name, price, quantity) VALUES (?, ?, ?)")) {


                String header = scanner.nextLine();
                String[] expectedHeader = {"ID", "Name", "Price", "Quantity", "Total Price"};
                String[] headerParts = header.split(",");

                if (!Arrays.equals(headerParts, expectedHeader)) {
                    JOptionPane.showMessageDialog(this, "Invalid file header format.", "Upload Products Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                while (scanner.hasNextLine()) {
                    String[] columns = scanner.nextLine().split(",");
                    if (columns.length >= 4) {
                        ps.setString(1, columns[1]);
                        ps.setString(2, columns[2]);
                        ps.setString(3, columns[3]);
                        ps.executeUpdate();
                    }
                }

                viewProductsInTheTable();
                sendToServer("UPLOAD",null);
                showFirstProduct();

            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "File not found: " + selectedFile.getAbsolutePath(), "Upload Products Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Upload Products Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (result == JFileChooser.CANCEL_OPTION) {
            JOptionPane.showMessageDialog(this, "Import process has been canceled.", "Message", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void sendToServer(String action, Product product) {
        try (Socket socket = new Socket("localhost", 9000);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject(action);
            output.writeObject(product);

            String response = (String) input.readObject();
            JOptionPane.showMessageDialog(this, response);

        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Can not send to server.\n"+e.getMessage(),"Send to Server Error",JOptionPane.ERROR_MESSAGE);
        }
    }




    private void updateToAll() {
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 9000);
                 ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {


                while (true) {
                    String message = (String) input.readObject();
                    if (message.equals("REFRESH")) {
                        SwingUtilities.invokeLater(()-> viewProductsInTheTable());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Connection lost. Please restart the application.", "Update All Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == insertButton){
            addNewProduct();
        }

        else if (e.getSource() == updateButton){
            updateProduct();
        }

        else if (e.getSource() == deleteButton){
            deleteProduct();
        }

        else if (e.getSource() == selectFirstButton){
            showFirstProduct();
        }

        else if (e.getSource() == selectLastButton){
            showLastProduct();
        }

        else if (e.getSource() == selectNextButton){
            showNextProdut();
        }

        else if (e.getSource() == selectPreviousButton){
            showPreviousProduct();
        }

        else if (e.getSource() == downloadItem){
            downloadProducts();
        }

        else if (e.getSource() == uploadItem){
            uploadProducts();
        }

        else if (e.getSource() == refreshItem){
            viewProductsInTheTable();
        }

        else if (e.getSource() == exitButton || e.getSource() == exitItem){
            System.exit(0);
        }

    }


}