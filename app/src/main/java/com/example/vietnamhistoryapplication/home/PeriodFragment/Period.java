package com.example.vietnamhistoryapplication.home.PeriodFragment;

import java.io.Serializable;

public class Period implements Serializable {
    String slug, title, periodRange, summary, image, description ;

    public Period(String slug, String title, String periodRange, String summary, String image,String description) {
        this.slug = slug;
        this.title = title;
        this.periodRange = periodRange;
        this.summary = summary;
        this.image = image;
        this.description = description;

    }
    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getPeriodRange() {
        return periodRange;
    }

    public String getSummary() {
        return summary;
    }

    public String getImage() {
        return image;
    }
    public String getDescription() {
        return description;
    }
}