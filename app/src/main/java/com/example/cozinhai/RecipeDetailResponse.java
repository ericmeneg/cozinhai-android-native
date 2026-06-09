package com.example.cozinhai;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RecipeDetailResponse {
    private int id;
    private String title;
    private String image;
    private int servings;
    @SerializedName("readyInMinutes")
    private int readyInMinutes;
    private String instructions;
    private List<ExtendedIngredient> extendedIngredients;
    private List<AnalyzedInstruction> analyzedInstructions;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getImage() { return image; }
    public int getServings() { return servings; }
    public int getReadyInMinutes() { return readyInMinutes; }
    public String getInstructions() { return instructions; }
    public List<ExtendedIngredient> getExtendedIngredients() { return extendedIngredients; }
    public List<AnalyzedInstruction> getAnalyzedInstructions() { return analyzedInstructions; }

    public static class ExtendedIngredient {
        private String original;
        public String getOriginal() { return original; }
    }

    public static class AnalyzedInstruction {
        private List<Step> steps;
        public List<Step> getSteps() { return steps; }
    }

    public static class Step {
        private int number;
        private String step;
        public int getNumber() { return number; }
        public String getStep() { return step; }
    }
}
