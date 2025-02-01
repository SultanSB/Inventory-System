package inventory_system;

import java.io.Serializable;

public class Product implements Serializable {

    private int id;
    private String name;
    private float price;
    private int quantity;
    private float totalPrice;


    public Product(int id, String name, float price, int quantity, float totalPrice)
    {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;

    }
    public Product(int id, String name, float price, int quantity)
    {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;

    }


    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public float getPrice()
    {
        return price;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public String toString(){

        return id+","+name+","+price+","+quantity+","+totalPrice;
    }

}