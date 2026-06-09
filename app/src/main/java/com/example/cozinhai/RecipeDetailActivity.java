package com.example.cozinhai;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.List;

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
    private android.widget.RatingBar ratingBar;
    private android.widget.EditText etComment;
    private android.widget.Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        findViews();
        setupRetrofit();

        int recipeId = getIntent().getIntExtra("RECIPE_ID", -1);
        if (recipeId != -1) {
            fetchRecipeDetails(recipeId);
            checkIfFavorite(recipeId);
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
        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSave.setOnClickListener(v -> toggleFavorite());
        btnSubmit.setOnClickListener(v -> submitRating());
    }

    private void submitRating() {
        android.content.SharedPreferences prefs = getSharedPreferences("CozinhaAiPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", "");
        String token = prefs.getString("access_token", "");

        if (userId.isEmpty() || token.isEmpty()) {
            Toast.makeText(this, "Faça login para avaliar", Toast.LENGTH_SHORT).show();
            return;
        }

        int recipeId = getIntent().getIntExtra("RECIPE_ID", -1);
        float grade = ratingBar.getRating();
        String comment = etComment.getText().toString();

        if (grade == 0) {
            Toast.makeText(this, "Por favor, dê uma nota", Toast.LENGTH_SHORT).show();
            return;
        }

        RatingRequest request = new RatingRequest(String.valueOf(recipeId), grade, comment);

        AuthApi authApi = NetworkClient.getAuthApi();
        authApi.rateRecipe(userId, "Bearer " + token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RecipeDetailActivity.this, "Avaliação enviada com sucesso!", Toast.LENGTH_SHORT).show();
                    etComment.setText("");
                    ratingBar.setRating(0);
                } else {
                    Log.e("RATINGS", "Erro ao avaliar: " + response.code());
                    Toast.makeText(RecipeDetailActivity.this, "Erro ao enviar avaliação", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("RATINGS", "Falha na conexão", t);
                Toast.makeText(RecipeDetailActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFavorite() {
        if ("favorited".equals(btnSave.getTag())) {
            removeFromFavorites();
        } else {
            saveToFavorites();
        }
    }

    private void saveToFavorites() {
        android.content.SharedPreferences prefs = getSharedPreferences("CozinhaAiPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", "");
        String token = prefs.getString("access_token", "");

        if (userId.isEmpty() || token.isEmpty()) {
            Toast.makeText(this, "Faça login para salvar favoritos", Toast.LENGTH_SHORT).show();
            return;
        }

        int recipeId = getIntent().getIntExtra("RECIPE_ID", -1);
        String recipeTitle = tvRecipeTitle.getText().toString();
        String recipeImage = currentRecipe != null ? currentRecipe.getImage() : "";
        
        FavoriteRequest request = new FavoriteRequest(String.valueOf(recipeId), recipeTitle, recipeImage);

        AuthApi authApi = NetworkClient.getAuthApi();
        String authHeader = "Bearer " + token;

        authApi.addFavorite(userId, authHeader, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    handleFavoriteSuccess();
                } else {
                    handleFavoriteError(response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handleFavoriteFailure(t);
            }
        });
    }

    private void handleFavoriteSuccess() {
        Toast.makeText(RecipeDetailActivity.this, "Receita salva nos favoritos!", Toast.LENGTH_SHORT).show();
        btnSave.setImageResource(android.R.drawable.btn_star_big_on);
        btnSave.setTag("favorited");
    }

    private void handleFavoriteError(int code) {
        Log.e("FAVORITES", "Erro ao salvar: " + code);
        Toast.makeText(RecipeDetailActivity.this, "Erro ao salvar favorito (" + code + ")", Toast.LENGTH_SHORT).show();
    }

    private void handleFavoriteFailure(Throwable t) {
        Log.e("FAVORITES", "Falha na conexão", t);
        Toast.makeText(RecipeDetailActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
    }

    private void removeFromFavorites() {
        android.content.SharedPreferences prefs = getSharedPreferences("CozinhaAiPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", "");
        String token = prefs.getString("access_token", "");

        if (userId.isEmpty() || token.isEmpty()) return;

        int recipeId = getIntent().getIntExtra("RECIPE_ID", -1);

        AuthApi authApi = NetworkClient.getAuthApi();
        authApi.removeFavorite(userId, String.valueOf(recipeId), "Bearer " + token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RecipeDetailActivity.this, "Receita removida dos favoritos", Toast.LENGTH_SHORT).show();
                    btnSave.setImageResource(android.R.drawable.btn_star_big_off);
                    btnSave.setTag("not_favorited");
                } else {
                    Log.e("FAVORITES", "Erro ao remover: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FAVORITES", "Falha na conexão ao remover", t);
            }
        });
    }

    private void checkIfFavorite(int recipeId) {
        android.content.SharedPreferences prefs = getSharedPreferences("CozinhaAiPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", "");
        String token = prefs.getString("access_token", "");

        if (userId.isEmpty() || token.isEmpty()) return;

        AuthApi authApi = NetworkClient.getAuthApi();
        authApi.getFavorites(userId, "Bearer " + token).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Recipe recipe : response.body()) {
                        if (recipe.getId() == recipeId) {
                            btnSave.setImageResource(android.R.drawable.btn_star_big_on);
                            btnSave.setTag("favorited");
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e("FAVORITES", "Erro ao carregar favoritos", t);
            }
        });
    }

    private void setupRetrofit() {
        spoonacularApi = NetworkClient.getSpoonacularApi();
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

    private RecipeDetailResponse currentRecipe;

    private void displayRecipeDetails(RecipeDetailResponse recipe) {
        this.currentRecipe = recipe;
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
