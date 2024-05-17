package com.example.task91p.model;

import com.google.android.gms.maps.model.LatLng;

public class Item {
    // Declare item fields
    private long item_id;
    private String name;
    private String phone;
    private String description;
    private String date;
    private LatLng location;

    public Item(long item_id, String name, String phone, String description, String date, LatLng location) {
        this.item_id = item_id;
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.date = date;
        this.location = location;
    }

    public Item(String name, String phone, String description, String date, LatLng location) {
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.date = date;
        this.location = location;
    }

    public Item() { }

    public long getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
