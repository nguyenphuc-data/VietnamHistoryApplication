package com.example.vietnamhistoryapplication.models;

import java.io.Serializable;

public class Event implements Serializable {
    private String name = "";
    private int year;
    private String desc = "";
    private String zone = "";
    private int order; // Trường mới: Thứ tự sự kiện trong dòng thời gian

    // Constructor rỗng cho Firestore
    public Event() {}

    // Constructor đầy đủ
    public Event(String name, int year, String desc, String zone, int order) {
        this.name = name;
        this.year = year;
        this.desc = desc;
        this.zone = zone;
        this.order = order;
    }

    // Getters
    public String getName() { return name; }
    public int getYear() { return year; }
    public String getDesc() { return desc; }
    public String getZone() { return zone; }

    // Getter cho trường mới 'order' (MANDATORY cho Firestore)
    public int getOrder() { return order; }

    // Setters (Nếu cần để hoạt động với Firestore)
    public void setName(String name) { this.name = name; }
    public void setYear(int year) { this.year = year; }
    public void setDesc(String desc) { this.desc = desc; }
    public void setZone(String zone) { this.zone = zone; }
    public void setOrder(int order) { this.order = order; }
}