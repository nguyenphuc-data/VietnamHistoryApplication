package com.example.vietnamhistoryapplication.models;

import java.io.Serializable;

public class Event implements Serializable {
    private String name = "";
    private int year = 0;
    private String desc = "";
    private String zone = "";

    public Event() {}

    public String getName() { return name; }
    public int getYear() { return year; }
    public String getDesc() { return desc; }
    public String getZone() { return zone; }
}