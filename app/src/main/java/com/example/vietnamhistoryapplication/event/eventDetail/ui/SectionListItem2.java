package com.example.vietnamhistoryapplication.event.eventDetail.ui;

import java.util.List;

public class SectionListItem2 extends EventDetailItem {
    private String title;
    private List<String> usAllies;
    private List<String> vn;

    public SectionListItem2(String title, List<String> usAllies, List<String> vn) {
        this.title = title;
        this.usAllies = usAllies;
        this.vn = vn;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getUsAllies() {
        return usAllies;
    }

    public List<String> getVn() {
        return vn;
    }
    public String asBulletedStringUs() {
        StringBuilder sb = new StringBuilder();
        if (usAllies != null) {
            for (String s : usAllies) {
                if (s == null || s.isEmpty()) continue;
                sb.append("• ").append(s).append("\n");
            }
        }
        return sb.toString().trim();
    }
    public String asBulletedStringVn() {
        StringBuilder sb = new StringBuilder();
        if (vn != null) {
            for (String s : vn) {
                if (s == null || s.isEmpty()) continue;
                sb.append("• ").append(s).append("\n");
            }
        }
        return sb.toString().trim();
    }
    @Override
    public int getType() {
        return TYPE_SECTION_LIST2;
    }
}
