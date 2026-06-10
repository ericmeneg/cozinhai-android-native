package com.example.cozinhai;

import com.google.gson.annotations.SerializedName;

public class UserComment {
    @SerializedName("recipeId")
    private String recipeId;

    @SerializedName("title")
    private String title;

    @SerializedName("recipeImage")
    private String recipeImage;

    @SerializedName("grade")
    private float grade;

    @SerializedName("comment")
    private String comment;

    public String getRecipeId() { return recipeId; }
    public String getTitle() { return title; }
    public String getRecipeImage() { return recipeImage; }
    public float getGrade() { return grade; }
    public String getComment() { return comment; }
}
