package com.example.vietnamhistoryapplication.stageDetail;


public class EventItem {
    public String slug;
    public String title;
    public String dateRange;
    public String smallTitle;
    public String image;
    public String type;
    public Integer sortOrder;
    public EventItem(String slug,String title, String dateRange, String smallTitle, String image, String type, Integer sortOrder){
        this.slug = slug;
        this.title = title;
        this.dateRange = dateRange;
        this.smallTitle = smallTitle;
        this.image = image;
        this.type = type;
        this.sortOrder = sortOrder;

    }

}
