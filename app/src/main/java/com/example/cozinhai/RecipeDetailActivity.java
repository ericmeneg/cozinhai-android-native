package com.example.cozinhai;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeDetailActivity extends AppCompatActivity {

    private static final String API_KEY = "3b8b03322d804d3496d6eee0aef27ead";
    private SpoonacularApi spoonacularApi;

    private ImageView ivRecipeImage;
    private TextView tvRecipeTitle, tvServings, tvReadyTime, tvIngredients, tvInstructions;
    private ImageButton btnBack, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        findViews();
        setupRetrofit();

        int recipeId = getIntent().getIntExtra("RECIPE_ID", -1);
        if (recipeId != -1) {
            fetchRecipeDetails(recipeId);
        } else {
            Toast.makeText(this, "Erro ao carregar ID da receita", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void findViews() {
        ivRecipeImage = findViewById(R.id.ivRecipeImage);
        tvRecipeTitle = findViewById(R.id.tvRecipeTitle);
        tvServings = findViewById(R.id.tvServings);
        tvReadyTime = findViewById(R.id.tvReadyTime);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvInstructions = findViewById(R.id.tvInstructions);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        spoonacularApi = retrofit.create(SpoonacularApi.class);
    }

    private void fetchRecipeDetails(int id) {
        spoonacularApi.getRecipeInformation(id, API_KEY).enqueue(new Callback<RecipeDetailResponse>() {
            @Override
            public void onResponse(Call<RecipeDetailResponse> call, Response<RecipeDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayRecipeDetails(response.body());
                } else {
                    Toast.makeText(RecipeDetailActivity.this, "Erro ao buscar detalhes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecipeDetailResponse> call, Throwable t) {
                Log.e("RecipeDetail", "Erro na API", t);
                Toast.makeText(RecipeDetailActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRecipeDetails(RecipeDetailResponse recipe) {
        tvRecipeTitle.setText(recipe.getTitle());
        tvServings.setText("Serve " + recipe.getServings() + " Pessoas");
        tvReadyTime.setText("Pronto em " + recipe.getReadyInMinutes() + " Minutos");

        Glide.with(this).load(recipe.getImage()).into(ivRecipeImage);

        StringBuilder ingredientsText = new StringBuilder();
        for (RecipeDetailResponse.ExtendedIngredient ingredient : recipe.getExtendedIngredients()) {
            ingredientsText.append("• ").append(ingredient.getOriginal()).append("\n");
        }
        tvIngredients.setText(ingredientsText.toString());

        if (recipe.getAnalyzedInstructions() != null && !recipe.getAnalyzedInstructions().isEmpty()) {
            StringBuilder instructionsText = new StringBuilder();
            for (RecipeDetailResponse.Step step : recipe.getAnalyzedInstructions().get(0).getSteps()) {
                instructionsText.append(step.getNumber()).append(". ").append(step.getStep()).append("\n\n");
            }
            tvInstructions.setText(instructionsText.toString());
        } else {
            tvInstructions.setText(recipe.getInstructions() != null ? recipe.getInstructions() : "Instruções não disponíveis.");
        }
    }
}
