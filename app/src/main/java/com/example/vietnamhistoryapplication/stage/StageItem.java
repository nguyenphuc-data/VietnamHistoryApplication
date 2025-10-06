package com.example.vietnamhistoryapplication.stage;

import java.io.Serializable;

public class StageItem implements Serializable {
    public String slug;
    public String title;
    public String stageRange;
    public String overview;
    public String image;

    public StageItem(String slug, String title, String stageRange, String overview,String image) {
        this.slug = slug;
        this.title = title;
        this.stageRange = stageRange;
        this.overview = overview;
        this.image = image;
    }
}
