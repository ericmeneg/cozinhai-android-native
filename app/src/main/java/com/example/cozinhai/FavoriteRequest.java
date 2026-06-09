package com.example.cozinhai;

import com.google.gson.annotations.SerializedName;

public class FavoriteRequest {
    @SerializedName("recipeId")
    private String recipeId;

    @SerializedName("title")
    private String title;

    @SerializedName("recipeImage")
    private String recipeImage;

    public FavoriteRequest(String recipeId, String title, String recipeImage) {
        this.recipeId = recipeId;
        this.title = title;
        this.recipeImage = recipeImage;
    }

    public String getRecipeId() { return recipeId; }
    public String getTitle() { return title; }
    public String getRecipeImage() { return recipeImage; }
}
