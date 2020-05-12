package com.soushetty.grocerylist.model;

public class GroceryList {
    private int id;
    private String itemname;
    private String quantity;
    private String color;
    private String size;
    private String brand;
    private String date_item_added;

    public GroceryList(){

    }

    public GroceryList(String itemname, String quantity, String color, String size, String brand, String date_item_added) {
        this.itemname = itemname;
        this.quantity = quantity;
        this.color = color;
        this.size = size;
        this.brand = brand;
        this.date_item_added = date_item_added;
    }

    public GroceryList(int id, String itemname, String quantity, String color, String size, String brand, String date_item_added) {
        this.id = id;
        this.itemname = itemname;
        this.quantity = quantity;
        this.color = color;
        this.size = size;
        this.brand = brand;
        this.date_item_added=date_item_added;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDate_item_added() {
        return date_item_added;
    }

    public void setDate_item_added(String date_item_added) {
        this.date_item_added = date_item_added;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
