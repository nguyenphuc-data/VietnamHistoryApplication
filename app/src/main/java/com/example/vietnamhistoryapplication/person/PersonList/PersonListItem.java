package com.example.vietnamhistoryapplication.person.PersonList;

public class PersonListItem {
    public String slug;
    public String name;
    public String date;
    public String title;
    public String image;

    public PersonListItem(String slug, String name, String date, String title,String image){
        this.slug = slug;
        this.name = name;
        this.date = date;
        this.title = title;
        this.image = image;
    }
}
