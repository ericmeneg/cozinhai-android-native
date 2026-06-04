package com.example.cozinhai;

import com.google.gson.annotations.SerializedName;

public class Recipe {
    private int id;
    private String title;
    private String image;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getImage() { return image; }
}
