package com.example.vietnamhistoryapplication.person.PersonDetail;

import java.util.List;

public class PersonDetailItem {
    public String horizontalImage;
    public String slug;
    public String name;
    public String title;
    public String birth_year;
    public String death_year;
    public String hometown;
    public String overview;
    public List<String> lifetime;
    public List<String> achievements;
    public Video video;

    public static class Video {
        public String content;
        public String link;

        public Video() {}

        public Video(String content, String link) {
            this.content = content;
            this.link = link;
        }
    }

    public PersonDetailItem() {}

    public PersonDetailItem(String slug, String name, String title, String birth_year, String death_year,
                            String hometown, String overview, List<String> lifetime, List<String> achievements,
                            String horizontalImage, Video video) {
        this.slug = slug;
        this.name = name;
        this.title = title;
        this.birth_year = birth_year;
        this.death_year = death_year;
        this.hometown = hometown;
        this.overview = overview;
        this.lifetime = lifetime;
        this.achievements = achievements;
        this.horizontalImage = horizontalImage;
        this.video = video;
    }
}