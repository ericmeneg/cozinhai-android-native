package com.example.cozinhai;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedRecipesActivity extends AppCompatActivity {

    private RecyclerView rvSavedRecipes;
    private SavedRecipesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes);

        rvSavedRecipes = findViewById(R.id.rvSavedRecipes);
        rvSavedRecipes.setLayoutManager(new LinearLayoutManager(this));

        fetchSavedRecipes();
    }

    private void fetchSavedRecipes() {
        android.content.SharedPreferences prefs = getSharedPreferences("CozinhaAiPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", "");
        String token = prefs.getString("access_token", "");

        if (userId.isEmpty() || token.isEmpty()) {
            Toast.makeText(this, "Faça login para ver favoritos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AuthApi authApi = NetworkClient.getAuthApi();
        authApi.getFavorites(userId, "Bearer " + token).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new SavedRecipesAdapter(response.body(), recipe -> {
                        android.content.Intent intent = new android.content.Intent(SavedRecipesActivity.this, RecipeDetailActivity.class);
                        intent.putExtra("RECIPE_ID", recipe.getId());
                        startActivity(intent);
                    });
                    rvSavedRecipes.setAdapter(adapter);
                } else {
                    Log.e("SAVED_RECIPES", "Erro ao carregar: " + response.code());
                    Toast.makeText(SavedRecipesActivity.this, "Erro ao carregar favoritos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e("SAVED_RECIPES", "Falha na conexão", t);
                Toast.makeText(SavedRecipesActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
