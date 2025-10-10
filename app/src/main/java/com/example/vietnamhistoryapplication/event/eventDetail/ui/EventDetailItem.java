package com.example.vietnamhistoryapplication.event.eventDetail.ui;

public abstract class EventDetailItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_INTRO = 1;
    public static final int TYPE_SECTION_LIST = 2;
    public static final int TYPE_SECTION_LIST2 = 3;
    public static final int TYPE_SLIDES = 4;
    public static final int TYPE_VIDEO = 5;

    public abstract int getType();
}
