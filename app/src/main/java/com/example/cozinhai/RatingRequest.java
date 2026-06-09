package com.example.cozinhai;

import com.google.gson.annotations.SerializedName;

public class RatingRequest {
    @SerializedName("recipeId")
    private String recipeId;

    @SerializedName("grade")
    private float grade;

    @SerializedName("comment")
    private String comment;

    public RatingRequest(String recipeId, float grade, String comment) {
        this.recipeId = recipeId;
        this.grade = grade;
        this.comment = comment;
    }

    public String getRecipeId() { return recipeId; }
    public float getGrade() { return grade; }
    public String getComment() { return comment; }
}
