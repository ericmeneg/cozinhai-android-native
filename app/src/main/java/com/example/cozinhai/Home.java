package com.example.cozinhai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends AppCompatActivity {

    private static final String API_KEY = "3b8b03322d804d3496d6eee0aef27ead";
    private static final String PREFS_NAME = "CozinhaAiPrefs";
    private static final String KEY_RECIPES = "daily_recipes";
    private static final String KEY_DATE = "last_update_date";

    private SpoonacularApi spoonacularApi;
    private View card1, card2, card3;
    private RecyclerView rvSeasonal;
    private Button selecionarBtn;
    private int selectedMonth;
    private final String[] monthNames = {
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        selectedMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

        setupRetrofit();
        findViews();
        checkDailyRecipes();
        setupBottomNavigation();
        updateSeasonalDisplay();

        selecionarBtn.setOnClickListener(v -> showMonthPicker());
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        spoonacularApi = retrofit.create(SpoonacularApi.class);
    }

    private void findViews() {
        card1 = findViewById(R.id.cardRecipe1);
        card2 = findViewById(R.id.cardRecipe2);
        card3 = findViewById(R.id.cardRecipe3);
        rvSeasonal = findViewById(R.id.rvSeasonalIngredients);
        selecionarBtn = findViewById(R.id.selecionarBtn);

        // Configuração fixa do RecyclerView para evitar problemas de layout
        rvSeasonal.setLayoutManager(new GridLayoutManager(this, 3));
        rvSeasonal.setNestedScrollingEnabled(false);
        rvSeasonal.setHasFixedSize(false);
    }

    private void checkDailyRecipes() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedDate = prefs.getString(KEY_DATE, "");
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (todayDate.equals(savedDate)) {
            String jsonRecipes = prefs.getString(KEY_RECIPES, null);
            if (jsonRecipes != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Recipe>>() {}.getType();
                List<Recipe> recipes = gson.fromJson(jsonRecipes, listType);
                displayRecipes(recipes);
                return;
            }
        }
        fetchRandomRecipes(todayDate);
    }

    private void fetchRandomRecipes(String todayDate) {
        spoonacularApi.getRandomRecipes(3, API_KEY).enqueue(new Callback<RandomRecipesResponse>() {
            @Override
            public void onResponse(Call<RandomRecipesResponse> call, Response<RandomRecipesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> recipes = response.body().getRecipes();
                    saveRecipesToCache(recipes, todayDate);
                    displayRecipes(recipes);
                } else {
                    Toast.makeText(Home.this, "Erro ao carregar recomendações", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RandomRecipesResponse> call, Throwable t) {
                Log.e("Home", "Erro na API", t);
            }
        });
    }

    private void saveRecipesToCache(List<Recipe> recipes, String date) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recipes);
        editor.putString(KEY_RECIPES, json);
        editor.putString(KEY_DATE, date);
        editor.apply();
    }

    private void displayRecipes(List<Recipe> recipes) {
        if (recipes == null || recipes.size() < 3) return;
        updateCard(card1, recipes.get(0));
        updateCard(card2, recipes.get(1));
        updateCard(card3, recipes.get(2));
    }

    private void updateCard(View card, Recipe recipe) {
        TextView title = card.findViewById(R.id.recipeTitle);
        ImageView image = card.findViewById(R.id.recipeImage);
        title.setText(recipe.getTitle());
        Glide.with(this).load(recipe.getImage()).centerCrop().into(image);
    }

    private void setupBottomNavigation() {
        findViewById(R.id.btnNavSearch).setOnClickListener(v -> {
            startActivity(new Intent(this, Search.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.btnNavProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void updateSeasonalDisplay() {
        selecionarBtn.setText("Mês Selecionado: " + monthNames[selectedMonth - 1]);

        List<SeasonalIngredient> allIngredients = IngredientData.getIngredients();
        List<SeasonalIngredient> filtered = new ArrayList<>();

        for (SeasonalIngredient ing : allIngredients) {
            if (ing.getMeses().contains(selectedMonth)) {
                filtered.add(ing);
            }
        }

        SeasonalIngredientAdapter adapter = new SeasonalIngredientAdapter(filtered);
        rvSeasonal.setAdapter(adapter);
    }

    private void showMonthPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione o mês");
        
        String[] displayMonths = new String[12];
        for(int i=0; i<12; i++) {
            displayMonths[i] = monthNames[i] + (i + 1 == selectedMonth ? " ✓" : "");
        }

        builder.setItems(displayMonths, (dialog, which) -> {
            selectedMonth = which + 1;
            updateSeasonalDisplay();
        });
        builder.show();
    }
}
