package com.example.vietnamhistoryapplication.event.eventDetail.ui;

import java.util.List;

public class SectionListItem extends EventDetailItem {
    public String title;
    public List<String> bullets;

    public SectionListItem(String title, List<String> bullets) {
        this.title = title;
        this.bullets = bullets;
    }
    public String getTitle() { return title; }
    public List<String> getBullets() { return bullets; }
    // Chuyển list thành chuỗi có dấu chấm
    public String asBulletedString() {
        StringBuilder sb = new StringBuilder();
        for (String item : bullets) {
            sb.append("• ").append(item).append("\n");
        }
        return sb.toString();
    }

    @Override
    public int getType() {
        return TYPE_SECTION_LIST;
    }
}
