package com.example.sarah.alkosh.model;

import java.util.List;

/**
 * Created by Sarah on 11/30/2017.
 */

public class OrderRequest {

    private String phone,address,total,name,status;
    private List<Order> items;

    public OrderRequest(String phone, String address, String total, String name, List<Order> items) {
        this.phone = phone;
        this.address = address;
        this.total = total;
        this.name = name;
        this.items = items;
        this.status="0";
    }

    public OrderRequest() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Order> getItems() {
        return items;
    }

    public void setItems(List<Order> items) {
        this.items = items;
    }
}
