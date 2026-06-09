package com.example.cozinhai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class SavedRecipesAdapter extends RecyclerView.Adapter<SavedRecipesAdapter.SavedRecipeViewHolder> {

    private final List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public SavedRecipesAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SavedRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card, parent, false);
        return new SavedRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedRecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe, listener);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class SavedRecipeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView recipeImage;
        private final TextView recipeTitle;
        private final View btnViewRecipe;

        public SavedRecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeTitle = itemView.findViewById(R.id.recipeTitle);
            btnViewRecipe = itemView.findViewById(R.id.btnViewRecipe);
        }

        public void bind(Recipe recipe, OnRecipeClickListener listener) {
            recipeTitle.setText(recipe.getTitle());
            Glide.with(itemView.getContext())
                    .load(recipe.getImage())
                    .into(recipeImage);

            itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
            btnViewRecipe.setOnClickListener(v -> listener.onRecipeClick(recipe));
        }
    }
}
