package com.example.cozinhai;

import com.google.gson.annotations.SerializedName;

public class FavoriteRequest {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("recipeImage")
    private String recipeImage;

    public FavoriteRequest(String id, String title, String recipeImage) {
        this.id = id;
        this.title = title;
        this.recipeImage = recipeImage;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getRecipeImage() { return recipeImage; }
}
