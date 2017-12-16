package com.example.sarah.alkosh.model;

/**
 * Created by Sarah on 11/30/2017.
 */

public class Order {

    private String ItemID,ItemName,Quantity,Price,Discount,Supplier;
    public Order() {
    }

    public Order(String itemID, String itemName, String quantity, String price, String discount, String supplier) {
        ItemID = itemID;
        ItemName = itemName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        Supplier = supplier;
    }

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getSupplier() {
        return Supplier;
    }

    public void setSupplier(String supplier) {
        Supplier = supplier;
    }
}
