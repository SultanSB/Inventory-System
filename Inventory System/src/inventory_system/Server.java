package inventory_system;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;


public class  Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            System.out.println("Server is listening on port 9000...");
            while(true) {
                Socket socket = serverSocket.accept();
                new myThread(socket).start();
            }
        } catch (IOException e) {
            System.out.println("Server Error: "+ e);
        }
    }
}

class myThread extends Thread {
    private static ArrayList<myThread> threads = new ArrayList<>();
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;


    public myThread(Socket socket) {
        this.socket = socket;
        threads.add(this);
    }

    @Override
    public void run() {
        try  {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());


            String action = (String) input.readObject();
            Product product = (Product) input.readObject();

            switch (action) {
                case "ADD":
                    addProduct(product);
                    output.writeObject("Product added successfully.");
                    break;
                case "UPDATE":
                    updateProduct(product);
                    output.writeObject("Product updated successfully.");
                    break;
                case "DELETE":
                    deleteProduct(product.getId());
                    output.writeObject("Product deleted successfully.");
                    break;
                case "UPLOAD":
                    output.writeObject("Your products have been uploaded successfully!");
                    break;
                default:
                    output.writeObject("Unknown action.");
            }

            updateAll();



        }catch (IOException | ClassNotFoundException | SQLException e) {
            System.out.println("Client has been disconnected");
        }

    }


    private synchronized void updateAll()  {
        for (myThread clientHandler : threads) {
            try {
                clientHandler.output.writeObject("REFRESH");
            } catch (IOException e) {
                System.out.print("");
            }

        }
    }



    private synchronized void addProduct(Product product) throws SQLException {
        try (Connection con = DriverManager.getConnection(DBInfo.DB_NAME_WITH_ENCODING, DBInfo.USER, DBInfo.PASSWORD);
             PreparedStatement ps = con.prepareStatement("INSERT INTO products(name, price, quantity) VALUES (?, ?, ?)")) {
            ps.setString(1, product.getName());
            ps.setFloat(2, product.getPrice());
            ps.setInt(3, product.getQuantity());
            ps.executeUpdate();
        }
    }

    private synchronized void updateProduct(Product product) throws SQLException {
        try (Connection con = DriverManager.getConnection(DBInfo.DB_NAME_WITH_ENCODING, DBInfo.USER, DBInfo.PASSWORD);
             PreparedStatement ps = con.prepareStatement("UPDATE products SET name = ?, price = ?, quantity = ? WHERE id = ?")) {
            ps.setString(1, product.getName());
            ps.setFloat(2, product.getPrice());
            ps.setInt(3, product.getQuantity());
            ps.setInt(4, product.getId());
            ps.executeUpdate();
        }
    }

    private synchronized void deleteProduct(int productId) throws SQLException {
        try (Connection con = DriverManager.getConnection(DBInfo.DB_NAME_WITH_ENCODING, DBInfo.USER, DBInfo.PASSWORD);
             PreparedStatement ps = con.prepareStatement("DELETE FROM products WHERE id = ?")) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }


}

