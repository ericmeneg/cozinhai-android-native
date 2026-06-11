package com.example.cozinhai;

import com.google.gson.annotations.SerializedName;

public class Recipe {
    @SerializedName(value = "id", alternate = {"recipeId"})
    private int id;

    private String title;

    @SerializedName(value = "image", alternate = {"recipeImage"})
    private String image;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getImage() { return image; }
}
